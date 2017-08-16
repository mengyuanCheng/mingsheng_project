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

import com.grgbanking.ct.R;
import com.grgbanking.ct.qcode.ScanActivity;
import com.grgbanking.ct.utils.FileUtil;

import java.util.ArrayList;

import static com.grgbanking.ct.qcode.ScanActivity.REQUEST_CODE_SCAN;

public class SaveQRCodeActivity extends Activity implements View.OnClickListener{
    private Context mContext;
    private ListView mListView;     // list列表
    private Button mBtnScanQRCode;  // 扫描按钮
    private Button mBtnSaveData;    // 保存数据
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayAdapter mArrayAdapter;

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
        mListView = (ListView) findViewById(R.id.save_qrcode_listview);
        mBtnSaveData = (Button) findViewById(R.id.save_btn_save_data);
        mBtnScanQRCode = (Button) findViewById(R.id.save_btn_scan_qrcode);

        mArrayAdapter = new ArrayAdapter(mContext,R.layout.array_listview_item_view,
                R.id.array_listview_textview,mArrayList);
        mListView.setAdapter(mArrayAdapter);

        mBtnScanQRCode.setOnClickListener(this);
        mBtnSaveData.setOnClickListener(this);
        /*
        长按删除所选项
         */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {

                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("删除所选项数据")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
    }

    /**
     * 根据<code>v</code>的id 来判断要执行的代码
     * @param v 点击事件的所有者
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_btn_scan_qrcode:
                Intent intent = new Intent(mContext, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            //TODO 保存信息,生成文件
            case R.id.save_btn_save_data:
                if(mArrayList.isEmpty()){
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示信息")
                            .setMessage("数据为空！")
                            .setPositiveButton("确认", null)
                            .show();
                }else {
                    String date = FileUtil.getDate();
                    Log.d("date==", date);
                    FileUtil.writeString(FILE_PATH + FILE_NAME + date + FILE_FORMAT,
                            "" + mArrayList.toString(), "GBk");
                    FileUtil.makeFileAvailable(mContext, FILE_PATH + FILE_NAME + date + FILE_FORMAT);
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示信息")
                            .setMessage("保存数据成功！")
                            .setPositiveButton("确认", null)
                            .show();
                }

                break;
            default:
                break;
        }
    }

    /**
     * 接受扫描的返回结果
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        储存数据的Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN) {
            switch (resultCode){
                case ScanActivity.RESULT_CODE_SCAN:
                    Bundle bundle = data.getBundleExtra("bundle");
                    ArrayList<String> list = bundle.getStringArrayList("list");
                    assert list != null;
                    for (String s : list) {
                        if (!mArrayList.contains(s)) {
                            mArrayList.add(s);
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
