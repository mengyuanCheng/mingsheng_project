package com.grgbanking.ct.http;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultInfo  {
    /**
     * 配钞人员
     */
    public static final String CODE_PEIXIANG = "3";
    /**
     * 押运人员
     */
    public static final String CODE_GUARDMANIINFO = "4";
    public static final String CODE_SUCCESS = "1";
    public static final String CODE_ERROR = "2";
    private String code;
    private String message;
    /**
     * 数据信息
     */
    private String text;
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    public ResultInfo(){

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
