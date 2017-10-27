package com.bap.yuwei.util;

import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isEmpty(String str) {
		if (str == null
				|| str.length() == 0
				|| str.equalsIgnoreCase("null")
				|| str.isEmpty()
				|| str.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotEmpty(String str) {
		if (str == null
				|| str.length() == 0
				|| str.equalsIgnoreCase("null")
				|| str.isEmpty()
				|| str.trim().equals("")) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * 验证是否是汉字
	 */
	public static boolean isChinese(char c){
		String regExp = "[\u4E00-\u9FA5]";
		return Pattern.matches(regExp, String.valueOf(c));
	}
	
	
	/**
	 * 获得EditText的值
	 * @param et
	 * @return
	 */
	public static String getEditTextValue(EditText et){
		return et.getText().toString().trim();
	}
	
	/**
	 * 获得TextView的值
	 * @param tv
	 * @return
	 */
	public static String getTextViewValue(TextView tv){
		return tv.getText().toString().trim();
	}

	public static String appendTitle(String title,String value){
		return title+value;
	}
	
}
