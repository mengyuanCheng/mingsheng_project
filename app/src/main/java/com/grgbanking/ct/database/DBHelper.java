package com.grgbanking.ct.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cmy on 2016/9/24.
 * emil ：mengyuan.cheng.mier@gmail.com
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * table 配箱表
     */
    public static final String TABLE_PeiXiangInfo_NAME = "PeiXiang";

    /**
     * table 押运人员
     */
    public static final String TABLE_ConvoyMan_NAME = "ConvoyMan";
    /**
     * table 网点人员
     */
    public static final String TABLE_NetMan_NAME = "NetMan";
    /**
     * table 登录人员
     */
    public static final String TABLE_LoginMan_NAME = "LoginMan";
    /**
     * 网点信息
     */
    public static final String TABLE_PdaNetInfo_NAME = "PdaNetInfo";
    /**
     * 款箱
     */
    public static final String TABLE_PdaCashboxInfo_NAME = "PdaCashboxInfo";
    /**
     * 网点任务
     */
    public static final String TABLE_NetTask_NAME = "NetTask";
    /**
     * 记录表
     */
    public static final String TABLE_WATERNET_NAME = "Waternet";

    /**
     * (网点出库任务记录)
     */
    public static final String TABLE_NETOUT_NAME = "Netout";
    /**
     * 流水表(网点入库任务记录)
     */
    public static final String TABLE_RECORDNET_NAME = "Recordnet";
    /**
     * 出库任务表
     */
    public static final String TABLE_EXTRACT_NAME = "Extract";
    /**
     * 出库款箱表
     */
    public static final String TABLE_EXTRACTBOXS_NAME = "ExtractBoxs";
    //database name
    private static final String DATABASE_NAME = "hlctPDA.db";
    private static final SQLiteDatabase.CursorFactory DATABASE_FACTORY = null;
    private static final int DATABASE_VERSION = 1;
    private String sql_create_PeiXiang = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PeiXiangInfo_NAME +
            "(BoxNum VARCHAR,"+
            "scanningDate VARCHAR," +
            "QR_code VARCHAR)";

    private String sql_create_ConvoyMan = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ConvoyMan_NAME +
            "(guardManId VARCHAR(20) PRIMARY KEY," +
            " guardManName VARCHAR(20)," +
            " guardManRFID VARCHAR(20))";

    private String sql_create_NetMan = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NetMan_NAME +
            "(netPersonId VARCHAR ," +
            " bankId VARCHAR," +
            " netPersonName VARCHAR," +
            " netPersonRFID VARCHAR)";

    private String sql_create_LoginMan = "CREATE TABLE IF NOT EXISTS " +
            TABLE_LoginMan_NAME +
            "(loginId VARCHAR PRIMARY KEY ," +
            "loginName VARCHAR," +
            "password VARCHAR," +
            "flag VARCHAR," +
            "line VARCHAR)";

    private String sql_create_PdaNetInfo = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PdaNetInfo_NAME +
            "(bankId VARCHAR PRIMARY KEY ," +
            "bankName VARCHAR," +
            "netTaskStatus VARCHAR," +
            "lineSn VARCHAR," +
            "lineId VARCHAR," +
            "flag VARCHAR)";

    private String sql_create_PdaCashboxInfo = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PdaCashboxInfo_NAME +
            "(rfidNum VARCHAR," +
            "bankId VARCHAR," +
            "boxSn VARCHAR)";

    private String sql_create_NetTask = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NetTask_NAME +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "bankId VARCHAR," +
            "bankName VARCHAR," +
            "netTaskStatus VARCHAR," +
            "rfidNum VARCHAR," +
            "boxSn VARCHAR)";


    private String sql_create_Waternet = "CREATE TABLE IF NOT EXISTS " +
            TABLE_WATERNET_NAME +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "scanningNetid INTEGER," +
            "boxSn VARCHAR," +
            "boxId VARCHAR," +
            "bankId VARCHAR," +
            "scanningDate VARCHAR," +
            "status VARCHAR," +
            "ScanningType VARCHAR)";

    private String sql_create_Recordnet = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RECORDNET_NAME +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "lineSn VARCHAR," +
            "scanningDate VARCHAR," +
            "bankman VARCHAR," +
            "bankman2 VARCHAR," +
            "guardman VARCHAR," +
            "guardman2 VARCHAR," +
            "lineType VARCHAR," +
            "scanStatus VARCHAR," +
            "note VARCHAR," +
            "bankId VARCHAR," +
            "bankmanId VARCHAR," +
            "bankmanId2 VARCHAR," +
            "guardmanId VARCHAR," +
            "guardmanId2 VARCHAR," +
            "lineId VARCHAR)";

    private String sql_create_Netout = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NETOUT_NAME +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "lineSn VARCHAR," +
            "scanningDate VARCHAR," +
            "bankman VARCHAR," +
            "bankman2 VARCHAR," +
            "guardman VARCHAR," +
            "guardman2 VARCHAR," +
            "lineType VARCHAR," +
            "scanStatus VARCHAR," +
            "note VARCHAR," +
            "bankId VARCHAR," +
            "bankmanId VARCHAR," +
            "bankmanId2 VARCHAR," +
            "guardmanId VARCHAR," +
            "guardmanId2 VARCHAR," +
            "lineId VARCHAR)";

    private String sql_create_Extract = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EXTRACT_NAME +
            "(bankId VARCHAR," +
            "bankName VARCHAR," +
            "netTaskStatus VARCHAR," +
            "lineSn VARCHAR," +
            "lineId VARCHAR)";

    private String sql_create_ExtractBoxs = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EXTRACTBOXS_NAME +
            "(rfidNum VARCHAR PRIMARY KEY ," +
            "bankId VARCHAR," +
            "boxSn VARCHAR)";


    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //        Log.i("", DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "onCreate被调用了");
        //创建表
        db.execSQL(sql_create_PeiXiang);
        db.execSQL(sql_create_ConvoyMan);
        db.execSQL(sql_create_NetMan);
        db.execSQL(sql_create_LoginMan);
        db.execSQL(sql_create_PdaNetInfo);
        db.execSQL(sql_create_PdaCashboxInfo);
        db.execSQL(sql_create_NetTask);
        db.execSQL(sql_create_Waternet);
        db.execSQL(sql_create_Recordnet);
        db.execSQL(sql_create_Netout);
        db.execSQL(sql_create_Extract);
        db.execSQL(sql_create_ExtractBoxs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //        db.execSQL("ALTER TABLE " +
        //                TABLE_NAME +
        //                " ADD COLUMN other STRING");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
