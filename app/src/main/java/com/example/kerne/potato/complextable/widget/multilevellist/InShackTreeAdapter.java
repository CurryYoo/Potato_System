package com.example.kerne.potato.complextable.widget.multilevellist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.R;
import com.example.kerne.potato.complextable.TableActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;


/**
 * Created by xulc on 2018/7/27.
 */

public class InShackTreeAdapter extends BaseAdapter {
    private Context mcontext;
    private Activity mActivity;
    private List<TreePoint> pointList;
    private String keyword = "";
    private HashMap<String, TreePoint> pointMap = new HashMap<>();

    private int operateMode = ModeClick;
    //两种操作模式  点击 或者 选择
    private static final int ModeClick = 1;
    private static final int ModeSelect = 2;

    @IntDef({ModeClick, ModeSelect})
    public @interface Mode {

    }

    //设置操作模式
    public void setOperateMode(@Mode int operateMode) {
        this.operateMode = operateMode;
    }

    public InShackTreeAdapter(final Context mcontext, List<TreePoint> pointList, HashMap<String, TreePoint> pointMap) {
        this.mcontext = mcontext;
        this.mActivity = (Activity) mcontext;
        this.pointList = pointList;
        this.pointMap = pointMap;
    }

    /**
     * 搜索的时候，先关闭所有的条目，然后，按照条件，找到含有关键字的数据
     * 如果是叶子节点，
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
        for (TreePoint treePoint : pointList) {
            treePoint.setExpand(false);
        }
        if (!"".equals(keyword)) {
            for (TreePoint tempTreePoint : pointList) {
                if (tempTreePoint.getNNAME().contains(keyword)) {
                    if ("0".equals(tempTreePoint.getISLEAF())) {   //非叶子节点
                        tempTreePoint.setExpand(true);
                    }
                    //展开从最顶层到该点的所有节点
                    openExpand(tempTreePoint);
                }
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 从TreePoint开始一直展开到顶部
     *
     * @param treePoint
     */
    private void openExpand(TreePoint treePoint) {
        if ("0".equals(treePoint.getPARENTID())) {
            treePoint.setExpand(true);
        } else {
            pointMap.get(treePoint.getPARENTID()).setExpand(true);
            openExpand(pointMap.get(treePoint.getPARENTID()));
        }
    }


    //第一要准确计算数量
    @Override
    public int getCount() {
        int count = 0;
        for (int i=0;i<pointList.size();i++) {
            if ("0".equals(pointList.get(i).getPARENTID())) {
                count++;
            } else {
                if (getItemIsExpand(pointList.get(i).getPARENTID())) {
                    count++;
                }
            }
        }
//        for (TreePoint tempPoint : pointList) {
//            if ("0".equals(tempPoint.getPARENTID())) {
//                count++;
//            } else {
//                if (getItemIsExpand(tempPoint.getPARENTID())) {
//                    count++;
//                }
//            }
//        }

        return count;
    }

    //判断当前Id的tempPoint是否展开了
    private boolean getItemIsExpand(String ID) {
        for (int i=0;i<pointList.size();i++) {
            if (ID.equals(pointList.get(i).getID())) {
                return pointList.get(i).isExpand();
            }
        }
        return false;
    }

    @Override
    public Object getItem(int position) {
        return pointList.get(convertPostion(position));
    }

