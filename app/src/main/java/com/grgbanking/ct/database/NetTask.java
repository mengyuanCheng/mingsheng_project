package com.grgbanking.ct.database;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class NetTask {
    private String bankId;
    private String bankName;
    private String netTaskStatus;
    private String rfidNum;
    private String boxSn;

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

    public String getRfidNum() {
        return rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public String getBoxSn() {
        return boxSn;
    }

    public void setBoxSn(String boxSn) {
        this.boxSn = boxSn;
    }

    public NetTask(String bankId,
                   String bankName,
                   String netTaskStatus,
                   String rfidNum,
                   String boxSn) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.netTaskStatus = netTaskStatus;
        this.rfidNum = rfidNum;
        this.boxSn = boxSn;
    }

    public NetTask(){

    }
}
