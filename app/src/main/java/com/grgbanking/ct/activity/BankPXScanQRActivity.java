package com.grgbanking.ct.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.adapter.QRCodeAdapter;
import com.grgbanking.ct.entity.BankTaskQR;
import com.grgbanking.ct.greendao.BankTaskQRDao;
import com.grgbanking.ct.greendao.GreenDaoManager;
import com.grgbanking.ct.utils.AudioManagerUtil;
import com.pda.scan.DecoderConfigValues.SymbologyID;
import com.pda.scan.IHWScan;

import java.util.ArrayList;
import java.util.List;

public class BankPXScanQRActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BankPXScanQRActivity";

    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";
    private Context mContext;
    private String mBankName = "";
    private String mDeno = "";     //从Intent 中获取的面值
    private int mPlanNumber;  //从 intent中获取的计划数量
    private TextView mTVBack;
    private TextView mTVScanResult;
    private TextView mTVDeno;
    private TextView mTVPlanNum;
    private Button mBtnScan;
    private Button mBtnStop;
    private Button mBtnCommit;
    private ListView mListView;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private QRCodeAdapter mQRAdapter;
    private IHWScan iScan;
    private boolean mrunning = false;
    private boolean connFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_pxscan_qr);
        mContext = BankPXScanQRActivity.this;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getRequestInfo();
        setUpLayout();
        getDataFromDB();
        initService();

    }

    private void initService() {
        //bind service
        bindScanService();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.scan.RESULT");
        //TODO
        registerReceiver(mScanReceiver, filter);
        //key receiver
        IntentFilter keyfilter = new IntentFilter();
        keyfilter.addAction("android.rfid.FUN_KEY");
        registerReceiver(keyReceiver, keyfilter);
    }

    @Override
    protected void onPause() {
        if (mrunning) {
            try {
                Log.e(TAG, "close----");
                iScan.close();
                mrunning = false;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.e("Action ------->", "onPause Method running");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        unregisterReceiver(keyReceiver);
        //TODO
        unregisterReceiver(mScanReceiver);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (connFlag) {
            mContext.getApplicationContext().unbindService(conn);
        }
        super.onDestroy();
    }

    /**
     * 获取Intent传递进来的信息
     */
    private void getRequestInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        String str = bundle.getString("deno");
        int i = bundle.getInt("list_num");
        mBankName = bundle.getString("bankName");
        if (str.equals("other")) {
            mDeno = str;
            mPlanNumber = i == 0 ? 0 : -1;
        } else {
            mDeno = str;
            mPlanNumber = i;
        }
    }

    /**
     * 从数据库中初始化数据
     */
    public void getDataFromDB() {
        BankTaskQRDao taskQRDao = GreenDaoManager.getInstance(mContext).getNewSession().getBankTaskQRDao();
        List<BankTaskQR> qrList = taskQRDao.queryBuilder()
                .where(BankTaskQRDao.Properties.BankName.eq(mBankName))
                .where(BankTaskQRDao.Properties.FaceValue.eq(mDeno))
                .build()
                .list();
        mArrayList.clear();
        if (qrList != null && qrList.size() > 0)
            for (BankTaskQR qr : qrList) {
                mArrayList.add(qr.getQrCode());
            }
        mQRAdapter.notifyDataSetChanged();
    }

    private void setUpLayout() {
        mTVBack = (TextView) findViewById(R.id.bank_px_scan_qr_back);
        mTVBack.setOnClickListener(this);
        mTVScanResult = (TextView) findViewById(R.id.bank_px_scan_qr_scanResult);
        mTVDeno = (TextView) findViewById(R.id.bank_px_scan_qr_deno);
        if (mDeno.equals("other"))
            mTVDeno.setText("其它");
        else
            mTVDeno.setText(mDeno);
        mTVPlanNum = (TextView) findViewById(R.id.bank_px_scan_qr_plan);
        if (mPlanNumber != -1) {
            mTVPlanNum.setText(mPlanNumber + "");
        } else {
            mTVPlanNum.setText("大于0");
        }

        mBtnScan = (Button) findViewById(R.id.bank_px_scan_qr_start);
        mBtnScan.setOnClickListener(this);
        mBtnStop = (Button) findViewById(R.id.bank_px_scan_qr_stop);
        mBtnStop.setOnClickListener(this);
        mBtnCommit = (Button) findViewById(R.id.bank_px_scan_qr_commit);
        mBtnCommit.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.bank_px_scan_qr_list);
        mQRAdapter = new QRCodeAdapter(mContext, mArrayList);
        mListView.setAdapter(mQRAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要删除这条信息吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mArrayList.remove(position);
                                mQRAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bank_px_scan_qr_back:
                onBackPressed();
                break;

            case R.id.bank_px_scan_qr_start:
                //开始扫描
                Log.e(TAG, "onClick: Scan");
                mrunning = true;
                new Thread(mScanThread).start();
                break;
            case R.id.bank_px_scan_qr_stop:
                //停止扫描
                mrunning = false;
                break;
            case R.id.bank_px_scan_qr_commit:
                //提交数据
                commitData();
                break;
            default:
                break;
        }
    }

    /**
     * 提交数据，返回给BankPeiXiangTaskActivity
     */
    private void commitData() {
        final Intent commitIntent = new Intent();
        final Bundle bundle = new Bundle();
        if (mDeno.equals("other")) {
            if (mPlanNumber == 0 && mArrayList.size() != 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("无任务,是否仍要提交?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bundle.putString("deno", mDeno);
                                bundle.putStringArrayList("list", mArrayList);
                                commitIntent.putExtra("bundle", bundle);
                                setResult(BankPeiXiangTaskActivity.RESULT_CODE, commitIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            } else {
                bundle.putString("deno", "other");
                bundle.putStringArrayList("list", mArrayList);
                commitIntent.putExtra("bundle", bundle);
                this.setResult(BankPeiXiangTaskActivity.RESULT_CODE, commitIntent);
                finish();
            }
        } else if (mPlanNumber != mArrayList.size()) {
            new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage("扫描钱捆数与任务数不同,请确认后重新提交")
                    .setPositiveButton("继续提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bundle.putString("deno", mDeno);
                            bundle.putStringArrayList("list", mArrayList);
                            commitIntent.putExtra("bundle", bundle);
                            setResult(BankPeiXiangTaskActivity.RESULT_CODE, commitIntent);
                            finish();

                        }
                    })
                    .setNegativeButton("取消提交", null)
                    .show();
        } else {
            bundle.putString("deno", mDeno);
            bundle.putStringArrayList("list", mArrayList);
            commitIntent.putExtra("bundle", bundle);
            this.setResult(BankPeiXiangTaskActivity.RESULT_CODE, commitIntent);
            finish();
        }

    }

    private int modeBroad = 0;
    //service connect
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName component) {
            Log.e(TAG, "onServiceDisconnected*****");
            connFlag = false;
        }

        @Override
        public void onServiceConnected(ComponentName component, IBinder ibinder) {
            Log.e(TAG, "onServiceConnected----");
            connFlag = true;
            //			iScan = IScan.Stub.asInterface(ibinder);
            iScan = IHWScan.Stub.asInterface(ibinder);
            try {
                iScan.init();
                //set para
                iScan.setInputMode(modeBroad);
                //open barcode
                iScan.enableSymbology(SymbologyID.SYM_QR);
                iScan.enableSymbology(SymbologyID.SYM_CODE128);
                iScan.enableSymbology(SymbologyID.SYM_CODE39);
                iScan.enableSymbology(SymbologyID.SYM_DATAMATRIX);
                iScan.enableSymbology(SymbologyID.SYM_PDF417);
                iScan.enableSymbology(SymbologyID.SYM_MAXICODE);
                iScan.enableSymbology(SymbologyID.SYM_HANXIN);//chinese hanxin
                iScan.enableSymbology(SymbologyID.SYM_ALL);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    };

    //bind service
    private void bindScanService() {
        Log.e(TAG, "bindScanService: ");
        Intent intent = new Intent();
        intent.setAction("com.scan.service");//SCAN ACTION
        intent.setPackage("com.pda.hwscan");
        boolean b = mContext.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindScanService: " + b);
    }

    //scan thread
    private Runnable mScanThread = new Runnable() {
        @Override
        public void run() {
            while (mrunning) {
                //				if(!isRecved){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //scan
                scan();

                //				}
            }
        }
    };

    //start scan
    private void scan() {
        if (connFlag) {
            try {
                Log.e("Action ------->", "Scan Method running");
                //isRecved = true;
                iScan.scan();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private Vibrator mVibrator;  //传感器

    //接收扫描结果
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String barcodeStr = "";
            new AudioManagerUtil(context).playDiOnce();
            mVibrator.vibrate(100);
            barcodeStr = intent.getStringExtra("barcode");
            if (barcodeStr != null) {
                Log.e(TAG, "onReceive1: " + barcodeStr);
            }
            mTVScanResult.setText(barcodeStr);

            if (mDeno.equals("other")) {
                if (mArrayList.contains(barcodeStr)) {
                    mTVScanResult.setText("扫描结果已存在");
                    return;
                } else {
                    mArrayList.add(barcodeStr);
                    Log.e("ScanActivity", "onReceive: " + barcodeStr);
                    if (mQRAdapter != null) {
                        mQRAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                if (mArrayList.contains(barcodeStr)) {
                    mTVScanResult.setText("扫描结果已存在");
                    return;
                } else if (mArrayList.size() >= mPlanNumber) {
                    mTVScanResult.setText("已扫描的钱捆达到规定数量");
                } else {
                    mArrayList.add(barcodeStr);
                    Log.e("ScanActivity", "onReceive: " + barcodeStr);
                    if (mQRAdapter != null) {
                        mQRAdapter.notifyDataSetChanged();
                    }
                }
            }

        }
    };

    private BroadcastReceiver keyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Action ------->", "keyReceiver Method running");
            int keyCode = intent.getIntExtra("keyCode", 0);
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            //F1 F2 F3 F4 F5
            if (keyDown && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2 || keyCode == KeyEvent.KEYCODE_F3 ||
                    keyCode == KeyEvent.KEYCODE_F4 || keyCode == KeyEvent.KEYCODE_F5 || keyCode == KeyEvent.KEYCODE_F6)) {
                scan();
            }
        }

    };

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("是否放弃提交数据?")
                .setPositiveButton("确认离开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }


}
