package com.bap.yuwei.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.bap.yuwei.activity.sys.LoginActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
	
	private static List<Activity> activities=new LinkedList<Activity>();
	private static MyApplication instance;
	private static Retrofit mRetrofit;
	private static HashMap<Class, Object> apis = new HashMap<>();
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public MyApplication(){}
	public static MyApplication getInstance(){
		if(instance==null){
			instance=new MyApplication(); 
		}
		return instance;
	}

	public void addActivity(Activity activity){
		activities.add(activity);
	}
	
	public void exit(){
		for(Activity activity:activities){
			activity.finish();
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initOkHttp();
		initImageLoader(getApplicationContext());//初始化imageloader
	}

	private void initOkHttp() {
		final Gson gson = new Gson();
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		//httpClient.addNetworkInterceptor(new HttpLoggingInterceptor());
		httpClient.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Interceptor.Chain chain) throws IOException {
				Request original = chain.request();
				Request request = original.newBuilder()
						.addHeader("X-Token", null == SharedPreferencesUtil.getString(getApplicationContext(), Constants.XTOKEN_KEY) ?
								"" : SharedPreferencesUtil.getString(getApplicationContext(), Constants.XTOKEN_KEY))
						.method(original.method(), original.body())
						.build();
				Response response = chain.proceed(request);
				ResponseBody responseBody = response.body();
				long contentLength = responseBody.contentLength();
				BufferedSource source = responseBody.source();
				source.request(Long.MAX_VALUE); // Buffer the entire body.
				Buffer buffer = source.buffer();
				Charset charset = UTF8;
				MediaType contentType = responseBody.contentType();
				if (contentType != null) {
					try {
						charset = contentType.charset(UTF8);
					} catch (UnsupportedCharsetException e) {
						return response;
					}
				}
				if (!isPlaintext(buffer)) {
					return response;
				}
				if (contentLength != 0) {
					try {
						String result = buffer.clone().readString(charset);
						AppResponse appResponse = gson.fromJson(result, AppResponse.class);
						if (appResponse.getCode() == ResponseCode.TOKEN_ERROR || appResponse.getCode() == ResponseCode.TOKEN_INVALID) {
							startActivity(new Intent(getApplicationContext(), LoginActivity.class));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return response;
			}
		});
		mRetrofit = new Retrofit.Builder()
				.client(httpClient.build())
				.baseUrl(Constants.URL)
				.addConverterFactory(GsonConverterFactory.create())
				//.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build();
	}


	private void initImageLoader(Context context) {
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.threadPoolSize(5);
		//config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
		//config.writeDebugLogs(); // Remove for release app
		ImageLoader.getInstance().init(config.build());
	}


	/**
	 * 获取 api
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */

	public <T> T getWebService(Class<T> service) {
		if (!apis.containsKey(service)) {
			T instance = mRetrofit.create(service);
			apis.put(service, instance);
		}
		return (T) apis.get(service);
	}


	/**
	 * 获得当前进程的名字
	 *
	 * @param context
	 * @return 进程号
	 */
	public static String getCurProcessName(Context context) {

		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	static boolean isPlaintext(Buffer buffer) throws EOFException {
		try {
			Buffer prefix = new Buffer();
			long byteCount = buffer.size() < 64 ? buffer.size() : 64;
			buffer.copyTo(prefix, 0, byteCount);
			for (int i = 0; i < 16; i++) {
				if (prefix.exhausted()) {
					break;
				}
				int codePoint = prefix.readUtf8CodePoint();
				if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
					return false;
				}
			}
			return true;
		} catch (EOFException e) {
			return false; // Truncated UTF-8 sequence.
		}
	}

	private boolean bodyEncoded(Headers headers) {
		String contentEncoding = headers.get("Content-Encoding");
		return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
	}
}


	//┏┓　　　┏┓  
	//┏┛┻━━━┛┻┓  
	//┃　　　　　　　┃ 　
	//┃　　　━　　　┃  
	//┃　┳┛　┗┳　┃  
	//┃　　　　　　　┃  
	//┃　　　┻　　　┃  
	//┃　　　　　　　┃  
	//┗━┓　　　┏━┛  
	//┃　　　┃                              神兽保佑　　代码无BUG！　　　　　　  
	//┃　　　┃                             
	//┃　　　┗━━━┓
	//┃　　　　　　　┣┓            祝后来的兄弟和本人都可以代码无BUG！
	//┃　　　　　　　┏┛
	//┗┓┓┏━┳┓┏┛  
	//  ┃┫┫　┃┫┫  
	//  ┗┻┛　┗┻┛ 
	

