package com.example.kerne.potato.Util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class ChangeStatusBar {
    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window=activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(colorId));
    }
}
