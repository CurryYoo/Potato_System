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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            holder.tvNum.setText("大田序号：" + bigfarmId);
            holder.tvName.setText("大田名称：" + name);
            holder.tvYear.setText("年份：" + year);
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mContext, GeneralClickActivity.class);
                intent.putExtra("bigfarmId", bigfarmId);
                intent.putExtra("img", img);
                intent.putExtra("year", year);
                intent.putExtra("uri", uri);
                mContext.startActivity(intent);

//                //弹出输入年份的对话框
//                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(mContext).builder()
//                        .setTitle("请输入年份：")
//                        .setEditText("");
//                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //showMsg(myAlertInputDialog.getResult());
//                        year = myAlertInputDialog.getResult();
//
//                        Intent intent = new Intent(mContext, GeneralActivity.class);
//                        intent.putExtra("bigfarmId", bigfarmId);
//                        intent.putExtra("year", Integer.parseInt(year));
////                        intent.putExtra("userRole", userRole);
//                        mContext.startActivity(intent);
//                        myAlertInputDialog.dismiss();
//                    }
//                }).setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //showMsg("取消");
//                        myAlertInputDialog.dismiss();
//                    }
//                });
//                myAlertInputDialog.show();

//                builder.setTitle("选择一个操作");
//                //    指定下拉列表的显示数据
//                final String[] options = {"进入"};
//                //    设置一个下拉的列表选择项
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if(which == 0){ //点击"进入"操作时
//                            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(mContext).builder()
//                                .setTitle("请输入年份：")
//                                .setEditText("");
//                            myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    //showMsg(myAlertInputDialog.getResult());
//                                    year = myAlertInputDialog.getResult();
//
//                                    Intent intent = new Intent(mContext, GeneralActivity.class);
//                                    intent.putExtra("bigfarmId", bigfarmId);
//                                    intent.putExtra("year", Integer.parseInt(year));
//                                    intent.putExtra("userRole", userRole);
//                                    mContext.startActivity(intent);
//                                    myAlertInputDialog.dismiss();
//                                }
//                            }).setNegativeButton("取消", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    //showMsg("取消");
//                                    myAlertInputDialog.dismiss();
//                                }
//                            });
//                            myAlertInputDialog.show();
//
//                        }
//                        else{ //点击"删除"操作时
//                            mList.remove(position);
//                            notifyItemRemoved(position);
//                            Toast.makeText(mContext, "删除farmlandId " + bigfarmId, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                builder.show();

                Toast.makeText(mContext, "你点击的bigfarmId是：" + bigfarmId, Toast.LENGTH_SHORT).show();
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

        TextView tvNum, tvName, tvYear;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvYear = itemView.findViewById(R.id.tv_year);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
