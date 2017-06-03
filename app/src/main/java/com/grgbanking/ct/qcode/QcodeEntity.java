package com.grgbanking.ct.qcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2017/1/12.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class QcodeEntity {

    private String rfidCode;
    private String netId;
    private Date inputTime;
    private String status;
    private String boxSN;
    private String guardName;

    public QcodeEntity() {

    }

    public static QcodeEntity JSONtoQcodeEntity(JSONObject jsonObject) {
        QcodeEntity qe = null;
        if (jsonObject != null && jsonObject.length() > 0) {
            qe = new QcodeEntity();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("boxList");
            List<QcodeEntity> qcodeEntityList = QcodeEntity.JSONArraytoQcodeEntity(jsonArray);
        } catch (Exception e) {

        }
        return qe;
    }

    public static List<QcodeEntity> JSONArraytoQcodeEntity(JSONArray jsonArray) {
        List<QcodeEntity> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<QcodeEntity>();

            for (int i = 0; i < jsonArray.length(); i++) {
                QcodeEntity qe = new QcodeEntity();
                try {
                    qe.setBoxSN((String) jsonArray.getJSONObject(i).get("boxSN"));
                    qe.setGuardName((String) jsonArray.getJSONObject(i).getString("guardName"));
                    qe.setStatus(jsonArray.getJSONObject(i).getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(qe);
            }
        }
        return list;
    }


    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBoxSN() {
        return boxSN;
    }

    public void setBoxSN(String boxSN) {
        this.boxSN = boxSN;
    }

    public String getGuardName() {
        return guardName;
    }

    public void setGuardName(String guardName) {
        this.guardName = guardName;
    }
}
