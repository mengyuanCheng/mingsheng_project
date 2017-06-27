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
    private String BoxNum;
    private ArrayList<String> QR_codelist;
    private String QR_code;
    private String scanningDate;

    public PeiXiangInfo (){

    }
    public PeiXiangInfo (String boxNum){
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

    public String getQR_code() {
        return QR_code;
    }

    public void setQR_code(String QR_code) {
        this.QR_code = QR_code;
    }



    @Override
    public String toString() {
        return "PeiXiangInfo{" +
                "BoxNum='" + BoxNum + '\'' +
                ", QR_codelist=" + QR_codelist +
                ", QR_code='" + QR_code + '\'' +
                ", scanningDate='" + scanningDate + '\'' +
                '}';
    }
}
