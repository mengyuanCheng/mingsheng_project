package com.hlct.framework.business.message.entity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PdaGuardManInfo implements Serializable {
    private static final long serialVersionUID = 7981560250804078649L;
    private String guardManId;
    private String guardManName;
    private String guardManRFID;

    public static List<PdaGuardManInfo> JSONArraytoPdaGuardManInfo(JSONArray jsonArray) {
        List<PdaGuardManInfo> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<PdaGuardManInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                PdaGuardManInfo info = new PdaGuardManInfo();
                try {
                    info.setGuardManId((String) jsonArray.getJSONObject(i).get("guardManId"));
                    info.setGuardManName((String) jsonArray.getJSONObject(i).get("guardManName"));
                    info.setGuardManRFID((String) jsonArray.getJSONObject(i).get("guardManRFID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }

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
}
