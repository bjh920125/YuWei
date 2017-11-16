package com.bap.yuwei.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.ImageViewPagerActivity;
import com.bap.yuwei.adapter.ImgGVAdapter;
import com.bap.yuwei.entity.BaseAttachment;
import com.bap.yuwei.entity.event.DeleteImageEvent;
import com.bap.yuwei.view.NoScrollGridView;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.bean.ImageItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseChoosePhotoActivity extends BaseActivity{
	protected List<String> filePaths;
	protected NoScrollGridView fileGridView;
	protected ImgGVAdapter mAdapter;

	protected boolean isShowAddBtn =true;
	public static final String ADD_BTN_NAME="addbtn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void initView() {
		fileGridView=(NoScrollGridView) findViewById(R.id.gv_img);
		filePaths=new ArrayList<>();
		if (isShowAddBtn){
			filePaths.add(ADD_BTN_NAME);
		}
		mAdapter=new ImgGVAdapter(filePaths, mContext);
		fileGridView.setAdapter(mAdapter);

		fileGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == filePaths.size() - 1) {//从相册选照片
					if (isShowAddBtn) {
						chooseMultiPictures(BaseChoosePhotoActivity.this, true);
					} else {
						showImages(position);
					}
				} else {//浏览已选照片大图
					showImages(position);
				}
			}
		});
	}

	/**
	 * 多选
	 *
	 * @param activity
	 * @param isShowCamera
	 */
	protected void chooseMultiPictures(Activity activity, boolean isShowCamera) {
		AndroidImagePicker.getInstance().pickMulti(activity, isShowCamera, new AndroidImagePicker.OnImagePickCompleteListener() {
			@Override
			public void onImagePickComplete(List<ImageItem> items) {
				if (items != null && items.size() > 0) {
					updateGridView(items);
				}
			}
		});
	}

	/**
	 * 单选
	 *
	 * @param activity
	 * @param isShowCamera
	 */
	protected void chooseSinglePictures(Activity activity, boolean isShowCamera) {
		//single select
		AndroidImagePicker.getInstance().pickSingle(activity, isShowCamera, new AndroidImagePicker.OnImagePickCompleteListener() {
			@Override
			public void onImagePickComplete(List<ImageItem> items) {
				if (items != null && items.size() > 0) {
					Log.i("path", "=====selected：" + items.get(0).path);
				}
			}
		});
	}


	/**
	 * 更新UI
	 */
	private void updateGridView(List<ImageItem> items) {
		for (ImageItem ii : items) {
			filePaths.add(ii.path);
		}
		if (isShowAddBtn) {
			resetAddBtn();
		}
		mAdapter.notifyDataSetChanged();
	}

	/***
	 * 查看大图
	 */
	protected void showImages(int startIndex){
		Intent intent=new Intent(mContext,ImageViewPagerActivity.class);
		intent.putExtra(BaseAttachment.KEY, (Serializable) filePaths);
		intent.putExtra(BaseAttachment.POSITION, startIndex);
		startActivity(intent);
	}

	/**
	 * 获取有效的照片（去掉最后一个加号照片）
	 * @return
	 */
	protected List<String> getUsefulImagePathes(){
		if(filePaths.size()>0 && filePaths.get(filePaths.size() - 1).equals(ADD_BTN_NAME)){
			filePaths.remove(filePaths.size() - 1);
		}
		return filePaths;
	}

	/**
	 * 恢复加号按钮图片
	 * 说明：上传照片时会调用上面getUsefulEveidence()方法，此时会去掉最后的加号按钮，一旦出现上传失败，则需要在调用下面的方法恢复加号按钮图片，否则不能再选图片
	 */
	protected synchronized void resetAddBtn(){
		int index=0;
		if(null!=filePaths){
			for(int i=0;i<filePaths.size();i++){
				if(filePaths.get(i).equals(ADD_BTN_NAME)){
					index=i;
				}
			}
			filePaths.remove(index);
			filePaths.add(filePaths.size(),ADD_BTN_NAME);
		}
	}

	/**
	 * 删除图片
	 * @param eie
	 */
	@Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
	public void deleteImage(DeleteImageEvent eie){
		int needDeleteImgIndex=eie.getDeleteIndex();
		filePaths.remove(needDeleteImgIndex);
		mAdapter.notifyDataSetChanged();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
