package com.hlct.framework.business.message.entity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdaCashboxInfo implements Serializable {
    private static final long serialVersionUID = 7981560250804078619L;
    private String rfidNum;
    private String boxSn;
    private String bankId;

    public static List<PdaCashboxInfo> JSONArraytoPdaNetInfo(
            JSONArray jsonArray) {
        List<PdaCashboxInfo> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<PdaCashboxInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                PdaCashboxInfo info = new PdaCashboxInfo();
                try {
                    info.setRfidNum((String) jsonArray.getJSONObject(i).get("rfidNum"));
                    info.setBoxSn((String) jsonArray.getJSONObject(i).get("boxSn"));
                    info.setBankId((String) jsonArray.getJSONObject(i).get("bankId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }

    public static Map<String, String> JSONArraytoPdaNetInfoMap(JSONArray jsonArray) {
        Map<String, String> map = new HashMap<String, String>();

        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    map.put((String) jsonArray.getJSONObject(i).get("rfidNum"),
                            (String) jsonArray.getJSONObject(i).get("boxSn")
                                    + "&" +
                                    jsonArray.getJSONObject(i).get("bankId"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }

    public String getRfidNum() {
        return rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public String getBoxSn() {
        return boxSn;
    }

    public void setBoxSn(String boxSn) {
        this.boxSn = boxSn;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }


}
