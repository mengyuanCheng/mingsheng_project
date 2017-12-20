package com.grgbanking.ct.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.adapter.BankListAdapter;
import com.grgbanking.ct.entity.BankPX;
import com.grgbanking.ct.entity.BankTaskQR;
import com.grgbanking.ct.entity.PeiXiangInfo;
import com.grgbanking.ct.greendao.BankPXDao;
import com.grgbanking.ct.greendao.BankTaskQRDao;
import com.grgbanking.ct.greendao.GreenDaoManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.grgbanking.ct.R.id.recyclerView;
import static com.grgbanking.ct.cach.DataCach.loginUser;

public class BankPeiXiangActivity extends AppCompatActivity {

    private static final String TAG = "BankPeiXiangActivity";
    private Context mContext;

    private TextView mBtnBack;
    private RecyclerView mRecyclerView;
    private Button mBtnCommit;
    private BankListAdapter mAdapter;
    private ArrayList<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_pei_xiang);
        mContext = BankPeiXiangActivity.this;
        mBtnBack = (TextView) findViewById(R.id.bank_peixiang_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBtnCommit = (Button) findViewById(R.id.bank_peixiang_commit);
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitData();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new BankListAdapter(mList, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        BankPXDao bankPXDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankPXDao();
        List<BankPX> bankPXList = bankPXDao.loadAll();
        for (BankPX bankPX : bankPXList) {
            mAdapter.addBankToList(bankPX.getBankName());
        }

        super.onResume();
    }

    /**
     * 通过网络将数据传给后台
     */
    private void commitData() {
        //TODO 从数据库中提取数据
        BankPXDao bankPXDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankPXDao();
        BankTaskQRDao taskQRDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankTaskQRDao();
        List<BankPX> bankPXList = bankPXDao.loadAll();

        //TODO 判断网点任务的钱捆数 是否和扫描的钱捆数相等,如果不相等,做出提示.
        boolean isAllScan = true;
        StringBuilder noticeMessage = new StringBuilder();
        for (BankPX bankPX : bankPXList) {
            List<BankTaskQR> qrList = taskQRDao.queryBuilder()
                    .where(BankTaskQRDao.Properties.BankName.eq(bankPX.getBankName()))
                    .where(BankTaskQRDao.Properties.FaceValue.notEq("other"))
                    .list();
            if (bankPX.getSum() == qrList.size()) {
                Log.e(TAG, "commitData: " + bankPX.getBankName() + "扫描完成");
                //noticeMessage.append(bankPX.getBankName()).append("扫描完成;").append("\n");
            } else if (bankPX.getSum() > qrList.size()) {
                isAllScan = false;
                noticeMessage.append(bankPX.getBankName()).append("没有扫描完成;").append("\n");
            } else {
                //查看数据查询的的结果是否包含deno是other的
                isAllScan = false;
                Log.e(TAG, "commitData: " + bankPX.getBankName() + "有多扫描的钱捆.");
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{").append("\"GUARD_CODE\":\"").append(loginUser.getLoginName()).append("\",\"BANKIDQR\": [");
        ArrayList<PeiXiangInfo> arrayList = new ArrayList<>();
        PeiXiangInfo peiXiangInfo;
        //这里将boxnum 当作bankid使用
        for (int i = 0; i < bankPXList.size(); i++) {
            peiXiangInfo = new PeiXiangInfo();
            peiXiangInfo.setBoxNum(bankPXList.get(i).getBankId());

            List<BankTaskQR> taskQRList = taskQRDao.queryBuilder()
                    .where(BankTaskQRDao.Properties.BankName.eq(bankPXList.get(i).getBankName()))
                    .list();
            ArrayList<String> list = new ArrayList<>();
            for (BankTaskQR bankTaskQR : taskQRList) {
                list.add(bankTaskQR.getQrCode());
            }
            peiXiangInfo.setQR_codelist(list);
            arrayList.add(peiXiangInfo);
        }
        for (PeiXiangInfo px : arrayList) {
            if (px.getQR_codelist() != null) {
                String temp = px.getQR_codelist().toString();
                Log.i("====tmp1==", "" + temp);
                String tmp2 = temp.replaceAll(", ", "|");
                Log.i("====tmp2==", "" + tmp2);
                String tmp3 = tmp2.substring(1, tmp2.length() - 1);
                Log.i("tmp3===", "" + tmp3);
                sb.append("{\"BANKID\":\"").append(px.getBoxNum()).append("\",\"QRCODE\":\"").append(tmp3).append("\"},");
            } else {
                sb.append("{\"BANKID\":").append(px.getBoxNum()).append(",\"QRCODE\":\"").append("").append("\"},");
            }

        }
        final String tmp = sb.toString().substring(0, sb.toString().length() - 1) + "]}";
        Log.e(TAG, "commitData:  要提交的数据" + tmp );

        if (!isAllScan) {
            new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(noticeMessage)
                    .setPositiveButton("继续提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           uploadData(tmp);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }else{
            uploadData(tmp);
        }


    }

    private void uploadData(String tmp){
        mBtnCommit.setText("正在提交");
        mBtnCommit.setEnabled(false);
        mBtnBack.setEnabled(false);
        //提交数据
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("content", tmp)
                .build();
        Request request = new Request.Builder()
                .url(Constants.URL_BANK_PX_NET_UPLOAD)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String failureMessage = e.getMessage();
                Log.e(TAG, "onFailure: " + failureMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, failureMessage, Toast.LENGTH_SHORT).show();
                        mBtnCommit.setText("提交数据");
                        mBtnCommit.setEnabled(true);
                        mBtnBack.setEnabled(true);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "onResponse: body" + string);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                            mBtnCommit.setText("提交数据");
                            mBtnCommit.setEnabled(true);
                            mBtnBack.setEnabled(true);
                            startActivity(new Intent(BankPeiXiangActivity.this,PeixiangActivity.class));
                            finish();
                        }
                    });

                }
            }
        });
    }

}

