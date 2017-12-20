package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.Constants;
import com.grgbanking.ct.activity.MApplication;
import com.grgbanking.ct.activity.ScanQRCodeActivity;
import com.grgbanking.ct.adapter.MyBaseAdapter;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.entity.PeiXiangInfo;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.utils.AudioManagerUtil;
import com.grgbanking.ct.utils.FileUtil;
import com.handheld.UHF.UhfManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pda.serialport.Tools;

import static com.grgbanking.ct.cach.DataCach.barcodeList;
import static com.grgbanking.ct.cach.DataCach.loginUser;
import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;
import static com.grgbanking.ct.utils.LoginUtil.isNetworkConnected;

/**
 * @author ：     cmy
 * @version :     2016/11/9.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */
public class QcodeActivity extends Activity {
    private static final String TAG = "QcodeActivity";
    /**
     * 定义文件路径
     */
    private static final String FILE_PATH = "/sdcard/Download/";
    /**
     * 定义文件格式
     */
    private static final String FILE_FORMAT = ".txt";
    /**
     * 定义文件名
     */
    private static final String FILE_NAME = "PX";
    private static final int MSG_UPDATE_LISTVIEW = 0;
    //用来存放更新后的数据
    private HashMap<String, List> reFreshDataMap = new HashMap();
    private SimpleAdapter listItemAdapter;
    private ArrayList<Map<String, Object>> listitem;
    private Context context;
    private Button BT_scan;
    private ListView LV_RFID;
    private Map<String, Integer> data = new HashMap<>();
    private boolean Scanflag = false;
    private boolean isCanceled = true;
    private Button BT_upDate;
    private ArrayList<String> QRcodelist;
    private String rfidNum;
    private ProgressDialog pd = null;
    private Handler mHandler;
    private Timer timer;

    ArrayList<PeiXiangInfo> peiXiangInfos = new ArrayList<>();
    /*-------------------------lzy 新建------------------------*/

    private ArrayList<String> mRFIDCodes = new ArrayList<>();     //存放rfid的list
    private ArrayList<String> mBoxNameList = new ArrayList<>();   //配箱名列表

    private ListView mListView;                  // listView   使用 自定义Adapter
    MyBaseAdapter myBaseAdapter;                 // 为listview自定义的adapter
    public static final int REQUEST_CODE = 000;   //startactivityforresult 的请求码
    public static final int RESULT_CODE = 111;   //startactivityforresult 的返回码


    private UhfManager uhfManager;
    private int power = 0;//rate of work
    private int area = 0;
    private boolean runFlag = true;      //判断是否正在进行RFID扫描
    private boolean startFlag = false;   //判断btnStart的状态
    //    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
    //        //如果收到了回调intent，则刷新数据
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            String action = intent.getAction();
    //            if (action.equals("action.refreshQCode")) {
    //                rfidNum = intent.getExtras().getString("rfidNum");
    //                QRcodelist = intent.getExtras().getStringArrayList("QRcodelist");
    //                Log.d("onReceive: ", "" + QRcodelist);
    //                Log.d("onReceive: ", rfidNum);
    //                if (QRcodelist != null) {
    //                    reFreshDataMap.put(rfidNum, QRcodelist);
    //                } else {
    //                    Toast.makeText(context, "未检测到任何二维码数据，请重新扫描!", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //        }
    //    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qcode_activity);
        context = QcodeActivity.this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshQCode");
        //        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
        findViewById();

        init();

