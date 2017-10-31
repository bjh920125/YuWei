package com.bap.yuwei.entity.goods;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/31.
 */

public class GoodsModel implements Serializable{
    public static final String KEY="goodsmodel.key";

    private long goodsModelId;
    private String goodsModelName;

    public long getGoodsModelId() {
        return goodsModelId;
    }

    public void setGoodsModelId(long goodsModelId) {
        this.goodsModelId = goodsModelId;
    }

    public String getGoodsModelName() {
        return goodsModelName;
    }

    public void setGoodsModelName(String goodsModelName) {
        this.goodsModelName = goodsModelName;
    }
}
