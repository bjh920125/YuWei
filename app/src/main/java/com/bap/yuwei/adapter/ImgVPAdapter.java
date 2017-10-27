package com.bap.yuwei.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.example.photoview.PhotoView;

import java.util.List;

/**
 * 大图浏览Adapter
 */
public class ImgVPAdapter extends PagerAdapter{
	private List<Object> mPicUrls;
	private Context mContext;

	public ImgVPAdapter(List<Object> picUrls, Context mContext) {
		this.mPicUrls = picUrls;
		this.mContext = mContext;
	}


	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view=LayoutInflater.from(mContext).inflate(R.layout.item_vp_image, null);
		PhotoView photoView=(PhotoView) view.findViewById(R.id.img_vp);
		final TextView txtProgress= (TextView) view.findViewById(R.id.txt_progress);
		//Glide.with(mContext).load(mPicUrls.get(position)).into(photoView);
		container.addView(view);
		return view;
	}
	
	@Override
	public int getCount() {
		return mPicUrls.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
	
    public int getItemPosition(Object object){   
        return POSITION_NONE;
 }
}