        myBaseAdapter = new MyBaseAdapter(context, peiXiangInfos);
        mListView.setAdapter(myBaseAdapter);
        /*
        * 设置listview的item点击事件
        * 跳转到对应的扫描二维码的activity
        * */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position ----->", "" + position);
                if (peiXiangInfos != null && peiXiangInfos.size() >= position + 1) {
                    PeiXiangInfo peiXiangInfo = peiXiangInfos.get(position);
                    Intent intent = new Intent(context, ScanQRCodeActivity.class);
                    //如果列表中没有数据,就不传过去,否则会造成空指针
                    if (peiXiangInfo.getQR_codelist() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list", peiXiangInfo.getQR_codelist());
                        intent.putExtra("bundle", bundle);
                    }
                    intent.addFlags(position);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Intent intent = new Intent(context, ScanQRCodeActivity.class);
                    intent.addFlags(position);
                    startActivityForResult(intent, REQUEST_CODE);
                }

            }
        });
        /*
        长按删除所选项操作
         */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(context)
                        .setTitle("提示")
                        .setMessage("确定删除这条信息吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRFIDCodes.remove(position);
                                mBoxNameList.remove(position);
                                peiXiangInfos.remove(position);
                                myBaseAdapter.notifyDataSetChanged();
                                onResume();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return false;
            }
        });

        onClickListener();
    }

    /**
     * 初始化数据，从数据库中获取扫描数据
     */
    private void init() {

        DBManager dbdb = new DBManager(this);
        peiXiangInfos = dbdb.queryPeiXiang();
        for (int i = 0; i < peiXiangInfos.size(); i++) {
            List codelist = peiXiangInfos.get(i).getQR_codelist();
            if (codelist != null) {
                for (int j = 0; j < codelist.size(); j++) {
                    barcodeList.add((String) codelist.get(j));
                }
            }
            mRFIDCodes.add(peiXiangInfos.get(i).getBoxNum());
            mBoxNameList.add(peiXiangInfos.get(i).getBoxName());
        }
        updataItem();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getManufacturer().equals("alps")) {
            uhfManager = UhfManager.getInstance();
            if (uhfManager == null) {
                Log.e("uhfmanager ---->", "打开失败");
                return;
            } else {
                Log.e("uhfmanager ---->", "打开成功");
            }
            uhfManager.setOutputPower(power);
            uhfManager.setWorkArea(area);

            ScanThread scanThread = new ScanThread();
            scanThread.start();
        }
    }

    //切换时取消扫描
    @Override
    protected void onPause() {
        if (!getManufacturer().equals("alps")) {
            cancelScan();
            UfhData.Set_sound(false);
        }
        super.onPause();
    }

    /**
     * 重新回到本Activity后刷新Listview的数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        saveInDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getManufacturer().equals("alps")) {
            if (uhfManager != null) {
                uhfManager.close();
                uhfManager = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getManufacturer().equals("alps")) {
            if (uhfManager != null) {
                uhfManager.close();
                uhfManager = null;
            }
        }
    }

    private void findViewById() {
        //        BT_save = (Button) findViewById(R.id.save_btn);
        BT_scan = (Button) findViewById(R.id.qcode_bt_scan);
        //        BT_stop = (Button) findViewById(R.id.qcode_bt_stop);
        //LV_RFID = (ListView) findViewById(R.id.list_qcdoe);
        BT_upDate = (Button) findViewById(R.id.qcode_update_bt);
        /*-----------------------------------------------------*/
        mListView = (ListView) findViewById(R.id.list_qcdoe);
    }

    private void onClickListener() {
        //        BT_save.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                saveInDatabase();
        //            }
        //        });
        BT_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getManufacturer().equals("alps")) {
                    if (BT_scan.getText().equals("扫描")) {
                        startFlag = true;
                        BT_scan.setText("停止");
                    } else {
                        startFlag = false;
                        BT_scan.setText("扫描");
                    }
                } else {
                    if (BT_scan.getText().equals("扫描")) {
                        connDevices();
                        startDevices();
                    } else if (BT_scan.getText().equals("停止")) {
                        cancelScan();
                        UfhData.Set_sound(false);
                    }
                }
            }
        });
        BT_upDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitData();
            }
        });

    }

    /**
     * 上传信息
     * 从数据库读取信息
     * 生成文件导出
     */
    private void commitData() {
        boolean flag = true;

        DBManager db = new DBManager(this);
        if (db.queryPeiXiang() == null || db.queryPeiXiang().size() == 0) {
            saveInDatabase();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{").append("\"GUARD_CODE\":\"").append(loginUser.getLoginName()).append("\",\"BOXRFIDQR\": [");
        DBManager dbManager = new DBManager(this);
        ArrayList<PeiXiangInfo> arrayList = dbManager.queryPeiXiang();
        for (PeiXiangInfo px : arrayList) {
            if (px.getQR_codelist() != null) {
                String temp = px.getQR_codelist().toString();
                Log.i("====tmp1==", "" + temp);
                String tmp2 = temp.replaceAll(", ", "|");
                Log.i("====tmp2==", "" + tmp2);
                String tmp3 = tmp2.substring(1, tmp2.length() - 1);
                Log.i("tmp3===", "" + tmp3);
                sb.append("{\"BOXRFID\":\"").append(px.getBoxNum()).append("\",\"QRCODE\":\"").append(tmp3).append("\"},");
            } else {
                flag = false;
                sb.append("{\"BOXRFID\":").append(px.getBoxNum()).append(",\"QRCODE\":\"").append("").append("\"},");
            }

        }

        String tmp = sb.toString().substring(0, sb.toString().length() - 1) + "]}";
        Log.i("====tmp==", "" + tmp);

        if (flag) {
            //将数据写入SD卡
            String date = FileUtil.getDate();
            String addr = FileUtil.createIfNotExist(FILE_PATH + FILE_NAME + date + FILE_FORMAT);

            byte[] writebytes = new byte[0];
            FileUtil.strToByteArray(tmp);
            FileUtil.writeBytes(addr, writebytes);
            FileUtil.writeString(addr, tmp, "utf-8");
            FileUtil.makeFileAvailable(context, addr);
            new AlertDialog.Builder(context)
                    .setTitle("提示信息")
                    .setMessage("生成文件成功")
                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("上传", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(getManufacturer().equals("alps") && isNetworkConnected(context)){
                                List<NameValuePair> params = new ArrayList<>();
                                String date = FileUtil.getDate();
                                String mResult=FileUtil.readTXT(FILE_PATH + FILE_NAME + date + FILE_FORMAT);
                                params.add(new BasicNameValuePair("content", mResult));
                                new HttpPostUtils(Constants.URL_PX_NET_UPLOAD, params, new UICallBackDao() {
                                    @Override
                                    public void callBack(ResultInfo resultInfo) {
                                        Toast.makeText(getApplicationContext(),resultInfo.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }).execute();
                            }else{
                                Toast.makeText(getApplicationContext(),"没有网络",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
            hideWaitDialog();
            dbManager.cleanPeiXiang();
            DataCach.clearAllDataCach();
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("提示信息")
                    .setMessage("空箱包无法提交")
                    .setPositiveButton("确认", null)
                    .show();
        }

    }

    /**
     * 将扫描上来的款箱及其二维码保存到数据库中
     */
    private void saveInDatabase() {

        showWaitDialog("正在保存中.....");
        DBManager db = new DBManager(this);
        db.cleanPeiXiang();
        if (!peiXiangInfos.isEmpty()) {
            if (db.addPeiXiang(peiXiangInfos)) {
                hideWaitDialog();
                //                Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
            } else {
                hideWaitDialog();
                //                Toast.makeText(context, "保存失败!", Toast.LENGTH_SHORT).show();
            }
        } else {
            hideWaitDialog();
            //            Toast.makeText(context, "数据为空!", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 连接设备
     */
    private void connDevices() {
        int result = UfhData.UhfGetData.OpenUhf(57600, (byte) 0xff, 4, 1, null);
        if (result == 0) {
            UfhData.UhfGetData.GetUhfInfo();
        } else {
            Toast.makeText(context, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开始扫描
     *
     * @return HashMap, rfid+rfid.
     */
    private void startDevices() {
        if (!UfhData.isDeviceOpen()) {
            Toast.makeText(this, R.string.detail_title, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            if (timer == null) {
                UfhData.Set_sound(true);
                UfhData.SoundFlag = true;

                isCanceled = false;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (Scanflag)
                            return;
                        Scanflag = true;
                        UfhData.read6c();
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                            mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
                            Scanflag = false;
                        }

                    }
                }, 0, 10);
                BT_scan.setText("停止");
            } else {
                cancelScan();
                UfhData.Set_sound(false);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消扫描
     */
    private void cancelScan() {
        try {
            isCanceled = true;
            mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
            if (timer != null) {
                timer.cancel();
                timer = null;
                UfhData.scanResult6c.clear();
                BT_scan.setText("扫描");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取扫描结果rfidcodes 保存在新的列表里
     * 更新 item
     */
    private void updataItem() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_LISTVIEW:
                        //data中存放Pda扫描上来的数据
                        if (getManufacturer().equals("alps")) {
                            String epc = msg.getData().getString("rfid");
                            Log.d(TAG, "handleMessage: " + epc);
                            data.put(epc, 0);
                        } else {
                            //data中存放Pda扫描上来的数据
                            data = UfhData.scanResult6c;
                        }
                        Iterator it = data.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry) it.next();
                            String rfid = (String) entry.getKey();
                            Log.e(TAG, "updataItem: " + rfid);

                            DBManager db = new DBManager(context);
                            Map<String, String> map = DataCach.getPdaLoginMsg().getAllPdaBoxsMap();
                            String boxName = map.get(rfid);
                            Log.e(TAG, "updataItem: " + boxName);
                            //            String boxName = db.queryCashBoxName(rfid);
                            if (boxName != null && !boxName.equals("")) {
                                String[] strings = boxName.split("&");
                                String boxName1 = strings[0];
                                if (!mBoxNameList.contains(boxName1)) {
                                    mBoxNameList.add(0, boxName1);
                                    mRFIDCodes.add(0, rfid);
                                    PeiXiangInfo peiXiangInfo = new PeiXiangInfo(rfid, boxName1);
                                    peiXiangInfos.add(0, peiXiangInfo);
                                }
                            }
                        }
                        myBaseAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

    }


    /**
     * 开始Dialog 请传入显示的字符
     *
     * @param msg
     */
    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    /**
     * 结束Dialog
     */
    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
    }

    /**
     * 处理扫描返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult ----->", "正在运行");
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_CODE:
                    PeiXiangInfo peiXiangInfo = new PeiXiangInfo();
                    Bundle bundle = data.getBundleExtra("bundle");
                    int position = data.getFlags();
                    ArrayList<String> list = bundle.getStringArrayList("list");
                    peiXiangInfo.setBoxNum(mRFIDCodes.get(position));
                    peiXiangInfo.setBoxName(mBoxNameList.get(position));
                    if (list != null && !list.isEmpty()) {
                        peiXiangInfo.setQR_codelist(list);
                    }
                    peiXiangInfo.setScanningDate(FileUtil.getDate());
                    peiXiangInfos.add(position, peiXiangInfo);
                    if (peiXiangInfos.size() > position + 1) {
                        peiXiangInfos.remove(position + 1);
                    }
                    for (PeiXiangInfo peiXiangInfo1 : peiXiangInfos) {
                        Log.i("peixiangInfos ---->", peiXiangInfo1.toString());
                    }

                    myBaseAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 子线程处理扫描结果
     */
    private class ScanThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
                    // manager.stopInventoryMulti()
                    epcList = uhfManager.inventoryRealTime(); // inventory real time
                    if (epcList != null && !epcList.isEmpty()) {
                        // play sound
                        new AudioManagerUtil(context).playDiOnce();
                        for (byte[] epc : epcList) {
                            String epcStr = Tools.Bytes2HexString(epc,
                                    epc.length);
                            Log.d(TAG, "run: " + epcStr);
                            Message message = Message.obtain();
                            message.what = MSG_UPDATE_LISTVIEW;
                            Bundle bundle = new Bundle();
                            bundle.putString("rfid", epcStr);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
