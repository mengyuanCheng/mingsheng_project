package com.grgbanking.ct.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.qcode.QcodeActivity;
import com.grgbanking.ct.qcode.ScanActivity;

import java.util.ArrayList;

import static com.grgbanking.ct.qcode.ScanActivity.REQUEST_CODE_SCAN;

/**
 * @author lazylee
 * @Description 用于显示rfid对应的款箱二维码结果列表
 * 通过跳转到扫描界面获取二维码数据数据
 * 保存信息将信息返回给 QcodeActivity
 */
public class ScanQRCodeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ScanQRCodeActivity";
    private Context mContext;
    private ListView mListView;     // list列表
    private Button mBtnScanQRCode;  // 扫描按钮
    private Button mBtnSaveData;    // 保存数据
    private int mFlag;
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        Intent intent = getIntent();
        mFlag = intent.getFlags();

        if (intent.getBundleExtra("bundle") != null) {
            Bundle bundle = intent.getBundleExtra("bundle");
            mArrayList = (ArrayList<String>) bundle.getSerializable("list");
            if (mArrayList != null) {
                Log.i("ScanQRCodeActivity->", mArrayList.toString());
            }
        }

        mContext = ScanQRCodeActivity.this;
        mListView = (ListView) findViewById(R.id.scan_qrcode_listview);
        mBtnScanQRCode = (Button) findViewById(R.id.scan_btn_qrcode);
        mBtnScanQRCode.setOnClickListener(this);
        mBtnSaveData = (Button) findViewById(R.id.scan_btn_save_data);
        mBtnSaveData.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter(mContext, R.layout.array_listview_item_view,
                R.id.array_listview_textview, mArrayList);
        mListView.setAdapter(mArrayAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("删除该钱捆?")
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*扫描二维码*/
            case R.id.scan_btn_qrcode:
                Intent intent = new Intent(mContext, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            /*保存数据*/
            case R.id.scan_btn_save_data:
                Intent commitIntent = new Intent();
                commitIntent.addFlags(mFlag);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("list", mArrayList);
                commitIntent.putExtra("bundle", bundle);
                this.setResult(QcodeActivity.RESULT_CODE, commitIntent);
                finish();
                break;
            default:
                break;
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
        if (requestCode == REQUEST_CODE_SCAN) {
            switch (resultCode) {
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
