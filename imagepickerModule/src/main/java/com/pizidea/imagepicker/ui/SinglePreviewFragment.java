package com.pizidea.imagepicker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.pizidea.imagepicker.ImgLoader;
import com.pizidea.imagepicker.UilImgLoader;
import com.pizidea.imagepicker.bean.ImageItem;
import com.pizidea.imagepicker.widget.TouchImageView;

/**
 * Created by Administrator on 2017/11/20.
 */

public class SinglePreviewFragment extends Fragment {

    Activity mContext;
    private boolean enableSingleTap = true;//singleTap to do something
    public static final String KEY_URL = "key_url";
    private TouchImageView imageView;
    private String url;
    ImgLoader mImagePresenter;//interface to load image,you can implements it with your own code

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mImagePresenter = new UilImgLoader();
        Bundle bundle = getArguments();

        ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);

        url = imageItem.path;


        imageView = new TouchImageView(mContext);
        imageView.setBackgroundColor(0xff000000);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);

        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (enableSingleTap) {
                    if(mContext instanceof ImagePreviewFragment.OnImageSingleTapClickListener){
                        ((ImagePreviewFragment.OnImageSingleTapClickListener)mContext).onImageSingleTap(e);
                    }
                }
                return false;
            }
            @Override public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
            @Override public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

        });

        ((UilImgLoader)mImagePresenter).onPresentImage(imageView, url, imageView.getWidth());//display the image with your own ImageLoader

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return imageView;
    }

}