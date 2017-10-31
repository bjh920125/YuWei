package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.sys.LoginActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/10/27.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout rlPersonInfo;
    private ImageView imgHead;
    private TextView txtName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshUI();
    }

    public void refreshUI() {
        if(null != mUser){
            txtName.setText(mUser.getUsername());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mUser.getAvatar(),imgHead, DisplayImageOptionsUtil.getOptions());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_person_info:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            default:break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mine, container, false);
        rlPersonInfo= (RelativeLayout) fragmentView.findViewById(R.id.rl_person_info);
        imgHead= (ImageView) fragmentView.findViewById(R.id.img_head);
        txtName= (TextView) fragmentView.findViewById(R.id.txt_name);
        rlPersonInfo.setOnClickListener(this);
        return fragmentView;
    }


    @Override
    public void initView() {
        refreshUI();
    }
}
