package com.bap.yuwei.util;

import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidateUtil {
    /**
     * 验证EditText的值是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(EditText value) {
        if (value.getText().toString().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证EditText的值是11位手机号码
     */
    public static boolean isPhone(EditText value) {
        if (value.getText().toString().replace(" ", "").length() == 11) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证EditText的值长度
     */
    public static boolean isLegalLength(EditText value, int length) {
        if (value.getText().toString().trim().length() >= length) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证是否是数字
     *
     * @param value
     * @return
     */
    public static boolean isNum(String value) {
        String reg = "^[0-9]*$";
        return Pattern.matches(reg, value);
    }

    /**
     * 验证身份证是否正确
     */
    public static boolean isLegalIDCardNo(String str) {
        String reg = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
        return Pattern.matches(reg, str);
    }
}
