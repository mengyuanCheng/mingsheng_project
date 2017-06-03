package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.MApplication;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.entity.PeiXiangInfo;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.utils.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.grgbanking.ct.cach.DataCach.loginUser;
import static com.grgbanking.ct.rfid.UfhData.timer;

/**
 * @author ：     cmy
 * @version :     2016/11/9.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */
// FIXME: 2017/2/16 停止扫描后，再次扫描时不允许重复的RFID被添加
// TODO: 2017/4/7 首次运行是检查数据库是否存在当天的数据。if{存在,从数据库中取出数据并展示},if{不存在,扫描数据}
// TODO: 2017/4/7 最后,将新增的数据保存到数据库中。
public class QcodeActivity extends Activity {
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
    //用来存放更新后的数据
    private HashMap<String, List> reFreshDataMap = new HashMap();
    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>> listitem;
    private Context context;
    private Button BT_scan;
    private Button BT_stop;
    private ListView LV_RFID;
    private Map<String, Integer> data;
    private boolean Scanflag = false;
    private boolean isCanceled = true;
    private Button BT_upDate;
    private ArrayList<String> QRcodelist;
    private String rfidNum;
    private ProgressDialog pd = null;
    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        //如果收到了回调intent，则刷新数据
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshQCode")) {
                rfidNum = intent.getExtras().getString("rfidNum");
                QRcodelist = intent.getExtras().getStringArrayList("QRcodelist");
                Log.d("onReceive: ", "" + QRcodelist);
                Log.d("onReceive: ", rfidNum);
                if (QRcodelist != null) {
                    reFreshDataMap.put(rfidNum, QRcodelist);
                } else {
                    Toast.makeText(context, "未检测到任何二维码数据，请重新扫描!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_activity);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshQCode");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);

        findViewById();
        onClickListener();

        listitem = new ArrayList<>();

        listItemAdapter = new SimpleAdapter(this,
                listitem,
                R.layout.qcode_list,
                new String[]{"list_rfid", "list_button",
                        "list_qrcode1", "list_qrcode2", "list_qrcode3",
                        "list_qrcode4", "list_qrcode5"},
                new int[]{R.id.qcode_list_tv, R.id.qcode_list_bt,
                        R.id.qcode_tv_1, R.id.qcode_tv_2, R.id.qcode_tv_3,
                        R.id.qcode_tv_4, R.id.qcode_tv_5}) {
            //添加监听器
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final int p = position;
                final View view = super.getView(position, convertView, parent);
                Button useBtn = (Button) view.findViewById(R.id.qcode_list_bt);
                final TextView rfid = (TextView) view.findViewById(R.id.qcode_list_tv);
                useBtn.setOnClickListener(new View.OnClickListener() {
                    /*传递rfidNum的值*/
                    @Override
                    public void onClick(View v) {
                        Log.d("onclick", "" + p + "===" + rfid.getText());
                        Intent intent = new Intent(QcodeActivity.this, ScanActivity.class);
                        intent.putExtra("rfidNum", rfid.getText());
                        startActivity(intent);
                    }
                });
                return super.getView(position, convertView, parent);
            }
        };
        LV_RFID.setAdapter(listItemAdapter);
    }

    /**
     * 重新回到本Activity后刷新Listview的数据
     */
    @Override
    protected void onResume() {
        for (int i = 0; i < listitem.size(); i++) {
            HashMap map = listitem.get(i);
            if (rfidNum != null && map.get("list_rfid").equals(rfidNum)) {
                List list = reFreshDataMap.get(rfidNum);
                for (int j = 0; j < list.size(); j++) {
                    String num = "" + (j + 1);
                    String QR_Code = (String) list.get(j);
                    map.put("list_button", "点击修改二维码");
                    map.put("list_qrcode" + num, QR_Code);
                }
                Log.d("map", "" + map);
                listitem.add(map);
            }
            listitem.remove(i);
        }
        listItemAdapter.notifyDataSetChanged();
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }


    private void findViewById() {
        BT_scan = (Button) findViewById(R.id.qcode_bt_scan);
        BT_stop = (Button) findViewById(R.id.qcode_bt_stop);
        LV_RFID = (ListView) findViewById(R.id.list_qcdoe);
        BT_upDate = (Button) findViewById(R.id.qcode_update_bt);
    }

    private void onClickListener() {
        BT_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connDevices();
                startDevices();
                updataItem();
            }
        });

        BT_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  2016/10/25 停止扫描
                cancelScan();
                UfhData.Set_sound(false);
            }
        });
        BT_upDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showWaitDialog("正在上传数据信息");
