package com.bap.yuwei.entity.http;

/**
 * 消息相应码
 * @author jianhua
 */
public class ResponseCode {
	/**请求成功*/
	public static final int SUCCESS=200;
	/**TOKEN错误*/
	public static final int TOKEN_ERROR = 429;
	/**TOKEN非法*/
	public static final int TOKEN_INVALID = 433;
	/**请求失败*/
	public static final int ERROR=500;


}
