package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseChoosePhotoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class ImgGVAdapter extends BaseAdapter{


	private List<String> imagePathes;
	private Context mContext;
	private LayoutInflater mInflater;
	private boolean showAddBtn;

	public ImgGVAdapter(List<String> images,Context context){
		this.imagePathes=images;
		this.mContext=context;
		this.mInflater=LayoutInflater.from(mContext);

	}


	@Override
	public int getCount() {
		return imagePathes.size();
	}

	@Override
	public Object getItem(int position) {
		return imagePathes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.item_image, null);
			viewHolder.img=(ImageView) convertView.findViewById(R.id.img_comment);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		String path=imagePathes.get(position);
		if(path.equals(BaseChoosePhotoActivity.ADD_BTN_NAME)){
			ImageLoader.getInstance().displayImage("drawable://"+R.drawable.add_image,viewHolder.img);
		}else{
			ImageLoader.getInstance().displayImage("file://"+path,viewHolder.img);
		}
		return convertView;
	}
	
	class ViewHolder{
		ImageView img;
	}

}
