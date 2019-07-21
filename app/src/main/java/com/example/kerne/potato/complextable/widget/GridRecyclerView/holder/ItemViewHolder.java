package com.example.kerne.potato.complextable.widget.GridRecyclerView.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kerne.potato.R;
import com.example.kerne.potato.complextable.widget.GridRecyclerView.UIUtil;

import butterknife.ButterKnife;

/**
 * auther: baiiu
 * time: 16/6/5 05 23:35
 * description:
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;
    private View.OnClickListener mListener;

    public ItemViewHolder(Context mContext, ViewGroup parent, View.OnClickListener mListener) {
        super(UIUtil.infalte(mContext, R.layout.holder_item, parent));
        textView = ButterKnife.findById(itemView, R.id.rv_item);
        this.mListener = mListener;
    }


    public void bind(String s,int i) {
        textView.setText(s);
        textView.setTag(i);
        textView.setOnClickListener(mListener);
    }
}
