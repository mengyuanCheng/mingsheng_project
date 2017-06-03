package com.grgbanking.ct.qcode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/11/11.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class Qcode {

    private String qcode;
    private String rfidNum;
    private List<String> qcodeList = new ArrayList<>();

    public Qcode() {

    }

    public String getRfidNum() {
        return rfidNum;
    }

    public void setRfidNum(String rfidNum) {
        this.rfidNum = rfidNum;
    }

    public List<String> getQcodeList() {
        return qcodeList;
    }

    public void setQcodeList(List<String> qcodeList) {
        this.qcodeList = qcodeList;
    }

    public String getQcode() {
        return qcode;
    }

    public void setQcode(String qcode) {
        this.qcode = qcode;
    }
}
