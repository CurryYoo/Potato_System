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

public class FarmlandClickAdapter extends RecyclerView.Adapter<FarmlandClickAdapter.RcvClickViewHolder> {

    private static final String TAG = FarmlandClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    private String shotId;
    private String farmlandId;
    private String year;
    private String imgUrl;

    public FarmlandClickAdapter(Context context, OnItemClickListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.farmland_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            shotId = jsonObject.getString("shotId");
            farmlandId = jsonObject.getString("farmlandId");
            year = jsonObject.getString("year");
            imgUrl = jsonObject.getString("imgUrl");
            holder.tvShotId.setText("航拍序号：" + shotId);
            holder.tvFarmlandId.setText("大田序号：" + farmlandId);
            holder.tvYear.setText("年份：" + year);
            holder.tvImgUrl.setText("图片地址：" + imgUrl);
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
                    shotId = jsonObject.getString("shotId");
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
                        if(which == 0){ //点击"编辑"操作时

                        }
                        else if(which == 1){ //点击"进入"操作时
                            Intent intent = new Intent(mContext, ShotClickActivity.class);
                            intent.putExtra("shotId", shotId);
                            mContext.startActivity(intent);
                        }
                        else{ //点击"删除"操作时
                            mList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(mContext, "删除shotId " + shotId, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                Toast.makeText(mContext, "你点击的shotId是：" + shotId, Toast.LENGTH_SHORT).show();
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

        TextView tvShotId, tvFarmlandId, tvYear, tvImgUrl;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            tvShotId = itemView.findViewById(R.id.tv_shotId);
            tvFarmlandId = itemView.findViewById(R.id.tv_farmlandId);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvImgUrl = itemView.findViewById(R.id.tv_imgUrl);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
