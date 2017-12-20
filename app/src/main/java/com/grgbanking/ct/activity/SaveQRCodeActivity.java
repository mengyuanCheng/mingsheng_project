package com.grgbanking.ct.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.entity.QRString;
import com.grgbanking.ct.greendao.GreenDaoManager;
import com.grgbanking.ct.greendao.QRStringDao;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.qcode.ScanActivity;
import com.grgbanking.ct.utils.FileUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static com.grgbanking.ct.qcode.ScanActivity.REQUEST_CODE_SCAN;
import static com.grgbanking.ct.utils.LoginUtil.getManufacturer;

public class SaveQRCodeActivity extends Activity implements View.OnClickListener {
    private Context mContext;
    private TextView mTVBack;
    private TextView mTVClear;
    private ListView mListView;     // list列表
    private Button mBtnScanQRCode;  // 扫描按钮
    private Button mBtnSaveData;    // 保存数据
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayAdapter mArrayAdapter;

    QRStringDao mQrDao;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_save_qrcode);

        mContext = SaveQRCodeActivity.this;
        mQrDao = GreenDaoManager.getInstance(mContext).getNewSession().getQRStringDao();
        mTVBack = (TextView) findViewById(R.id.save_qr__back);
        mTVClear = (TextView) findViewById(R.id.save_qr_clear);
        mListView = (ListView) findViewById(R.id.save_qrcode_listview);
        mBtnSaveData = (Button) findViewById(R.id.save_btn_save_data);
        mBtnScanQRCode = (Button) findViewById(R.id.save_btn_scan_qrcode);

        mArrayAdapter = new ArrayAdapter(mContext, R.layout.array_listview_item_view,
                R.id.array_listview_textview, mArrayList);
        mListView.setAdapter(mArrayAdapter);
        mTVBack.setOnClickListener(this);
        mTVClear.setOnClickListener(this);
        mBtnScanQRCode.setOnClickListener(this);
        mBtnSaveData.setOnClickListener(this);
        /*
        长按删除所选项
         */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("删除所选项数据")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mQrDao.delete(new QRString(mArrayList.get(position)));
                                mArrayList.remove(position);
                                mArrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return false;
            }
        });

        getDateFromDB();
    }

    private void getDateFromDB() {

        List<QRString> list = mQrDao.loadAll();
        mArrayList.clear();
        for (QRString qrString : list
                ) {
            mArrayList.add(qrString.getQrCode());
        }
        mArrayAdapter.notifyDataSetChanged();
    }


    /**
     * 根据<code>v</code>的id 来判断要执行的代码
     *
     * @param v 点击事件的所有者
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_qr__back:
                onBackPressed();
                break;
            case R.id.save_qr_clear:
                mQrDao.deleteAll();
                mArrayList.clear();
                mArrayAdapter.notifyDataSetChanged();
                break;
            case R.id.save_btn_scan_qrcode:
                Intent intent = new Intent(mContext, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            //TODO 保存信息,生成文件
            case R.id.save_btn_save_data:
                if (mArrayList.isEmpty()) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示信息")
                            .setMessage("数据为空！")
                            .setPositiveButton("确认", null)
                            .show();
                } else {
                    String date = FileUtil.getDate();
                    Log.d("date==", date);
                    FileUtil.writeString(FILE_PATH + FILE_NAME + date + FILE_FORMAT,
                            "" + mArrayList.toString(), "GBk");
                    FileUtil.makeFileAvailable(mContext, FILE_PATH + FILE_NAME + date + FILE_FORMAT);
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示信息")
                            .setMessage("保存数据成功！")
                            .setPositiveButton("确认", null)
                            .setNegativeButton("上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (getManufacturer().equals("alps")) {
                                        //TODO 上传生成的文件
                                        List<NameValuePair> params = new ArrayList<>();
                                        String date = FileUtil.getDate();
                                        String mResult = FileUtil.readTXT(FILE_PATH + FILE_NAME + date + FILE_FORMAT);
                                        params.add(new BasicNameValuePair("content", mResult));
                                        new HttpPostUtils(Constants.URL_QRCODE_NET_UPLOAD, params, new UICallBackDao() {
                                            @Override
                                            public void callBack(ResultInfo resultInfo) {
                                                Toast.makeText(getApplicationContext(), resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).execute();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "没有网络", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();
                }

                break;
            default:
                break;
        }
    }

    /**
     * 接受扫描的返回结果
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        储存数据的Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN) {
            switch (resultCode) {
                case ScanActivity.RESULT_CODE_SCAN:
                    Bundle bundle = data.getBundleExtra("bundle");
                    ArrayList<String> list = bundle.getStringArrayList("list");
                    QRString qrString = new QRString();
                    assert list != null;
                    for (String s : list) {
                        if (!mArrayList.contains(s)) {
                            mArrayList.add(s);
                            qrString.setQrCode(s);
                            mQrDao.insert(qrString);
                        }
                    }
                    mArrayAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
