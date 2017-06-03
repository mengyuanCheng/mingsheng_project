package com.grgbanking.ct.database;

/**
 * @author ：     cmy
 * @version :     2016/10/27.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class CashBox {
    private String rfidNum;
    private String bankId;
    private String boxSn;

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

    public CashBox(String rfidNum,
                   String bankId,
                   String boxSn
    ) {
        this.rfidNum = rfidNum;
        this.bankId = bankId;
        this.boxSn = boxSn;
    }

    public CashBox(){

    }

}
