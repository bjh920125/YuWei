package com.bap.yuwei.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.goods.ShopGoodsListActivity;
import com.bap.yuwei.entity.goods.Category;
import com.bap.yuwei.entity.goods.CategoryII;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.goods.ShopCategory;
import com.bap.yuwei.view.NoScrollGridView;

import java.util.List;

/**
 * Created by jianhua on 17/11/4.
 */

public class CategoryAdapter extends BaseAdapter{

    private ShopCategory shopCategory;
    private List<Category> mCategories;
    private Context context;
    private LayoutInflater mInflater;
    private Shop mShop;

    public CategoryAdapter(Shop shop,ShopCategory shopCategory,List<Category> mCategories, Context context) {
        this.mShop=shop;
        this.shopCategory=shopCategory;
        this.mCategories = mCategories;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int i) {
        return mCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder=null;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_category_module, parent, false);
            viewHolder.txtCategory= (TextView) convertView.findViewById(R.id.txt_category);
            viewHolder.llShowAll= (LinearLayout) convertView.findViewById(R.id.ll_show_all);
            viewHolder.gvCategory= (NoScrollGridView) convertView.findViewById(R.id.gv_category);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final Category category=mCategories.get(position);
        viewHolder.txtCategory.setText(category.getCategoryName());
        viewHolder.llShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, ShopGoodsListActivity.class);
                i.putExtra(Shop.KEY,mShop);
                i.putExtra(ShopGoodsListActivity.CATEGORY_KEY,category.getCategoryId()+",");
                context.startActivity(i);
            }
        });

        CategoryItemAdapter itemAdapter=new CategoryItemAdapter(shopCategory.getCategoryMap().get(category.getCategoryId()+""),context);
        viewHolder.gvCategory.setAdapter(itemAdapter);

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.gvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CategoryII categoryII= (CategoryII) (finalViewHolder.gvCategory).getItemAtPosition(i);
                Intent ii=new Intent(context, ShopGoodsListActivity.class);
                ii.putExtra(Shop.KEY,mShop);
                ii.putExtra(ShopGoodsListActivity.CATEGORY_KEY,category.getCategoryId()+","+categoryII.getCategoryId2()+",");
                context.startActivity(ii);
            }
        });
        return convertView;
    }

    class ViewHolder{
        LinearLayout llShowAll;
        TextView txtCategory;
        NoScrollGridView gvCategory;
    }
}
