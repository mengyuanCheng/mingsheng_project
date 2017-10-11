package com.hlct.framework.business.message.entity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PdaLoginMessage implements Serializable {
    public static final String CODE_SUCCESS = "1";
    public static final String CODE_ERROR = "2";
    /**
     *
     */
    private static final long serialVersionUID = 7981560250804078638L;
    private String lineId;
    private String lineSn;
    private String lineNotes;
    private List<PdaUserInfo> pdaLoginManInfo;
    private List<PdaNetInfo> netInfoList;
    private List<PdaGuardManInfo> guardManInfoList;
    private Map<String, String> allPdaBoxsMap;
    private String code;
    private String message;

    public static PdaLoginMessage JSONtoPdaLoginMessage(JSONObject jsonObject) {
        PdaLoginMessage plm = null;

        if (jsonObject != null && jsonObject.length() > 0) {
            plm = new PdaLoginMessage();
            try {
                plm.setLineId(jsonObject.getString("lineId"));
                plm.setLineSn(jsonObject.getString("lineSn"));
                plm.setLineNotes(jsonObject.getString("lineNotes"));
                plm.setCode(jsonObject.getString("code"));
                plm.setMessage(jsonObject.getString("message"));

                JSONArray loginManInfoArray = jsonObject.getJSONArray("pdaLoginManInfo");
                List<PdaUserInfo> userInfolist = PdaUserInfo.JSONArraytoPdaLoginManInfo(loginManInfoArray);
                plm.setPdaLoginManInfo(userInfolist);

                JSONArray netInfoArray = jsonObject.getJSONArray("netInfoList");
                List<PdaNetInfo> netInfoList = PdaNetInfo.JSONArraytoPdaNetInfo(netInfoArray);
                plm.setNetInfoList(netInfoList);

                JSONArray guardManInfoArray = jsonObject.getJSONArray("guardManInfoList");
                List<PdaGuardManInfo> guardManInfoList = PdaGuardManInfo.JSONArraytoPdaGuardManInfo(guardManInfoArray);
                plm.setGuardManInfoList(guardManInfoList);

                JSONObject allPdaBoxsObj = jsonObject.getJSONObject("allPdaBoxsMap");

                Iterator iterator = allPdaBoxsObj.keys();
                String key, value;
                Map<String, String> allPdaBoxsMap = new HashMap<>();
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    Log.d("test", "key: " + key);
                    value = allPdaBoxsObj.getString(key);
                    Log.d("test", "value: " + value);
                    allPdaBoxsMap.put(key, value);
                }
                plm.setAllPdaBoxsMap(allPdaBoxsMap);

                //                Map<String, String> allPdaBoxsMap = PdaCashboxInfo.JSONArraytoPdaNetInfoMap(allPdaBoxsArray);
                //                                plm.setAllPdaBoxsMap(allPdaBoxsMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return plm;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineSn() {
        return lineSn;
    }

    public void setLineSn(String lineSn) {
        this.lineSn = lineSn;
    }

    public String getLineNotes() {
        return lineNotes;
    }

    public void setLineNotes(String lineNotes) {
        this.lineNotes = lineNotes;
    }

    public List<PdaNetInfo> getNetInfoList() {
        return netInfoList;
    }

    public void setNetInfoList(List<PdaNetInfo> netInfoList) {
        this.netInfoList = netInfoList;
    }

    public List<PdaGuardManInfo> getGuardManInfoList() {
        return guardManInfoList;
    }

    public void setGuardManInfoList(List<PdaGuardManInfo> guardManInfoList) {
        this.guardManInfoList = guardManInfoList;
    }

    public Map<String, String> getAllPdaBoxsMap() {
        return allPdaBoxsMap;
    }

    public void setAllPdaBoxsMap(Map<String, String> allPdaBoxsMap) {
        this.allPdaBoxsMap = allPdaBoxsMap;
    }

    public List<PdaUserInfo> getPdaLoginManInfo() {
        return pdaLoginManInfo;
    }

    public void setPdaLoginManInfo(List<PdaUserInfo> pdaLoginManInfo) {
        this.pdaLoginManInfo = pdaLoginManInfo;
    }

}