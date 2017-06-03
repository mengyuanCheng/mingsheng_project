package com.grgbanking.ct.database;

import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class NetInfo  implements Serializable {
    private String bankId;
    private String bankName;
    private String netTaskStatus;
    private String lineSn;
    private String lineId;
    //0是网点入库，1是网点出库
    private String flag;
    private List<PdaCashboxInfo> cashBoxInfoList;
    private List<PdaNetPersonInfo> netPersonInfoList;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNetTaskStatus() {
        return netTaskStatus;
    }

    public void setNetTaskStatus(String netTaskStatus) {
        this.netTaskStatus = netTaskStatus;
    }

    public NetInfo(String bankId,
                   String bankName,
                   String netTaskStatus) {

        this.bankId = bankId;
        this.bankName = bankName;
        this.netTaskStatus = netTaskStatus;
    }

    public NetInfo() {

    }

    public List<PdaNetPersonInfo> getNetPersonInfoList() {
        return netPersonInfoList;
    }

    public void setNetPersonInfoList(List<PdaNetPersonInfo> netPersonInfoList) {
        this.netPersonInfoList = netPersonInfoList;
    }

    public List<PdaCashboxInfo> getCashBoxInfoList() {
        return cashBoxInfoList;
    }

    public void setCashBoxInfoList(List<PdaCashboxInfo> cashBoxInfoList) {
        this.cashBoxInfoList = cashBoxInfoList;
    }

    public String getLineSn() {
        return lineSn;
    }

    public void setLineSn(String lineSn) {
        this.lineSn = lineSn;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
