package com.example.kerne.potato.temporarystorage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kerne.potato.R;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Util {
    //查看大图
    public static void watchLargePhoto(Context context, Uri imageUri) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View imgLargeView = layoutInflater.inflate(R.layout.dialog_watch_big_photo, null);
        final AlertDialog alertDialogShowLargeImage = new AlertDialog.Builder(context).setTitle("点击可关闭").create();
        //获取ImageView
        ImageView imvLargePhoto = (ImageView) imgLargeView.findViewById(R.id.imv_large_photo);
        //设置图片到ImageView
        imvLargePhoto.setImageURI(imageUri);
        //定义dialog
        alertDialogShowLargeImage.setView(imgLargeView);
        alertDialogShowLargeImage.show();
        //点击大图关闭dialog
        imgLargeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogShowLargeImage.cancel();
            }
        });
    }

    //选择日期
    public static void showDatePickerDialog(Context context, final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String zeroMonth = "";
                String zeroDay = "";
                if (month < 10) zeroMonth = "0"; //当月份小于10时，需要在月份前加入0，需要符合yyyy-mm-dd当格式
                if (dayOfMonth < 10) zeroDay = "0"; //同上
                editText.setText(year + "-" + zeroMonth + (month + 1) + "-" + zeroDay + dayOfMonth); //yyyy-mm-dd
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //计算平均数
    public static String getAverage(ArrayList<String> arrayList) {
        //解析两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        float sum = 0;
        for (String s :
                arrayList) {
            sum += Float.parseFloat(s);
        }
//        float avg = sum / arrayList.size();
        float avg = sum / arrayList.size();
        return decimalFormat.format(avg);
    }

    //计算生育日数

    public static String getGrowingDays(Context context,String sowingDateStr, String matureDateStr) throws Exception {
        //解析日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //播种期
        Date sowingDate = simpleDateFormat.parse(sowingDateStr);
        //成熟期
        Date matureDate = simpleDateFormat.parse(matureDateStr);
        Integer growingDays = ((int) ((matureDate.getTime() - sowingDate.getTime()) / (3600 * 1000 * 24)));
        if (growingDays < 0) {
            throw new Exception("输入的成熟期早于播种期！");
        }
        return growingDays.toString();
    }
}
