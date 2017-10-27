package com.bap.yuwei.util;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;

public class MediaUtil {
	
	public static final String PICTURE_DIR= "picture";
	public static final String VIDEO_DIR= "video";
	public static final String VOICE_DIR= "audio";
	
	/**
	 * APP的目录
	 * @param context
	 * @return
	 */
	public static String getAppFolder(Context context){
		String path=null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			path= Environment.getExternalStorageDirectory()+"/haoyidian/";
		}else{
			path=context.getApplicationContext().getFilesDir().getAbsolutePath()+"/haoyidian/";
		}
		File file=new File(path);
		if(!file.exists()){
			file.mkdir();
		}
		return path;
	}
	
	/**
	 * sd卡是否挂载
	 * @return
	 */
	public static boolean isSdCardMounted(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获得设备ID
	 */
	public static String getDeviceId(Context context){
		TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE );
		return telephonyManager.getDeviceId();  
	}
	
	
	/**
	 * 获取设备型号
	 */
	public static String getDeviceModel(){
		return android.os.Build.MODEL;
	}
}
