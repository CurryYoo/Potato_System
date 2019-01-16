package com.example.kerne.potato;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Adapter
 *
 * Created by Tnno Wu on 2018/03/05.
 */

public class ShotClickAdapter extends RecyclerView.Adapter<ShotClickAdapter.RcvClickViewHolder> {

    private static final String TAG = ShotClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    private String fieldId;
    private String name;
    private String experimentName;

    public ShotClickAdapter(Context context, OnItemClickListener listener) {
        mContext = context;

        mListener = listener;
    }

    public void setRcvClickDataList(List<JSONObject> list) {
        Log.d(TAG, "setRcvClickDataList: " + list.size());

        mList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RcvClickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shot_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            fieldId = jsonObject.getString("fieldId");
            name = jsonObject.getString("name");
            experimentName = jsonObject.getString("experimentName");
            holder.tvFieldId.setText("试验田序号：" + fieldId);
            holder.tvName.setText("试验田名称：" + name);
            holder.tvExperimentName.setText("试验类型：" + experimentName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 第一种写法：直接在 Adapter 里写
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //builder.setIcon(R.drawable.ic_launcher_background);
                try {
                    fieldId = jsonObject.getString("fieldId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.setTitle("选择一个操作");
                //    指定下拉列表的显示数据
                final String[] options = {"编辑", "进入", "删除"};
                //    设置一个下拉的列表选择项
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){

                        }
                        else if(which == 1){
                            Intent intent = new Intent(mContext, FieldClickActivity.class);
                            intent.putExtra("fieldId", fieldId);
                            mContext.startActivity(intent);
                        }
                        else{
                            mList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(mContext, "删除fieldId " + fieldId, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                Toast.makeText(mContext, "你点击的fieldId是：" + fieldId, Toast.LENGTH_SHORT).show();
            }
        });

        // 第二种写法：将点击事件传到 Activity 里去写
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onItemClick(content);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class RcvClickViewHolder extends RecyclerView.ViewHolder {

        TextView tvFieldId, tvName, tvExperimentName;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            tvFieldId = itemView.findViewById(R.id.tv_fieldId);
            tvName = itemView.findViewById(R.id.tv_name);
            tvExperimentName = itemView.findViewById(R.id.tv_experimentName);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
