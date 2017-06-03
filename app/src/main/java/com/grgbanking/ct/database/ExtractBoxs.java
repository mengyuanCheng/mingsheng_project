package com.grgbanking.ct.database;

import java.io.Serializable;

/**
 * @author ：     cmy
 * @version :     2016/11/4.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ExtractBoxs implements Serializable {
    private String rfidNum;
    private String bankId;
    private String boxSn;



    public ExtractBoxs() {

    }


    public String getRfidNum() {
        return rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBoxSn() {
        return boxSn;
    }

    public void setBoxSn(String boxSn) {
        this.boxSn = boxSn;
    }
}
