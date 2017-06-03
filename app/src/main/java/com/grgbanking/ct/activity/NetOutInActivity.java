package com.grgbanking.ct.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.CashBox;
import com.grgbanking.ct.database.ConvoyMan;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.Extract;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.database.LoginMan;
import com.grgbanking.ct.database.NetInfo;
import com.grgbanking.ct.database.NetMan;
import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginManInfo;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.utils.FileUtil;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaGuardManInfo;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;
import com.hlct.framework.business.message.entity.PdaUserInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetOutInActivity extends Activity {

    /**
     * 定义文件名
     */
    private static final String FILE_NAME = "NETOUT";
    /**
     * 定义文件格式
     */
    private static final String FILE_FORMAT = ".txt";
    /**
     * 定义文件路径
     */
    private static final String FILE_PATH = "/sdcard/Download/";
    private static final String TAG = "NetOutInActivity";
    static PdaGuardManInfo guardManInfo = null;//保存押运人员
    static ConvoyManInfo convoyManinfo = null;//保存押运人员
    /**
     * 定义当前时间
     */
    String date = FileUtil.getDate();
    Button sysOutButton;
    Button netInButton = null;
    Button netOutButton = null;
    Button downButton = null;
    Button upButton = null;
    OnClickListener saomiaoButtonclick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            //清空缓存
            DataCach.clearAllDataCach();

            Intent intent = new Intent();
            intent.setClass(NetOutInActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private List<ConvoyManInfo> convoyManInfo = new ArrayList<ConvoyManInfo>();
    private List<NetInfo> netInfos = new ArrayList<NetInfo>();
    private List<PdaUserInfo> pdaUserInfos = new ArrayList<PdaUserInfo>();
    private List<PdaCashboxInfo> pdaCashboxInfos = new ArrayList<PdaCashboxInfo>();
    private List<CashBox> cashBoxes = new ArrayList<CashBox>();
    private Context context;
    private ProgressDialog pd = null;
    private Toast toast = null;
    private long mExitTime;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        //		startService(new Intent(context, GrgbankService.class));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.netoutin);


        sysOutButton = (Button) findViewById(R.id.net_sysout_view);
        sysOutButton.setOnClickListener(saomiaoButtonclick);

        netInButton = (Button) findViewById(R.id.peixiang_button);
        netOutButton = (Button) findViewById(R.id.net_out_button);


        //初始化缓存，将缓存清空
        DataCach.clearAllDataCach();

        netInButton.setOnClickListener(new OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               showWaitDialog("正在加载网点入库信息...");
                                               LoginUser loginUser = DataCach.loginUser;
                                               //判断是出库还是入库
                                               DataCach.netType = Constants.NET_COMMIT_TYPE_IN;

                                               List<NameValuePair> params = new ArrayList<>();
                                               params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                                               params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));
                                               //访问数据库
                                               DBManager db = new DBManager(context);
                                               PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();
                                               //取出 押运人员
                                               ArrayList<ConvoyMan> manList = (ArrayList<ConvoyMan>) db.queryConvoyMan();
                                               List<PdaGuardManInfo> pdaGuarManInfoList = new ArrayList<>();
                                               //存入pdaLoginMessage
                                               if (manList != null && manList.size() > 0) {
                                                   for (ConvoyMan cMan : manList) {
                                                       PdaGuardManInfo manInfo = new PdaGuardManInfo();
                                                       manInfo.setGuardManId(cMan.getGuardManId());
                                                       manInfo.setGuardManName(cMan.getGuardManName());
                                                       manInfo.setGuardManRFID(cMan.getGuardManRFID());
                                                       pdaGuarManInfoList.add(manInfo);
                                                   }
                                                   pdaLoginMsg.setPdaGuardManInfo(pdaGuarManInfoList);
                                               }
                                               //取出网点人员
                                               List<NetMan> netMen = db.queryNetMan();
                                               List<PdaNetPersonInfo> pdaNetPersonInfoList = new ArrayList<>();
                                               if (netMen != null && netMen.size() > 0) {
                                                   for (NetMan info : netMen) {
                                                       PdaNetPersonInfo pdaNetPersonInfo = new PdaNetPersonInfo();
                                                       pdaNetPersonInfo.setNetPersonId(info.getNetPersonId());
                                                       pdaNetPersonInfo.setNetPersonName(info.getNetPersonName());
                                                       pdaNetPersonInfo.setNetPersonRFID(info.getNetPersonRFID());
                                                       pdaNetPersonInfoList.add(pdaNetPersonInfo);
                                                   }
                                               }
                                               //取出所有款箱
                                               Map<String, String> pdaCashboxInfoMap = new HashMap<>();
                                               List<CashBox> cashBoxes = db.queryCashBox();
                                               List<PdaCashboxInfo> pdaCashboxInfoList = new ArrayList<>();
                                               if (cashBoxes != null && cashBoxes.size() > 0) {
                                                   for (CashBox info : cashBoxes) {
                                                       PdaCashboxInfo pdaCashboxInfo = new PdaCashboxInfo();
                                                       pdaCashboxInfo.setBankId(info.getBankId());
                                                       pdaCashboxInfo.setBoxSn(info.getBoxSn());
                                                       pdaCashboxInfo.setRfidNum(info.getRfidNum());
                                                       pdaCashboxInfoList.add(pdaCashboxInfo);
                                                       pdaCashboxInfoMap.put(info.getRfidNum(), info.getBoxSn());
                                                   }
                                               }

                                               //取出网点信息
                                               List<NetInfo> netInfo = db.queryNetInfo();
                                               List<PdaNetInfo> pdaNetInfoList = new ArrayList<>();
                                               //存入pdaLoginMessage
                                               if (netInfo != null && netInfo.size() > 0) {
                                                   for (NetInfo info : netInfo) {
                                                       PdaNetInfo pdaNetInfo = new PdaNetInfo();
                                                       pdaNetInfo.setBankId(info.getBankId());
                                                       pdaNetInfo.setNetTaskStatus(info.getNetTaskStatus());
                                                       pdaNetInfo.setBankName(info.getBankName());
                                                       pdaNetInfo.setLineId(info.getLineId());
                                                       pdaNetInfo.setFlag(info.getFlag());
                                                       pdaNetInfo.setCashBoxInfoList(pdaCashboxInfoList);
                                                       pdaNetInfo.setNetPersonInfoList(pdaNetPersonInfoList);
                                                       pdaNetInfoList.add(pdaNetInfo);
                                                       pdaLoginMsg.setLineId(info.getLineId());
                                                       pdaLoginMsg.setLineSn(info.getLineSn());
                                                   }
                                               }
                                               pdaLoginMsg.setNetInfoList(pdaNetInfoList);
                                               pdaLoginMsg.setAllPdaBoxsMap(pdaCashboxInfoMap);


                                               //取出登录人员

                                               List<LoginMan> loginMan = db.queryLoginMan();
                                               List<PdaLoginManInfo> pdaLoginManInfoList = new ArrayList<>();
                                               if (loginMan != null && loginMan.size() > 0) {
                                                   for (LoginMan info : loginMan) {
                                                       PdaLoginManInfo pdaLoginManInfo = new PdaLoginManInfo();
                                                       pdaLoginManInfo.setLogin_name(info.getLogin_name());
                                                       pdaLoginManInfo.setLoginId(info.getLoginId());
                                                       pdaLoginManInfo.setPassword(info.getPassword());
                                                       pdaLoginManInfo.setLine(info.getLine());
                                                       pdaLoginManInfo.setFlag(info.getFlag());
                                                       pdaLoginManInfoList.add(pdaLoginManInfo);
                                                   }
                                                   pdaLoginMsg.setPdaLoginManInfo(pdaLoginManInfoList);
                                               }


                                               // 传递 pdaLoginMsg
                                               hideWaitDialog();
                                               Intent intent = new Intent(NetOutInActivity.this, MainActivity.class);
                                               intent.putExtra("pdaLoginMsg", pdaLoginMsg);
                                               startActivity(intent);
                                           }
                                       }

        );

        netOutButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showWaitDialog("正在加载网点出库信息");
                                                LoginUser loginUser = DataCach.loginUser;
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
                                                params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));
                                                //判断是出库还是入库
                                                DataCach.netType = Constants.NET_COMMIT_TYPE_OUT;
                                                //访问数据库
                                                DBManager db = new DBManager(context);
                                                PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();
                                                try {
                                                    //取出所有款箱
                                                    ArrayList<ExtractBoxs> ExtractBoxsList = (ArrayList<ExtractBoxs>) db.queryExtractBoxs();
                                                    Map<String, String> ExtractBoxsmap = new HashMap<String, String>();
                                                    for (ExtractBoxs ExtractBox : ExtractBoxsList) {
                                                        ExtractBox.getRfidNum();
                                                        ExtractBox.getBankId();
                                                        ExtractBox.getBoxSn();
                                                        ExtractBoxsmap.put(ExtractBox.getRfidNum(), ExtractBox.getBoxSn() + "&" + ExtractBox.getBankId());
                                                    }
                                                    pdaLoginMsg.setAllPdaBoxsMap(ExtractBoxsmap);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }

                                                try {
                                                    //取出网点人员
                                                    List<NetMan> netMen = db.queryNetMan();
                                                    List<PdaNetPersonInfo> pdaNetPersonInfoList = new ArrayList<PdaNetPersonInfo>();
                                                    if (netMen != null && netMen.size() > 0) {
                                                        for (NetMan info : netMen) {
                                                            PdaNetPersonInfo pdaNetPersonInfo = new PdaNetPersonInfo();
                                                            pdaNetPersonInfo.setNetPersonId(info.getNetPersonId());
                                                            pdaNetPersonInfo.setNetPersonName(info.getNetPersonName());
                                                            pdaNetPersonInfo.setNetPersonRFID(info.getNetPersonRFID());
                                                            pdaNetPersonInfoList.add(pdaNetPersonInfo);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }


                                                //取出网点信息
                                                List<NetInfo> netInfo = db.queryNetInfo();
                                                if (netInfo != null && netInfo.size() > 0) {
                                                    for (NetInfo info : netInfo) {
                                                        PdaNetInfo pdaNetInfo = new PdaNetInfo();
                                                        pdaLoginMsg.setLineId(info.getLineId());
                                                        pdaLoginMsg.setLineSn(info.getLineSn());
                                                    }
                                                }

                                                try {
                                                    //取出所有网点信息
                                                    List<PdaNetPersonInfo> netPersonInfoList = new ArrayList<PdaNetPersonInfo>();
                                                    List<Extract> extractList = db.queryExtract();
                                                    for (Extract info : extractList) {
                                                        List<NetMan> netMens = db.queryNetManByBankId(info.getBankId());
                                                        for (NetMan net : netMens) {
                                                            PdaNetPersonInfo pdaNetPersonInfo = new PdaNetPersonInfo();
                                                            pdaNetPersonInfo.setNetPersonId(net.getNetPersonId());
                                                            pdaNetPersonInfo.setNetPersonName(net.getNetPersonName());
                                                            pdaNetPersonInfo.setNetPersonRFID(net.getNetPersonRFID());
                                                            netPersonInfoList.add(pdaNetPersonInfo);
                                                        }
                                                        info.setNetPersonInfoList(netPersonInfoList);
                                                    }
                                                    pdaLoginMsg.setExtracts(extractList);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }


                                                try {
                                                    //取出 押运人员
                                                    ArrayList<ConvoyMan> manList = (ArrayList<ConvoyMan>) db.queryConvoyMan();
                                                    List<PdaGuardManInfo> pdaGuarManInfoList = new ArrayList<PdaGuardManInfo>();
                                                    //存入pdaLoginMessage
                                                    if (manList != null && manList.size() > 0) {
                                                        for (ConvoyMan cMan : manList) {
                                                            PdaGuardManInfo manInfo = new PdaGuardManInfo();
                                                            manInfo.setGuardManId(cMan.getGuardManId());
                                                            manInfo.setGuardManName(cMan.getGuardManName());
                                                            manInfo.setGuardManRFID(cMan.getGuardManRFID());
                                                            pdaGuarManInfoList.add(manInfo);
                                                        }
                                                        pdaLoginMsg.setPdaGuardManInfo(pdaGuarManInfoList);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                                }

                                                hideWaitDialog();
                                                // 传递 pdaLoginMsg
                                                Intent intent = new Intent(NetOutInActivity.this, MainActivity.class);
                                                intent.putExtra("pdaLoginMsg", pdaLoginMsg);
                                                startActivity(intent);
                                            }
                                        }

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("destrou", "===");
        MApplication.getInstance().destory();
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

    /**
     * 双击返回退出
     */
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            MApplication.getInstance().destory();
        }
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
     * 判断是否完成解析
     *
     * @return boolean
     */
    private boolean IsDone() {
        return false;
    }
}
