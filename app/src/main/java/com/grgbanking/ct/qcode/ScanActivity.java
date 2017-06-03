package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.MApplication;
import com.grgbanking.ct.cach.DataCach;

import java.util.ArrayList;

/**
 * @author ：     cmy
 * @version :     2016/11/11.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ScanActivity extends Activity {
    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";
    int count = 1;//保存按钮点击次数
    private Context context;
    private EditText showScanResult;
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
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            showScanResult.setText("");
            mVibrator.vibrate(100);
            byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);

            String barcodeStr = intent.getStringExtra("barcode_string");//直接获取字符串

            showScanResult.setText(barcodeStr);
        }
    };
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.sacn_activity);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setupView();
        getInfo();
    }

    private void initScan() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private void setupView() {
        showScanResult = (EditText) findViewById(R.id.scan_result);
        btn = (Button) findViewById(R.id.manager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DataCach.codeMap.put("" + count, barcodeStr);
                DataCach.qcodeMap.put(rfidNum, DataCach.codeMap);
                count++;
                if (barcodeStr != null) {
                    if (QRcodelist.size() < 5) {
                        QRcodelist.add(barcodeStr);
                        showTextToast("保存成功！");
                        //                        Toast.makeText(ScanActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        showTextToast("您最多可保存5个二维码");
                        //                        Toast.makeText(ScanActivity.this, "您最多可保存5个二维码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showTextToast("未检测到任何二维码数据，请重新扫描！");
                    //                    Toast.makeText(ScanActivity.this, "未检测到任何二维码数据，请重新扫描！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mScanManager.stopDecode();
                isScaning = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mScanManager.startDecode();
            }
        });

        mClose = (Button) findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mScanManager.stopDecode();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    protected void onResume() {
        super.onResume();
        initScan();
        showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
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
}
