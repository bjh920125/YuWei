package com.bap.yuwei.activity.sys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.sys.ShippingAddress;

public class UpdateReceiveAddressActivity extends AddReceiveAddressActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUiWithValue() {
        etName.setText(address.getConsignee());
        etTel.setText(address.getCellphone());
        txtArea.setText(address.getProvince()+address.getCity()+address.getRegion());
        etAddress.setText(address.getStreet());
        if(address.getIsDefault()){
            cbDefault.setChecked(true);
        }else {
            cbDefault.setChecked(false);
        }
    }
}
