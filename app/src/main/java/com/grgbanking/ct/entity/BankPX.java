package com.grgbanking.ct.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lazylee on 2017/11/28.
 */
@Entity
public class BankPX implements Serializable {

    private static final long serialVersionUID = 7981560250804079659L;

    private String bankName;
    private String bankId;
    private int hundredDeno;
    private int fiftyDeno;
    private int twentyDeno;
    private int tenDeno;
    private int fiveDeno;
    private int oneDeno;
    private String totalSum;
    private String otherInfo;

    @Generated(hash = 2038326380)
    public BankPX(String bankName, String bankId, int hundredDeno, int fiftyDeno, int twentyDeno,
            int tenDeno, int fiveDeno, int oneDeno, String totalSum, String otherInfo) {
        this.bankName = bankName;
        this.bankId = bankId;
        this.hundredDeno = hundredDeno;
        this.fiftyDeno = fiftyDeno;
        this.twentyDeno = twentyDeno;
        this.tenDeno = tenDeno;
        this.fiveDeno = fiveDeno;
        this.oneDeno = oneDeno;
        this.totalSum = totalSum;
        this.otherInfo = otherInfo;
    }

    @Generated(hash = 827785452)
    public BankPX() {
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getHundredDeno() {
        return this.hundredDeno;
    }

    public void setHundredDeno(int hundredDeno) {
        this.hundredDeno = hundredDeno;
    }

    public int getFiftyDeno() {
        return this.fiftyDeno;
    }

    public void setFiftyDeno(int fiftyDeno) {
        this.fiftyDeno = fiftyDeno;
    }

    public int getTwentyDeno() {
        return this.twentyDeno;
    }

    public void setTwentyDeno(int twentyDeno) {
        this.twentyDeno = twentyDeno;
    }

    public int getTenDeno() {
        return this.tenDeno;
    }

    public void setTenDeno(int tenDeno) {
        this.tenDeno = tenDeno;
    }

    public int getFiveDeno() {
        return this.fiveDeno;
    }

    public void setFiveDeno(int fiveDeno) {
        this.fiveDeno = fiveDeno;
    }

    public int getOneDeno() {
        return this.oneDeno;
    }

    public void setOneDeno(int oneDeno) {
        this.oneDeno = oneDeno;
    }

    public String getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(String totalSum) {
        this.totalSum = totalSum;
    }

    public String getOtherInfo() {
        return this.otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public static List<BankPX> JSONArraytoBankPXList(JSONArray jsonArray) {
        List<BankPX> list = null;

        if (jsonArray != null && jsonArray.length() > 0) {
            list = new ArrayList<BankPX>();

            for (int i = 0; i < jsonArray.length(); i++) {
                BankPX bankPX = new BankPX();
                try {
                    bankPX.setBankName((String) jsonArray.getJSONObject(i).get("bankName"));
                    bankPX.setBankId((String) jsonArray.getJSONObject(i).get("bankid"));
                    bankPX.setHundredDeno((int) jsonArray.getJSONObject(i).get("hundredDeno"));
                    bankPX.setFiftyDeno((int) jsonArray.getJSONObject(i).get("fiftyDeno"));
                    bankPX.setTwentyDeno((int) jsonArray.getJSONObject(i).get("twentyDeno"));
                    bankPX.setTenDeno((int) jsonArray.getJSONObject(i).get("tenDeno"));
                    bankPX.setFiveDeno((int) jsonArray.getJSONObject(i).get("fiveDeno"));
                    bankPX.setOneDeno((int) jsonArray.getJSONObject(i).get("oneDeno"));
                    bankPX.setTotalSum((String) jsonArray.getJSONObject(i).get("totalSum"));
                    bankPX.setOtherInfo((String) jsonArray.getJSONObject(i).get("otherInfo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(bankPX);
            }
        }
        return list;
    }



    
    /**
     * 获取钱捆总数
     * @return  sum
     */
    public int getSum() {
        return getFiftyDeno() + getHundredDeno() + getTwentyDeno() +
                getTenDeno() + getFiveDeno() + getOneDeno();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BankPX bankPX = (BankPX) o;

        return bankId.equals(bankPX.bankId);
    }

    @Override
    public int hashCode() {
        return bankId.hashCode();
    }
}
