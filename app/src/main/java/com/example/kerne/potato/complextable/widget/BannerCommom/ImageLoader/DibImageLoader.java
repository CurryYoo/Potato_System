package com.example.kerne.potato.complextable.widget.BannerCommom.ImageLoader;

import android.content.Context;
import android.widget.ImageView;

public class DibImageLoader implements ImageLoader {
    private static  ImageLoader imageLoader = new DibImageLoader();
    private DibImageLoader() {}

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        DibImageLoaderFactory.getLoader(context).loadImage(path, imageView);

    }

    @Override
    public void displayCircleImage(Context context, String path, ImageView imageView) {
        DibImageLoaderFactory.getLoader(context).loadCircleImage(path,imageView);
    }

    public static  ImageLoader getInstance() {
        return imageLoader;
    }
}
