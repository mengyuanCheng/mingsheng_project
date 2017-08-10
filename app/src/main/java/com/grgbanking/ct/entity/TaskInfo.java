package com.grgbanking.ct.entity;

/**
 * @Author : mengyuan.cheng
 * @Version : 2017/7/24
 * @E-mail : mengyuan.cheng.mier@gmail.com
 * @Description :任务信息表 储存任务的完成状态
 */
public class TaskInfo {
    /**
     * 网点ID
     */
    private String bankID;
    /**
     * 完成时间
     */
    private String time;
    /**
     * 完成状态 0是完成,1是未完成
     */
    private String status;
    /**
     * 出库或入库 1是入库 ,0是出库
     */
    private String netType;


    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }
}
