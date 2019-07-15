package com.example.kerne.potato;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class BigfarmClickAdapter extends RecyclerView.Adapter<BigfarmClickAdapter.RcvClickViewHolder> {

    private static final String TAG = BigfarmClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private BigfarmClickAdapter.OnItemClickListener mListener;

    private String bigfarmId;
    private String name;
    private String img;
    private int year;
    private String uri;

    private String userRole;

    public BigfarmClickAdapter(Context context, BigfarmClickAdapter.OnItemClickListener listener) {
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
    public BigfarmClickAdapter.RcvClickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bigfarm_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BigfarmClickAdapter.RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            bigfarmId = jsonObject.getString("bigfarmId");
            name = jsonObject.getString("name");
            img = jsonObject.getString("img");
            year = jsonObject.getInt("year");
//            userRole = jsonObject.getString("userRole");
//            holder.tvNum.setText("大田序号：" + bigfarmId);
            holder.tvName.setText(mContext.getText(R.string.big_farm) + "：" + name);
            holder.tvYear.setText(mContext.getText(R.string.year) + "：" + year);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 第一种写法：直接在 Adapter 里写
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //builder.setIcon(R.drawable.ic_launcher_background);
                try {
                    bigfarmId = jsonObject.getString("bigfarmId");
                    img = jsonObject.getString("img");
                    year = jsonObject.getInt("year");
                    uri = jsonObject.getString("uri");
                    name = jsonObject.getString("name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mContext, GeneralClickActivity.class);
                intent.putExtra("bigfarmId", bigfarmId);
                intent.putExtra("img", img);
                intent.putExtra("year", year);
                intent.putExtra("uri", uri);
                intent.putExtra("name", name);
                mContext.startActivity(intent);

                showShortToast(mContext, mContext.getText(R.string.big_farm) + "：" + name+" ("+year+")");
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

        TextView tvNum;
        TextView tvName, tvYear;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
//            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvYear = itemView.findViewById(R.id.tv_year);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
