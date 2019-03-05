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

import com.example.kerne.potato.temporarystorage.SaveDataActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Adapter
 *
 * Created by Tnno Wu on 2018/03/05.
 */

public class SpeciesClickAdapter extends RecyclerView.Adapter<SpeciesClickAdapter.RcvClickViewHolder> {

    private static final String TAG = SpeciesClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    private String plotId;
    private String speciesId;
    private String fieldId;
    private String userRole;

    public SpeciesClickAdapter(Context context, OnItemClickListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.field_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            speciesId = jsonObject.getString("speciesId");
            //fieldId = jsonObject.getString("fieldId");
            userRole = jsonObject.getString("userRole");
            //holder.tvPlotId.setText("plot编号：" + plotId);
            holder.tvSpeciesId.setText("品种编号：" + speciesId);
            //holder.tvFieldId.setText("所属试验田：" + fieldId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 第一种写法：直接在 Adapter 里写
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (userRole.equals("experimenter") || userRole.equals("admin")) {
                if (!userRole.equals("farmer")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    //builder.setIcon(R.drawable.ic_launcher_background);
                    builder.setTitle("选择一个操作");
                    //    指定下拉列表的显示数据
                    final String[] options = {"编辑", "进入", "删除"};
                    //    设置一个下拉的列表选择项

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                            } else if (which == 1) {
                                Intent intent = new Intent(mContext, SaveDataActivity.class);
                                intent.putExtra("userRole", userRole);
                                intent.putExtra("speciesId", speciesId);
                                mContext.startActivity(intent);
                            } else {
                                mList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(mContext, "删除speciesId " + speciesId, Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(GeneralClickActivity.this, "选择的城市为：" + options[which], Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                    Toast.makeText(mContext, "你点击的speciesId是：" + speciesId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "对不起，您没有该权限！", Toast.LENGTH_SHORT).show();

                }
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

        TextView tvPlotId, tvSpeciesId, tvFieldId;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            tvPlotId = itemView.findViewById(R.id.tv_plotId);
            tvSpeciesId = itemView.findViewById(R.id.tv_speciesId);
            tvFieldId = itemView.findViewById(R.id.tv_fieldId);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
