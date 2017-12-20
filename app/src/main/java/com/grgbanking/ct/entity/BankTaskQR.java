package com.grgbanking.ct.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 记录配箱页面中 网点任务中的钞票捆 所属哪个银行,面值和它的QRcode;
 * Created by lazylee on 2017/11/29.
 */
@Entity
public class BankTaskQR {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String bankName;
    @NotNull
    private String faceValue;
    private String qrCode;



    @Generated(hash = 1193836961)
    public BankTaskQR(Long id, @NotNull String bankName, @NotNull String faceValue,
            String qrCode) {
        this.id = id;
        this.bankName = bankName;
        this.faceValue = faceValue;
        this.qrCode = qrCode;
    }

    @Generated(hash = 1435021561)
    public BankTaskQR() {
    }

    

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFaceValue() {
        return this.faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BankTaskQR that = (BankTaskQR) o;

        if (bankName != null ? !bankName.equals(that.bankName) : that.bankName != null)
            return false;
        if (faceValue != null ? !faceValue.equals(that.faceValue) : that.faceValue != null)
            return false;
        return qrCode != null ? qrCode.equals(that.qrCode) : that.qrCode == null;
    }

    @Override
    public int hashCode() {
        int result = bankName != null ? bankName.hashCode() : 0;
        result = 31 * result + (faceValue != null ? faceValue.hashCode() : 0);
        result = 31 * result + (qrCode != null ? qrCode.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}