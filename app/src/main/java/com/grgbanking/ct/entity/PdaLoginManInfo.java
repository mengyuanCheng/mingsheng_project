package com.grgbanking.ct.entity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class PdaLoginManInfo implements Serializable {
    private static final long serialVersionUID=7981560250804078659L;
    private String loginId;
    private String loginName;
    private String password;
    private String flag;
    private String line;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLogin_name() {
        return loginName;
    }

    public void setLogin_name(String login_name) {
        this.loginName = login_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public static List<PdaLoginManInfo> JSONArraytoPdaLoginManInfo(JSONArray jsonArray) {
        List<PdaLoginManInfo> list = null;
        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<PdaLoginManInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                PdaLoginManInfo info = new PdaLoginManInfo();
                try {
                    info.setLoginId((String) jsonArray.getJSONObject(i).get("loginId"));
                    info.setLogin_name((String) jsonArray.getJSONObject(i).get("loginName"));
                    info.setPassword((String) jsonArray.getJSONObject(i).get("password"));
                    info.setFlag((String) jsonArray.getJSONObject(i).get("flag"));
                    info.setLine((String) jsonArray.getJSONObject(i).get("line"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }
}
