package com.grgbanking.ct.database;

/**
 * 押运人员
 * Created by cmy on 2016/9/14.
 */
public class ConvoyMan  {
    public String getGuardManId() {
        return guardManId;
    }

    public void setGuardManId(String guardManId) {
        this.guardManId = guardManId;
    }

    public String getGuardManName() {
        return guardManName;
    }

    public void setGuardManName(String guardManName) {
        this.guardManName = guardManName;
    }

    public String getGuardManRFID() {
        return guardManRFID;
    }

    public void setGuardManRFID(String guardManRFID) {
        this.guardManRFID = guardManRFID;
    }

    private String guardManId;      //押运人员ID
    private String guardManName;    //押运人员姓名
    private String guardManRFID;    //押运人员RFID

    public ConvoyMan(String guardManId,
                     String guardManName,
                     String guardManRFID) {
        this.guardManId = guardManId;
        this.guardManName = guardManName;
        this.guardManRFID = guardManRFID;
    }
    public ConvoyMan(){

    }
}
