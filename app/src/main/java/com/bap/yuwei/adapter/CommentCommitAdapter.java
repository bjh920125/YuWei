package com.bap.yuwei.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.ImageViewPagerActivity;
import com.bap.yuwei.entity.BaseAttachment;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.EvaluateItemForm;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.view.NoScrollGridView;
import com.bap.yuwei.view.RatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.bean.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.bap.yuwei.activity.base.BaseChoosePhotoActivity.ADD_BTN_NAME;

/**
 * Created by Administrator on 2017/11/20.
 */

public class CommentCommitAdapter extends BaseAdapter{

    private List<OrderItem> orderItems;
    private List<EvaluateItemForm> itemForms;
    private Context context;
    private LayoutInflater mInflater;

    public CommentCommitAdapter(List<OrderItem> orderItems,List<EvaluateItemForm> itemForms, Context context){
        this.context=context;
        this.orderItems=orderItems;
        this.itemForms=itemForms;
        this.mInflater= LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public OrderItem getItem(int i) {
        return orderItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_comment_commit, parent, false);
            viewHolder.imgGoods= (ImageView) convertView.findViewById(R.id.img_goods);
            viewHolder.txtTitle= (TextView) convertView.findViewById(R.id.txt_goods_title);
            viewHolder.etDesc= (EditText) convertView.findViewById(R.id.et_desc);
            viewHolder.rbDesc= (RatingBar) convertView.findViewById(R.id.star_desc);
            viewHolder.fileGridView= (NoScrollGridView) convertView.findViewById(R.id.gv_img);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        OrderItem orderItem=orderItems.get(i);
        final EvaluateItemForm form=itemForms.get(i);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderItem.getGoodsImage(),viewHolder.imgGoods);
        viewHolder.txtTitle.setText(orderItem.getTitle());
        viewHolder.rbDesc.setStar(5.0f);
        form.setGoodsScore(5);
        form.setIsAnonymous(false);
        viewHolder.rbDesc.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                form.setGoodsScore((int)ratingCount);
            }
        });
        viewHolder.etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                form.setEvaluateComment(StringUtils.getEditTextValue(viewHolder.etDesc));
            }
        });


        final List<String> filePaths=new ArrayList<>();
        filePaths.add(ADD_BTN_NAME);
        final ImgGVAdapter adapter=new ImgGVAdapter(filePaths, context);
        viewHolder.fileGridView.setAdapter(adapter);
        viewHolder.fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == filePaths.size() - 1) {//从相册选照片
                    AndroidImagePicker.getInstance().pickMulti((Activity) context, true, new AndroidImagePicker.OnImagePickCompleteListener() {
                        @Override
                        public void onImagePickComplete(List<ImageItem> items) {
                            if (items != null && items.size() > 0) {
                                updateGridView(items,filePaths,adapter);
                                form.setFilePathes(filePaths);
                                String[] ss=new String[filePaths.size()-1];
                                form.setEvaluationImages(ss);
                            }
                        }
                    });
                } else {//浏览已选照片大图
                    showImages(position,filePaths);
                }
            }
        });
        return convertView;
    }

    /**
     * 更新UI
     */
    private void updateGridView(List<ImageItem> items,List<String> filePaths,ImgGVAdapter adapter) {
        for (ImageItem ii : items) {
            filePaths.add(ii.path);
        }
        resetAddBtn(filePaths);
        adapter.notifyDataSetChanged();
    }

    /**
     * 恢复加号按钮图片
     * 说明：上传照片时会调用上面getUsefulEveidence()方法，此时会去掉最后的加号按钮，一旦出现上传失败，则需要在调用下面的方法恢复加号按钮图片，否则不能再选图片
     */
    protected synchronized void resetAddBtn(List<String> filePaths){
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

    /***
     * 查看大图
     */
    protected void showImages(int startIndex,List<String> filePaths){
        Intent intent=new Intent(context,ImageViewPagerActivity.class);
        intent.putExtra(BaseAttachment.KEY, (Serializable) filePaths);
        intent.putExtra(BaseAttachment.POSITION, startIndex);
        context.startActivity(intent);
    }

    class ViewHolder{
        ImageView imgGoods;
        TextView txtTitle;
        EditText etDesc;
        RatingBar rbDesc;
        NoScrollGridView fileGridView;
    }
}
