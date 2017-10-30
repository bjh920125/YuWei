package com.bap.yuwei.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bap.yuwei.entity.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
	
	private static List<Activity> activities=new LinkedList<Activity>();
	private static MyApplication instance;
	private static Retrofit mRetrofit;
	private static HashMap<Class, Object> apis = new HashMap<>();

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

	private void initOkHttp(){
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Interceptor.Chain chain) throws IOException {
				Request original = chain.request();
				Request request = original.newBuilder()
						.addHeader("X-Token", null == SharedPreferencesUtil.getString(getApplicationContext(), Constants.XTOKEN_KEY) ?
								"":SharedPreferencesUtil.getString(getApplicationContext(), Constants.XTOKEN_KEY))
						.method(original.method(), original.body())
						.build();
				return chain.proceed(request);
			}
		});

		mRetrofit=new Retrofit.Builder()
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
	
}
