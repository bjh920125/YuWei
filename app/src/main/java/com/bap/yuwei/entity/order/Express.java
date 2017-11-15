package com.bap.yuwei.entity.order;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/15.
 */
public class Express implements Serializable{

    public static final String KEY="express.key";

    private String com;
    private String nu;
    private String condition;
    private String ischeck;
    private String message;
    private String state;
    private String status;
    private List<ExpressItem> data;

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExpressItem> getData() {
        return data;
    }

    public void setData(List<ExpressItem> data) {
        this.data = data;
    }

    public String getStatusText(){
        String result=null;
        switch (state){
            case "0":
                result="在途";
                break;
            case "1":
                result="已揽件";
                break;
            case "2":
                result="疑难件";
                break;
            case "3":
                result="已签收";
                break;
            case "4":
                result="签退";
                break;
            case "5":
                result="派件";
                break;
            case "6":
                result="退回";
                break;
            case "10":
                result="待清关";
                break;
            case "11":
                result="清关中";
                break;
            case "12":
                result="已清关";
                break;
            case "13":
                result="清关异常";
                break;
            case "14":
                result="已拒签";
                break;
            default:break;
        }
        return result;
    }

    public String getComName(){
        String result=null;
        switch (com){
            case "youzhengguonei":
                result="邮政包裹/平邮";
                break;
            case "ems":
                result="EMS";
                break;
            case "shunfeng":
                result="顺丰";
                break;
            case "shentong":
                result="申通";
                break;
            case "yuantong":
                result="圆通";
                break;
            case "zhongtong":
                result="中通";
                break;
            case "huitongkuaidi":
                result="汇通";
                break;
            case "yunda":
                result="韵达";
                break;
            case "tiantian":
                result="天天快递";
                break;
            case "debangwuliu":
                result="德邦";
                break;
            case "jd":
                result="京东";
                break;
            case "rufengda":
                result="如风达";
                break;
            default:break;
        }
        return result;
    }
}