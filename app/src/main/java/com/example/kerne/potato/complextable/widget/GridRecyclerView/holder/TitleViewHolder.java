package com.example.kerne.potato.complextable.widget.GridRecyclerView.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kerne.potato.R;
import com.example.kerne.potato.complextable.widget.GridRecyclerView.UIUtil;

/**
 * auther: baiiu
 * time: 16/6/5 05 23:30
 * description:
 */
public class TitleViewHolder extends RecyclerView.ViewHolder {

    private View.OnClickListener mListener;

    public TitleViewHolder(Context mContext, ViewGroup parent, View.OnClickListener mListener) {
        super(UIUtil.infalte(mContext, R.layout.holder_title, parent));
        this.mListener = mListener;
    }


    public void bind(String s) {
        ((TextView) itemView).setText(s);
        itemView.setTag(s);
        itemView.setOnClickListener(mListener);
    }
}
