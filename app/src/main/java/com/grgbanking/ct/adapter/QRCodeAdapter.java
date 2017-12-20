package com.grgbanking.ct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grgbanking.ct.R;

import java.util.List;

/**
 * Created by lazylee on 2017/12/1.
 */

public class QRCodeAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    public QRCodeAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        //加在列表后面
        return mList.get(mList.size() - position - 1);
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
                    .inflate(R.layout.qr_scan_list_item, parent, false);
            holder.mTVCount = (TextView) convertView
                    .findViewById(R.id.qr_scan_list_item_count);
            holder.mTVQRCOde = (TextView) convertView
                    .findViewById(R.id.qr_scan_list_item_qrCode);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTVCount.setText((mList.size() - position) + "");
        holder.mTVQRCOde.setText(mList.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView mTVCount;
        TextView mTVQRCOde;

    }
}
