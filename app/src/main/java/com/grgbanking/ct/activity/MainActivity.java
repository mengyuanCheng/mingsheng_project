package com.grgbanking.ct.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.Extract;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.database.NetInfo;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.database.PersonTableHelper;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.entity.TaskInfo;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.utils.FileUtil;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.grgbanking.ct.activity.Constants.FILE_FORMAT;
import static com.grgbanking.ct.activity.Constants.FILE_NAME_IN;
import static com.grgbanking.ct.activity.Constants.FILE_NAME_OUT;
import static com.grgbanking.ct.activity.Constants.FILE_PATH;
import static com.grgbanking.ct.cach.DataCach.netType;
import static com.grgbanking.ct.utils.FileUtil.ConversionDate;

public class MainActivity extends Activity {
    private PopupMenu popupMenu;
    private Menu menu;
    private ListView listView;
    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>> listItem;
    private Person person = null;
    private TextView mainTitle;
    private Button mainBackButton = null;
    private Context context;
    private PdaLoginMsg pdaLoginMsg;
    private ProgressDialog pd = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);


        popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
        menu = popupMenu.getMenu();

        try {
            //接收数据
            pdaLoginMsg = (PdaLoginMsg) getIntent().getSerializableExtra("pdaLoginMsg");
            //放入缓存
            DataCach.setPdaLoginMsg(pdaLoginMsg);

            Map<String, String> allPdaBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
            Log.d("allbox", "" + allPdaBoxsMap);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }


        // 通过XML文件添加菜单项
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popupmenu, menu);


        mainTitle = (TextView) findViewById(R.id.main_title_view);
        listView = (ListView) findViewById(R.id.ListView01);
        //		saomiaoImageView=(ImageView) findViewById(R.id.saosao_button);
        //		saomiaoImageView.setOnClickListener(saomiaoButtonclick);
        mainBackButton = (Button) findViewById(R.id.main_btn_back);

        String netType = DataCach.netType;

        if (netType.equals(Constants.NET_COMMIT_TYPE_IN)) {
            mainTitle.setText("网点入库任务列表");
        } else {
            mainTitle.setText("网点出库任务列表");
        }

        // 生成动态数组，加入数据
        listItem = new ArrayList<>();
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(
                this,
                listItem,
                R.layout.main_list_item,
                new String[]{"list_img", "list_title", "list_position", "list_worktime"},
                new int[]{R.id.list_img, R.id.list_title, R.id.list_position, R.id.list_worktime});
        // 添加并且显示
        listView.setAdapter(listItemAdapter);

        person = PersonTableHelper.queryEntity(this);
        //		String userId =person.getUser_id();
        //		String name = person.getUser_name();

        showWaitDialog("正在加载中...");

        hideWaitDialog();


        // 添加点击
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                Map map = listItem.get(arg2);
                String bankName = (String) map.get("list_title");
                String bankId = queryBankId(bankName);
                String netType = DataCach.netType;
                boolean flag = getTaskStatus(netType, bankId);
                if (netType == "1" && flag == false) {//网点入库未完成
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("count", arg2);
                    Log.d("", "" + arg2);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                } else if (netType == "0" && flag == false) {//网点出库未完成
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("count", arg2);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                } else {
                    showInfoDialog("该任务已完成");
                }
            }
        });


        // 点击返回按钮操作内容
        mainBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //                清空缓存
                finish();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item_back:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                return false;
            }
        });
    }

    private void showInfoDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //    设置Title的内容
        builder.setTitle("提示");
        //    设置Content来显示一个信息
        builder.setMessage(msg);
        //    设置一个NeutralButton
        builder.setNeutralButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> params = new ArrayList<>();
                String date = FileUtil.getDate();
                String mResult="";
                if (mainTitle.getText().toString().equals("网点入库任务列表")){
                    mResult =FileUtil.readTXT(FILE_PATH + FILE_NAME_IN + date + FILE_FORMAT);
                }else if (mainTitle.getText().toString().equals("网点出库任务列表")){
                    mResult =FileUtil.readTXT(FILE_PATH + FILE_NAME_OUT + date + FILE_FORMAT);
                }
                params.add(new BasicNameValuePair("content", mResult));
                new HttpPostUtils(Constants.URL_PDA_UPLOAD, params, new UICallBackDao() {
                    @Override
                    public void callBack(ResultInfo resultInfo) {
                        Toast.makeText(getApplicationContext(),resultInfo.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }).execute();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //    展示Dialog
        builder.show();
    }

    public void popupmenu(View v) {
        popupMenu.show();
    }

    @Override
    protected void onStart() {
        loadLoginMessageCach();
        super.onStart();
    }

    private void loadLoginMessageCach() {
        listItem.clear();

        synchronized (DataCach.taskMap) {
            if (netType.equals("1")) {//网点入库
                if (pdaLoginMsg != null) {
                    List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
                    if (netInfoList != null && netInfoList.size() > 0) {
                        for (int i = 0; i < netInfoList.size(); i++) {
                            PdaNetInfo pni = netInfoList.get(i);
                            String bankId = pni.getBankId();
                            String bankName = pni.getBankName();
                            String netstatus = pni.getNetTaskStatus();

                            List<PdaNetPersonInfo> personList = pni.getNetPersonInfoList();
                            List<PdaCashboxInfo> cashBoxList = pni.getCashBoxInfoList();

                            int count = 0;
                            for (PdaCashboxInfo cashinfo : cashBoxList) {
                                if (cashinfo.getBankId().equals(bankId)) {
                                    count++;
                                }
                            }
                            HashMap<String, Object> map1 = new HashMap<>();
                            //判断网点是否已完成
                            if (getTaskStatus("1", pni.getBankId()) == true) {
                                map1.put("list_img", R.drawable.task_1);// 图像资源的ID
                                map1.put("list_title", pni.getBankName());
                                map1.put("list_position", count);
                                map1.put("list_worktime", "已完成");
                                //建立一个新的对象
                                PdaNetInfo net = new PdaNetInfo();
                                //储存新的款箱信息
                                List<PdaCashboxInfo> cashList = new ArrayList<PdaCashboxInfo>();
                                //获取当前所有的款箱
                                List<PdaCashboxInfo> cashBoxList2 = pni.getCashBoxInfoList();
                                for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                                    if (cashinfo2.getBankId().equals(bankId)) {
                                        cashList.add(cashinfo2);
                                    }
                                }
                                net.setBankId(bankId);
                                net.setNetTaskStatus(netstatus);
                                net.setBankName(bankName);
                                net.setCashBoxInfoList(cashList);
                                net.setNetPersonInfoList(personList);
                                map1.put("data", net);
                            } else {
                                map1.put("list_img", R.drawable.task_2);// 图像资源的ID
                                map1.put("list_title", pni.getBankName());
                                map1.put("list_position", count);
                                map1.put("list_worktime", "未完成");
                                //建立一个新的对象
                                PdaNetInfo net = new PdaNetInfo();
                                //存储新的款箱信息
                                List<PdaCashboxInfo> cashList = new ArrayList<>();
                                //获取当前所有的款箱
                                List<PdaCashboxInfo> cashBoxList2 = pni.getCashBoxInfoList();
                                for (PdaCashboxInfo cashinfo2 : cashBoxList2) {
                                    if (cashinfo2.getBankId().equals(bankId)) {
                                        cashList.add(cashinfo2);
                                    }
                                }
                                net.setBankId(bankId);
                                net.setNetTaskStatus(netstatus);
                                net.setBankName(bankName);
                                net.setCashBoxInfoList(cashList);
                                net.setNetPersonInfoList(personList);
                                map1.put("data", net);
                            }
                            DataCach.taskMap.put("" + i, map1);
                            listItem.add(map1);
                            Log.d("入库任务", "" + count + "---" + listItem);
                        }
                    }
                }
            } else {//网点出库
                /**判断网点完成状态 */
                if (pdaLoginMsg != null) {
                    List<Extract> extractList = pdaLoginMsg.getExtracts();
                    for (int i = 0; i < extractList.size(); i++) {
                        Extract et = extractList.get(i);
                        String bankId = et.getBankId();
                        String bankName = et.getBankName();
                        String netstatus = et.getNetTaskStatus();
                        List<PdaNetPersonInfo> personList = et.getNetPersonInfoList();
                        Map<String, String> ExtractBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
                        List<PdaCashboxInfo> pdaCashboxInfolist = new ArrayList<PdaCashboxInfo>();

                        DBManager db = new DBManager(context);
                        List<ExtractBoxs> eb = db.queryExtractBoxs();
                        /**统计每个网点下款箱的个数*/
                        int count = 0;
                        for (ExtractBoxs extractBox : eb) {
                            if (extractBox.getBankId().equals(bankId)) {
                                count++;
                            }
                        }
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        //判断网点是否已完成
                        if (getTaskStatus("0", et.getBankId()) == true) {
                            map.put("list_img", R.drawable.task_1);// 图像资源的ID
                            map.put("list_title", et.getBankName());
                            map.put("list_position", count);
                            map.put("list_worktime", "已完成");
                            //建立一个新的對象
                            PdaNetInfo net = new PdaNetInfo();
                            //存储新的款箱信息
                            List<ExtractBoxs> EBoxsList = new ArrayList<ExtractBoxs>();
                            //获取当前所有的款箱
                            List<ExtractBoxs> EBoxsList2 = eb;
                            for (ExtractBoxs ex : EBoxsList2) {
                                if (ex.getBankId().equals(bankId)) {
                                    EBoxsList.add(ex);
                                }
                            }

                            net.setBankId(bankId);
                            net.setNetTaskStatus(netstatus);
                            net.setBankName(bankName);
                            net.setExtractBoxsList(EBoxsList);
                            net.setNetPersonInfoList(personList);
                            map.put("data", net);

                        } else {
                            map.put("list_img", R.drawable.task_2);// 图像资源的ID
                            map.put("list_title", et.getBankName());
                            map.put("list_position", count);
                            map.put("list_worktime", "未完成");
                            //建立一个新的對象
                            PdaNetInfo net = new PdaNetInfo();
                            //存储新的款箱信息
                            List<ExtractBoxs> EBoxsList = new ArrayList<ExtractBoxs>();
                            //获取当前所有的款箱
                            List<ExtractBoxs> EBoxsList2 = eb;
                            for (ExtractBoxs ex : EBoxsList2) {
                                if (ex.getBankId().equals(bankId)) {
                                    EBoxsList.add(ex);
                                }
                            }
                            net.setBankId(bankId);
                            net.setNetTaskStatus(netstatus);
                            net.setBankName(bankName);
                            net.setExtractBoxsList(EBoxsList);
                            net.setNetPersonInfoList(personList);
                            map.put("data", net);
                        }
                        DataCach.taskMap.put("" + i, map);
                        listItem.add(map);
                        Log.d("出库任务", "" + count + "---" + listItem);
                    }
                }
            }
            listItemAdapter.notifyDataSetChanged();
        }
    }
    //    }

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("onKeyDown", "" + keyCode);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断网点的完成状态
     *
     * @param netType 1是入库 ,0是出库
     * @return true是已完成 false是未完成
     */
    private boolean getTaskStatus(String netType, String bankId) {
        boolean flag = false;
        List<TaskInfo> tkList = new ArrayList<>();
        DBManager db = new DBManager(getApplicationContext());
        try {
            tkList = db.queryTaskInfo(bankId);
            for (int i = 0; i < tkList.size(); i++) {
                FileUtil f = new FileUtil();
                boolean b = FileUtil.areSameDay(ConversionDate(tkList.get(i).getTime()),
                        new Date(System.currentTimeMillis()));
                if (b) {
                    flag = tkList.get(i).getNetType().equals(netType);
                } else {
                    return flag;
                }
            }
            return flag;
        } catch (Exception e) {
            return flag;
        }

    }

    /**
     * 根据网点名称查询网点ID
     *
     * @param bankName
     * @return bankID
     */
    public String queryBankId(String bankName) {
        Log.d("debug", bankName);
        DBManager db = new DBManager(context);
        String id = null;
        List<NetInfo> netinfoList = db.queryNetInfo();
        for (NetInfo netinfo : netinfoList) {
            if (netinfo.getBankName().equals(bankName)) {
                id = netinfo.getBankId();
            }
        }
        //        Log.d("debug", id);
        return id;
    }

}
