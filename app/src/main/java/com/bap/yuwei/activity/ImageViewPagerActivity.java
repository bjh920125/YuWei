package com.bap.yuwei.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseChoosePhotoActivity;
import com.bap.yuwei.adapter.ImgVPAdapter;
import com.bap.yuwei.entity.BaseAttachment;
import com.bap.yuwei.entity.event.DeleteImageEvent;
import com.bap.yuwei.view.PhotoViewInViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 大图展示（左右滑动）
 * @author jianhua
 *
 */
public class ImageViewPagerActivity extends AppCompatActivity implements OnPageChangeListener{
	private PhotoViewInViewPager mViewPager;
	private ImageView imgDelete;
	private TextView tvIndex;
	
	private ImgVPAdapter mAdapter;
	private List<Object> mFilePathes;
	private int startIndex;
	private int currentIndex;
	private boolean needDeleteImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		mFilePathes=(List<Object>) getIntent().getSerializableExtra(BaseAttachment.KEY);
		removeAddIcon();
		startIndex=getIntent().getIntExtra(BaseAttachment.POSITION, 0);
		currentIndex=startIndex;
		needDeleteImg=getIntent().getBooleanExtra(BaseAttachment.DELETE_FLAG, true);
		if(!needDeleteImg){
			imgDelete.setVisibility(View.GONE);
		}
		mAdapter=new ImgVPAdapter(mFilePathes, this);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(startIndex);
		mViewPager.setOnPageChangeListener(this);
	    mViewPager.setPageTransformer(true, new DepthPageTransformer());
		setTextIndex(startIndex);
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int position) {
		setTextIndex(position);
		currentIndex=position;
	}

	public void initView() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image_view_pager);
		mViewPager=(PhotoViewInViewPager) findViewById(R.id.vp_image);
		tvIndex=(TextView) findViewById(R.id.tv_index);
		imgDelete=(ImageView) findViewById(R.id.img_delete);
	}

	public void onViewClick(View view) {
		switch(view.getId()){
		case R.id.img_delete:
			if(mFilePathes.size()>0){//有图片删除
				//向上报界面发送事件，更新选择的图片
				EventBus.getDefault().post(new DeleteImageEvent(mFilePathes.get(currentIndex),currentIndex));
				mFilePathes.remove(currentIndex);
				mAdapter.notifyDataSetChanged();
				if(mFilePathes.size()==0){
					//删除后没有图片时结束页面
					finish();
				}else{//更新数字指示器
					if(currentIndex==mFilePathes.size()){
						setTextIndex(currentIndex-1);
					}else{
						setTextIndex(currentIndex);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置当前位置，如：1/5
	 * @param position
	 */
	private void setTextIndex(int position){
		tvIndex.setText((position+1)+"/"+mFilePathes.size());
	}

	/**
	 * 去掉添加图片的icon
	 */
	private void removeAddIcon(){
		int lastIndex=mFilePathes.size()-1;
		if(mFilePathes.get(lastIndex).equals(BaseChoosePhotoActivity.ADD_BTN_NAME)){
			mFilePathes.remove(lastIndex);
		}
	}
	
	/***
	 * 切换动画
	 */
	class DepthPageTransformer implements PageTransformer {  
	    private static final float MIN_SCALE = 0.75f;  
	  
	    public void transformPage(View view, float position) {  
	        int pageWidth = view.getWidth();  
	  
	        if (position < -1) { // [-Infinity,-1)  
	            // This page is way off-screen to the left.  
	            view.setAlpha(0);  
	  
	        } else if (position <= 0) { // [-1,0]  
	            // Use the default slide transition when moving to the left page  
	            view.setAlpha(1);  
	            view.setTranslationX(0);  
	            view.setScaleX(1);  
	            view.setScaleY(1);  
	  
	        } else if (position <= 1) { // (0,1]  
	            // Fade the page out.  
	            view.setAlpha(1 - position);  
	  
	            // Counteract the default slide transition  
	            view.setTranslationX(pageWidth * -position);  
	  
	            // Scale the page down (between MIN_SCALE and 1)  
	            float scaleFactor = MIN_SCALE  + (1 - MIN_SCALE) * (1 - Math.abs(position));  
	            view.setScaleX(scaleFactor);  
	            view.setScaleY(scaleFactor);  
	  
	        } else { // (1,+Infinity]  
	            // This page is way off-screen to the right.  
	            view.setAlpha(0);  
	        }  
	    }  
	}
}
