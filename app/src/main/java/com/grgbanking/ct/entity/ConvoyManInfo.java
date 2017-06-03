package com.grgbanking.ct.entity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/10/24.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ConvoyManInfo {

    private String guardManId;
    private String guardManName;
    private String guardManRFID;
    public String getGuardManId() {
        return guardManId;
    }
    public void setGuardManId(String guardManId) {
        this.guardManId = guardManId;
    }
    public String getGuardManName() {
        return guardManName;
    }
    public void setGuardManName(String guardManName) {
        this.guardManName = guardManName;
    }
    public String getGuardManRFID() {
        return guardManRFID;
    }
    public void setGuardManRFID(String guardManRFID) {
        this.guardManRFID = guardManRFID;
    }
    public static List<ConvoyManInfo> JSONArraytoPdaGuardManInfo(JSONArray jsonArray) {
        List<ConvoyManInfo> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<ConvoyManInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                ConvoyManInfo info = new ConvoyManInfo();
                try {
                    info.setGuardManId((String)jsonArray.getJSONObject(i).get("guardManId"));
                    info.setGuardManName((String)jsonArray.getJSONObject(i).get("guardManName"));
                    info.setGuardManRFID((String)jsonArray.getJSONObject(i).get("guardManRFID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }
}
