package com.bap.yuwei.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	public static void showLong(Context context,String info){
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}
	
	public static void showShort(Context context,String info){
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

}
