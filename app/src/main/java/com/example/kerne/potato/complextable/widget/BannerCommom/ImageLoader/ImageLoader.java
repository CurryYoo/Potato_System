package com.example.kerne.potato.complextable.widget.BannerCommom.ImageLoader;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
    void displayCircleImage(Context context, String path, ImageView imageView);
}
