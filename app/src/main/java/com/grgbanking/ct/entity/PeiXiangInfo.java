package com.grgbanking.ct.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author ：     cmy
 * @version :     2017/6/1.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class PeiXiangInfo implements Serializable {
    private String BoxNum;     //扫描到的rfid;
    private ArrayList<String> QR_codelist;
    private String boxName;    //根据boxNum查询到的配箱名
    private String scanningDate;

    public PeiXiangInfo (){

    }

    public PeiXiangInfo (String boxNum){
        this.BoxNum = boxNum;
    }

    public PeiXiangInfo(String boxNum,String boxName){
        this.boxName = boxName;
        this.BoxNum = boxNum;
    }


    public String getScanningDate() {
        return scanningDate;
    }

    public void setScanningDate(String scanningDate) {
        this.scanningDate = scanningDate;
    }

    public String getBoxNum() {
        return BoxNum;
    }

    public void setBoxNum(String boxNum) {
        BoxNum = boxNum;
    }

    public ArrayList<String> getQR_codelist() {
        return QR_codelist;
    }

    public void setQR_codelist(ArrayList<String> QR_codelist) {
        this.QR_codelist = QR_codelist;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    @Override
    public String toString() {
        return "PeiXiangInfo{" +
                "BoxNum='" + BoxNum + '\'' +
                ", QR_codelist=" + QR_codelist +
                ", boxName='" + boxName + '\'' +
                ", scanningDate='" + scanningDate + '\'' +
                '}';
    }
}
