package com.grgbanking.ct.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.grgbanking.ct.R;
import com.grgbanking.ct.entity.PeiXiangInfo;
import com.grgbanking.ct.qcode.QcodeActivity;
import com.grgbanking.ct.qcode.ScanActivity;

import java.util.ArrayList;

import static com.grgbanking.ct.qcode.ScanActivity.REQUEST_CODE_SCAN;

public class ScanQRCodeActivity extends Activity implements View.OnClickListener{

    private Context mContext;
    private ListView mListView;     // list列表
    private Button mBtnScanQRCode;  // 扫描按钮
    private Button mBtnSaveData;    // 保存数据
    private int mFlag;
    ArrayList<String> mArrayList = new ArrayList<>();
    ArrayAdapter mArrayAdapter;
    PeiXiangInfo peiXiangInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        Intent intent = getIntent();
        mFlag = intent.getFlags();
        if(intent.getBundleExtra("bundle") != null){
            Bundle bundle = intent.getBundleExtra("bundle");
            mArrayList = (ArrayList<String>) bundle.getSerializable("list");
            if(mArrayList != null){
                Log.i("ScanQRCodeActivity->",mArrayList.toString());
            }


        }

        mContext = ScanQRCodeActivity.this;
        mListView = (ListView) findViewById(R.id.scan_qrcode_listview);
        mBtnScanQRCode = (Button) findViewById(R.id.scan_btn_qrcode);
        mBtnScanQRCode.setOnClickListener(this);
        mBtnSaveData = (Button) findViewById(R.id.scan_btn_save_data);
        mBtnSaveData.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter(mContext,R.layout.array_listview_item_view,
                R.id.array_listview_textview,mArrayList);
        mListView.setAdapter(mArrayAdapter);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                this.setResult(QcodeActivity.RESULT_CODE,commitIntent);
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
            switch (resultCode){
                case ScanActivity.RESULT_CODE_SCAN:
                    Bundle bundle = data.getBundleExtra("bundle");
                    ArrayList<String> list = bundle.getStringArrayList("list");
                    assert list != null;
                    for (String s : list) {
                        if (!mArrayList.contains(s) && mArrayList.size() <= 5) {
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
