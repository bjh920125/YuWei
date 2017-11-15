package com.bap.yuwei.entity.order;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/15.
 */

public class ExpressItem implements Serializable{

    private String context;
    private String ftime;
    private String time;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getFtime() {
        return ftime;
    }

    public void setFtime(String ftime) {
        this.ftime = ftime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
