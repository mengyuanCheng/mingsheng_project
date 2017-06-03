package com.grgbanking.ct.scan;

/**
 * @author ：     cmy
 * @version :     2016/11/1.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class Waternet {
    private int id;//自增ID
    private int scanningNetid; //这个表b_scanning_recordnet 的外键
    private String boxSn; //箱包名称
    private String boxId; //箱包ID
    private String bankId;//网点ID
    private String scanningDate;//日期
    private String status; //0:正确入库   1：错误入库  2：遗漏
    private String ScanningType;//1:网点入库 ； 0：网点出库

    public Waternet() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScanningNetid() {
        return scanningNetid;
    }

    public void setScanningNetid(int scanningNetid) {
        this.scanningNetid = scanningNetid;
    }

    public String getScanningType() {
        return ScanningType;
    }

    public void setScanningType(String scanningType) {
        ScanningType = scanningType;
    }

    public String getBoxSn() {
        return boxSn;
    }

    public void setBoxSn(String boxSn) {
        this.boxSn = boxSn;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScanningDate() {
        return scanningDate;
    }

    public void setScanningDate(String scanningDate) {
        this.scanningDate = scanningDate;
    }

}