//                ResultInfo ri = new ResultInfo();
//                Map dataMap = new HashMap();
//                dataMap.put("GUARD_CODE", loginUser.getLoginName());
//                Set keyset = reFreshDataMap.keySet();
//                ArrayList datalist = new ArrayList();
//
//                dataMap.put("BOXRFIDQR", datalist);
//                StringBuffer sb = new StringBuffer();
//                sb.append("{").append("GUARD_CODE:").append(loginUser.getLoginName()).append(",BOXRFIDQR: [");
//
//                Iterator iterator = keyset.iterator();
//                while (iterator.hasNext()) {
//                    Object key = iterator.next();
//                    Object value = reFreshDataMap.get(key);
//                    String tmp = value.toString();
//                    Log.d("====tmp1==", "" + tmp);
//                    String tmp2 = tmp.replaceAll(", ", "|");
//                    Log.d("====tmp2==", "" + tmp2);
//                    String tmp3 = tmp2.substring(1, tmp2.length() - 1);
//                    Log.d("tmp3===", "" + tmp3);
//
//                    sb.append("{BOXRFID:").append(key).append(",QRCODE:").append(tmp3).append("},");
//                }
//
//                String tmp = sb.toString().substring(0, sb.toString().length() - 1) + "]}";
//                ri.setCode(ri.CODE_SUCCESS);
//                ri.setText(tmp);
//                Log.d("====tmp==", "" + tmp);
//
//                /* 将数据写入SD卡*/
//                /**/
//                String date = FileUtil.getDate();
//                String addr = FileUtil.createIfNotExist(FILE_PATH + FILE_NAME + date + FILE_FORMAT);
//                byte[] writebytes = new byte[0];
//                FileUtil.strToByteArray(tmp);
//                FileUtil.writeBytes(addr, writebytes);
//                FileUtil.writeString(addr, tmp, "utf-8");
//
//                Toast.makeText(QcodeActivity.this, "数据保存成功！", Toast.LENGTH_SHORT);
//
//                hideWaitDialog();


                saveInDatabase();
            }
        });
    }

    /**
     * 将扫描上来的款箱及其二维码保存到数据库中
     */
    private void saveInDatabase() {
        Set keyset = reFreshDataMap.keySet();
        ArrayList datalist = new ArrayList();
        List<PeiXiangInfo> peiXiangInfos = new ArrayList<>();

        Iterator iterator = keyset.iterator();
        while (iterator.hasNext()) {
            PeiXiangInfo px = new PeiXiangInfo();
            Object key = iterator.next();
            String s = (String) key;
            Object value = reFreshDataMap.get(key);
            datalist = (ArrayList) value;

            String QR_codeAll = null;
            if (datalist!=null&&datalist.size()>0){
                for (int i = 0; i <datalist.size() ; i++) {
                    String QR_code = (String) datalist.get(i);
                    if (QR_codeAll==null){
                        QR_codeAll = QR_code;
                    }else {
                        QR_codeAll = QR_codeAll+"|"+QR_code;
                    }
                }
                px.setBoxNum(s);
                px.setQR_codelist(datalist);
                px.setQR_code(QR_codeAll);
                px.setScanningDate(FileUtil.getDate());
                peiXiangInfos.add(px);
            }
        }
        DBManager db = new DBManager(context);
        db.addPeiXiang(peiXiangInfos);

//        DBManager dbdb = new DBManager(context);
//        dbdb.queryPeiXiang();
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
    private Map<String, String> startDevices() {
        StringBuffer sb = new StringBuffer();
        HashMap<String, String> keymap = new HashMap<>();
        UfhData.Set_sound(true);
        UfhData.SoundFlag = true;
        if (!UfhData.isDeviceOpen()) {
            Toast.makeText(this, R.string.detail_title, Toast.LENGTH_LONG).show();
            return null;
        }
        UfhData.read6c();
        data = UfhData.scanResult6c;
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next().toString();
            keymap.put(s, s);
            Log.d("!!!", s);
        }

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
                    Scanflag = false;
                }
            }, 0, 10);
        } else {

        }
        UfhData.Set_sound(true);
        return keymap;

    }

    /**
     * 取消扫描
     */
    private void cancelScan() {
        isCanceled = true;
        //mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
        if (timer != null) {
            timer.cancel();
            timer = null;
            UfhData.scanResult6c.clear();
        }
    }

    /**
     * 更新 item
     */
    private void updataItem() {
        Map<String, String> keymap = startDevices();
        Iterator it = keymap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String rfid = (String) entry.getKey();
            HashMap<String, Object> map = new HashMap<>();
            map.put("list_rfid", rfid);
            map.put("list_button", "点击添加二维码");
            listitem.add(map);
            reFreshDataMap.put(rfid, null);
        }
        listItemAdapter.notifyDataSetChanged();
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

}
