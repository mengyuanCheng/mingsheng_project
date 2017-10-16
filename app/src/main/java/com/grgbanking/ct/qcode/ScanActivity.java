package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.MApplication;
import com.grgbanking.ct.utils.AudioManagerUtil;
import com.pda.scan.DecoderConfigValues.SymbologyID;
import com.pda.scan.IHWScan;

import java.util.ArrayList;
import java.util.Timer;

import static com.grgbanking.ct.cach.DataCach.barcodeList;
import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;

/**
 * @author ：     cmy
 * @version :     2016/11/11.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description : 二维码扫描界面,扫描结果返回给ScanQRCodeActivity
 */

public class ScanActivity extends Activity {
    private static final String TAG = "ScanActivity";
    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";
    public static final int REQUEST_CODE_SCAN = 222;   //请求码
    public static final int RESULT_CODE_SCAN = 333;    //返回码
    int count = 1;//保存按钮点击次数
    private Context context;
    private TextView showScanResult;
    private Button btn;
    private Button mScan;
    private Button mClose;
    private int type;
    private int outPut;
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private String rfidNum = "";
    private ArrayList<String> QRcodelist = new ArrayList();

    /*----------------------------------------------------------*/
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> mArrayList = new ArrayList<>();

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String barcodeStr = "";
            if (getManufacturer().equals("alps")) {
                new AudioManagerUtil(context).playDiOnce();
                mVibrator.vibrate(100);
                barcodeStr = intent.getStringExtra("barcode");
                if (barcodeStr != null) {
                    Log.e(TAG, "onReceive1: " + barcodeStr);
                }
            } else {
                isScaning = false;
                soundpool.play(soundid, 1, 1, 0, 0, 1);
                showScanResult.setText("");
                mVibrator.vibrate(100);
                /*byte[] barcode = intent.getByteArrayExtra("barcode");
                int barocodelen = intent.getIntExtra("length", 0);
                byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                Log.i("debug", "----codetype--" + temp);
                barcodeStr = new String(barcode, 0, barocodelen);*/
                barcodeStr = intent.getStringExtra("barcode_string");//直接获取字符串
            }
            showScanResult.setText(barcodeStr);
            if (mArrayList.contains(barcodeStr) || barcodeList.contains(barcodeStr)) {
                Toast.makeText(ScanActivity.this, "扫描结果已经存在", Toast.LENGTH_SHORT).show();
                return;
            } else {
                barcodeList.add(barcodeStr);
                mArrayList.add(barcodeStr);
                Log.e("ScanActivity", "onReceive: " + barcodeStr);
                if (mArrayAdapter != null) {
                    mArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private Toast toast = null;

    /*--------------------------------------------*/
    private IHWScan iScan;
    private boolean mrunning = false;
    private boolean connFlag = false;
    private boolean isRecved = false;
    private Timer scanTimer;

    private BroadcastReceiver keyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Action ------->", "keyReceiver Method running");
            int keyCode = intent.getIntExtra("keyCode", 0);
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            //F1 F2 F3 F4 F5
            if (keyDown && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2 || keyCode == KeyEvent.KEYCODE_F3 ||
                    keyCode == KeyEvent.KEYCODE_F4 || keyCode == KeyEvent.KEYCODE_F5 || keyCode == KeyEvent.KEYCODE_F6)) {
                mrunning = true;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.sacn_activity);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        context = ScanActivity.this;
        setupView();
        getInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getManufacturer().equals("alps")) {
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
                connFlag = false;
                try {
                    Log.e(TAG, "close----");
                    iScan.close();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.e("Action ------->", "onPause Method running");
                unbindService(conn);
            }
        } else {
            if (mScanManager != null) {
                mScanManager.stopDecode();
                isScaning = false;
            }
            unregisterReceiver(mScanReceiver);

            //传递数据
            Intent intent = new Intent();
            intent.setAction("action.refreshQCode");
            intent.putExtra("rfidNum", rfidNum);
            intent.putStringArrayListExtra("QRcodelist", QRcodelist);
            Log.d("onPause: ", "" + QRcodelist);
            ScanActivity.this.sendBroadcast(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getManufacturer().equals("alps")) {
            Log.e(TAG, "onResume: running");
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
        } else {
            initScan();
            showScanResult.setText("");
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void initScan() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private void setupView() {
        showScanResult = (TextView) findViewById(R.id.scan_result);
        //        btn = (Button) findViewById(R.id.manager);
        //        btn.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View arg0) {
        //                saveData();
        //               /* DataCach.codeMap.put("" + count, barcodeStr);
        //                DataCach.qcodeMap.put(rfidNum, DataCach.codeMap);
        //                count++;
        //                if (barcodeStr != null) {
        //                    if (QRcodelist.size() < 5) {
        //                        QRcodelist.add(barcodeStr);
        //                        showTextToast("保存成功！");
        //                        //                        Toast.makeText(ScanActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
        //                    } else {
        //                        showTextToast("您最多可保存5个二维码");
        //                        //                        Toast.makeText(ScanActivity.this, "您最多可保存5个二维码", Toast.LENGTH_SHORT).show();
        //                    }
        //                } else {
        //                    showTextToast("未检测到任何二维码数据，请重新扫描！");
        //                    //                    Toast.makeText(ScanActivity.this, "未检测到任何二维码数据，请重新扫描！", Toast.LENGTH_SHORT).show();
        //                }*/
        //            }
        //        });
        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getManufacturer().equals("alps")) {
                    Log.e(TAG, "onClick: Scan");
                    mrunning = true;
                    new Thread(mScanThread).start();
                } else {
                    mScanManager.stopDecode();
                    isScaning = true;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mScanManager.startDecode();
                }
            }
        });

        mClose = (Button) findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (getManufacturer().equals("alps")) {
                    //TODO
                    mrunning = false;
                    /*if (scanTimer != null) {
                        scanTimer.cancel();
                    }*/
                } else {
                    mScanManager.stopDecode();
                }
                saveData();
            }
        });

        /*-----------------------------------------------------------*/
        mListView = (ListView) findViewById(R.id.scan_listview);
        mArrayAdapter = new ArrayAdapter(context, R.layout.array_listview_item_view,
                R.id.array_listview_textview, mArrayList);
        mListView.setAdapter(mArrayAdapter);

    }

    /*
    * 按保存按钮的时候
    * */
    private void saveData() {
        Intent commitIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", mArrayList);
        commitIntent.putExtra("bundle", bundle);
        this.setResult(RESULT_CODE_SCAN, commitIntent);
        finish();
    }


    /**
     * 获取qcode传过来的rfidNum
     *
     * @return
     */
    public String getInfo() {
        rfidNum = getIntent().getStringExtra("rfidNum");
        return rfidNum;
    }

    /**
     * 显示toast消息
     *
     * @param msg
     */
    private void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    //bind service
    private void bindScanService() {
        Intent intent = new Intent();
        intent.setAction("com.scan.service");//SCAN ACTION
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    private int modeBroad = 0;
    //service connect
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName component) {
            Log.e(TAG, "onServiceDisconnected*****");

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

        ;
    };
    //start scan
    private void scan() {
        if (connFlag) {
            try {
                Log.e("Action ------->", "Scan Method running");
                isRecved = true;
                iScan.scan();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
