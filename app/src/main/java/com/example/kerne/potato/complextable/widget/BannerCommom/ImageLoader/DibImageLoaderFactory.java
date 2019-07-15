package com.example.kerne.potato.complextable.widget.BannerCommom.ImageLoader;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class DibImageLoaderFactory {
    /**
     * This method is appropriate for resources that will be used outside of the normal fragment
     * or activity lifecycle (For example in services, or for notification thumbnails).
     */
    public static GlideImageLoader getLoader(Context context) {
        return new GlideImageLoader(context);
    }

    public static GlideImageLoader getLoader(FragmentActivity activity) {
        return new GlideImageLoader(activity);
    }

    public static GlideImageLoader getLoader(Fragment fragment) {
        return new GlideImageLoader(fragment);
    }

    /**
     * This method may be inefficient aways and is definitely inefficient for large hierarchies.
     * Consider memoizing the result after the View is attached or again, prefer the Activity and
     * Fragment variants whenever possible.
     */
    public static GlideImageLoader getLoader(View view) {
        return new GlideImageLoader(view);
    }
}
