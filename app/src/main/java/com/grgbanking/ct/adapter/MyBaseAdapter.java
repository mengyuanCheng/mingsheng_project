package com.grgbanking.ct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.entity.PeiXiangInfo;

import java.util.ArrayList;

/**
 * Created by lazylee on 2017/6/30.
 */

public class MyBaseAdapter extends BaseAdapter {

    private ArrayList<PeiXiangInfo> mList;
    private Context mContext;

    public MyBaseAdapter(Context context, ArrayList<PeiXiangInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.array_listview_item_view, parent, false);
            holder.textViewNum = (TextView) convertView
                    .findViewById(R.id.array_listview_num);
            holder.textViewRFID = (TextView) convertView
                    .findViewById(R.id.array_listview_textview);
            holder.textViewScanNum = (TextView)convertView
                    .findViewById(R.id.array_listview_scan_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewNum.setText(mList.size() - position + "");
        holder.textViewRFID.setText(mList.get(position).getBoxName());
        if (mList.get(position).getQR_codelist() != null && !mList.get(position).getQR_codelist().isEmpty()){
            holder.textViewScanNum.setText("已扫" + mList.get(position).getQR_codelist().size());
        }else {
            holder.textViewScanNum.setText("未扫");
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textViewNum;
        TextView textViewRFID;
        TextView textViewScanNum;
    }
}
