package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.goods.CategoryII;

import java.util.List;

/**
 * Created by jianhua on 17/11/4.
 */

public class CategoryItemAdapter extends BaseAdapter {

    private List<CategoryII> mCategories;
    private Context context;
    private LayoutInflater mInflater;

    public CategoryItemAdapter(List<CategoryII> mCategories, Context context) {
        this.mCategories = mCategories;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public CategoryII getItem(int i) {
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
            convertView = mInflater.inflate(R.layout.item_category_text, parent, false);
            viewHolder.txtCategory= (TextView) convertView.findViewById(R.id.txt_category);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        CategoryII category=mCategories.get(position);
        viewHolder.txtCategory.setText(category.getCategoryName2());
        return convertView;
    }

    class ViewHolder{
        TextView txtCategory;
    }
}
