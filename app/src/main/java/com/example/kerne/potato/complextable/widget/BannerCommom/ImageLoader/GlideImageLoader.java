package com.example.kerne.potato.complextable.widget.BannerCommom.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GlideImageLoader {
    private RequestManager requestManager;
    GlideImageLoader(Context context) {
        requestManager = Glide.with(context);
    }

    GlideImageLoader(Fragment fragment) {
        requestManager = Glide.with(fragment);
    }

    GlideImageLoader(FragmentActivity activity) {
        requestManager = Glide.with(activity);
    }

    GlideImageLoader(View view) {
        requestManager = Glide.with(view);
    }

    private  static RequestOptions getRequstOptions() {
        return RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .disallowHardwareConfig();
    }

    public void loadImage(Object url, ImageView imageView) {
        requestManager.asDrawable().load(url).apply(getRequstOptions()).into(imageView);
    }

    public void loadImage(Object url, ImageView imageView,  int placeHolder) {
        requestManager.asDrawable().load(url).apply(getRequstOptions().placeholder(placeHolder)).into(imageView);
    }

    public void loadCircleImage(Object url,ImageView imageView) {
        requestManager.asDrawable().load(url).apply(getRequstOptions().circleCrop()).into(imageView);
    }

    public void loadCircleImageDontAnimate(Object url, ImageView imageView) {
        requestManager.asDrawable().load(url).apply(getRequstOptions().circleCrop().dontAnimate()).into(imageView);
    }

    public void loadOverrideImage(Object url, ImageView imageView, int width, int height) {
        requestManager.asDrawable().load(url).apply(getRequstOptions().override(width, height)).into(imageView);
    }

    public void loadOverrideImage(Object url, ImageView imageView, int width, int height, int placeholderOf) {
        requestManager.asDrawable().load(url).apply(getRequstOptions()
                .override(width, height).placeholder(placeholderOf)).into(imageView);
    }

    public void loadGif(Object url, ImageView imageView) {
        requestManager.asGif().load(url).into(imageView);
    }

    public void loadTopCropImage(String url, ImageView imageView, int w, int h, int placeholder) {
        requestManager.asDrawable().load(url).apply(getRequstOptions().override(w, h)
                .transform(new CropTransformation(w, h, CropTransformation.CropType.TOP)).placeholder(placeholder)).into(imageView);
    }

    public void loadCornersImage(Object url, int radius, ImageView imageView, int w, int h) {
        if (w != 0 && h != 0) {
            requestManager.asDrawable().load(url).apply(getRequstOptions().override(w, h).centerCrop()
                    .transform(new RoundedCornersTransformation(radius, 0))).into(imageView);
        } else {
            requestManager.asDrawable().load(url).apply(getRequstOptions().centerCrop()
                    .transform(new RoundedCornersTransformation(radius, 0))).into(imageView);
        }
    }

    public void asGif(Object url, ImageView imageView) {
        requestManager.asGif().load(url).into(imageView);
    }

    public interface
    SimpleTargetCall {
        void targetCall(Bitmap resource);
    }

    public void asBitmapToSimpleTarget(Object url, final SimpleTargetCall simpleTargetCall) {
        requestManager.asBitmap().load(url).apply(getRequstOptions().centerCrop()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (simpleTargetCall != null) {
                    simpleTargetCall.targetCall(resource);
                }
            }
        });
    }

    public void asBitmapToSimpleTarget(Object url, int width, int height, final SimpleTargetCall simpleTargetCall) {
        requestManager.asBitmap().load(url).apply(getRequstOptions().centerCrop()).into(new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (simpleTargetCall != null) {
                    simpleTargetCall.targetCall(resource);
                }
            }
        });
    }

    /**
     * 监听加载完成的回调
     *
     * @param url
     * @param requestListener
     */
    public void loadRequestListener(Object url, ImageView imageView, boolean centerCrop, final LoadRequestListener requestListener) {
        RequestBuilder<Drawable> listener = requestManager.load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (requestListener != null) {
                    requestListener.onLoadFailed(e, model, target, isFirstResource);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (requestListener != null) {
                    requestListener.onResourceReady(resource, model, target, dataSource, isFirstResource);
                }
                return false;
            }
        });
        if (centerCrop) {
            listener.apply(RequestOptions.centerCropTransform()).into(imageView);
        } else {
            listener.into(imageView);

        }
    }

    public static File download(Context context, String url) {
        File file = null;
        try {
            file = Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 监听回调
     */
    public interface LoadRequestListener {
        void onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource);

        void onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource);
    }

    public static void pauseRequests(Context context) {
        if (context != null) {
            if (context instanceof AppCompatActivity) {
                if (((AppCompatActivity) context).isFinishing()) {
                    return;
                }
            } else if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (Util.isOnMainThread()) {
                Glide.with(context).pauseRequests();
            }
        }

    }

    public static void resumeRequests(Context context) {
        if (context != null) {
            if (context instanceof AppCompatActivity) {
                if (((AppCompatActivity) context).isFinishing()) {
                    return;
                }
            } else if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            if (Util.isOnMainThread()) {
                Glide.with(context).resumeRequests();
            }
        }

    }

}
