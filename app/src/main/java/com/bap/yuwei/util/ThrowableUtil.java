package com.bap.yuwei.util;

public class ThrowableUtil {
	/**
	 * 返回错误类型
	 * @param t
	 * @return
	 */
	public static String getErrorMsg(Throwable t){
		String strMsg="网络连接异常，请检查网络连接！";
		if(t instanceof java.net.SocketException){
			strMsg="网络连接异常，请检查网络连接！";
		}else if(t instanceof java.net.SocketTimeoutException){
			strMsg="网络连接超时，请稍候再试！";
		}else if(t instanceof java.net.ConnectException){
			strMsg="网络连接异常，请检查网络连接！";
		}else{
			//手动取消网络访问
			strMsg="";
		}
		return strMsg;
	}
}

