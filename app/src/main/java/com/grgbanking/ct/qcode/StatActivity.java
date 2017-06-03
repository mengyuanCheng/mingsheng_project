package com.grgbanking.ct.qcode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.grgbanking.ct.R;
import com.grgbanking.ct.activity.Constants;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2017/1/10.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class StatActivity extends Activity {

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    private Button refresh;

    private ListView lv_data;
    private ArrayAdapter dataAdapter;


    private List<String> dataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_activity);
        refresh = (Button) findViewById(R.id.bt_refresh);
        lv_data = (ListView) findViewById(R.id.lv_data);
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getData());
        lv_data.setAdapter(dataAdapter);
        onClickListener();
    }

    private List<String> getData() {
        return dataList;
    }


    private void onClickListener() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });

    }

    /**
     * 查询数据
     */
    private void query() {
        Log.d("query", "query is successful");

        new HttpPostUtils(Constants.URL_QCODE_QUERY, params, new UICallBackDao() {
            @Override
            public void callBack(ResultInfo resultInfo) {
                Log.d("getCode", "" + resultInfo.getCode());
                Log.d("getCode", "" + resultInfo.getMessage());
                if (resultInfo.getCode().equals(resultInfo.CODE_SUCCESS)) {
                    //成功
                    JSONObject jsonObject = resultInfo.getJsonObject();
                    Toast.makeText(getApplicationContext(), resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    if (jsonObject != null && jsonObject.length() > 0) {
                        QcodeEntity qcodeEntity = QcodeEntity.JSONtoQcodeEntity(jsonObject);
                    }
                    try {
                        List<QcodeEntity> qcodeEntityList = QcodeEntity.JSONArraytoQcodeEntity(jsonObject.getJSONArray("boxList"));
                        if (qcodeEntityList != null && qcodeEntityList.size() > 0) {
                            for (QcodeEntity qe : qcodeEntityList) {
                                String boxsn = qe.getBoxSN();
                                //                                String guardname = qe.getGuardName();
                                String status = qe.getStatus();

                                dataList.add(boxsn + "      " + status);
                                dataAdapter.notifyDataSetChanged();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (resultInfo.getCode().equals(resultInfo.CODE_ERROR)) {
                    Toast.makeText(getApplicationContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();

        refresh();
    }

    private void refresh() {
        lv_data.setAdapter(dataAdapter);
    }

}