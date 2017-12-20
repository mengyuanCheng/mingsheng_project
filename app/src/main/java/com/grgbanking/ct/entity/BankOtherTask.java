package com.grgbanking.ct.entity;

/**
 * 储存 其他信息 的 bean
 * Created by lazylee on 2017/11/30.
 */

public class BankOtherTask {

    private String mOther;
    private int finish;

    public BankOtherTask() {
    }

    public BankOtherTask(String mOther, int finish) {
        this.mOther = mOther;
        this.finish = finish;
    }

    public String getmOther() {
        return mOther;
    }

    public void setmOther(String mOther) {
        this.mOther = mOther;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        return "BankOtherTask{" +
                "mOther='" + mOther + '\'' +
                ", finish=" + finish +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BankOtherTask that = (BankOtherTask) o;

        return mOther != null ? mOther.equals(that.mOther) : that.mOther == null;
    }

    @Override
    public int hashCode() {
        return mOther != null ? mOther.hashCode() : 0;
    }
}
