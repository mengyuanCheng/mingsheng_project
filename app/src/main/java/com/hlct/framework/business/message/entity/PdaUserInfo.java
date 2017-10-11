package com.hlct.framework.business.message.entity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/10/27.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class PdaUserInfo implements Serializable{
    private static final long serialVersionUID=7981560250804078659L;
    private String loginId;
    private String loginName;
    private String password;
    private String flag;
    private String line;

    public static List<PdaUserInfo> JSONArraytoPdaLoginManInfo(JSONArray jsonArray) {
        List<PdaUserInfo> list = null;
        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<PdaUserInfo>();

            for (int i = 0; i < jsonArray.length(); i++) {
                PdaUserInfo info = new PdaUserInfo();
                try {
                    info.setLoginId((String) jsonArray.getJSONObject(i).get("loginId"));
                    info.setPassword((String) jsonArray.getJSONObject(i).get("password"));
                    info.setFlag((String) jsonArray.getJSONObject(i).get("flag"));
                    info.setLine((String) jsonArray.getJSONObject(i).get("line"));
                    info.setLogin_name((String) jsonArray.getJSONObject(i).get("loginName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(info);
            }
        }

        return list;
    }

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
}
