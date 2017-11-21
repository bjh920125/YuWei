package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.Evaluation;
import com.bap.yuwei.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */
public class CommentAdapter extends ListBaseAdapter<Evaluation> {

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_comment;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Evaluation evaluation=mDataList.get(position);
        ImageView imgHead=holder.getView(R.id.img_head);
        TextView txtUserName=holder.getView(R.id.txt_user_name);
        TextView txtDesc=holder.getView(R.id.txt_desc);
        TextView txtTimeModel=holder.getView(R.id.txt_time_model);
        NoScrollGridView gvImages=holder.getView(R.id.gv_img);
        TextView txtAdditionComment=holder.getView(R.id.txt_addition_comment);
        TextView txtShopName=holder.getView(R.id.txt_shop_name);
        TextView txtShopReply=holder.getView(R.id.txt_shop_reply);
        LinearLayout llAdditionComment=holder.getView(R.id.ll_addtional_comment);
        LinearLayout llReply=holder.getView(R.id.ll_shop_replay);
        LinearLayout llStart=holder.getView(R.id.ll_star);

        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+evaluation.getAvatar(),imgHead);
        String userName=evaluation.getEvaluaterUsername();
        StringBuilder sb=new StringBuilder(userName);
        txtUserName.setText(sb.replace(1,userName.length()-1,"***"));
        txtDesc.setText(evaluation.getEvaluationComment());
        txtTimeModel.setText(evaluation.getCreateTime()+"    分类："+evaluation.getGoodsModel());
        if(null!=evaluation.getHasAdditionalComment() && evaluation.getHasAdditionalComment()==true){
            llAdditionComment.setVisibility(View.VISIBLE);
            txtAdditionComment.setText(evaluation.getAdditionalComment());
        }else {
            llAdditionComment.setVisibility(View.GONE);
        }

        if(null!=evaluation.getHasReplied() && evaluation.getHasReplied()==true){
            llReply.setVisibility(View.VISIBLE);
            txtShopName.setText(evaluation.getShopName()+"：");
            txtShopReply.setText(evaluation.getReply());
        }else {
            llReply.setVisibility(View.GONE);
        }

        String[] images=evaluation.getEvaluationImages();
        if(null!=images && images.length>0){
            for(int i=0;i<images.length;i++){
                images[i]=Constants.PICTURE_URL+ images[i];
            }
            List<String> imgList=Arrays.asList(evaluation.getEvaluationImages());
            ImgGVAdapter adapter=new ImgGVAdapter(imgList,mContext);
            gvImages.setAdapter(adapter);
        }
        llStart.removeAllViews();
        for(int i=0;i<evaluation.getGoodsScore();i++){
            ImageView imageView=new ImageView(mContext);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 5, 15, 5);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.aoj);
            llStart.addView(imageView);
        }

    }
}

