package com.hlct.framework.business.message.entity;

import com.grgbanking.ct.database.ExtractBoxs;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PdaNetInfo implements Serializable {
    private static final long serialVersionUID = 7981560250804078639L;
    private String bankId;
    private String bankName;
    private String netTaskStatus;
    private String lineId;
    //0是网点入库，1是网点出库
    private String flag;
    private List<PdaCashboxInfo> cashBoxInfoList;
    private List<PdaNetPersonInfo> netPersonInfoList;

    public List<ExtractBoxs> getExtractBoxsList() {
        return extractBoxsList;
    }

    public void setExtractBoxsList(List<ExtractBoxs> extractBoxsList) {
        this.extractBoxsList = extractBoxsList;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private List<ExtractBoxs> extractBoxsList;

    public static List<PdaNetInfo> JSONArraytoPdaNetInfo(JSONArray jsonArray) {
        List<PdaNetInfo> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<PdaNetInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                PdaNetInfo info = new PdaNetInfo();
                try {
                    info.setBankId((String) jsonArray.getJSONObject(i).get("bankId"));
                    info.setBankName((String) jsonArray.getJSONObject(i).get("bankName"));
                    info.setNetTaskStatus((String) jsonArray.getJSONObject(i).get("netTaskStatus"));
                    info.setFlag((String) jsonArray.getJSONObject(i).get("flag"));

                    JSONArray cashBoxInfoArray = ((JSONArray) jsonArray.getJSONObject(i).get("cashBoxInfoList"));
                    List<PdaCashboxInfo> cashBoxInfoList = PdaCashboxInfo.JSONArraytoPdaNetInfo(cashBoxInfoArray);
                    info.setCashBoxInfoList(cashBoxInfoList);

                    JSONArray netPersonInfoArray = ((JSONArray) jsonArray.getJSONObject(i).get("netPersonInfoList"));
                    List<PdaNetPersonInfo> netPersonInfoList = PdaNetPersonInfo.JSONArraytoPdaNetPersonInfo(netPersonInfoArray);
                    info.setNetPersonInfoList(netPersonInfoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public List<PdaCashboxInfo> getCashBoxInfoList() {
        return cashBoxInfoList;
    }

    public void setCashBoxInfoList(List<PdaCashboxInfo> cashBoxInfoList) {
        this.cashBoxInfoList = cashBoxInfoList;
    }

    public List<PdaNetPersonInfo> getNetPersonInfoList() {
        return netPersonInfoList;
    }

    public void setNetPersonInfoList(List<PdaNetPersonInfo> netPersonInfoList) {
        this.netPersonInfoList = netPersonInfoList;
    }

    public String getNetTaskStatus() {
        return netTaskStatus;
    }

    public void setNetTaskStatus(String netTaskStatus) {
        this.netTaskStatus = netTaskStatus;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
