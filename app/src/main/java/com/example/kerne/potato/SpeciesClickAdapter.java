package com.example.kerne.potato;

import android.app.Activity;
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

import static android.app.Activity.RESULT_OK;

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
    private String blockId;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.species_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            speciesId = jsonObject.getString("speciesId");
            //fieldId = jsonObject.getString("fieldId");
//            userRole = jsonObject.getString("userRole");
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
////                if (userRole.equals("experimenter") || userRole.equals("admin")) {
//                userRole = UserRole.getUserRole();
//                if (!userRole.equals("farmer")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //builder.setIcon(R.drawable.ic_launcher_background);
                try {
                    speciesId = jsonObject.getString("speciesId");
                    blockId = jsonObject.getString("blockId");
                    fieldId = jsonObject.getString("fieldId");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, SaveDataActivity.class);
//                    intent.putExtra("userRole", userRole);
                intent.putExtra("speciesId", speciesId);
                intent.putExtra("expType", SpeciesClickActivity.expType);
                intent.putExtra("blockId", blockId);
                intent.putExtra("fieldId", fieldId);
                mContext.startActivity(intent);
                Toast.makeText(mContext, "你点击的speciesId是：" + speciesId, Toast.LENGTH_SHORT).show();

//                } else {
//                    Toast.makeText(mContext, "对不起，您没有该权限！请登录有权限的账号！", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    ((Activity)(mContext)).startActivityForResult(intent, 1);
//                }
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
