package com.grgbanking.ct.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.entity.TaskInfo;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.rfid.UfhData.UhfGetData;
import com.grgbanking.ct.scan.Recordnet;
import com.grgbanking.ct.scan.Waternet;
import com.grgbanking.ct.utils.AudioManagerUtil;
import com.grgbanking.ct.utils.FileUtil;
import com.handheld.UHF.UhfManager;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaGuardManInfo;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pda.serialport.Tools;

import static com.grgbanking.ct.R.id.detail_btn_commit_n;
import static com.grgbanking.ct.activity.Constants.FILE_FORMAT;
import static com.grgbanking.ct.activity.Constants.FILE_NAME_IN;
import static com.grgbanking.ct.activity.Constants.FILE_NAME_OUT;
import static com.grgbanking.ct.activity.Constants.FILE_PATH;
import static com.grgbanking.ct.activity.Constants.NET_COMMIT_TYPE_IN;
import static com.grgbanking.ct.activity.Constants.NET_COMMIT_TYPE_OUT;
import static com.grgbanking.ct.cach.DataCach.pdaLoginMsg;
import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;

@SuppressLint("NewApi")
public class DetailActivity extends Activity {

    private static final String TAG = "DetailActivity";
    //Dao对象的管理者
    private static final int SCAN_INTERVAL = 10;
    private static final int MSG_UPDATE_LISTVIEW = 0;
    private static HashMap<String, Object> boxesMap1 = null;//保存正确款箱map
    private static HashMap<String, Object> boxesMap2 = null;//保存多出的款箱map
    private static HashMap<String, Object> boxesMap3 = null;//保存错误的款箱map
    private static PdaNetPersonInfo netPersonInfo = null;//保存网点人员
    private static PdaGuardManInfo guardManInfo = null;//保存押运人员
    private static PdaNetPersonInfo netPersonInfo2 = null;//保存第二个网点人员
    private static PdaGuardManInfo guardManInfo2 = null;//保存第二个押运人员
    TextView positionTextView = null;
    TextView branchNameTextView = null;
    Button commitYesButton = null;
    Button commitNoButton = null;
    TextView detailTitleTextView = null;
    Button startDeviceButton = null;
    TextView person1TextView = null;
    TextView person2TextView = null;
    TextView person3TextView = null;
    TextView person4TextView = null;
    ListView deviceListView;
    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    private Context context;
    private EditText remarkEditView;
    private int tty_speed = 57600;
    private byte addr = (byte) 0xff;
    private Timer timer;
    private boolean Scanflag = false;
    private boolean isCanceled = true;
    private Handler mHandler;
    private Map<String, Integer> data = new HashMap<>();
    private ProgressDialog pd = null;

    /****************掃描方式二*********************/
    private UhfManager uhfManager;
    private int power = 0;//rate of work
    private int area = 0;
    private boolean runFlag = true;      //判断是否正在进行RFID扫描
    private boolean startFlag = false;   //判断btnStart的状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail);
        context = getApplicationContext();
        remarkEditView = (EditText) findViewById(R.id.detail_remark);
        commitNoButton = (Button) findViewById(detail_btn_commit_n);
        branchNameTextView = (TextView) findViewById(R.id.detail_branch_name);
        detailTitleTextView = (TextView) findViewById(R.id.detail_title_view);
        startDeviceButton = (Button) findViewById(R.id.Button01);
        person1TextView = (TextView) findViewById(R.id.yayun1_tv);
        person2TextView = (TextView) findViewById(R.id.wangdian1_tv);
        person3TextView = (TextView) findViewById(R.id.yayun2_tv);
        person4TextView = (TextView) findViewById(R.id.wangdian2_tv);
        deviceListView = (ListView) findViewById(R.id.ListView_boxs);

        // 生成动态数组，加入数据
        listItem = new ArrayList<>();
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(
                this,
                listItem,
                R.layout.boxes_list_item,
                new String[]{"list_img", "list_title"},
                new int[]{R.id.list_boxes_img, R.id.list_boxes_title});
        // 添加并且显示
        deviceListView.setAdapter(listItemAdapter);

        showWaitDialog("正在加载中，请稍后...");

        loadDevices();

        //启动RFID扫描功能刷新扫描款箱数据
        flashInfo();

