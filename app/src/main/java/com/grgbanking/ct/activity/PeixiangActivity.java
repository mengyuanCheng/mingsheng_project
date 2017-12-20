package com.grgbanking.ct.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.qcode.QcodeActivity;

import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;

/**
 * Created by Administrator on 2016/7/13.
 */


public class PeixiangActivity extends Activity {

    private Button mPxButton;
    private Button mPxback;
    private Button mStatButton;
    private Button mQRButton;
    private Button mBtnBankPeiXiang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peixiang);

        mPxButton = (Button) findViewById(R.id.peixiang_button);
        mPxback = (Button) findViewById(R.id.net_sysout_view);
        //        StatButton = (Button) findViewById(R.id.stat_button);
        mQRButton = (Button) findViewById(R.id.QRCode_button);
        mBtnBankPeiXiang = (Button) findViewById(R.id.bank_peixiang);
        mPxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this, QcodeActivity.class);
                startActivity(intent);
            }
        });
        mPxback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        //        StatButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Intent intent = new Intent();
        //                intent.setClass(PeixiangActivity.this, StatActivity.class);
        //                startActivity(intent);
        //            }
        //        });

        mQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this, SaveQRCodeActivity.class);
                startActivity(intent);
            }
        });
        if (getManufacturer().equals("alps")) {
            mBtnBankPeiXiang.setVisibility(View.VISIBLE);
            mPxButton.setVisibility(View.GONE);
        }else{
            mBtnBankPeiXiang.setVisibility(View.GONE);
            mPxButton.setVisibility(View.VISIBLE);
        }

        mBtnBankPeiXiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeixiangActivity.this, BankPeiXiangActivity.class));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("onKeyDown", "" + keyCode);
            exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * APP退出时间
     */
    private long mExitTime;

    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            MApplication.getInstance().destory();
        }
    }

}
