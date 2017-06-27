package com.grgbanking.ct.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.grgbanking.ct.R;
import com.grgbanking.ct.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import static com.grgbanking.ct.R.id.close;
import static com.grgbanking.ct.R.id.manager;

/**
 * @author ：     cmy
 * @version :     2017/3/4.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class QRCodeActivity extends Activity {

    /**
     * 开始定义要用到的资源文件
     */
    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";//
    /**
     * 定义文件名
     */
    private static final String FILE_NAME = "QRCODE";
    /**
     * 定义文件格式
     */
    private static final String FILE_FORMAT = ".txt";
    /**
     * 1
     * 定义文件路径
     */
    private static final String FILE_PATH = "/sdcard/Download/";
    /**
     * 定义一个List集合 用来保存所有的二维码
     */
    List<String> QRCodeList = new ArrayList<>();
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
            Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);
            String barcodeStr = intent.getStringExtra("barcode_string");//直接获取字符串
            showScanResult.setText(barcodeStr);
        }
    };

    /**
     * 将扫描到的二维码数据保存到SD卡
     */
    private void saveInSDCard(List<String> QRCodeList) {
        String date = FileUtil.getDate();
        Log.d("date==", date);
        FileUtil.writeString(FILE_PATH + FILE_NAME + date + FILE_FORMAT, "" + QRCodeList, "GBk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setupView();
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
        btn = (Button) findViewById(manager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                QRCodeList.add(barcodeStr);
            }
        });

        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
        mClose = (Button) findViewById(close);
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

    /**
     * 退出时弹出dialog
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提示是否需要提交并做相应的跳转
     */
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
        builder.setMessage("是否提交？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveInSDCard(QRCodeList);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }
}