        hideWaitDialog();
        // 点击返回按钮操作内容
        findViewById(R.id.detail_btn_back).setOnClickListener(click);

        commitNoButton.setOnClickListener(click);
        startDeviceButton.setOnClickListener(click);


        // 点击添加照片按钮操作内容
        //		findViewById(R.id.add_photo).setOnClickListener(click);
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

    OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            String context = startDeviceButton.getText().toString();
            switch (arg0.getId()) {
                case R.id.detail_btn_back:
                    if (context.equals("停止扫描")) {
                        Toast.makeText(DetailActivity.this, "请先停止扫描", Toast.LENGTH_LONG).show();
                    } else {
                    }
                    break;
                case detail_btn_commit_n:
                    if (context.equals("停止扫描")) {
                        Toast.makeText(DetailActivity.this, "请先停止扫描", Toast.LENGTH_LONG).show();
                    } else {
                        //判断人员是否扫描完成
                        String flag = personIsScan();
                        if (flag.equals("true")) {

                        } else {
                            showInfoDialog(flag);
                            break;
                        }
                        //判断款箱是否扫描正确
                        flag = boxIsScan();
                        if (flag.equals("true")) {

                        } else {
                            showInfoDialog(flag);
                            break;
                        }
                        //写入文件
                        writeFile();
                        //判断文件是否成功生成
                        flag = fileIsWrite();
                        if (flag.equals("true")) {
                            showInfoDialog("交接成功!");
                        } else {
                            showInfoDialog(flag);
                        }
                    }
                    break;
                case R.id.Button01:
                    if (getManufacturer().equals("alps")) {
                        if (context.equals("启动扫描")) {
                            startFlag = true;
                            startDeviceButton.setText("停止扫描");
                        } else {
                            startFlag = false;
                            startDeviceButton.setText("启动扫描");
                        }
                    } else {
                        if (context.equals("启动扫描")) {
                            connDevices();
                            startDevices();
                        } else {
                            cancelScan();
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void flashInfo() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
//                if (isCanceled)
//                    return;
                switch (msg.what) {
                    case MSG_UPDATE_LISTVIEW:

                        if (getManufacturer().equals("alps")) {
                            String epc = msg.getData().getString("rfid");
                            Log.d(TAG, "handleMessage: " + epc);
                            data.put(epc,0);
                        }else{
                            //data中存放Pda扫描上来的数据
                            data = UfhData.scanResult6c;
                        }

                        String person1 = person1TextView.getText().toString();
                        String person2 = person2TextView.getText().toString();
                        String person3 = person3TextView.getText().toString();
                        String person4 = person4TextView.getText().toString();
                        Iterator it = data.keySet().iterator();
                        try {
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                Log.i("==key==", "" + key);
                                //判断是否是押运人员
                                if (key.indexOf(Constants.PRE_RFID_GUARD) != -1) {
                                    PdaLoginMsg plm = DataCach.getPdaLoginMsg();
                                    List<PdaGuardManInfo> guardManInfoList = plm.getPdaGuardManInfo();
                                    //扫描第一个押运人员
                                    //如果person1还没有扫描到数据 则继续扫描
                                    if (person1.trim().equals("")) {
                                        //PdaLoginMessage plm = DataCach.getPdaLoginMessage();
                                        if (plm != null) {
                                            if (guardManInfoList != null && guardManInfoList.size() > 0) {
                                                for (PdaGuardManInfo info : guardManInfoList) {
                                                    Log.i("==key==info", "" + info.getGuardManRFID());
                                                    Log.i("==key==info", "" + info.getGuardManName());
                                                    if (info.getGuardManRFID().equals(key)) {
                                                        person1TextView.setText(info.getGuardManName());
                                                        guardManInfo = info;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //扫描第二个押运人员
                                    if (person3.trim().equals("")) {
                                        if (plm != null) {
                                            for (PdaGuardManInfo info : guardManInfoList) {
                                                if (info.getGuardManRFID().equals(key)) {
                                                    if (info.getGuardManName().
                                                            equals(person1TextView.getText())) {
                                                    } else {
                                                        person3TextView.setText(info.getGuardManName());
                                                        guardManInfo2 = info;
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                //判断是否是网点人员
                                else if (key.indexOf(Constants.PRE_RFID_BANKEMPLOYEE) != -1) {
                                    Log.d("网店人员",key);
                                    Intent intent = getIntent();
                                    Bundle bundle = intent.getBundleExtra("bundle");
                                    int count = bundle.getInt("count");
                                    HashMap<String, Object> map = DataCach.taskMap.get(count + "");
                                    PdaNetInfo pni = (PdaNetInfo) map.get("data");
                                    List<PdaNetPersonInfo> netPersonInfoList = pni.getNetPersonInfoList();
                                    Log.d("===netPersonInfoList", netPersonInfo + "");
                                    if (person2.trim().equals("")) {
                                        if (pni != null) {
                                            if (netPersonInfoList != null && netPersonInfoList.size() > 0) {
                                                Log.d("===netPersonInfoList", netPersonInfo + "");
                                                Log.d("===网点人员", key);
                                                for (PdaNetPersonInfo info : netPersonInfoList) {
                                                    if (info.getNetPersonRFID().equals(key)) {
                                                        person2TextView.setText(info.getNetPersonName());
                                                        netPersonInfo = info;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //扫描第二个网点人员
                                    if (person4.trim().equals("")) {
                                        for (PdaNetPersonInfo info : netPersonInfoList) {
                                            if (info.getNetPersonRFID().equals(key)) {
                                                if (info.getNetPersonName().
                                                        equals(person2TextView.getText())) {
                                                } else {
                                                    person4TextView.setText(info.getNetPersonName());
                                                    netPersonInfo2 = info;
                                                }
                                                break;
                                            }
                                        }
                                    }

                                }
                                //判断是否是正确款箱RFID
                                else if (boxesMap1.get(key) != null) {
                                    HashMap<String, Object> map = DataCach.boxesMap.get(key);
                                    map.put("list_img", R.drawable.boxes_list_status_1);// 图像资源的ID
                                    HashMap<String, Object> map1 = (HashMap<String, Object>) boxesMap1.get(key);
                                    //记录该款箱是否已扫描  0:未扫描;1:已扫描
                                    map1.put("status", "1");
                                }
                                //判断是否是错误款箱RFID
//                                else if (boxesMap2.get(key) == null) {
//                                    ResultInfo resultInfo = new ResultInfo();
//                                    String date = FileUtil.getDate();
//                                    resultInfo = (ResultInfo) FileUtil.readString(FILE_PATH + date + "WDRW.dat");
//                                    PdaLoginMessage pda = resultInfo.getPdaLogMess();
//                                    Map<String, String> allPdaBoxsMap = pda.getAllPdaBoxsMap();
//                                    if (allPdaBoxsMap.get(key) != null) {
//                                        HashMap<String, Object> map1 = new HashMap<>();
//                                        map1.put("list_img", R.drawable.boxes_list_status_3);// 图像资源的ID
//                                        String string = allPdaBoxsMap.get(key);
//                                        String a[] = string.split("&");
//                                        map1.put("list_title", a[0]);
//                                        DataCach.boxesMap.put(key, map1);
//                                        boxesMap2.put(key, map1);
//                                        listItem.add(map1);
//                                    }
//                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listItemAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };
    }

    private void connDevices() {
        int result = UhfGetData.OpenUhf(tty_speed, addr, 4, 1, null);
        if (result == 0) {//连接设备成功
            UhfGetData.GetUhfInfo();
        } else {//连接设备失败
            Toast.makeText(context, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动扫描
     */
    private void startDevices() {
        try {
            if (!UfhData.isDeviceOpen()) {
                Toast.makeText(this, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
                return;
            }
            if (timer == null) {
                //声音开关初始化
                UfhData.Set_sound(true);
                UfhData.SoundFlag = true;

                isCanceled = false;
                timer = new Timer();
                //
                timer.schedule(new TimerTask() {
                    @Override
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
                startDeviceButton.setText("停止扫描");
            } else {
                cancelScan();
                UfhData.Set_sound(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelScan() {
        try {
            isCanceled = true;
            mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
            if (timer != null) {
                timer.cancel();
                timer = null;
                startDeviceButton.setText("启动扫描");
                UfhData.scanResult6c.clear();
                UhfGetData.CloseUhf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDevices() {
        //加载数据要先把数据缓存清空
        person1TextView.setText("");
        person2TextView.setText("");
        DataCach.boxesMap = null;
        DataCach.boxesMap = new LinkedHashMap<>();
        boxesMap1 = null;
        boxesMap1 = new HashMap<>();
        boxesMap2 = null;
        boxesMap2 = new HashMap<>();
        boxesMap3 = null;
        boxesMap3 = new HashMap<>();
        netPersonInfo = null;
        guardManInfo = null;
        netPersonInfo2 = null;
        guardManInfo2 = null;
        listItem.clear();


        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        int count = bundle.getInt("count");
        if (DataCach.taskMap.get(count + "") != null) {
            HashMap<String, Object> map = DataCach.taskMap.get(count + "");
            PdaNetInfo pni = (PdaNetInfo) map.get("data");
            detailTitleTextView.setText(pni.getBankName());

            //网点入库任务款箱
            List<PdaCashboxInfo> cashBoxInfoList = pni.getCashBoxInfoList();
            //网点出库任务款箱
            List<ExtractBoxs> extractBoxsList = pni.getExtractBoxsList();

            if (cashBoxInfoList != null && cashBoxInfoList.size() > 0) {

                for (PdaCashboxInfo pci : cashBoxInfoList) {

                    HashMap<String, Object> map1 = new HashMap<>();
                    map1.put("list_img", R.drawable.boxes_list_status_2);// 图像资源的ID
                    map1.put("list_title", pci.getBoxSn());

                    //记录该款箱是否已扫描  0:未扫描;1:已扫描
                    map1.put("status", "0");

                    DataCach.boxesMap.put(pci.getRfidNum(), map1);

                    if (DataCach.netType.equals("1")) {//网点入库item显示
                        boxesMap1.put(pci.getRfidNum(), map1);
                        listItem.add(map1);
                    }
                }
            } else if (extractBoxsList != null && extractBoxsList.size() > 0) {
                for (ExtractBoxs eb : extractBoxsList) {

                    HashMap<String, Object> map1 = new HashMap<>();
                    map1.put("list_img", R.drawable.boxes_list_status_2);// 图像资源的ID
                    map1.put("list_title", eb.getBoxSn());

                    //记录该款箱是否已扫描  0:未扫描;1:已扫描
                    map1.put("status", "0");

                    DataCach.boxesMap.put(eb.getRfidNum(), map1);

                    if (DataCach.netType.equals("0")) {//网点出库item显示
                        boxesMap1.put(eb.getRfidNum(), map1);
                        listItem.add(map1);
                    }
                }
            }
        }

        listItemAdapter.notifyDataSetChanged();
    }

    private void showInfoDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        //    设置Title的内容
        builder.setTitle("提示");
        //    设置Content来显示一个信息
        builder.setMessage(msg);
        //    设置一个NeutralButton
        builder.setNeutralButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //    展示Dialog
        builder.show();
    }


    /**
     * 判断交接人员是否全部存在
     *
     * @return
     */
    private String personIsScan() {
        String msg;
        if (person1TextView.getText().toString().isEmpty() ||
                person2TextView.getText().toString().isEmpty() ||
                person3TextView.getText().toString().isEmpty() ||
                person4TextView.getText().toString().isEmpty()
                ) {
            msg = "交接人员不全,请重新扫描";
        } else {
            msg = "true";
        }
        return msg;
    }

    /**
     * 判断款箱是否都正确
     *
     * @return
     */
    private String boxIsScan() {
        String msg = "";

        if (!boxesMap2.isEmpty()) {//如果有多出的款箱
            msg = "交接款箱有误,请重新扫描";
        } else {//如果没多出的,判断有没有漏的
            if (!boxesMap1.isEmpty()) {
                Iterator it = boxesMap1.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    HashMap<String, Object> map1 = (HashMap<String, Object>) boxesMap1.get(key);
                    if (map1.get("status").equals("0")) {
                        msg = "交接款箱有误,请重新扫描";
                        Log.d("status", "" + map1.get("status"));
                        break;
                    } else {
                        msg = "true";
                    }
                }
            } else {
                msg = "交接款箱有误,请重新扫描";
            }
        }
        return msg;
    }

    /**
     * 写入文件
     */

    private void writeFile() {

        //时间戳
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        String date = format.format(curDate);

        //开始保存数据到数据库
        DBManager db = new DBManager(context);
        //初始化实体类
        Recordnet recordnet = new Recordnet();

        //线路名称
        recordnet.setLineSn(pdaLoginMsg.getLineSn());

        //lineId
        recordnet.setLineId(pdaLoginMsg.getLineId());

        //日期（扫描时间）
        recordnet.setScanningDate(date);

        // 网点人员名称
        recordnet.setBankman(person2TextView.getText().toString());
        recordnet.setBankman2(person4TextView.getText().toString());

        //押运人员名称
        recordnet.setGuardman(person1TextView.getText().toString());
        recordnet.setGuardman2(person3TextView.getText().toString());

        //1:网点入库 ； 0：网点出库
        recordnet.setLineType(DataCach.netType);

        //判断款箱扫描状态
        String rightRfidNums = "";
        String missRfidNums = "";
        String errorRfidNums = "";
        if (!boxesMap2.isEmpty()) {
            Iterator it = boxesMap2.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                errorRfidNums += ";" + key;
            }
            if (errorRfidNums.length() > 0) {//多扫描的RFID
                errorRfidNums = errorRfidNums.substring(1);
                recordnet.setErrorRfidNumsSub(errorRfidNums);
            }
        }
        if (boxesMap1 != null && boxesMap1.size() > 0) {
            Iterator it = boxesMap1.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                HashMap<String, Object> map1 = (HashMap<String, Object>) boxesMap1.get(key);
                if (map1.get("status").equals("1")) {
                    rightRfidNums += ";" + key;
                } else {
                    missRfidNums += ";" + key;
                }
            }
            if (rightRfidNums.length() > 0) {//正确扫描的RFID
                rightRfidNums = rightRfidNums.substring(1);
                recordnet.setRightRfidNumsSub(rightRfidNums);
            }
            if (missRfidNums.length() > 0) {//未扫描的RFID
                missRfidNums = missRfidNums.substring(1);
                recordnet.setMissRfidNumsSub(missRfidNums);
            }
        }
        //0：扫描正确 ; 1：扫描错误
        if (rightRfidNums.length() > 0 && missRfidNums.length() == 0 && errorRfidNums.length() == 0) {
            recordnet.setScanStatus(Constants.NET_COMMIT_STATUS_RIGHT);
        } else {
            recordnet.setScanStatus(Constants.NET_COMMIT_STATUS_ERROR);
        }

        //备注信息
        recordnet.setNote(remarkEditView.getText().toString());

        // 得到跳转到该Activity的Intent对象
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        int count = bundle.getInt("count");
        if (DataCach.taskMap.get(count + "") != null) {
            HashMap<String, Object> map = DataCach.taskMap.get(count + "");
            map.put("list_img", R.drawable.task_1);// 图像资源的ID
            map.put("list_worktime", "已完成");
        }
        HashMap<String, Object> map = DataCach.taskMap.get(count + "");
        PdaNetInfo pdanetinfo = (PdaNetInfo) map.get("data");
        recordnet.setBankId(pdanetinfo.getBankId());
        Log.d("debug", pdanetinfo.getBankId());

        //网点人员ID
        try {
            recordnet.setBankmanId(netPersonInfo.getNetPersonId());
            recordnet.setBankmanId2(netPersonInfo2.getNetPersonId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //押运人员ID
        try {
            recordnet.setGuardmanId(guardManInfo.getGuardManId());
            recordnet.setGuardmanId2(guardManInfo2.getGuardManId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //存入数据库
        db.addRecordnet(recordnet);
        //取当前流水表最大值
        int maxId = db.queryMaxRecordNet();
        //0:正确入库   1：错误入库  2：遗漏
        if (boxesMap1 != null && boxesMap1.size() > 0) {
            Iterator it2 = boxesMap1.keySet().iterator();
            while (it2.hasNext()) {
                Waternet net = new Waternet();
                String key2 = (String) it2.next();
                HashMap<String, Object> map1 = (HashMap<String, Object>) boxesMap1.get(key2);
                if (map1.get("status").equals("1")) {
                    net.setStatus("2");
                } else if (map1.get("status").equals("0")) {
                    net.setStatus("1");
                }
                String title = map.get("list_title").toString();
                net.setBoxId(key2);
                net.setBoxSn(title);
                net.setScanningType((DataCach.netType));
                net.setBankId(pdanetinfo.getBankId());
                net.setScanningDate(date);
                net.setScanningNetid(maxId);
                db.addWaternet(net);
            }
        }
        if (boxesMap2 != null && boxesMap2.size() > 0) {
            Iterator it2 = boxesMap1.keySet().iterator();
            while (it2.hasNext()) {
                Waternet net = new Waternet();
                String key2 = (String) it2.next();
                HashMap<String, Object> map2 = (HashMap<String, Object>) boxesMap1.get(key2);
                net.setStatus("1");
                String title = map.get("list_title").toString();
                net.setBoxId(key2);
                net.setBoxSn(title);
                net.setScanningType((DataCach.netType));
                net.setBankId(pdanetinfo.getBankId());
                Log.d("debug", pdanetinfo.getBankId());
                net.setScanningDate(date);
                net.setScanningNetid(maxId);
                db.addWaternet(net);
            }
        }

        switch (DataCach.netType) {
            case NET_COMMIT_TYPE_IN: {
                //生成 NETIN.txt 文件
                try {
                    if (guardManInfo.getGuardManId() != null &&
                            netPersonInfo.getNetPersonId() != null &&
                            guardManInfo2.getGuardManId() != null &&
                            netPersonInfo2.getNetPersonId() != null) {
                        /**将对象转化为json*/
                        Gson gson = new Gson();
                        String par = gson.toJson(recordnet);
                        /** 获取当前时间，将json写入文件*/
                        String date1 = FileUtil.getDate();
                        FileUtil.writeString(FILE_PATH + FILE_NAME_IN + date1 + FILE_FORMAT, par, "GBK");
                        {
                            TaskInfo tk = new TaskInfo();
                            tk.setBankID(recordnet.getBankId());
                            tk.setNetType("1");
                            tk.setStatus("0");
                            tk.setTime(date1);
                            db.addTaskInfo(tk);
                        }
                        commitNoButton.setEnabled(false);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case NET_COMMIT_TYPE_OUT: {
                //生成 NETOUT.txt 文件
                try {
                    if (guardManInfo.getGuardManId() != null &&
                            netPersonInfo.getNetPersonId() != null &&
                            guardManInfo2.getGuardManId() != null &&
                            netPersonInfo2.getNetPersonId() != null) {
                        /**将对象转化为json*/
                        Gson gson = new Gson();
                        String par = gson.toJson(recordnet);
                        /** 获取当前时间，将json写入文件*/
                        String date1 = FileUtil.getDate();
                        FileUtil.writeString(FILE_PATH + FILE_NAME_OUT + date1 + FILE_FORMAT, par, "GBK");
                        {
                            TaskInfo tk = new TaskInfo();
                            tk.setBankID(recordnet.getBankId());
                            tk.setNetType("0");
                            tk.setStatus("0");
                            tk.setTime(date1);
                            db.addTaskInfo(tk);
                        }
                        commitNoButton.setEnabled(false);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hideWaitDialog();
                break;
            }
        }
    }

    /**
     * 判断文件是否生成
     *
     * @return
     */
    private String fileIsWrite() {
        String msg = "";
        String date = FileUtil.getDate();
        switch (DataCach.netType) {

            case NET_COMMIT_TYPE_OUT:
                if (FileUtil.isExist(FILE_PATH + FILE_NAME_OUT + date + FILE_FORMAT)) {
                    msg = "true";
                    FileUtil.makeFileAvailable(context, FILE_PATH + FILE_NAME_OUT + date + FILE_FORMAT);
                } else {
                    msg = "文件生成有误，请重新交接";
                }
                break;
            case NET_COMMIT_TYPE_IN:
                if (FileUtil.isExist(FILE_PATH + FILE_NAME_IN + date + FILE_FORMAT)) {
                    msg = "true";
                    FileUtil.makeFileAvailable(context, FILE_PATH + FILE_NAME_IN + date + FILE_FORMAT);
                } else {
                    msg = "文件生成有误，请重新交接";
                }
                break;
        }
        return msg;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (startDeviceButton.getText().equals("停止扫描")) {
                Toast.makeText(context, "请先停止扫描", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                UhfGetData.CloseUhf();
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showWaitDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.show();
    }

    private void hideWaitDialog() {
        if (pd != null) {
            pd.cancel();
        }
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
                            bundle.putString("rfid",epcStr);
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
