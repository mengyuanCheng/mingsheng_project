package com.grgbanking.ct.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grgbanking.ct.entity.ConvoyManInfo;
import com.grgbanking.ct.entity.PeiXiangInfo;
import com.grgbanking.ct.scan.Recordnet;
import com.grgbanking.ct.scan.Waternet;
import com.hlct.framework.business.message.entity.PdaCashboxInfo;
import com.hlct.framework.business.message.entity.PdaNetPersonInfo;
import com.hlct.framework.business.message.entity.PdaUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmy on 2016/10/13.
 * emil ：mengyuan.cheng.mier@gmail.com
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }


    /**
     * add convoyMan
     *
     * @param convoyMen
     */
    public void addConvoyMan(List<ConvoyManInfo> convoyMen) {

        try {
            ContentValues values = new ContentValues();
            for (ConvoyManInfo convoyMan : convoyMen) {
                db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select count(*)from ConvoyMan where guardManId=?", new String[]{convoyMan.getGuardManId()});
                cursor.moveToFirst();
                Long count = cursor.getLong(0);
                db.close();
                if (count < 1) {
                    db = helper.getWritableDatabase();
                    //                db.beginTransaction();  //开启事务
                    values.put("guardManId", convoyMan.getGuardManId());
                    values.put("guardManName", convoyMan.getGuardManName());
                    values.put("guardManRFID", convoyMan.getGuardManRFID());
                    db.insert(DBHelper.TABLE_ConvoyMan_NAME, null, values);
                    //                db.setTransactionSuccessful();//设置事物成功完成
                    db.close();
                }
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add netMan
     *
     * @param netMen
     */
    public void addNetMan(List<NetMan> netMen) {
        //        db.beginTransaction();//开始事物
        try {
            ContentValues values = new ContentValues();
            for (NetMan netMan : netMen) {
                db = helper.getWritableDatabase();
                values.put("netPersonId", netMan.getNetPersonId());
                values.put("bankId", netMan.getBankId());
                values.put("netPersonName", netMan.getNetPersonName());
                values.put("netPersonRFID", netMan.getNetPersonRFID());
                db.insert(DBHelper.TABLE_NetMan_NAME, null, values);
                db.close();
            }
            //            db.setTransactionSuccessful();//设置事物成功完成
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add LoginMan
     *
     * @param loginMen
     */
    public void addLoginMan(List<PdaUserInfo> loginMen) {
        try {
            ContentValues values = new ContentValues();
            for (PdaUserInfo loginMan : loginMen) {
                db = helper.getWritableDatabase();
                values.put("loginId", loginMan.getLoginId());
                values.put("loginName", loginMan.getLogin_name());
                values.put("password", loginMan.getPassword());
                values.put("flag", loginMan.getFlag());
                values.put("line", loginMan.getLine());
                db.insert(DBHelper.TABLE_LoginMan_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    public void cleanPeiXiang() {
        //        db.beginTransaction();
        db = helper.getWritableDatabase();
        if (queryPeiXiang().isEmpty()) {
            return;
        }
        db.execSQL("DELETE FROM " + DBHelper.TABLE_PeiXiangInfo_NAME);
    }

    public boolean addPeiXiang(List<PeiXiangInfo> peiXiangInfos) {
        Gson gson = new Gson();
        //                db.beginTransaction();//开始事物
        boolean isSaved = true;
        try {
            ContentValues values = new ContentValues();
            for (PeiXiangInfo px : peiXiangInfos) {
                db = helper.getWritableDatabase();
                values.put("BoxNum", px.getBoxNum());
                values.put("scanningDate", px.getScanningDate());
                values.put("boxName", px.getBoxName());
                values.put("QR_codelist", gson.toJson(px.getQR_codelist()));
                db.insert(DBHelper.TABLE_PeiXiangInfo_NAME, null, values);
                db.close();
                isSaved = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSaved = false;
        }
        return isSaved;
    }


    /**
     * add cashBox
     *
     * @param cashBoxes
     */
    public void addCashBox(List<CashBox> cashBoxes) {
        try {
            ContentValues values = new ContentValues();
            for (CashBox cashBox : cashBoxes) {
                db = helper.getWritableDatabase();
                values.put("rfidNum", cashBox.getRfidNum());
                values.put("bankId", cashBox.getBankId());
                values.put("boxSn", cashBox.getBoxSn());
                db.insert(DBHelper.TABLE_PdaCashboxInfo_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }

    /**
     * add NetInfo
     *
     * @param netInfos
     */
    public void addNetInfo(List<NetInfo> netInfos) {
        try {
            ContentValues values = new ContentValues();
            for (NetInfo netInfo : netInfos) {
                db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select count(*)from PdaNetInfo where bankId=?", new String[]{netInfo.getBankId()});
                cursor.moveToFirst();
                Long count = cursor.getLong(0);
                db.close();
                if (count < 1) {
                    db = helper.getWritableDatabase();
                    values.put("bankId", netInfo.getBankId());
                    values.put("bankName", netInfo.getBankName());
                    values.put("netTaskStatus", netInfo.getNetTaskStatus());
                    values.put("lineSn", netInfo.getLineSn());
                    values.put("lineId", netInfo.getLineId());
                    values.put("flag", netInfo.getFlag());
                    db.insert(DBHelper.TABLE_PdaNetInfo_NAME, null, values);
                    db.close();
                }

                //插入网点人员
                ContentValues values1 = new ContentValues();
                List<PdaNetPersonInfo> personList = netInfo.getNetPersonInfoList();
                if (personList != null && personList.size() > 0) {
                    for (PdaNetPersonInfo personInfo : personList) {
                        db = helper.getWritableDatabase();
                        values1.put("bankId", netInfo.getBankId());
                        values1.put("netPersonId", personInfo.getNetPersonId());
                        values1.put("netPersonName", personInfo.getNetPersonName());
                        values1.put("netPersonRFID", personInfo.getNetPersonRFID());
                        db.insert(DBHelper.TABLE_NetMan_NAME, null, values1);
                        db.close();
                    }
                }
                //插入款箱
                ContentValues values2 = new ContentValues();
                List<PdaCashboxInfo> boxList = netInfo.getCashBoxInfoList();
                if (boxList != null && boxList.size() > 0) {
                    for (PdaCashboxInfo cashboxInfo : boxList) {
                        db = helper.getWritableDatabase();
                        values2.put("bankId", cashboxInfo.getBankId());
                        values2.put("rfidNum", cashboxInfo.getRfidNum());
                        values2.put("boxSn", cashboxInfo.getBoxSn());
                        db.insert(DBHelper.TABLE_PdaCashboxInfo_NAME, null, values2);
                        db.close();
                    }
                }

            }
        } finally {
            //            db.endTransaction();//结束事物

        }
    }

    /**
     * add NetTask
     *
     * @param netTasks
     */
    public void addNetTask(List<NetTask> netTasks) {
        try {
            ContentValues values = new ContentValues();
            for (NetTask netTask : netTasks) {
                db = helper.getWritableDatabase();
                values.put("bankId", netTask.getBankId());
                values.put("bankName", netTask.getBankName());
                values.put("netTaskStatus", netTask.getNetTaskStatus());
                values.put("rfidNum", netTask.getRfidNum());
                values.put("boxSn", netTask.getBoxSn());
                db.insert(DBHelper.TABLE_NetTask_NAME, null, values);
                db.close();
            }
        } finally {
            //            db.endTransaction();//结束事物
        }
    }


    /**
     * 添加到 recordnet表
     *
     * @param recordnet
     */
    public void addRecordnet(Recordnet recordnet) {
        try {
            ContentValues values = new ContentValues();
            db = helper.getWritableDatabase();
            values.put("lineSn", recordnet.getLineSn());
            values.put("scanningDate", recordnet.getScanningDate());
            values.put("bankman", recordnet.getBankman());
            values.put("bankman2", recordnet.getBankman2());
            values.put("guardman", recordnet.getGuardman());
            values.put("guardman2", recordnet.getGuardman2());
            values.put("lineType", recordnet.getLineType());
            values.put("scanStatus", recordnet.getScanStatus());
            values.put("note", recordnet.getNote());
            values.put("bankId", recordnet.getBankId());
            values.put("bankmanId", recordnet.getBankmanId());
            values.put("bankmanId2", recordnet.getBankmanId2());
            values.put("guardmanId", recordnet.getGuardmanId());
            values.put("guardmanId2", recordnet.getGuardmanId2());
            values.put("lineId", recordnet.getLineId());
            db.insert(DBHelper.TABLE_RECORDNET_NAME, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加Waternet表
     *
     * @param waternet
     */
    public void addWaternet(Waternet waternet) {
        try {
            ContentValues values = new ContentValues();
            db = helper.getWritableDatabase();
            values.put("scanningNetid", waternet.getScanningNetid());
            values.put("boxSn", waternet.getBoxSn());
            values.put("boxId", waternet.getBoxId());
            values.put("bankId", waternet.getBankId());
            values.put("scanningDate", waternet.getScanningDate());
            values.put("status", waternet.getStatus());
            values.put("ScanningType", waternet.getScanningType());
            db.insert(DBHelper.TABLE_WATERNET_NAME, null, values);
            db.close();
        } finally {

        }
    }

    public void addExtract(Extract extract) {
        try {
            ContentValues values = new ContentValues();
            db = helper.getWritableDatabase();
            values.put("bankId", extract.getBankId());
            values.put("bankName", extract.getBankName());
            values.put("netTaskStatus", extract.getNetTaskStatus());
            values.put("lineSn", extract.getLineSn());
            values.put("lineId", extract.getLineId());

            //插入网点人员
            ContentValues values1 = new ContentValues();
            List<PdaNetPersonInfo> personList = extract.getNetPersonInfoList();
            if (personList != null && personList.size() > 0) {
                for (PdaNetPersonInfo personInfo : personList) {
                    db = helper.getWritableDatabase();
                    values1.put("bankId", extract.getBankId());
                    values1.put("netPersonId", personInfo.getNetPersonId());
                    values1.put("netPersonName", personInfo.getNetPersonName());
                    values1.put("netPersonRFID", personInfo.getNetPersonRFID());
                    db.insert(DBHelper.TABLE_NetMan_NAME, null, values1);
                    db.close();
                }
            }

            List<PdaCashboxInfo> el = extract.getCashBoxInfoList();
            for (int i = 0; i < el.size(); i++) {
                PdaCashboxInfo p = new PdaCashboxInfo();
                ExtractBoxs e = new ExtractBoxs();
                p = el.get(i);
                e.setRfidNum(p.getRfidNum());
                e.setBoxSn(p.getBoxSn());
                e.setBankId(p.getBankId());
                addExtractBoxs(e);
            }
            db.insert(DBHelper.TABLE_EXTRACT_NAME, null, values);
            db.close();
        } finally {

        }
    }

    /**
     * 添加到 ExtractBoxs表
     *
     * @param extractBoxs
     */
    public void addExtractBoxs(ExtractBoxs extractBoxs) {
        try {
            ContentValues values = new ContentValues();
            db = helper.getWritableDatabase();
            values.put("rfidNum", extractBoxs.getRfidNum());
            values.put("bankId", extractBoxs.getBankId());
            values.put("boxSn", extractBoxs.getBoxSn());
            db.insert(DBHelper.TABLE_EXTRACTBOXS_NAME, null, values);
            //            db.close();
        } finally {

        }
    }

    /**
     * 查询 Extract
     */
    public List<Extract> queryExtract() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Extract> extractList = new ArrayList<Extract>();
        Cursor c = db.rawQuery("SELECT * FROM Extract", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Extract extract = new Extract();
                    extract.setBankId(c.getString(0));
                    extract.setBankName(c.getString(1));
                    extract.setNetTaskStatus(c.getString(2));
                    extract.setLineSn(c.getString(3));
                    extract.setLineId(c.getString(4));
                    extractList.add(extract);
                } while (c.moveToNext());
            }
        }
        return extractList;
    }

    /**
     * 查询 ExtractBoxs
     */

    public List<ExtractBoxs> queryExtractBoxs() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ArrayList<ExtractBoxs> ExtractBoxsList = new ArrayList<ExtractBoxs>();
            Cursor c = db.rawQuery("SELECT * FROM ExtractBoxs", null);
            c.moveToFirst();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        ExtractBoxs boxs = new ExtractBoxs();
                        boxs.setRfidNum(c.getString(0));
                        boxs.setBankId(c.getString(1));
                        boxs.setBoxSn(c.getString(2));
                        ExtractBoxsList.add(boxs);
                    } while (c.moveToNext());
                }
            }

            return ExtractBoxsList;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 查询 recordnet表
     */
    public List<Recordnet> queryRecordnet() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Recordnet> recordnetArrayList = new ArrayList<Recordnet>();
        Cursor c = db.rawQuery("SELECT * FROM Recordnet", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Recordnet r = new Recordnet();
                    int id = c.getInt(0);
                    String lineSn = c.getString(1);
                    String scanningDate = c.getString(2);
                    String bankman = c.getString(3);
                    String bankman2 = c.getString(4);
                    String guardman = c.getString(5);
                    String guardman2 = c.getString(6);
                    String lineType = c.getString(7);
                    String scanStatus = c.getString(8);
                    String note = c.getString(9);
                    String bankId = c.getString(10);
                    String bankmanId = c.getString(11);
                    String bankmanId2 = c.getString(12);
                    String guardmanId = c.getString(13);
                    String guardmanId2 = c.getString(14);
                    String lineId = c.getString(15);
                    r.setId(id);
                    r.setLineSn(lineSn);
                    r.setScanningDate(scanningDate);
                    r.setBankman(bankman);
                    r.setBankman2(bankman2);
                    r.setGuardman(guardman);
                    r.setGuardman2(guardman2);
                    r.setLineType(lineType);
                    r.setScanStatus(scanStatus);
                    r.setNote(note);
                    r.setBankId(bankId);
                    r.setBankmanId(bankmanId);
                    r.setBankmanId2(bankmanId2);
                    r.setGuardmanId(guardmanId);
                    r.setGuardmanId2(guardmanId2);
                    r.setLineId(lineId);
                    recordnetArrayList.add(r);
                } while (c.moveToNext());
            }
        }
        return recordnetArrayList;
    }

    /**
     * 查询 Waternet表
     *
     * @return
     */
    public List<Waternet> queryWaternet(int netId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Waternet> waternetArrayList = new ArrayList<Waternet>();
        Log.i("======", "===netId=" + netId);
        Cursor c = db.rawQuery("SELECT * FROM Waternet  where scanningNetid=?  ", new String[]{String.valueOf(netId)});
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                Waternet w = new Waternet();
                int id = c.getInt(0);
                int ScanningNetid = c.getInt(1);
                String ScanningDate = c.getString(2);
                String BoxId = c.getString(3);
                String BoxSn = c.getString(4);
                String BankId = c.getString(5);
                String Status = c.getString(6);
                String ScanningType = c.getString(7);
                w.setId(id);
                w.setScanningNetid(ScanningNetid);
                w.setBoxSn(BoxSn);
                w.setBoxId(BoxId);
                w.setBankId(BankId);
                w.setScanningDate(ScanningDate);
                w.setStatus(Status);
                w.setScanningType(ScanningType);
                waternetArrayList.add(w);
            } while (c.moveToNext());
        }
        return waternetArrayList;
    }

    /**
     * 网点人员查询
     *
     * @return ConvoyMan list
     */
    public List<ConvoyMan> queryConvoyMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<ConvoyMan> manList = new ArrayList<ConvoyMan>();
        Cursor c = db.rawQuery("SELECT * FROM ConvoyMan", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    ConvoyMan cMan = new ConvoyMan();
                    String id = c.getString(0);
                    String name = c.getString(1);
                    String rfid = c.getString(2);
                    cMan.setGuardManId(id);
                    cMan.setGuardManName(name);
                    cMan.setGuardManRFID(rfid);
                    manList.add(cMan);
                } while (c.moveToNext());
            }
        }
        return manList;
    }

    /**
     * 查询网点人员
     *
     * @return
     */
    public List<NetMan> queryNetMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetMan> manList = new ArrayList<NetMan>();
        Cursor c = db.rawQuery("SELECT * FROM NetMan", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetMan nMan = new NetMan();
                    String NetPersonId = c.getString(0);
                    String bankId = c.getString(1);
                    String netPersonName = c.getString(2);
                    String netPersonRFID = c.getString(3);
                    nMan.setNetPersonId(NetPersonId);
                    nMan.setBankId(bankId);
                    nMan.setNetPersonName(netPersonName);
                    nMan.setNetPersonRFID(netPersonRFID);
                    manList.add(nMan);
                } while (c.moveToNext());
            }
        }
        return manList;
    }

    /**
     * 根据网点号查询网点人员
     *
     * @return
     */
    public List<NetMan> queryNetManByBankId(String bankId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetMan> manList = new ArrayList<NetMan>();
        Cursor c = db.rawQuery("SELECT * FROM NetMan where bankId = ?", new String[]{bankId});
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetMan nMan = new NetMan();
                    String NetPersonId = c.getString(0);
                    //                    String bankId = c.getString(1);
                    String netPersonName = c.getString(2);
                    String netPersonRFID = c.getString(3);
                    nMan.setNetPersonId(NetPersonId);
                    nMan.setBankId(bankId);
                    nMan.setNetPersonName(netPersonName);
                    nMan.setNetPersonRFID(netPersonRFID);
                    manList.add(nMan);
                } while (c.moveToNext());
            }
        }
        return manList;
    }

    /**
     * 查询登录人员
     *
     * @return
     */
    public List<LoginMan> queryLoginMan() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<LoginMan> manList = new ArrayList<LoginMan>();
        Cursor c = db.rawQuery("SELECT * FROM LoginMan", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    LoginMan lMan = new LoginMan();
                    String loginId = c.getString(0);
                    String login_name = c.getString(1);
                    String password = c.getString(2);
                    String flag = c.getString(3);
                    String line = c.getString(4);
                    lMan.setLoginId(loginId);
                    lMan.setLogin_name(login_name);
                    lMan.setPassword(password);
                    lMan.setFlag(flag);
                    lMan.setLine(line);
                    manList.add(lMan);
                } while (c.moveToNext());
            }
        }
        return manList;
    }

    /**
     * 根据RFID查询款箱名称
     *
     * @param rfidNum rfid号
     * @return boxName
     */
    public String queryCashBoxName(String rfidNum) {
        String boxName = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM PdaCashboxInfo where rfidNum = ?", new String[]{rfidNum});
        if (c.moveToFirst()) {
            boxName = c.getString(c.getColumnIndex("BoxSn"));
        }
        c.close();
        return boxName;
    }

    /**
     * 查询所有款箱
     *
     * @return ArrayList<CashBox>
     */
    public List<CashBox> queryCashBox() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<CashBox> boxList = new ArrayList<CashBox>();
        Cursor c = db.rawQuery("SELECT * FROM PdaCashboxInfo", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    CashBox cb = new CashBox();
                    String rfidNum = c.getString(0);
                    String bankId = c.getString(1);
                    String boxSn = c.getString(2);
                    cb.setRfidNum(rfidNum);
                    cb.setBankId(bankId);
                    cb.setBoxSn(boxSn);
                    boxList.add(cb);
                } while (c.moveToNext());
            }
        }
        return boxList;
    }

    /**
     * 查询网点信息
     *
     * @return
     */
    public List<NetInfo> queryNetInfo() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetInfo> netInfos = new ArrayList<NetInfo>();
        Cursor c = db.rawQuery("SELECT * FROM PdaNetInfo", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetInfo nf = new NetInfo();
                    String bankId = c.getString(0);
                    String bankName = c.getString(1);
                    String netTaskStatus = c.getString(2);
                    String lineSn = c.getString(3);
                    String lineId = c.getString(4);
                    String flag = c.getString(5);
                    nf.setBankId(bankId);
                    nf.setBankName(bankName);
                    nf.setNetTaskStatus(netTaskStatus);
                    nf.setLineSn(lineSn);
                    nf.setLineId(lineId);
                    nf.setFlag(flag);
                    netInfos.add(nf);
                } while (c.moveToNext());
            }
        }

        return netInfos;
    }

    /**
     * 查询NetTask
     *
     * @return
     */
    public List<NetTask> queryNetTask() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NetTask> netTasks = new ArrayList<NetTask>();
        Cursor c = db.rawQuery("SELECT * FROM NetTask", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NetTask nt = new NetTask();
                    String bankId = c.getString(0);
                    String bankName = c.getString(1);
                    String netTaskStatus = c.getString(2);
                    String rfidNum = c.getString(3);
                    String boxSn = c.getString(4);
                    nt.setBankId(bankId);
                    nt.setBankName(bankName);
                    nt.setNetTaskStatus(netTaskStatus);
                    nt.setRfidNum(rfidNum);
                    nt.setBoxSn(boxSn);
                    netTasks.add(nt);
                } while (c.moveToNext());
            }
        }

        return netTasks;
    }

    public String queryLogin(String loginName, String password) {
        String flag = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT flag FROM LoginMan where loginName=? and password=? ", new String[]{loginName, password});
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    flag = c.getString(0);
                } while (c.moveToNext());
            }
        }
        return flag;
        //        Cursor cursor = db.query(true, DBHelper.TABLE_LoginMan_NAME, new String[]{"_id, name, age"},
        //                "name like ?", new String[]{"乔%"},
        //                null, null,
        //                "_id desc", "5, 10");
        //        cursor.close();
    }

    /**
     * 查询流水表当前最大值
     *
     * @return ConvoyMan list
     */
    public int queryMaxRecordNet() {
        SQLiteDatabase db = helper.getReadableDatabase();
        int maxId = 0;
        Cursor c = db.rawQuery("select max(id) from Recordnet", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    maxId = c.getInt(0);
                } while (c.moveToNext());
            }
        }
        return maxId;
    }

    public ArrayList<PeiXiangInfo> queryPeiXiang() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Gson gson = new Gson();
        ArrayList<PeiXiangInfo> peiXiangInfoArrayList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM PeiXiang", null);
        c.moveToFirst();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    PeiXiangInfo px = new PeiXiangInfo();
                    String BoxNum = c.getString(0);
                    String scanningDate = c.getString(1);
                    String boxName = c.getString(2);
                    ArrayList<String> mList = gson.fromJson(c.getString(3), new TypeToken<ArrayList<String>>() {
                    }.getType());
                    px.setBoxNum(BoxNum);
                    px.setScanningDate(scanningDate);
                    px.setBoxName(boxName);
                    px.setQR_codelist(mList);
                    peiXiangInfoArrayList.add(px);
                } while (c.moveToNext());
            }
        }
        return peiXiangInfoArrayList;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

    public void delete() {
        try {
            db = helper.getWritableDatabase();
            db.delete(DBHelper.TABLE_LoginMan_NAME, null, null);
            db.delete(DBHelper.TABLE_ConvoyMan_NAME, null, null);
            db.delete(DBHelper.TABLE_NetMan_NAME, null, null);
            db.delete(DBHelper.TABLE_NetTask_NAME, null, null);
            db.delete(DBHelper.TABLE_PdaNetInfo_NAME, null, null);
            db.delete(DBHelper.TABLE_PdaCashboxInfo_NAME, null, null);
            db.delete(DBHelper.TABLE_LoginMan_NAME, null, null);
            db.delete(DBHelper.TABLE_RECORDNET_NAME, null, null);
            db.delete(DBHelper.TABLE_WATERNET_NAME, null, null);
            db.delete(DBHelper.TABLE_EXTRACT_NAME, null, null);
            db.delete(DBHelper.TABLE_EXTRACTBOXS_NAME, null, null);
            db.close();
        } catch (Exception e) {
            Log.e("DBManager", "" + e);
        }

        //        db.endTransaction();//结束事物
    }
}
