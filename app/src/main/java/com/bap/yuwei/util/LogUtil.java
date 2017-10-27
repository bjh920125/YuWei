package com.bap.yuwei.util;

import android.util.Log;

/**
 * Created by jianhua on 2016/6/30.
 */
public class LogUtil {
    //bebug模式
    private static final boolean DEBUG=true;
    //全部logName
    private static final String APP_NAME ="HAO YI DIAN";

    /**
     * 自定义输出
     * @param msg
     */
    public static void print(String msg){
        if (DEBUG){
            Log.i(APP_NAME,msg);
        }
    }

    /**
     * 自定义输出
     * @param name
     * @param msg
     */
    public static void print(String name,String msg){
        if (DEBUG) {
            Log.i(name, msg);
        }
    }
}
