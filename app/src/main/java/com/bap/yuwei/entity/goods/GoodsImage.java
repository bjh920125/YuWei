package com.bap.yuwei.entity.goods;

/**
 * Created by Administrator on 2017/10/31.
 */

public class GoodsImage {
    public static final String KEY="goodsimage.key";

    private long goodsImageId;
    private String goodsImagePath;

    public long getGoodsImageId() {
        return goodsImageId;
    }

    public void setGoodsImageId(long goodsImageId) {
        this.goodsImageId = goodsImageId;
    }

    public String getGoodsImagePath() {
        return goodsImagePath;
    }

    public void setGoodsImagePath(String goodsImagePath) {
        this.goodsImagePath = goodsImagePath;
    }
}
