package com.bap.yuwei.entity.order;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_PRE_BUYER_SEND;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_PRE_SELLER_RECEIVE;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_CLOSED;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_HAS_REFUSED;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_PRE_DEAL;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_SUCCESS;

/**
 * Created by Administrator on 2017/9/11.
 * 退货退款
 */
public class Refund implements Serializable{
    public static final String KEY="refund.key";

    private Long refundId;
    private Long orderId;//订单Id
    private Long orderItemId;//订单条目id
    private Long goodsId;//商品id
    private String title;//商品标题
    private String model;//商品型号
    private String goodsImage;//商品图片
    private Long userId;//买家id
    private String username;//买家名称
    private BigDecimal payMoney;//付款金额
    private BigDecimal refundMoney;//退款金额
    private Integer quantity;//数量
    private BigDecimal freight;//运费
    private BigDecimal modifiedPrice;//修改后的价格
    private String createTime;//创建时间
    private String timeoutTime;//超时时间
    private String refundTime;//退款完成时间
    private String nowTime;//当前时间
    private Long shopId;//店铺id
    private String shopName;//店铺名称
    private String qq;//卖家QQ
    private String refundDesc;//退款说明
    private String refundReason;//退款原因
    private String logisticsCompany;//物流公司
    private String logisticsCompanyCode;//物流公司编码
    private String wayBillNo;//运单号
    private String receiveAddress;//卖家收货信息
    private String sellerMessage;//卖家留言
    private Integer refundType;//退款类型   0：仅退款  1：退货退款
    private Integer status;// 2:退款待处理  3：已拒绝退款  4：待买家发货  5：待商家收货  6：退款关闭  7：退款成功
    private Integer oldStatus;//订单条目的原始状态，用于取消退款时还原订单条目的状态
    private Integer isPlatform;//平台介入  0：否 1：是
    private Integer platformDealStatus;//平台处理结果 0：未处理  1：已处理
    private List<RefundImage> images;
    private List<RefundConsultHistory> histories;//协商历史


    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(String timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsCompanyCode() {
        return logisticsCompanyCode;
    }

    public void setLogisticsCompanyCode(String logisticsCompanyCode) {
        this.logisticsCompanyCode = logisticsCompanyCode;
    }

    public String getWayBillNo() {
        return wayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPlatform() {
        return isPlatform;
    }

    public void setIsPlatform(Integer isPlatform) {
        this.isPlatform = isPlatform;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<RefundImage> getImages() {
        return images;
    }

    public void setImages(List<RefundImage> images) {
        this.images = images;
    }

    public List<RefundConsultHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<RefundConsultHistory> histories) {
        this.histories = histories;
    }

    public Integer getPlatformDealStatus() {
        return platformDealStatus;
    }

    public void setPlatformDealStatus(Integer platformDealStatus) {
        this.platformDealStatus = platformDealStatus;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getSellerMessage() {
        return sellerMessage;
    }

    public void setSellerMessage(String sellerMessage) {
        this.sellerMessage = sellerMessage;
    }

    public Integer getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Integer oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getModifiedPrice() {
        return modifiedPrice;
    }

    public void setModifiedPrice(BigDecimal modifiedPrice) {
        this.modifiedPrice = modifiedPrice;
    }

    public String getNowTime() {
        return new String();
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getStatusText(){
        String result=null;
        switch (status){
            case ORDER_ITEM_STATUS_REFUND_PRE_DEAL:
                result="退款待处理";
                break;
             case ORDER_ITEM_STATUS_REFUND_HAS_REFUSED:
                result="退款被拒绝";
                break;
            case ORDER_ITEM_STATUS_PRE_BUYER_SEND:
                result="待买家发货";
                break;
            case ORDER_ITEM_STATUS_PRE_SELLER_RECEIVE:
                result="待商家确认收货";
             break;
            case ORDER_ITEM_STATUS_REFUND_CLOSED:
                result="退款关闭";
                 break;
            case ORDER_ITEM_STATUS_REFUND_SUCCESS:
              result="退款成功";
                 break;
            default:break;
        }
        return result;
    }
}
