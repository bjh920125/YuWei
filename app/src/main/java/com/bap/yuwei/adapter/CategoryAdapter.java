package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bap.yuwei.R;
import com.bap.yuwei.entity.goods.Category;
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

    public CategoryAdapter(ShopCategory shopCategory,List<Category> mCategories, Context context) {
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
        Category category=mCategories.get(position);
        viewHolder.txtCategory.setText(category.getCategoryName());
        viewHolder.llShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        CategoryItemAdapter itemAdapter=new CategoryItemAdapter(shopCategory.getCategoryMap().get(category.getCategoryId()+""),context);
        viewHolder.gvCategory.setAdapter(itemAdapter);
        return convertView;
    }

    class ViewHolder{
        LinearLayout llShowAll;
        TextView txtCategory;
        NoScrollGridView gvCategory;
    }
}
