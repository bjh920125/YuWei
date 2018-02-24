package com.bap.yuwei.util;

import android.graphics.Bitmap;

import com.bap.yuwei.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class DisplayImageOptionsUtil {
	/**
	 * 有渐隐渐显效果
	 * @return
	 */
	public static DisplayImageOptions getOptions(){
		return new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_img)
		.showImageForEmptyUri(R.drawable.image_break)
		.showImageOnFail(R.drawable.image_break)
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new FadeInBitmapDisplayer(100))
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	/**
	 * 无渐隐渐显效果
	 * @return
	 */
	public static DisplayImageOptions getOptionsWithoutFade(){
		return new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_img)
		.showImageForEmptyUri(R.drawable.image_break)
		.showImageOnFail(R.drawable.image_break)
		.cacheOnDisk(true)
		.cacheInMemory(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}


	/**
	 * 圆角图片
	 * @return
	 */
	public static DisplayImageOptions getOptionsRounded(){
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_img)
				.showImageForEmptyUri(R.drawable.image_break)
				.showImageOnFail(R.drawable.image_break)
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(10))
				.build();
	}


	/**
	 * 圆角图片
	 * @return
	 */
	public static DisplayImageOptions getOptionsRounded(int round){
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_img)
				.showImageForEmptyUri(R.drawable.image_break)
				.showImageOnFail(R.drawable.image_break)
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(round))
				.build();
	}
}
