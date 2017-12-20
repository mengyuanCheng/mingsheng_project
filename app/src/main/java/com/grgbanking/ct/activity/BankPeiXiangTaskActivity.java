package com.grgbanking.ct.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.adapter.BankTaskAdapter;
import com.grgbanking.ct.entity.BankDenoTask;
import com.grgbanking.ct.entity.BankOtherTask;
import com.grgbanking.ct.entity.BankPX;
import com.grgbanking.ct.entity.BankTaskQR;
import com.grgbanking.ct.greendao.BankPXDao;
import com.grgbanking.ct.greendao.BankTaskQRDao;
import com.grgbanking.ct.greendao.GreenDaoManager;
import com.grgbanking.ct.utils.StringTools;

import java.util.ArrayList;
import java.util.List;

public class BankPeiXiangTaskActivity extends AppCompatActivity {
    private static final String TAG = "BankPeiXiangTask";

    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE = 200;
    private Context mContext;
    private TextView mBack;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private BankTaskAdapter mAdapter;
    private String mBankName;
    private List<BankDenoTask> mList = new ArrayList<>();
    private BankOtherTask mOtherTask = new BankOtherTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_pei_xiang_task);
        mContext = BankPeiXiangTaskActivity.this;
        mBack = (TextView) findViewById(R.id.bank_task_back);
        mTitle = (TextView) findViewById(R.id.bank_task_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.bank_task_recyclerView);
        mAdapter = new BankTaskAdapter(mList, mOtherTask, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        setBankTitle();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Item 点击事件。
        mAdapter.setOnItemClickListener(new BankTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent = new Intent(mContext, BankPXScanQRActivity.class);
                if (pos == mList.size() - 1) {
                    String otherInfo = mOtherTask.getmOther();
                    if (StringTools.isEmpty(otherInfo)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("deno", "other");
                        bundle.putInt("list_num", 0);
                        bundle.putString("bankName", mBankName);
                        intent.putExtra("bundle", bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("deno", "other");
                        bundle.putInt("list_num", 1);
                        bundle.putString("bankName", mBankName);
                        intent.putExtra("bundle", bundle);
                    }

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("deno", mList.get(pos).getDeno());
                    bundle.putInt("list_num", mList.get(pos).getPlan());
                    intent.putExtra("bundle", bundle);
                    bundle.putString("bankName", mBankName);
                }
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    /**
     * 加载数据
     */
    private void getDataFromDB() {
        BankPXDao bankPXDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankPXDao();
        BankPX bankPX = bankPXDao.queryBuilder().where(BankPXDao.Properties.BankName.eq(mBankName))
                .build()
                .unique();
        List<BankDenoTask> list = new ArrayList<>();
        BankTaskQRDao taskQRDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankTaskQRDao();
        List<BankTaskQR> qrHundredList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("100"))
                .build()
                .list();
        if (qrHundredList != null) {
            list.add(new BankDenoTask("100", bankPX.getHundredDeno(), qrHundredList.size()));
        } else {
            list.add(new BankDenoTask("100", bankPX.getHundredDeno(), 0));
        }
        List<BankTaskQR> qrFifthList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("50"))
                .list();
        if (qrFifthList != null) {
            list.add(new BankDenoTask("50", bankPX.getFiftyDeno(), qrFifthList.size()));
        } else {
            list.add(new BankDenoTask("50", bankPX.getFiftyDeno(), 0));
        }
        List<BankTaskQR> qrTwentyList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("20"))
                .list();
        if (qrTwentyList != null) {
            list.add(new BankDenoTask("20", bankPX.getTwentyDeno(), qrTwentyList.size()));
        } else {
            list.add(new BankDenoTask("20", bankPX.getTwentyDeno(), 0));
        }
        List<BankTaskQR> qrTenList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("10"))
                .list();
        if (qrTenList != null) {
            list.add(new BankDenoTask("10", bankPX.getTenDeno(), qrTenList.size()));
        } else {
            list.add(new BankDenoTask("10", bankPX.getTenDeno(), 0));
        }
        List<BankTaskQR> qrFiveList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("5"))
                .list();
        if (qrFiveList != null) {
            list.add(new BankDenoTask("5", bankPX.getFiveDeno(), qrFiveList.size()));
        } else {
            list.add(new BankDenoTask("5", bankPX.getFiveDeno(), 0));
        }
        List<BankTaskQR> qrOneList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("1"))
                .list();
        if (qrOneList != null) {
            list.add(new BankDenoTask("1", bankPX.getOneDeno(), qrOneList.size()));
        } else {
            list.add(new BankDenoTask("1", bankPX.getOneDeno(), 0));
        }
        mOtherTask.setmOther(bankPX.getOtherInfo());
        List<BankTaskQR> qrOtherList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq("other"))
                .list();
        if (qrOtherList != null) {
            mOtherTask.setFinish(qrOtherList.size());
        } else {
            mOtherTask.setFinish(0);
        }
        mList.clear();
        mList.addAll(list);
        mList.add(new BankDenoTask(mOtherTask.getmOther(), 0, mOtherTask.getFinish()));
        Log.e(TAG, "getDataFromDB: " + "更新数据");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //更新数据
        getDataFromDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            Bundle bundle = data.getBundleExtra("bundle");
            String deno = bundle.getString("deno");
            ArrayList<String> list = bundle.getStringArrayList("list");
            Log.e(TAG, "onActivityResult:  list size" + list.size());
            BankPXDao bankPXDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankPXDao();
            BankPX bankPX = bankPXDao.queryBuilder().where(BankPXDao.Properties.BankName.eq(mBankName))
                    .build()
                    .unique();
            if (!deno.equals("other")) {
                BankDenoTask denoTask = new BankDenoTask();
                denoTask.setDeno(deno);
                denoTask.setFinish(list.size());
                switch (deno) {
                    case "100":
                        denoTask.setPlan(bankPX.getHundredDeno());
                        break;
                    case "50":
                        denoTask.setPlan(bankPX.getFiftyDeno());
                        break;
                    case "20":
                        denoTask.setPlan(bankPX.getTwentyDeno());
                        break;
                    case "10":
                        denoTask.setPlan(bankPX.getTenDeno());
                        break;
                    case "5":
                        denoTask.setPlan(bankPX.getFiveDeno());
                        break;
                    case "1":
                        denoTask.setPlan(bankPX.getOneDeno());
                        break;
                    default:
                        break;
                }
                mAdapter.updateTask(denoTask);
            } else {
                BankOtherTask otherTask = new BankOtherTask();
                otherTask.setmOther(bankPX.getOtherInfo());
                otherTask.setFinish(list.size());
                mAdapter.updateOtherTask(otherTask);
            }
            BankTaskQRDao qrDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankTaskQRDao();
            List<BankTaskQR> existList = qrDao.queryBuilder().where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                    .where(BankTaskQRDao.Properties.FaceValue.eq(deno))
                    .list();
            //删除掉之前存在内容
            if (existList != null && existList.size() > 0) {
                for (BankTaskQR bankTaskQR : existList) {
                    qrDao.delete(bankTaskQR);
                }
            }

            BankTaskQR qr = new BankTaskQR();
            qr.setBankName(mBankName);
            qr.setFaceValue(deno);
            for (int i = 0; i < list.size(); i++) {
                qr.setId(null);
                qr.setQrCode(list.get(i));
                long l = qrDao.insert(qr);    //别用 insertOrReplace()  否则会直插入一条数据
                Log.e(TAG, "onActivityResult: insert in " + l);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 根据intent接收的bankName 设置 标题栏的title
     */
    private void setBankTitle() {
        Intent intent = getIntent();
        mBankName = intent.getStringExtra("bankName");
        mTitle.setText(mBankName);
    }
}
