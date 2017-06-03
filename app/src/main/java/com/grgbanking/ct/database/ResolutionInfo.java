package com.grgbanking.ct.database;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author ：     cmy
 * @version :     2016/10/24.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ResolutionInfo {

    private String code;
    private String message;
    private String text;
    private JSONArray jsonArray;
    private JSONObject jsonObject;


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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
