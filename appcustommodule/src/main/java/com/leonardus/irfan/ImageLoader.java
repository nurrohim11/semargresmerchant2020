package com.leonardus.irfan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoader {

    public static void load(Context context, String url, ImageView view){
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(new ColorDrawable(Color.WHITE))).into(view);
    }

    public static void load(Context context, int res_id, ImageView view){
        Glide.with(context).load(res_id).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(new ColorDrawable(Color.WHITE))).into(view);
    }
}
