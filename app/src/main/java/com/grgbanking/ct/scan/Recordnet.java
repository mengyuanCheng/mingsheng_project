package com.grgbanking.ct.scan;

/**
 * @author ：     cmy
 * @version :     2016/11/1.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class Recordnet {

    private int id;//自增ID
    private String lineSn;//线路名称
    private String scanningDate;//日期
    private String bankman; //网点人员名称
    private String guardman;//押运人员名称
    private String bankman2;//第二个网点人员
    private String guardman2;//第二个押运人员
    private String lineType;//1:网点入库 ； 0：网点出库
    private String scanStatus; //0：扫描正确 ; 1：扫描错误
    private String note;//备注信息
    private String lineId;//线路ID

    private String rightRfidNumsSub = "";
    private String missRfidNumsSub = "";
    private String errorRfidNumsSub = "";

    private String bankId;//网点号
    private String bankmanId;//网点人员ID
    private String bankmanId2;//网点人员ID2
    private String guardmanId;//押运人员ID
    private String guardmanId2;//押运人员ID2

    public String getBankmanId2() {
        return bankmanId2;
    }

    public void setBankmanId2(String bankmanId2) {
        this.bankmanId2 = bankmanId2;
    }

    public String getGuardmanId2() {
        return guardmanId2;
    }

    public void setGuardmanId2(String guardmanId2) {
        this.guardmanId2 = guardmanId2;
    }

    public Recordnet() {

    }

    public String getRightRfidNumsSub() {
        return rightRfidNumsSub;
    }

    public void setRightRfidNumsSub(String rightRfidNumsSub) {
        this.rightRfidNumsSub = rightRfidNumsSub;
    }

    public String getMissRfidNumsSub() {
        return missRfidNumsSub;
    }

    public void setMissRfidNumsSub(String missRfidNumsSub) {
        this.missRfidNumsSub = missRfidNumsSub;
    }

    public String getErrorRfidNumsSub() {
        return errorRfidNumsSub;
    }

    public void setErrorRfidNumsSub(String errorRfidNumsSub) {
        this.errorRfidNumsSub = errorRfidNumsSub;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankmanId() {
        return bankmanId;
    }

    public void setBankmanId(String bankmanId) {
        this.bankmanId = bankmanId;
    }

    public String getGuardmanId() {
        return guardmanId;
    }

    public void setGuardmanId(String guardmanId) {
        this.guardmanId = guardmanId;
    }

    public String getScanningDate() {
        return scanningDate;
    }

    public void setScanningDate(String scanningDate) {
        this.scanningDate = scanningDate;
    }

    public String getLineSn() {
        return lineSn;
    }

    public void setLineSn(String lineSn) {
        this.lineSn = lineSn;
    }

    public String getBankman() {
        return bankman;
    }

    public void setBankman(String bankman) {
        this.bankman = bankman;
    }

    public String getGuardman() {
        return guardman;
    }

    public void setGuardman(String guardman) {
        this.guardman = guardman;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(String scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }


    public String getBankman2() {
        return bankman2;
    }

    public void setBankman2(String bankman2) {
        this.bankman2 = bankman2;
    }

    public String getGuardman2() {
        return guardman2;
    }

    public void setGuardman2(String guardman2) {
        this.guardman2 = guardman2;
    }
}
