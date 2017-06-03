package com.grgbanking.ct.database;

/**
 * Created by cmy on 2016/9/14.
 */
public class NetMan {

    public String getNetPersonId() {
        return netPersonId;
    }

    public void setNetPersonId(String netPersonId) {
        this.netPersonId = netPersonId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getNetPersonName() {
        return netPersonName;
    }

    public void setNetPersonName(String netPersonName) {
        this.netPersonName = netPersonName;
    }

    public String getNetPersonRFID() {
        return netPersonRFID;
    }

    public void setNetPersonRFID(String netPersonRFID) {
        this.netPersonRFID = netPersonRFID;
    }

    String netPersonId;     //网点人员Id
    String bankId;          //网点号
    String netPersonName;   //网点人员姓名
    String netPersonRFID;   //网点人员RFID


    public NetMan(String netPersonId,
                  String bankId,
                  String netPersonName,
                  String netPersonRFID) {
        this.netPersonId = netPersonId;
        this.bankId = bankId;
        this.netPersonName = netPersonName;
        this.netPersonRFID = netPersonRFID;
    }

    public NetMan() {

    }

}
