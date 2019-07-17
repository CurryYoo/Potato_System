package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kerne.potato.complextable.TableActivity;

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

public class GeneralClickAdapter extends RecyclerView.Adapter<GeneralClickAdapter.RcvClickViewHolder> {

    private static final String TAG = GeneralClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<JSONObject> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    private String farmlandId;
    private String name;
    private String bigFarmName;

    //棚外特有属性
    private int length;
    private int width;

    private String type;
    private int year;

    //棚内特有属性
    private String expType;
    private String fieldId;
    private int num;
    private int rows;

    private String userRole;

    public GeneralClickAdapter(Context context, OnItemClickListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.general_click_recycle_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final RcvClickViewHolder holder, final int position) {
        final JSONObject jsonObject = mList.get(position);

        try {
            farmlandId = jsonObject.getString("farmlandId");
            name = jsonObject.getString("name");
            type = jsonObject.getString("type");
//            userRole = jsonObject.getString("userRole");
//            holder.tvNum.setText("实验田序号：" + farmlandId);
            holder.tvName.setText(mContext.getText(R.string.farm) + "：" + name);
            String Stype = type.equals("greenhouse")?"大棚区域":"棚外区域";
            holder.tvType.setText(mContext.getText(R.string.type) + "：" + Stype);
            if (type.equals("greenhouse")) {
                holder.is_shack.setVisibility(View.VISIBLE);
            }
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
                    farmlandId = jsonObject.getString("farmlandId");
                    type = jsonObject.getString("type");
                    name = jsonObject.getString("name");
                    bigFarmName = jsonObject.getString("bigFarmName");
                    year = jsonObject.getInt("year");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent;
                if (type.equals("common")) {
                    try {
                        length = jsonObject.getInt("length");
                        width = jsonObject.getInt("width");
                        name = jsonObject.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent = new Intent(mContext, GeneralActivity.class);
                    intent.putExtra("length", length);
                    intent.putExtra("width", width);
                    intent.putExtra("farmlandId", farmlandId);
                    intent.putExtra("type", type);
                    intent.putExtra("farmName", name);
                    intent.putExtra("bigFarmName", bigFarmName);
                    intent.putExtra("year", year);
                    mContext.startActivity(intent);
                    showShortToast(mContext, mContext.getText(R.string.farm) + "：" + name);
                } else if (type.equals("greenhouse")) {
                    try {
                        expType = jsonObject.getString("expType");
                        fieldId = jsonObject.getString("fieldId");
                        num = jsonObject.getInt("num");
                        rows = jsonObject.getInt("rows");
                        name = jsonObject.getString("name");
                        Log.d("pengnei2", num + "," + rows);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (num == 0) {
                        showShortToast(mContext, mContext.getString(R.string.toast_shack_farm_null_error));
                    } else {
                        intent = new Intent(mContext, TableActivity.class);

                        final Intent finalIntent = intent;
                        finalIntent.putExtra("expType", expType);
                        finalIntent.putExtra("fieldId", fieldId);
                        finalIntent.putExtra("num", num);
                        finalIntent.putExtra("rows", rows);
                        finalIntent.putExtra("farmlandId", farmlandId);
                        finalIntent.putExtra("type", type);
                        finalIntent.putExtra("farmName", name);
                        finalIntent.putExtra("bigFarmName", bigFarmName);
                        finalIntent.putExtra("year", year);
                        mContext.startActivity(finalIntent);
                        showShortToast(mContext, mContext.getText(R.string.farm) + "：" + name);
                    }
                }
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
//                        intent.putExtra("farmlandId", farmlandId);
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
//                                    intent.putExtra("farmlandId", farmlandId);
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
//                            Toast.makeText(mContext, "删除farmlandId " + farmlandId, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                builder.show();

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

        //        TextView tvNum;
        TextView tvName, tvType;
        TextView is_shack;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
//            tvNum = itemView.findViewById(R.id.tv_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            is_shack = itemView.findViewById(R.id.is_shack);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String content);
    }
}
