package com.grgbanking.ct.entity;

/**
 * 辅助类 用于记录某网点中 面额 任务数 和已完成数
 * Created by lazylee on 2017/11/29.
 */

public class BankDenoTask {

    private String deno;    //面值
    private int plan;       //计划数量
    private int finish;     //完成数量

    public BankDenoTask() {
    }

    public BankDenoTask(String deno, int plan, int finish) {
        this.deno = deno;
        this.plan = plan;
        this.finish = finish;
    }

    public String getDeno() {
        return deno;
    }

    public void setDeno(String deno) {
        this.deno = deno;
    }

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof BankDenoTask))
            return false;

        return (((BankDenoTask) o).deno.equals(getDeno()));
    }

}
