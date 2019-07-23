package com.example.kerne.potato.complextable.widget.GridRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.kerne.potato.complextable.widget.GridRecyclerView.holder.ItemViewHolder;
import com.example.kerne.potato.complextable.widget.GridRecyclerView.holder.TitleViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * auther: baiiu
 * time: 16/6/5 05 23:28
 * description:
 */
public class DoubleGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private List<JSONObject> topGridData;
    private List<JSONObject> bottomGridData;
    private View.OnClickListener mListener;

    public DoubleGridAdapter(Context context, List<JSONObject> topGridData, List<JSONObject> bottomGridList, View.OnClickListener listener) {
        this.mContext = context;
        this.topGridData = topGridData;
        this.bottomGridData = bottomGridList;
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == topGridData.size() + 1) {
            return TYPE_TITLE;
        }

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case TYPE_TITLE:
                holder = new TitleViewHolder(mContext, parent);
                break;
            case TYPE_ITEM:
                holder = new ItemViewHolder(mContext, parent, mListener);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_TITLE:
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                if (position == 0) {
                    titleViewHolder.bind("试验田");
                } else {
                    titleViewHolder.bind("棚区试验田");
                }
                break;
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                try {
                    if (position < topGridData.size() + 1) {
                        itemViewHolder.bind(topGridData.get(position - 1).getString("name"), position - 1);
                    } else {
                        itemViewHolder.bind(bottomGridData.get(position - topGridData.size() - 2).getString("expType"), position - 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return topGridData.size() + bottomGridData.size() + 2;
    }
}
