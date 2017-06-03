package com.hlct.framework.business.message.entity;

import java.io.Serializable;

/**
 * @author ：     cmy
 * @version :     2017/4/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class PdaLineInfo implements Serializable {

    private static final long serialVersionUID = 7981560250804078669L;
    private String lineName;

    private String lineID;

    /**
     * 0是出库,1是入库
     */
    private String type;

    /**
     * 扫描状态
     */
    private String Status;

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return Status;
    }

    /**
     * 线路提交状态,0是未提交,1是已提交
     * @param status
     */
    public void setStatus(String status) {
        Status = status;
    }
}
