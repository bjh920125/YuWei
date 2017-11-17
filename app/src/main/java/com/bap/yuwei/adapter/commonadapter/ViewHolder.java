package com.bap.yuwei.adapter.commonadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewHolder{
    private final SparseArray<View> mViews;  
    private int mPosition;  
    private View mConvertView;  
    private DisplayImageOptions options;
    private ViewHolder(Context context, ViewGroup parent, int layoutId,int position){
        this.mPosition = position;  
        this.mViews = new SparseArray<View>();  
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);  
        mConvertView.setTag(this);
        options = DisplayImageOptionsUtil.getOptionsWithoutFade();
    }  
  
 
    public static ViewHolder get(Context context, View convertView,  ViewGroup parent, int layoutId, int position){
        if (convertView == null){
            return new ViewHolder(context, parent, layoutId, position);  
        }  
        return (ViewHolder) convertView.getTag();  
    }  
  
    public View getConvertView(){
        return mConvertView;  
    }  

    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);  
        if (view == null){  
            view = mConvertView.findViewById(viewId);  
            mViews.put(viewId, view);  
        }  
        return (T) view;  
    }  
  
    /** 
     * 设置TextView的值
     * @param viewId 
     * @param text 
     * @return 
     */  
    public ViewHolder setText(int viewId, String text){
        TextView view = getView(viewId);  
        view.setText(text);  
        return this;  
    }


    /**
     * 设置TextView的值
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setHtmlText(int viewId, String text){
        TextView view = getView(viewId);
        view.setText( Html.fromHtml(text));
        return this;
    }

    /** 
     * 设置ButtonTextView的值
     * @param viewId 
     * @param text 
     * @return 
     */  
    public ViewHolder setButtonText(int viewId, String text){
        Button view = getView(viewId);  
        view.setText(text);  
        return this;  
    } 
    
    
    /** 
     * 设置TextView的颜色
     * @param viewId 
     * @return
     */  
    public ViewHolder setTextColor(int viewId,int color){
        TextView view = getView(viewId);  
        view.setTextColor(color);  
        return this;  
    }  
    
    /**
     * 设置TextView的值和颜色
     * @param viewId
     * @param text
     * @param color
     * @return
     */
    public ViewHolder setTextWithColor(int viewId,String text,int color){
    	TextView view=getView(viewId);
    	view.setText(text);
    	view.setTextColor(color);  
    	return this;  
    }
    
    /**
     *设置TextView的横线
     */
    public ViewHolder setTextWithFlag(int viewId,String text,int flag){
    	TextView view=getView(viewId);
    	view.setText(text);
    	view.getPaint().setFlags(flag);
    	return this;
    }
    
    public ViewHolder setBoldText(int viewId,String text){
    	TextView view=getView(viewId);
    	view.setText(text);
    	TextPaint tp = view.getPaint();
    	tp.setFakeBoldText(true);
    	return this;
    }
    
    /** 
     * 设置ImageView的图片
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageResource(int viewId, int drawableId){
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;  
    }  
  
    /** 
     * 设置ImageView的图片
     * @param viewId 
     * @return
     */  
    public ViewHolder setImageBitmap(int viewId, Bitmap bm){
        ImageView view = getView(viewId);  
        view.setImageBitmap(bm);  
        return this;  
    }  
  
    /** 
     * 设置ImageView的图片 
     * @param viewId 
     * @return
     */  
    public ViewHolder setImageByUrl(int viewId, String url){
    	ImageView view=getView(viewId);
    	ImageLoader.getInstance().displayImage(url, view,options);
        return this;
    }  
    
  /**
   * 设置View的click事件
   * @param viewId
   * @param onClickListener
   * @return
   */
    public ViewHolder setOnClickListener(int viewId,OnClickListener onClickListener){
    	View view=getView(viewId);
    	view.setOnClickListener(onClickListener);
    	return this;
    }
    /**
     * 设置View的可见性
     * @return
     */
    public ViewHolder setVisibility(int viewId,int visibility){
    	View view=getView(viewId);
    	view.setVisibility(visibility);
    	return this;
    }
    
    /**
     * 设置checkBox
     * @param viewId
     * @param checked
     * @return
     */
    public ViewHolder setChecked(int viewId,boolean checked){
    	CheckBox checkBox=getView(viewId);
    	checkBox.setChecked(checked);
    	return this;
    }
    
    /**
     * 设置背景色
     * @param viewId
     * @param color
     * @return
     */
    public ViewHolder setBackgroundColor(int viewId,int color){
    	View view=getView(viewId);
    	view.setBackgroundColor(color);
    	return this;
    }
    
    public int getPosition(){
        return mPosition;  
    } 
}  