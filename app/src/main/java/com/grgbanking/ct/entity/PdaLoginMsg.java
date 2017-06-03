package com.grgbanking.ct.entity;

import com.grgbanking.ct.database.Extract;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaGuardManInfo;
import com.hlct.framework.business.message.entity.PdaNetInfo;
import com.hlct.framework.business.message.entity.PdaUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */


public class PdaLoginMsg extends PdaLoginManInfo implements Serializable {

    private String lineId;
    private String lineSn;
    private String lineNotes;
    private String code;
    private String msg;
    private List<PdaUserInfo> pdaUserInfo;
    private List<PdaNetInfo> netInfoList;
    private List<PdaGuardManInfo> pdaGuardManInfo;
    private List<PdaLoginManInfo> pdaLoginManInfo;
    private Map<String, String> allPdaBoxsMap;
    private List<PdaCashboxInfo> pdaCashboxInfo;
    private List<Extract> extracts;


    public List<PdaUserInfo> getPdaUserInfo() {
        return pdaUserInfo;
    }

    public void setPdaUserInfo(List<PdaUserInfo> pdaUserInfo) {
        this.pdaUserInfo = pdaUserInfo;
    }

    public List<PdaCashboxInfo> getPdaCashboxInfo() {
        return pdaCashboxInfo;
    }

    public void setPdaCashboxInfo(List<PdaCashboxInfo> pdaCashboxInfo) {
        this.pdaCashboxInfo = pdaCashboxInfo;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<PdaNetInfo> getNetInfoList() {
        return netInfoList;
    }

    public void setNetInfoList(List<PdaNetInfo> netInfoList) {
        this.netInfoList = netInfoList;
    }

    public List<PdaGuardManInfo> getPdaGuardManInfo() {
        return pdaGuardManInfo;
    }

    public void setPdaGuardManInfo(List<PdaGuardManInfo> pdaGuardManInfo) {
        this.pdaGuardManInfo = pdaGuardManInfo;
    }

    public List<PdaLoginManInfo> getPdaLoginManInfo() {
        return pdaLoginManInfo;
    }

    public void setPdaLoginManInfo(List<PdaLoginManInfo> pdaLoginManInfo) {
        this.pdaLoginManInfo = pdaLoginManInfo;
    }

    public Map<String, String> getAllPdaBoxsMap() {
        return allPdaBoxsMap;
    }

    public void setAllPdaBoxsMap(Map<String, String> allPdaBoxsMap) {
        this.allPdaBoxsMap = allPdaBoxsMap;
    }

    public static PdaLoginMsg JSONtoPdaLoginMsg(JSONObject jsonObject) {
        PdaLoginMsg plm = null;
        if (jsonObject != null && jsonObject.length() > 0) {
            plm = new PdaLoginMsg();
        }
        try {
            plm.setLineId(jsonObject.getString("lineId"));
            plm.setLineSn(jsonObject.getString("lineSn"));
            plm.setLineNotes(jsonObject.getString("lineNotes"));
            plm.setCode(jsonObject.getString("code"));
            plm.setMsg(jsonObject.getString("message"));

            /**
             * setNetlnfoList
             */
            try {
                JSONArray netInfoArray = jsonObject.getJSONArray("netInfoList");
                List<PdaNetInfo> netInfoList = PdaNetInfo.JSONArraytoPdaNetInfo(netInfoArray);
                plm.setNetInfoList(netInfoList);
            } catch (Exception e) {

            }


            /**
             * setGuardManInfoList
             */
            JSONArray guardManInfoArray = jsonObject.getJSONArray("guardManInfoList");
            List<PdaGuardManInfo> guardManInfoList = PdaGuardManInfo.JSONArraytoPdaGuardManInfo(guardManInfoArray);
            plm.setPdaGuardManInfo(guardManInfoList);


            /**
             * set allPdaBoxsList
             */
            JSONArray allPdaBoxsArray = jsonObject.getJSONArray("allPdaBoxsList");
            Map<String, String> allPdaBoxsMap = PdaCashboxInfo.JSONArraytoPdaNetInfoMap(allPdaBoxsArray);
            plm.setAllPdaBoxsMap(allPdaBoxsMap);


            //            /**
            //             * setAllPdaBoxsList
            //             */
            //            JSONArray allPdaBoxsArray = jsonObject.getJSONArray("allPdaBoxsList");
            //            List<PdaCashboxInfo> cashboxInfoList = PdaCashboxInfo.JSONArraytoPdaNetInfo(allPdaBoxsArray);
            //            plm.setPdaCashboxInfo(cashboxInfoList);

            /**
             * setloginList
             */
            try {
                JSONArray loginListArray = jsonObject.getJSONArray("pdaLoginManInfo");
                List<PdaLoginManInfo> loginManInfoList = PdaLoginManInfo.JSONArraytoPdaLoginManInfo(loginListArray);
                plm.setPdaLoginManInfo(loginManInfoList);
            } catch (Exception e) {

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return plm;
    }

    public List<Extract> getExtracts() {
        return extracts;
    }

    public void setExtracts(List<Extract> extracts) {
        this.extracts = extracts;
    }
}
