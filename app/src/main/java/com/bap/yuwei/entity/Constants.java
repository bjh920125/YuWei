package com.bap.yuwei.entity;

/**
 * Created by Administrator on 2017/10/27.
 */

public class Constants {

    public static final String URL="http://111.231.141.183:9877/";
    //public static final String URL="http://192.168.1.182:9877/eoogo/";

    public static final String GOODS_SHARE_URL="http://122.152.192.178/goodDetailsShare.html?goodsId=";

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

    /**订单来源 0表示立即购买*/
    public static final int ORDER_RESUORCE_BUY=0;
    /**订单来源 1表示从购物车*/
    public static final int ORDER_RESUORCE_CART=1;

    /**仅退款*/
    public static final int REFUND_MONEY=0;
    /**退货退款*/
    public static final int REFUND_MONEY_AND_GOODS=1;


    //0=待付款,1=待发货/已付款,2=已发货,3=退款中,4=待评价,5=已完成,6=已关闭,7=已取消,8=已退款
    public static final int ORDER_STATUS_PENDING_PAY = 0; //待付款
    public static final int ORDER_STATUS_PRE_DELIVERED = 1; //待发货/已付款
    public static final int ORDER_STATUS_HAS_SENDED = 2;// 已发货
    public static final int ORDER_3 = 3; //退款中
    public static final int ORDER_STATUS_PRE_EVALUATED = 4; //待评价
    public static final int ORDER_STATUS_HAS_COMPLETED = 5; //已完成
    public static final int ORDER_STATUS_CLOSED = 6; //已关闭
    public static final int ORDER_STATUS_HAS_CANCELED = 7; //已取消
    public static final int ORDER_8 = 8; //已退款


    public static final int ORDER_ITEM_STATUS_HAS_BUYER_RECEIVED = 1;//已收货
    public static final int ORDER_ITEM_STATUS_PRE_BUYER_RECEIVE = 0;//待收货
    public static final int ORDER_ITEM_STATUS_REFUND_PRE_DEAL = 2;//退款待处理
    public static final int ORDER_ITEM_STATUS_REFUND_HAS_REFUSED = 3;//已拒绝退款
    public static final int ORDER_ITEM_STATUS_PRE_BUYER_SEND = 4;//待买家发货
    public static final int ORDER_ITEM_STATUS_PRE_SELLER_RECEIVE = 5;//待商家收货
    public static final int ORDER_ITEM_STATUS_REFUND_CLOSED = 6;//退款关闭
    public static final int ORDER_ITEM_STATUS_REFUND_SUCCESS = 7;//退款成功
    public static final int ORDER_ITEM_STATUS_REFUND_DEALING = 8;//退款处理中（申请提交给了支付宝，但还没反回结果的状态）
    public static final int ORDER_ITEM_STATUS_AFTER_SALES_DEALING = 9;//售后处理中
    public static final int ORDER_ITEM_STATUS_AFTER_SALES_AGREE = 10;//同意售后
    public static final int ORDER_ITEM_STATUS_AFTER_SALES_REFUSE = 11;//拒绝售后
}
