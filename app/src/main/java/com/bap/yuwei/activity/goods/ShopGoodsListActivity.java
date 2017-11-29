package com.bap.yuwei.activity.goods;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.event.CategoryEvent;
import com.bap.yuwei.util.SoftInputUtil;
import com.bap.yuwei.util.StringUtils;
import com.github.jdsjlzx.recyclerview.LRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 店铺里的商品列表
 */
public class ShopGoodsListActivity extends ShopHomeActivity {

    public static final String CATEGORY_KEY="category.key";
    public static final String KEYWORDS_KEY="keywords.key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryNodes=getIntent().getStringExtra(CATEGORY_KEY);
        goodsTitle=getIntent().getStringExtra(KEYWORDS_KEY);
        etSearch.setText(goodsTitle);
        gvGoods.refresh();

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SoftInputUtil.hideKeyboard(mContext);
                    goodsTitle= StringUtils.getEditTextValue(etSearch);
                    gvGoods.refresh();
                }
                return true;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchByCategoryEvent(CategoryEvent event){
        categoryNodes=event.categoryNodes;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_goods_list;
    }


    @Override
    protected void initView() {
        gvGoods= (LRecyclerView) findViewById(R.id.rv_goods);
        txtMult= (TextView) findViewById(R.id.txt_mult);
        txtSell= (TextView) findViewById(R.id.txt_sell);
        txtTime= (TextView) findViewById(R.id.txt_time);
        txtPrice= (TextView) findViewById(R.id.txt_price);
        etSearch= (EditText) findViewById(R.id.et_words);
        gvGoods.setHasFixedSize(true);
        gvGoods.setLayoutManager(new GridLayoutManager(this,2));
        gvGoods.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        gvGoods.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        gvGoods.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }

    @Override
    protected void initUIWithValue(){

    }

    protected void getShopCollect(){

    }
}
