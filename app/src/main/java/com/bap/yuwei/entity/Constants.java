package com.bap.yuwei.entity;

/**
 * Created by Administrator on 2017/10/27.
 */

public class Constants {

    public static final String URL="http://122.152.192.178:9877/";
    public static final String PICTURE_URL="http://oxqok0jcq.bkt.clouddn.com/";


    /**当前用户*/
    public static final String USER_KEY="user.key";
    /**TOKEN*/
    public static final String TOKEN_KEY="token.key";
    /**X-TOKEN*/
    public static final String XTOKEN_KEY="xtoken.key";

    /**设备类型*/
    public static final int DEVICE_TYPE=2;
    /**买家*/
    public static final int BUYER=0;

    /**系统消息*/
    public static final int SYS_MSG=0;
    /**物流消息*/
    public static final int EXPRESS_MSG=1;
    /**订单消息*/
    public static final int ORDER_MSG=2;
    /**通知消息*/
    public static final int NOTICE_MSG=3;


    /**普通发票*/
    public static final int INVOICE_COMMON=0;
    /**电子发票*/
    public static final int INVOICE_ELEC=1;
    /**增值税发票*/
    public static final int INVOICE_VAT=2;

    /**个人抬头发票*/
    public static final int INVOICE_HEADER_PERSONAL=0;
    /**公司抬头发票*/
    public static final int INVOICE_HEADER_UNIT=1;
}
