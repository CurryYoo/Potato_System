package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kerne.potato.temporarystorage.SaveDataActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

/**
 * Item 点击对应的 Adapter
 * <p>
 * Created by Tnno Wu on 2018/03/05.
 */

public class SpeciesListAdapter extends RecyclerView.Adapter<SpeciesListAdapter.RcvClickViewHolder> {

    private static final String TAG = SpeciesListAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    private String plotId;
    private String speciesId;
    private String fieldId;
    private String userRole;
    private String blockId;
    private String expType;

    public SpeciesListAdapter(Context context, OnItemClickListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.species_list_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            speciesId = jsonObject.getString("speciesId");
            blockId = jsonObject.getString("blockId");
            fieldId = jsonObject.getString("fieldId");
            expType = jsonObject.getString("expType");
            if (expType == null) {
                expType = "";
            }
//            userRole = jsonObject.getString("userRole");
            holder.tvPlotId.setText(mContext.getText(R.string.exp_type) + "：" + expType);
            holder.tvSpeciesId.setText(mContext.getText(R.string.species_id) + "：" + speciesId);
            holder.tvFieldId.setText(mContext.getText(R.string.block_id) + "：" + fieldId);
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
                    expType = jsonObject.getString("expType");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, SaveDataActivity.class);
//                    intent.putExtra("userRole", userRole);
                intent.putExtra("speciesId", speciesId);
                intent.putExtra("expType", expType);
                intent.putExtra("blockId", blockId);
                intent.putExtra("fieldId", fieldId);
                mContext.startActivity(intent);
                showShortToast(mContext, mContext.getString(R.string.block_id) + "：" + fieldId);
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
            tvPlotId = itemView.findViewById(R.id.tv_plotId1);
            tvSpeciesId = itemView.findViewById(R.id.tv_speciesId1);
            tvFieldId = itemView.findViewById(R.id.tv_fieldId1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}