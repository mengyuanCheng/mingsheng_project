package com.grgbanking.ct.cach;

import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginMsg;
import com.hlct.framework.business.message.entity.PdaLoginMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 添加DataCach数据时注意更新clearAllDataCach()方法
 * 调用DataCach时注意先调用clearAllDataCach()方法
 */
public class DataCach {

    public static LoginUser loginUser = new LoginUser();

    /**
     * 1:网点入库 ； 0：网点出库
     */
    public static String netType = null;

    public static LinkedHashMap<String, HashMap<String, Object>> taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
    public static LinkedHashMap<String, HashMap<String, Object>> boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
    public static PdaLoginMsg pdaLoginMsg = null;
    public static HashMap<String, String> codeMap = new HashMap<String, String>();
    public static HashMap<String, Object> qcodeMap = new HashMap();
    public static List<String> barcodeList = new ArrayList<>();
    private static PdaLoginMessage pdaLoginMessage = null;

    public static PdaLoginMsg getPdaLoginMsg() {
        return DataCach.pdaLoginMsg;
    }

    public static void setPdaLoginMsg(PdaLoginMsg pdaLoginMsg) {
        if (DataCach.pdaLoginMsg != null) {
            DataCach.pdaLoginMsg = null;
        }
        DataCach.pdaLoginMsg = pdaLoginMsg;
    }
    public static PdaLoginMessage getPdaLoginMessage() {
        return DataCach.pdaLoginMessage;
    }

    public static void setPdaLoginMessage(PdaLoginMessage pdaLoginMessage) {
        if (DataCach.pdaLoginMessage != null) {
            DataCach.pdaLoginMessage = null;
        }
        DataCach.pdaLoginMessage = pdaLoginMessage;
    }

    /**
     * 清空缓存
     */
    public static void clearAllDataCach() {
        barcodeList.clear();
        netType = "";
        taskMap = null;
        taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
        pdaLoginMessage = null;
        boxesMap = null;
        boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
        pdaLoginMsg = null;
        codeMap = new HashMap<String, String>();
        qcodeMap = new HashMap<String, Object>();
    }
}
