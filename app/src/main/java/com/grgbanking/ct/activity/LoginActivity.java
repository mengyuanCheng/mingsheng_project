package com.grgbanking.ct.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.CashBox;
import com.grgbanking.ct.database.DBManager;
import com.grgbanking.ct.database.Extract;
import com.grgbanking.ct.database.ExtractBoxs;
import com.grgbanking.ct.database.NetInfo;
import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.update.CheckUpdateInfos;
import com.grgbanking.ct.utils.FileUtil;
import com.grgbanking.ct.utils.LoginUtil;
import com.grgbanking.ct.utils.StringTools;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaGuardManInfo;
import com.hlct.framework.business.message.entity.PdaLoginMessage;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaUserInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.grgbanking.ct.activity.Constants.FILE_PATH;
import static com.grgbanking.ct.http.ResultInfo.CODE_GUARDMANIINFO;
import static com.grgbanking.ct.utils.IntenetUtil.NETWORN_NONE;
import static com.grgbanking.ct.utils.IntenetUtil.NETWORN_WIFI;
import static com.grgbanking.ct.utils.IntenetUtil.getNetworkState;
import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;

public class LoginActivity extends Activity {
    private static String TAG = "LoginActivity";
    //获取当前时间戳
    String date = FileUtil.getDate();
    String flag;//登录的状态
    String loginNameViewValue = null; //UI控件内容
    String passwordViewValue = null;//UI控件内容
    List<NameValuePair> params = new ArrayList<>();
    TextView detail_branch_name;
    /**
     * 弹出更新提示框  ,如果取消更新，刚退出APP
     */
    Builder update_version;
    ProgressDialog pd;
    /**
     * 判断新版本的URL是否为空，如果不为空，说明有新版本，提示用户更新中
     */
    boolean isDown = false;
    long downLoadId = -1;
    int count = 0;
    private List<ConvoyManInfo> convoyManInfo = new ArrayList<ConvoyManInfo>();
    private List<NetInfo> netInfos = new ArrayList<NetInfo>();
    private List<PdaUserInfo> pdaUserInfos = new ArrayList<PdaUserInfo>();
    private List<PdaCashboxInfo> pdaCashboxInfos = new ArrayList<PdaCashboxInfo>();
    private List<CashBox> cashBoxes = new ArrayList<CashBox>();
    //    String userId = null; //登录成功后的用户ID
    //    String userName = null;//登录成功后的用户姓名
    private int network;//网络连接状态
    private Button loginButtonView;
    private EditText loginNameView;
    private EditText passwordView;
    private CheckBox remPasswordView;
    private Context context;
    private LoginUtil loginUtil;
    private String fileName;
    private com.hlct.framework.pda.common.entity.ResultInfo mResultInfo;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SdCardPath")
        @Override
        public void onReceive(Context context, Intent intent) {
            /** 判断 是否是当前操作的下载ID，有可能是其它应用下载成功的回调*/
            if (downLoadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)) {
                String path = "/sdcard/ct/ct.apk";
                File appFile = new File(path);
                if (appFile.exists()) {
                    cancelWaitDialog();
                    installApk(appFile);
                }
            }
        }
    };
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case 0:
                    showUpdataDialog(arg0.getData().getString("address"));
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MApplication.getInstance().addActivity(this);
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        //		startService(new Intent(context, GrgbankService.class));


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        findViewById();
        initCach();
        superManLogin();

        loginUtil = new LoginUtil(this);

        if (loginUtil.getBooleanInfo(Constants.ISSAVEPASS)) {
            remPasswordView.setChecked(true);
            loginNameView.setText(loginUtil.getStringInfo(Constants.USER_NAME));
            passwordView.setText(loginUtil.getStringInfo(Constants.PASSWORD));
        }

        //        remPasswordView.setChecked(true);//默认记住密码


        //登录操作
        loginButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNameViewValue = loginNameView.getText().toString();
                passwordViewValue = passwordView.getText().toString();
                String modle = getManufacturer();
                if (FileUtil.isExist(FILE_PATH + "WDRW.dat")) {
                    //如果有
                    fileName = "WDRW.dat";
                    mAsyncTask mAsyncTask = new mAsyncTask();
                    mAsyncTask.execute();
                } else {
                    //判断有无文件
                    boolean isExist = FileUtil.isExist(FILE_PATH + date + "WDRW.dat");
                    if (isExist) {
                        fileName = date + "WDRW.dat";
                        /**
                         *  显示一个进度条,提示当前解析进度.
                         *  添加一个异步任务,用来解析“.dat”文件,并且写入数据库
                         */
                        mAsyncTask mAsyncTask = new mAsyncTask();
                        mAsyncTask.execute();
                    } else if (modle.equals("alps")) {
                        wifiLogin();
                    } else {
                        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        showWaitingDialog(progressDialog, "提示", "未发现今日文件！", true);
                    }
                }

            }

        });

        //        //记住密码操作
        //        remPasswordView.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //
        //            }
        //        });
        //
        //        loginNameView.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                loginNameView.setText("");
        //            }
        //        });
        //
        //        passwordView.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                passwordView.setText("");
        //            }
        //        });
    }

    /**
     * 无网络连接的情况下，访问数据库进行登录操作
     */
    private void databaseLogin() {
        //访问数据库进行操作
        try {
            DBManager db = new DBManager(context);
            flag = db.queryLogin(loginNameViewValue, passwordViewValue);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "帐号密码有误,请重新输入", Toast.LENGTH_SHORT).show();
        }

        Log.d("Test", "登陆成功");
        //押运人员
        if (flag.equals(ResultInfo.CODE_GUARDMANIINFO)) {
            success();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, NetOutInActivity.class);
            startActivity(intent);
            finish();
        }
        //配箱人员
        else if (flag.equals(ResultInfo.CODE_PEIXIANG)) {
            success();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, PeixiangActivity.class);
            startActivity(intent);
            finish();
        } else {
            //帐号密码错误
            Toast.makeText(context, "帐号密码有误,请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 使用wifi连接的情况下，访问后台服务器进行登录操作
     */
    private void wifiLogin() {
        // FIXME: 2017/10/11 账号密码错误  然后修改了账号密码 还是显示账号密码错误
        ProgressDialog waitingDialog = new ProgressDialog(LoginActivity.this);
        loginNameViewValue = loginNameView.getText().toString();
        passwordViewValue = passwordView.getText().toString();

        if (StringTools.isEmpty(loginNameViewValue) || StringTools.isEmpty(passwordViewValue)) {
            Log.v("V", "用户名或密码为空");
            Toast.makeText(context, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
            if (waitingDialog.isShowing()) {
                waitingDialog.dismiss();
            }
            return;
        }

        loginButtonView.setText("正在登录中...");
        loginButtonView.setEnabled(false);

        params.add(new BasicNameValuePair("login_name", loginNameViewValue));
        params.add(new BasicNameValuePair("login_password", passwordViewValue));

        if (remPasswordView.isChecked()) {
            loginUtil.setUserInfo(Constants.USER_NAME, loginNameViewValue);
            loginUtil.setUserInfo(Constants.PASSWORD, passwordViewValue);
            loginUtil.setUserInfo(Constants.ISSAVEPASS, true);
        } else if (!remPasswordView.isChecked()) {
            loginUtil.setUserInfo(Constants.ISSAVEPASS, false);
        }
        //访问后台服务器进行登录操作
        Log.d(loginNameViewValue, passwordViewValue);
        new HttpPostUtils(Constants.URL_PDA_LOGIN, params, new UICallBackDao() {
            @Override
            public void callBack(ResultInfo resultInfo) {
                Log.i(TAG, "use wifi to logining");
                if (resultInfo.getCode() != null && !resultInfo.getCode().isEmpty()) {
                    if (!resultInfo.getCode().equals("2")){
                        /** 开始组装数据*/

                        //取出所有数据
                        PdaLoginMessage pdaLoginMessage = resultInfo.getPdaLogMess();
                        PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();

                        Log.d("Message", pdaLoginMessage.getMessage());
                        //保存到缓存
                        pdaLoginMsg.setCode(pdaLoginMessage.getCode());
                        pdaLoginMsg.setPdaGuardManInfo(pdaLoginMessage.getGuardManInfoList());
                        pdaLoginMsg.setLineId(pdaLoginMessage.getLineId());
                        pdaLoginMsg.setLineSn(pdaLoginMessage.getLineSn());
                        pdaLoginMsg.setMsg(pdaLoginMessage.getMessage());
                        pdaLoginMsg.setNetInfoList(pdaLoginMessage.getNetInfoList());
                        pdaLoginMsg.setLineNotes(pdaLoginMessage.getLineNotes());
                        pdaLoginMsg.setAllPdaBoxsMap(pdaLoginMessage.getAllPdaBoxsMap());
                        pdaLoginMsg.setPdaUserInfo(pdaLoginMessage.getPdaLoginManInfo());

                        DataCach.setPdaLoginMsg(pdaLoginMsg);
                        Map<String, String> allPdaBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
                        DataCach.netType = CODE_GUARDMANIINFO;

                        //取出押运人员数据
                        List<PdaGuardManInfo> guardManInfoList = pdaLoginMsg.getPdaGuardManInfo();
                        if (guardManInfoList != null && guardManInfoList.size() > 0) {
                            for (PdaGuardManInfo info : guardManInfoList) {
                                ConvoyManInfo manInfo = new ConvoyManInfo();
                                manInfo.setGuardManId(info.getGuardManId());
                                manInfo.setGuardManName(info.getGuardManName());
                                manInfo.setGuardManRFID(info.getGuardManRFID());
                                convoyManInfo.add(manInfo);
                            }
                            //存入数据库
                            DBManager dbmanager = new DBManager(context);
                            try {
                                dbmanager.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dbmanager.addConvoyMan(convoyManInfo);
                        }

                        //取出网点信息
                        List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
                        if (netInfoList != null && netInfoList.size() > 0) {
                            for (PdaNetInfo info : netInfoList) {
                                NetInfo netInfo = new NetInfo();
                                Extract extract = new Extract();
                                ExtractBoxs extractBoxs = new ExtractBoxs();
                                /**判断是出库还是入库决定存入哪张表*/
                                if (info.getFlag().equals("1")) {
                                    extract.setLineSn(pdaLoginMsg.getLineSn());
                                    extract.setBankId(info.getBankId());
                                    extract.setNetTaskStatus(info.getNetTaskStatus());
                                    extract.setBankName(info.getBankName());
                                    extract.setLineId(pdaLoginMsg.getLineId());
                                    extract.setCashBoxInfoList(info.getCashBoxInfoList());
                                    extract.setNetPersonInfoList(info.getNetPersonInfoList());
                                    DBManager extractdb = new DBManager(context);
                                    extractdb.addExtract(extract);
                                } else if (info.getFlag().equals("0")) {
                                    netInfo.setLineSn(pdaLoginMsg.getLineSn());
                                    netInfo.setBankId(info.getBankId());
                                    netInfo.setNetTaskStatus(info.getNetTaskStatus());
                                    netInfo.setBankName(info.getBankName());
                                    netInfo.setLineId(pdaLoginMsg.getLineId());
                                    netInfo.setNetPersonInfoList(info.getNetPersonInfoList());
                                    netInfo.setCashBoxInfoList(info.getCashBoxInfoList());
                                    netInfo.setFlag(info.getFlag());
                                    netInfos.add(netInfo);
                                }
                            }
                            //存入数据库
                            DBManager manager = new DBManager(context);
                            manager.addNetInfo(netInfos);
                        }

                        //保存登录人员
                        List<PdaUserInfo> pdaUserInfo = pdaLoginMsg.getPdaUserInfo();
                        if (pdaUserInfo != null && pdaUserInfo.size() > 0) {
                            for (PdaUserInfo info : pdaUserInfo) {
                                info.getFlag();
                                info.getLine();
                                info.getLogin_name();
                                info.getLoginId();
                                info.getLine();
                                pdaUserInfos.add(info);
                            }
                            //存入数据库
                            DBManager loginDb = new DBManager(context);
                            loginDb.addLoginMan(pdaUserInfos);
                        }

                        //保存pda款箱
                        List<PdaCashboxInfo> pdaCashboxInfoList = pdaLoginMsg.getPdaCashboxInfo();
                        if (pdaCashboxInfoList != null && pdaCashboxInfoList.size() > 0) {
                            for (PdaCashboxInfo info : pdaCashboxInfoList) {
                                CashBox cashBox = new CashBox();
                                cashBox.setBankId(info.getBankId());
                                cashBox.setBoxSn(info.getBoxSn());
                                cashBox.setRfidNum(info.getRfidNum());
                                cashBoxes.add(cashBox);
                            }
                            //存入数据库
                            DBManager cashBoxDB = new DBManager(context);
                            cashBoxDB.addCashBox(cashBoxes);
                        }

                    }

                    //押运人员
                    if (ResultInfo.CODE_GUARDMANIINFO.equals(resultInfo.getCode())) {
                        success();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, NetOutInActivity.class);
                        startActivity(intent);
                        finish();
                        //配箱人员
                    } else if (ResultInfo.CODE_PEIXIANG.equals(resultInfo.getCode())) {
                        success();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, PeixiangActivity.class);
                        startActivity(intent);
                        finish();
                    }else {//如果账号密码错误
                            Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            loginButtonView.setText("登录");
                    }
                } else {
                    Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    loginButtonView.setText("登录");
                }
                loginButtonView.setEnabled(true);
            }
        }).execute();
    }

    /**
     * 超级管理员登陆
     */
    private void superManLogin() {
        detail_branch_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 1) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, NetOutInActivity.class);
                    startActivity(intent);
                }
                count++;
            }
        });
    }

    //findViewById.
    private void findViewById() {
        remPasswordView = (CheckBox) this.findViewById(R.id.cb);
        loginNameView = (EditText) this.findViewById(R.id.username_edit);
        passwordView = (EditText) this.findViewById(R.id.password_edit);
        loginButtonView = (Button) this.findViewById(R.id.login_button);
        detail_branch_name = (TextView) this.findViewById(R.id.detail_branch_name);
    }

    /*
     *初始化缓存，将缓存清空
     */
    public void initCach() {
        DataCach.setPdaLoginMessage(null);
    }

    private void showUpdataDialog(final String url) {
        /** 当发现没有版本更新，则允许登录，否则 继续不可操作  */
        loginButtonView.setEnabled(url == null ? true : false);
        /**显示当前版本 */
        //		try {
        //			detail_branch_name.setText("版本号:v"+getPackageManager().getPackageInfo(context.getPackageName(),0).versionName);
        //		} catch (NameNotFoundException e) {
        //			e.printStackTrace();
        //		}
        detail_branch_name.setText("版本号:v1.0");
        if (url == null) {
            return;
        }
        update_version = new Builder(this);
        update_version.setTitle("版本升级");
        update_version.setMessage("有新的版本需要升级");
        update_version.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                downLoadFile(url);
                showWaitDialog();
            }
        });
        update_version.setNeutralButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        update_version.create().show();
    }

    /**
     * 检测是否要更新
     */
    private void checkUpdate() {
        /** 开始检测更新，登录按钮不可操作  */
        loginButtonView.setEnabled(false);
        detail_branch_name.setText("正在检测更新");

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String add = CheckUpdateInfos.getUpdataInfoJSON(LoginActivity.this);
                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("address", add);
                    msg.what = 0;
                    msg.setData(b);
                    handler.sendMessage(msg);
                    Log.v("tag", "服务器新版本下载地址： " + add);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showWaitDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("正在下载更新，请稍后...");
        }
        pd.show();
    }

    private void cancelWaitDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @SuppressLint("SdCardPath")
    private void downLoadFile(String url) {
        if (!isDown && url != null) {
            isDown = true;
            String path = "/sdcard/ct/ct.apk";
            File appFile = new File(path);
            if (appFile.exists()) {
                appFile.delete();
            }
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalPublicDir("ct", "ct.apk");
            request.setTitle("MeiLiShuo");
            request.setDescription("MeiLiShuo desc");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //  request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setMimeType("application/cn.trinea.download.file");
            downLoadId = downloadManager.enqueue(request);
            Log.v("tag", "下载。。。。 。ID 》 " + downLoadId);
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    protected void installApk(File file) {
        Intent intent = new Intent();        //执行动作
        intent.setAction(Intent.ACTION_VIEW);        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void success() {
        LoginUser loginUser = DataCach.loginUser;
        loginUser.setLoginName(loginNameViewValue);
        loginUser.setPassword(passwordViewValue);
        loginButtonView.setText("登录");
        loginButtonView.setEnabled(true);
        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示加载界面
     *
     * @param title dialog's Title
     * @param msg   dialog's Message
     * @param b     如果是false则不可取消。否之则可以取消。
     */
    public void showWaitingDialog(ProgressDialog waitingDialog, String title, String msg, boolean b) {
        waitingDialog.setTitle(title);
        waitingDialog.setMessage(msg);
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(b);
        waitingDialog.show();
    }

    private class mAsyncTask extends AsyncTask<String, Integer, String> {
        String title = "正在加载";
        String msg = "请稍候";
        ProgressDialog waitingDialog = new ProgressDialog(LoginActivity.this);

        /**
         * 执行后台任务前做一些UI操作
         */
        @Override
        protected void onPreExecute() {//显示一个等待dialog
            showWaitingDialog(waitingDialog, title, msg, false);
            super.onPreExecute();
        }

        /**
         * 后台任务
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {

            com.hlct.framework.pda.common.entity.ResultInfo
                    resultInfo = new com.hlct.framework.pda.common.entity.ResultInfo();
            if (getManufacturer().equals("alps")) {
            } else {
                try {
                    resultInfo = (com.hlct.framework.pda.common.entity.ResultInfo)
                            FileUtil.readString(FILE_PATH + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                /** 开始组装数据*/

                //取出所有数据
                PdaLoginMessage pdaLoginMessage = resultInfo.getPdaLogMess();
                PdaLoginMsg pdaLoginMsg = new PdaLoginMsg();

                Log.d("Message", pdaLoginMessage.getMessage());
                //保存到缓存
                pdaLoginMsg.setCode(pdaLoginMessage.getCode());
                pdaLoginMsg.setPdaGuardManInfo(pdaLoginMessage.getGuardManInfoList());
                pdaLoginMsg.setLineId(pdaLoginMessage.getLineId());
                pdaLoginMsg.setLineSn(pdaLoginMessage.getLineSn());
                pdaLoginMsg.setMsg(pdaLoginMessage.getMessage());
                pdaLoginMsg.setNetInfoList(pdaLoginMessage.getNetInfoList());
                pdaLoginMsg.setLineNotes(pdaLoginMessage.getLineNotes());
                pdaLoginMsg.setAllPdaBoxsMap(pdaLoginMessage.getAllPdaBoxsMap());
                pdaLoginMsg.setPdaUserInfo(pdaLoginMessage.getPdaLoginManInfo());

                DataCach.setPdaLoginMsg(pdaLoginMsg);
                Map<String, String> allPdaBoxsMap = pdaLoginMsg.getAllPdaBoxsMap();
                DataCach.netType = CODE_GUARDMANIINFO;

                //取出押运人员数据
                List<PdaGuardManInfo> guardManInfoList = pdaLoginMsg.getPdaGuardManInfo();
                if (guardManInfoList != null && guardManInfoList.size() > 0) {
                    for (PdaGuardManInfo info : guardManInfoList) {
                        ConvoyManInfo manInfo = new ConvoyManInfo();
                        manInfo.setGuardManId(info.getGuardManId());
                        manInfo.setGuardManName(info.getGuardManName());
                        manInfo.setGuardManRFID(info.getGuardManRFID());
                        convoyManInfo.add(manInfo);
                    }
                    //存入数据库
                    DBManager dbmanager = new DBManager(context);
                    try {
                        dbmanager.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dbmanager.addConvoyMan(convoyManInfo);
                }

                //取出网点信息
                List<PdaNetInfo> netInfoList = pdaLoginMsg.getNetInfoList();
                if (netInfoList != null && netInfoList.size() > 0) {
                    for (PdaNetInfo info : netInfoList) {
                        NetInfo netInfo = new NetInfo();
                        Extract extract = new Extract();
                        ExtractBoxs extractBoxs = new ExtractBoxs();
                        /**判断是出库还是入库决定存入哪张表*/
                        if (info.getFlag().equals("1")) {
                            extract.setLineSn(pdaLoginMsg.getLineSn());
                            extract.setBankId(info.getBankId());
                            extract.setNetTaskStatus(info.getNetTaskStatus());
                            extract.setBankName(info.getBankName());
                            extract.setLineId(pdaLoginMsg.getLineId());
                            extract.setCashBoxInfoList(info.getCashBoxInfoList());
                            extract.setNetPersonInfoList(info.getNetPersonInfoList());
                            DBManager extractdb = new DBManager(context);
                            extractdb.addExtract(extract);
                        } else if (info.getFlag().equals("0")) {
                            netInfo.setLineSn(pdaLoginMsg.getLineSn());
                            netInfo.setBankId(info.getBankId());
                            netInfo.setNetTaskStatus(info.getNetTaskStatus());
                            netInfo.setBankName(info.getBankName());
                            netInfo.setLineId(pdaLoginMsg.getLineId());
                            netInfo.setNetPersonInfoList(info.getNetPersonInfoList());
                            netInfo.setCashBoxInfoList(info.getCashBoxInfoList());
                            netInfo.setFlag(info.getFlag());
                            netInfos.add(netInfo);
                        }
                    }
                    //存入数据库
                    DBManager manager = new DBManager(context);
                    manager.addNetInfo(netInfos);
                }

                //保存登录人员
                List<PdaUserInfo> pdaUserInfo = pdaLoginMsg.getPdaUserInfo();
                if (pdaUserInfo != null && pdaUserInfo.size() > 0) {
                    for (PdaUserInfo info : pdaUserInfo) {
                        info.getFlag();
                        info.getLine();
                        info.getLogin_name();
                        info.getLoginId();
                        info.getLine();
                        pdaUserInfos.add(info);
                    }
                    //存入数据库
                    DBManager loginDb = new DBManager(context);
                    loginDb.addLoginMan(pdaUserInfos);
                }

                //保存pda款箱
                List<PdaCashboxInfo> pdaCashboxInfoList = pdaLoginMsg.getPdaCashboxInfo();
                if (pdaCashboxInfoList != null && pdaCashboxInfoList.size() > 0) {
                    for (PdaCashboxInfo info : pdaCashboxInfoList) {
                        CashBox cashBox = new CashBox();
                        cashBox.setBankId(info.getBankId());
                        cashBox.setBoxSn(info.getBoxSn());
                        cashBox.setRfidNum(info.getRfidNum());
                        cashBoxes.add(cashBox);
                    }
                    //存入数据库
                    DBManager cashBoxDB = new DBManager(context);
                    cashBoxDB.addCashBox(cashBoxes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //                Toast.makeText(LoginActivity.this,""+e,Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        /**
         * 执行完成后
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            loginNameViewValue = loginNameView.getText().toString();
            passwordViewValue = passwordView.getText().toString();

            if (StringTools.isEmpty(loginNameViewValue) || StringTools.isEmpty(passwordViewValue)) {
                Log.v("V1", "用户名或密码为空");
                Toast.makeText(context, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                return;
            }

            loginButtonView.setText("正在登录中...");
            loginButtonView.setEnabled(false);

            params.add(new BasicNameValuePair("login_name", loginNameViewValue));
            params.add(new BasicNameValuePair("login_password", passwordViewValue));

            if (remPasswordView.isChecked()) {
                loginUtil.setUserInfo(Constants.USER_NAME, loginNameViewValue);
                loginUtil.setUserInfo(Constants.PASSWORD, passwordViewValue);
                loginUtil.setUserInfo(Constants.ISSAVEPASS, true);
            } else if (!remPasswordView.isChecked()) {
                loginUtil.setUserInfo(Constants.ISSAVEPASS, false);
            }

            /** 判断机器类型 */
            network = getNetworkState(context);
            switch (network) {
                case NETWORN_NONE: {
                    databaseLogin();
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    loginButtonView.setEnabled(true);
                    loginButtonView.setText("登录");
                    break;
                }
                case NETWORN_WIFI: {
                    databaseLogin();
                    loginButtonView.setEnabled(true);
                    loginButtonView.setText("登录");
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    break;
                }
            }

            super.onPostExecute(s);
        }
    }

    // TODO: 2017/10/9 根据设备的不同采用不同的登陆方式,(网络登陆)
}