    private int convertPostion(int position) {
        int count = 0;
        for (int i = 0; i < pointList.size(); i++) {
            TreePoint treePoint = pointList.get(i);
            if ("0".equals(treePoint.getPARENTID())) {
                count++;
            } else {
                if (getItemIsExpand(treePoint.getPARENTID())) {
                    count++;
                }
            }
            if (position == (count - 1)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item_adapter_treeview, null);
            holder = new ViewHolder();
            holder.text =  convertView.findViewById(R.id.text);
            holder.icon =  convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TreePoint tempPoint = (TreePoint) getItem(position);
        int level = TreeUtils.getLevel(tempPoint, pointMap);
        holder.icon.setPadding(25 * level, holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
        if ("0".equals(tempPoint.getISLEAF())) {  //如果为父节点
            if (!tempPoint.isExpand()) {    //不展开,显示加号
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(R.drawable.outline_list_collapse);
            } else {                        //展开,显示减号
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(R.drawable.outline_list_expand);
            }

            holder.text.setTextColor(mcontext.getResources().getColor(R.color.primary_text));
        } else {   //如果叶子节点，不占位显示
            holder.icon.setImageResource(R.drawable.outline_list_expand);
            holder.icon.setVisibility(View.INVISIBLE);
            holder.text.setTextColor(mcontext.getResources().getColor(R.color.colorOrange));
        }
        //如果存在搜索关键字
        if (keyword != null && !"".equals(keyword) && tempPoint.getNNAME().contains(keyword)) {
            int index = tempPoint.getNNAME().indexOf(keyword);
            int len = keyword.length();
            Spanned temp = Html.fromHtml(tempPoint.getNNAME().substring(0, index)
                    + "<font color=#FF0000>"
                    + tempPoint.getNNAME().substring(index, index + len) + "</font>"
                    + tempPoint.getNNAME().substring(index + len, tempPoint.getNNAME().length()));

            holder.text.setText(temp);
        } else {
            holder.text.setText(tempPoint.getNNAME());
        }
        holder.text.setCompoundDrawablePadding(DensityUtil.dip2px(mcontext, 10));
        return convertView;
    }


    public void onItemClick(int position) throws JSONException {
        TreePoint treePoint = (TreePoint) getItem(position);
        if ("1".equals(treePoint.getISLEAF())) {   //点击叶子节点
            //处理回填
            Intent intent = new Intent(mcontext, TableActivity.class);
            intent.putExtra("fieldId", treePoint.getJsonObject().getString("id"));
            intent.putExtra("expType", treePoint.getJsonObject().getString("expType"));
            intent.putExtra("farmlandId", treePoint.getJsonObject().getString("farmlandId"));
            intent.putExtra("num", treePoint.getJsonObject().getInt("num"));
            intent.putExtra("rows", treePoint.getJsonObject().getInt("rows"));
            intent.putExtra("type", treePoint.getJsonObject().getString("type"));
            intent.putExtra(("bigFarmName"),treePoint.getJsonObject().getString("bigFarmName"));
            intent.putExtra(("farmName"),treePoint.getJsonObject().getString("farmName"));
            intent.putExtra(("year"),treePoint.getJsonObject().getInt("year"));
            mcontext.startActivity(intent);
            Toast.makeText(mcontext, "实验类型:" + treePoint.getJsonObject().getString("expType"), Toast.LENGTH_SHORT).show();
        } else {  //如果点击的是父类
            if (treePoint.isExpand()) {
                for (TreePoint tempPoint : pointList) {
                    if (tempPoint.getPARENTID().equals(treePoint.getID())) {
                        if ("0".equals(treePoint.getISLEAF())) {
                            tempPoint.setExpand(false);
                        }
                    }
                }
                treePoint.setExpand(false);
            } else {
                treePoint.setExpand(true);
            }
        }
        this.notifyDataSetChanged();
    }


//    //选择操作
//    private void onModeSelect(TreePoint treePoint) {
//        if ("1".equals(treePoint.getISLEAF())) {   //选择叶子节点
//            //处理回填
//            treePoint.setSelected(!treePoint.isSelected());
//        } else {                                   //选择父节点
//            int position = pointList.indexOf(treePoint);
//            boolean isSelect = treePoint.isSelected();
//            treePoint.setSelected(!isSelect);
//            if (position == -1) {
//                return;
//            }
//            if (position == pointList.size() - 1) {
//                return;
//            }
//            position++;
//            for (; position < pointList.size(); position++) {
//                TreePoint tempPoint = pointList.get(position);
//                if (tempPoint.getPARENTID().equals(treePoint.getPARENTID())) {    //如果找到和自己同级的数据就返回
//                    break;
//                }
//                tempPoint.setSelected(!isSelect);
//            }
//        }
//        this.notifyDataSetChanged();
//    }

    //选中所有的point
//    private void selectPoint(TreePoint treePoint) {
//        if(){
//
//        }
//    }


    private String getSubmitResult(TreePoint treePoint) {
        StringBuilder sb = new StringBuilder();
        addResult(treePoint, sb);
        String result = sb.toString();
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void addResult(TreePoint treePoint, StringBuilder sb) {
        if (treePoint != null && sb != null) {
            sb.insert(0, treePoint.getNNAME() + "-");
            if (!"0".equals(treePoint.getPARENTID())) {
                addResult(pointMap.get(treePoint.getPARENTID()), sb);
            }
        }
    }


    class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
