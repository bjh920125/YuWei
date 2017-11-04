package com.bap.yuwei.entity.goods;

import java.util.List;
import java.util.Map;

/**
 * Created by jianhua on 17/11/4.
 */
public class ShopCategory {
    private List<Category> categories;
    private Map<String,List<CategoryII>> categoryMap;
    private List<String> dates;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Map<String, List<CategoryII>> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, List<CategoryII>> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
