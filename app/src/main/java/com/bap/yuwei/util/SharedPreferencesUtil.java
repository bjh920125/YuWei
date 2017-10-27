package com.bap.yuwei.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	
	private final static String FILE_NAME="haoyidianxml";
	
	/**
	 * 设置String类型的值
	 * @param context
	 */
	public static void putString(Context context,String fieldName,String fieldValue){
		Editor editor=context.getSharedPreferences(FILE_NAME, 0).edit();
		editor.putString(fieldName, fieldValue);
		editor.commit();
	}
	
	/**
	 * 设置int类型的值
	 */
	public static void putInt(Context context,String fieldName,int fieldValue){
		Editor editor=context.getSharedPreferences(FILE_NAME, 0).edit();
		editor.putInt(fieldName, fieldValue);
		editor.commit();
	}
	
	/**
	 * 设置boolean类型的值
	 */
	public static void putBoolean(Context context,String fieldName,boolean fieldValue){
		Editor editor=context.getSharedPreferences(FILE_NAME, 0).edit();
		editor.putBoolean(fieldName, fieldValue);
		editor.commit();
	}
	
	/**
	 * 获取String类型的值
	 */
	public static String getString(Context context,String fieldName){
		SharedPreferences data=context.getSharedPreferences(FILE_NAME, 0);
		return data.getString(fieldName, null);
	}
	
	/**
	 * 获取int类型的值
	 */
	public static int getInt(Context context,String fieldName){
		SharedPreferences data=context.getSharedPreferences(FILE_NAME, 0);
		return data.getInt(fieldName, -1);
	}
	
	/**
	 * 获取boolean类型的值
	 */
	public static boolean getBoolean(Context context,String fieldName){
		SharedPreferences data=context.getSharedPreferences(FILE_NAME, 0);
		return data.getBoolean(fieldName, false);
	}
}
