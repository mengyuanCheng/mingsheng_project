package com.grgbanking.ct.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 保存 在统计未扫描二维码.
 * Created by lazylee on 2017/12/11.
 */
@Entity
public class QRString {
    @Id
    @Unique
    String qrCode;

    @Generated(hash = 231950316)
    public QRString(String qrCode) {
        this.qrCode = qrCode;
    }

    @Generated(hash = 1683701817)
    public QRString() {
    }
    
    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
