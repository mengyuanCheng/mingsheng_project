package com.grgbanking.ct.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.entity.BankDenoTask;
import com.grgbanking.ct.entity.BankOtherTask;

import java.util.List;

/**
 * 配箱 网点任务的面值 adapter
 * Created by lazylee on 2017/11/28.
 */

public class BankTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<BankDenoTask> mList;
    private BankOtherTask mOther;
    private Context mContext;

    public static final int TYPE_CNY = 1;
    public static final int TYPE_OTHER = 2;

    private OnItemClickListener mOnItemClickListener;

    public BankTaskAdapter(List<BankDenoTask> list, BankOtherTask otherTask, Context context) {
        list.add(new BankDenoTask(otherTask.getmOther(), 0, otherTask.getFinish()));
        this.mList = list;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_CNY) {
            view = layoutInflater.inflate(R.layout.bank_task_list_item, parent, false);
            viewHolder = new BankTaskViewHolder(view);
        } else {
            view = layoutInflater.inflate(R.layout.bank_task_other_item, parent, false);
            viewHolder = new BankTaskOtherViewHolder(view);
        }
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            BankTaskOtherViewHolder otherViewHolder = (BankTaskOtherViewHolder) holder;
            otherViewHolder.mOtherInfo.setText(mList.get(position).getDeno());
            otherViewHolder.mOtherFinish.setText(mList.get(position).getFinish()+"");
            otherViewHolder.itemView.setTag(position);
        } else {
            BankTaskViewHolder viewHolder = (BankTaskViewHolder) holder;
            viewHolder.mDeno.setText(mList.get(position).getDeno());
            viewHolder.mAmount.setText(mList.get(position).getPlan() + "");
            viewHolder.mFinish.setText(mList.get(position).getFinish() + "");
            viewHolder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //position 从0开始。
        if (position == getItemCount() - 1) {
            return TYPE_OTHER;
        } else {
            return TYPE_CNY;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void addToList(BankDenoTask task) {
        if (!mList.contains(task)) {
            mList.add(task);
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     *
     * @param task 任务
     */
    public void updateTask(BankDenoTask task) {
        if (mList.contains(task)) {
            int position = mList.indexOf(task);
            mList.remove(position);
            mList.add(position, task);
            notifyDataSetChanged();
        }
    }

    /**
     * 更新 otherTask
     *
     * @param otherTask
     */
    public void updateOtherTask(BankOtherTask otherTask) {
        mList.remove(getItemCount() - 1);
        mList.add(new BankDenoTask(otherTask.getmOther(), 0, otherTask.getFinish()));
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
}

class BankTaskViewHolder extends RecyclerView.ViewHolder {

    TextView mDeno;
    TextView mAmount;     //任务数
    TextView mFinish;     //已完成数

    public BankTaskViewHolder(View itemView) {
        super(itemView);
        mDeno = (TextView) itemView.findViewById(R.id.bank_task_deno);
        mAmount = (TextView) itemView.findViewById(R.id.bank_task_amount);
        mFinish = (TextView) itemView.findViewById(R.id.bank_task_finish);
    }


}

class BankTaskOtherViewHolder extends RecyclerView.ViewHolder {

    TextView mOtherInfo;
    TextView mOtherFinish;

    public BankTaskOtherViewHolder(View itemView) {
        super(itemView);
        mOtherInfo = (TextView) itemView.findViewById(R.id.bank_task_other_deno);
        mOtherFinish = (TextView) itemView.findViewById(R.id.bank_task_other_finish);
    }
}

