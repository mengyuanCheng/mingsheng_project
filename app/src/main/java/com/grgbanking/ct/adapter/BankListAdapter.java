package com.grgbanking.ct.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.BankPeiXiangTaskActivity;

import java.util.ArrayList;

/**
 * BankPeiXiangActicity 中的recycler Adapter
 * Created by lazylee on 2017/11/27.
 */

public class BankListAdapter extends RecyclerView.Adapter<BankViewHolder> {

    private ArrayList<String> mList;
    private Context mContext;

    public BankListAdapter(ArrayList<String> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_list_item, parent, false);
        BankViewHolder viewHolder = new BankViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BankViewHolder holder, final int position) {
        holder.mBankName.setText(mList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 携带 bank name 跳转
                Intent intent = new Intent(mContext, BankPeiXiangTaskActivity.class);
                intent.putExtra("bankName", mList.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "这是第" + position + "个item", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addBankToList(String s) {
        if (!mList.contains(s)) {
            mList.add(s);
            notifyDataSetChanged();
        }
    }

    public void addBankToList(ArrayList<String> list) {
        mList.addAll(list);
    }

    public void removeBankToList(String s) {
        if (mList.contains(s)) {
            mList.remove(s);
            notifyDataSetChanged();
        }
    }

    public void clearList() {
        if (mList != null && mList.size() != 0) {
            mList.clear();
            notifyDataSetChanged();
        }
    }
}

class BankViewHolder extends RecyclerView.ViewHolder {
    TextView mBankName;

    BankViewHolder(View itemView) {
        super(itemView);
        mBankName = (TextView) itemView.findViewById(R.id.item_bank_name);
    }
}