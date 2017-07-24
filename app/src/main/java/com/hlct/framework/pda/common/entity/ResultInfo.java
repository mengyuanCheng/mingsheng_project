package com.hlct.framework.pda.common.entity;

import com.hlct.framework.business.message.entity.PdaLoginMessage;

/**
 * @author ：     cmy
 * @version :     2017/2/21.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */
public class ResultInfo implements java.io.Serializable{



    //	private JSONArray jsonArray;
    //	private JSONObject jsonObject = new JSONObject();
    /** 序列化 必须和服务端保持一致*/
    private static final long serialVersionUID=7981560250804078637L;
    public static final String CODE_SUCCESS = "1";
    public static final String CODE_ERROR = "2";
    public static final String CODE_PC = "3";//配钞人员
    public static final String CODE_YY = "4";//押运人员
    private String code;
    private String message;
    private String text;
    private PdaLoginMessage pdaLogMess;

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
    //	public JSONArray getJsonArray() {
    //		return jsonArray;
    //	}
    //	public void setJsonArray(JSONArray jsonArray) {
    //		this.jsonArray = jsonArray;
    //	}
    //	public JSONObject getJsonObject() {
    //		return jsonObject;
    //	}
    //	public void setJsonObject(JSONObject jsonObject) {
    //		this.jsonObject = jsonObject;
    //	}

//    @SuppressWarnings("unchecked")
//    public String toJSONString() {
//        JSONObject object = JSONObject.fromObject(this);
//        return object.toString();
//    }

    public PdaLoginMessage getPdaLogMess() {
        return pdaLogMess;
    }

    public void setPdaLogMess(PdaLoginMessage pdaLogMess) {
        this.pdaLogMess = pdaLogMess;
    }

}
