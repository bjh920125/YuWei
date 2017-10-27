package com.bap.yuwei.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;



import java.util.List;

/**
 * Created by wgyscsf on 2016/8/24.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class IntentOtherAppUtils {
    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isAppAvilible(Context context, String packages) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packages)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void goOtherApp(Context context, String packages) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packages);
        context.startActivity(intent);
    }

    public static void goOtherApp(Context context, String packages,String cls) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packages,cls));
 /*       intent.putExtra("userId", "");
        intent.putExtra("tel","");
        intent.putExtra("flag",1);*/
        context.startActivity(intent);
    }

}
