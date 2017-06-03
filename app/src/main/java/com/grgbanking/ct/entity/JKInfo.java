package com.grgbanking.ct.entity;

/**
 * @author ：     cmy
 * @version :     2017/4/23.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class JKInfo {
    //押运人员ID1
    private String ConvoyManID1;
    //押运人员ID2
    private String ConvoyManID2;
    //网点人员ID1
    private String NetPersonId1;
    //网点人员ID2
    private String NetPersonId2;
    //网点人员1
    private String NetPersion1;
    //网点人员2
    private String NetPersion2;
    //押运人员1
    private String ConvoyMan1;
    //押运人员2
    private String ConvoyMan2;
    //线路ID
    private String LineID;
    //出库or入库
    private String LineType;
    //线路名称
    private String LineName;
    //当前时间日期
    private String scanningData;

    public String getScanningData() {
        return scanningData;
    }

    public void setScanningData(String data) {
        scanningData = data;
    }

    public String getNetPersion1() {
        return NetPersion1;
    }

    public void setNetPersion1(String netPersion1) {
        NetPersion1 = netPersion1;
    }

    public String getNetPersion2() {
        return NetPersion2;
    }

    public void setNetPersion2(String netPersion2) {
        NetPersion2 = netPersion2;
    }

    public String getConvoyMan1() {
        return ConvoyMan1;
    }

    public void setConvoyMan1(String convoyMan1) {
        ConvoyMan1 = convoyMan1;
    }

    public String getConvoyMan2() {
        return ConvoyMan2;
    }

    public void setConvoyMan2(String convoyMan2) {
        ConvoyMan2 = convoyMan2;
    }

    public String getLineID() {
        return LineID;
    }

    public void setLineID(String lineID) {
        LineID = lineID;
    }

    public String getLineType() {
        return LineType;
    }

    public void setLineType(String lineType) {
        LineType = lineType;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getNetPersonId1() {
        return NetPersonId1;
    }

    public void setNetPersonId1(String netPersonId1) {
        NetPersonId1 = netPersonId1;
    }

    public String getNetPersonId2() {
        return NetPersonId2;
    }

    public void setNetPersonId2(String netPersonId2) {
        NetPersonId2 = netPersonId2;
    }

    public String getConvoyManID2() {
        return ConvoyManID2;
    }

    public void setConvoyManID2(String convoyManID2) {
        ConvoyManID2 = convoyManID2;
    }

    public String getConvoyManID1() {
        return ConvoyManID1;
    }

    public void setConvoyManID1(String convoyManID1) {
        ConvoyManID1 = convoyManID1;
    }

}
