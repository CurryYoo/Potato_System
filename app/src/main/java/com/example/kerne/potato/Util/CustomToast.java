package com.example.kerne.potato.Util;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.R;

public class CustomToast {
    /**
     * 展示toast==LENGTH_SHORT
     *
     * @param msg
     */
    public static void showShortToast(Context context,String msg) {
        showToast(context,msg, Toast.LENGTH_SHORT);
    }

    /**
     * 展示toast==LENGTH_LONG
     *
     * @param msg
     */
    public static void showLongToast(Context context,String msg) {
        showToast(context,msg, Toast.LENGTH_LONG);
    }


    public static void showToast(Context context,String massage, int show_length) {
        //使用布局加载器，将编写的toast_layout布局加载进来
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null);
        //获取TextView
        TextView title = view.findViewById(R.id.tvToastContent);
        //设置显示的内容
        title.setText(massage);
        Toast toast = new Toast(context);
        //设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移70个单位，
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 250);
        //设置显示时间
        toast.setDuration(show_length);

        toast.setView(view);
        toast.show();
    }


}