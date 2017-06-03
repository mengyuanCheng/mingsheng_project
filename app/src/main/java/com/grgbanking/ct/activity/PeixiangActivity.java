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
import com.grgbanking.ct.qcode.StatActivity;

/**
 * Created by Administrator on 2016/7/13.
 */


public class PeixiangActivity extends Activity {

    private Button PxButton;
    private Button Pxback;
    private Button StatButton;
    private Button QRButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peixiang);

        PxButton = (Button) findViewById(R.id.peixiang_button);
        Pxback = (Button) findViewById(R.id.net_sysout_view);
        StatButton = (Button) findViewById(R.id.stat_button);
        QRButton = (Button) findViewById(R.id.QRCode_button);

        PxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this, QcodeActivity.class);
                startActivity(intent);
            }
        });

        Pxback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        StatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this, StatActivity.class);
                startActivity(intent);
            }
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this,QRCodeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0) {
            Log.d("onKeyDown", "" + keyCode);
            exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /** APP退出时间*/
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
