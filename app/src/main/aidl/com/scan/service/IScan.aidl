// IScan.aidl
package com.scan.service;
import com.scan.service.IScanResult;
interface IScan {
    int init();  //init scaner engine

    void close();

    void scan();  //start scanning

    void setOnResultListener(IScanResult iLister); //listen scan result

    void setChar(String charSetName);
}
