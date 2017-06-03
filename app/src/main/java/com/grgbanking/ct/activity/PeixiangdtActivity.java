package com.grgbanking.ct.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;
import com.hlct.framework.business.message.entity.PdaGuardManInfo;
import com.hlct.framework.business.message.entity.PdaLoginMessage;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.rfid.UfhData.UhfGetData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/12.
 */
public class PeixiangdtActivity extends Activity {

    private static final int MSG_UPDATE_LISTVIEW = 0;
    private static final int SCAN_INTERVAL = 10;
    static PdaNetPersonInfo netPersonInfo = null;//保存箱包号
    static PdaGuardManInfo guardManInfo = null;//保存配钞人员
    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    private Button PeixiangDtBack;
    private Button PeixiangDtCommit;
    private TextView PeixiangDtName;
    private TextView PeixiangDTNumber;
    private Button PeixiangDTConnect;
    private Button PeixiangDtScan;
    private ListView peixiangListView;
    private Timer timer;
    private boolean isCanceled = true;
    private boolean Scanflag = false;
    private Handler mHandler;
    private Map<String, Integer> data;
    private int tty_speed = 57600;
    private byte addr = (byte) 0xff;
    /**
     * @param msg
     * 等待时的Dialog
     */
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.peixiangdetail);
        
        findViewById();
        setOnClickListener();

        //         生成动态数组，加入数据
        //        listItem = new ArrayList<HashMap<String, Object>>();
        //         生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.peixiangdt_list_item, new String[]{"code"}, new int[]{R.id.code});
        //         添加并且显示
        peixiangListView.setAdapter(listItemAdapter);


        //启动RFID扫描功能刷新扫描款箱数据
        flashInfo();

    }

    private void findViewById() {
        PeixiangDtBack = (Button) findViewById(R.id.peixiangdetail_btn_back);
        PeixiangDtCommit = (Button) findViewById(R.id.peixiangdetail_btn_commit);
        PeixiangDtName = (TextView) findViewById(R.id.peixiangdetail_tv_name);
        PeixiangDTNumber = (TextView) findViewById(R.id.peixiangdetail_tv_num);
        PeixiangDTConnect = (Button) findViewById(R.id.peixiangdetail_btn_lianjie);
        PeixiangDtScan = (Button) findViewById(R.id.peixiangdetail_btn_saomiao);
        peixiangListView = (ListView) findViewById(R.id.peixiang_ListView);
    }

    private void setOnClickListener() {
        PeixiangDtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit("");
            }
        });
        PeixiangDtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backListPage();
            }
        });
        PeixiangDTConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        PeixiangDtScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    /**
     * 启动扫描
     */
    private void scan() {
        //        showWaitDialog("正在扫描中");
        Log.v("正在启动扫描", "1");
        if (!UfhData.isDeviceOpen()) {
            //            hideWaitDialog();
            Toast.makeText(PeixiangdtActivity.this, "请先连接设备", Toast.LENGTH_LONG).show();
            return;
        }
        if (timer == null) {
            UfhData.Set_sound(true);
            UfhData.SoundFlag = false;
            isCanceled = false;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (Scanflag)
                        return;
                    Scanflag = true;
                    UfhData.read6c();
                    mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                    mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
                    Scanflag = false;
                }
            }, 0, SCAN_INTERVAL);
            PeixiangDtScan.setText("Stop");
        } else {
            cancelScan();
            Log.v("取消扫描", "");
            UfhData.Set_sound(false);
        }
        Log.v("扫描完成", "");
    }

    /**
     * 启动RFID扫描功能刷新扫描款箱数据
     */
    private void flashInfo() {
        mHandler = new Handler() {

            public void handleMessage(Message msg) {
                if (isCanceled)
                    return;
                switch (msg.what) {
                    case MSG_UPDATE_LISTVIEW:
                        data = UfhData.scanResult6c;
                        Log.v("data:", "" + data);
                        String name = PeixiangDtName.getText().toString();
                        String number = PeixiangDTNumber.getText().toString();

                        Iterator it = data.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();

                            if (key.indexOf(Constants.PRE_RFID_GUARD) != -1) {//获取配钞人员
                                if (name.trim().equals("")) {
                                    PdaLoginMessage plm = DataCach.getPdaLoginMessage();
                                    Log.v("plm", "" + plm);
                                    if (plm != null) {
                                        List<PdaGuardManInfo> guardManInfoList = plm.getGuardManInfoList();
                                        if (guardManInfoList != null && guardManInfoList.size() > 0) {
                                            for (PdaGuardManInfo info : guardManInfoList) {
                                                if (info.getGuardManRFID().equals(key)) {
                                                    PeixiangDtName.setText(info.getGuardManName());
                                                    guardManInfo = info;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (key.indexOf(Constants.PRE_RFID_BANKEMPLOYEE) != -1) {//获取箱包号
                                if (number.trim().equals("")) {
                                    Intent intent = getIntent();
                                    Bundle bundle = intent.getBundleExtra("bundle");
                                    int count = bundle.getInt("count");
                                    HashMap<String, Object> map = DataCach.taskMap.get(count + "");
                                    PdaNetInfo pni = (PdaNetInfo) map.get("data");
                                    Log.v("pni", "" + pni);
                                    if (pni != null) {
                                        List<PdaNetPersonInfo> netPersonInfolist = pni.getNetPersonInfoList();
                                        if (netPersonInfolist != null && netPersonInfolist.size() > 0) {
                                            for (PdaNetPersonInfo info : netPersonInfolist) {
                                                if (info.getNetPersonRFID().equals(key)) {
                                                    PeixiangDTNumber.setText(info.getNetPersonName());
                                                    netPersonInfo = info;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (key.indexOf(Constants.NET_TASK_STATUS_FINISH) != -1) {//获取二维码字符串（）

                            }
                        }
                }
            }
        };
    }

    /**
     * 取消扫描
     */
    private void cancelScan() {
        isCanceled = true;
        mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
        if (timer != null) {
            timer.cancel();
            timer = null;
            PeixiangDtScan.setText("Scan");
            UfhData.scanResult6c.clear();
        }
    }

    /**
     * 连接设备
     */
    private void connect() {
        showWaitDialog("正在连接中");
        int result = UhfGetData.OpenUhf(tty_speed, addr, 4, 1, null);

        if (result == 0) {
            UhfGetData.GetUhfInfo();
            hideWaitDialog();
            Toast.makeText(PeixiangdtActivity.this, "连接设备成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PeixiangdtActivity.this, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 返回配箱activity
     */
    private void backListPage() {
        if (PeixiangDtScan.getText().toString().equals("Stop")) {
            Toast.makeText(PeixiangdtActivity.this, "请先停止扫描", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getApplicationContext(), PeixiangActivity.class));
            finish();
        }

    }

    /**
     * 提交页面数据到服务器
     *
     * @param flag
     */
    private void commit(String flag) {

        if (PeixiangDtScan.getText().toString().equals("Stop")) {
            Toast.makeText(PeixiangdtActivity.this, "请先停止扫描", Toast.LENGTH_SHORT).show();
        } else {

            showWaitDialog("正在处理中...");
            String name = PeixiangDtName.getText().toString();
            String number = PeixiangDTNumber.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("number", number));


            new HttpPostUtils(Constants.URL_SAVE_TASK, params, new UICallBackDao() {
                @Override
                public void callBack(ResultInfo resultInfo) {
                    hideWaitDialog();

                    if ("1".equals(resultInfo.getCode())) {
                        new AlertDialog.Builder(PeixiangdtActivity.this)
                                .setTitle("消息")
                                .setMessage("提交成功！")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {// 设置确定按钮
                                            @Override
                                            // 处理确定按钮点击事件
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                backListPage();
                                            }
                                        }).show();
                    } else {
                        Toast.makeText(PeixiangdtActivity.this, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }).execute();
        }
    }

    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    /**
     * 去除Dialog
     */
    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
    }
}