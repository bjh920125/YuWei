package com.bap.yuwei.activity.goods;

import android.os.Bundle;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.event.CategoryEvent;
import com.bap.yuwei.entity.goods.Category;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SearchGoodsCategoryActivity extends BaseActivity {

    private LinearListView lvCategory;

    private List<Category> mCategories;
    private CommonAdapter<Category> mAdapter;

    private List<String> categoryNames;
    public static final String CATEGORY_KEY="category_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryNames=getIntent().getStringArrayListExtra(CATEGORY_KEY);
        mCategories=new ArrayList<>();
        for(String s:categoryNames){
            String[] ss=s.split(":");
            mCategories.add(new Category(Long.valueOf(ss[0]),ss[1]));
        }
        mAdapter=new CommonAdapter<Category>(mContext,mCategories,R.layout.item_search_goods_category) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                viewHolder.setText(R.id.txt_category,item.getCategoryName());
            }
        };
        lvCategory.setAdapter(mAdapter);

        lvCategory.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Category category=mCategories.get(position);
                EventBus.getDefault().post(new CategoryEvent(category.getCategoryId()+""));
                finish();
            }
        });
    }

    public void showAll(View v){
        EventBus.getDefault().post(new CategoryEvent(""));
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_goods_category;
    }

    @Override
    protected void initView() {
        lvCategory= (LinearListView) findViewById(R.id.lv_category);
    }
}
