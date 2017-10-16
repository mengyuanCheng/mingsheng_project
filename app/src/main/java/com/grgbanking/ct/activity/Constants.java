package com.grgbanking.ct.activity;

public class Constants {

    public static final String USER_NAME = "user_name";//SharedPreferences的key用来获取用户名
    public static final String PASSWORD = "password";//SharedPreferences的key用来获取密码
    public static final String ISSAVEPASS = "savePassWord";//SharedPreferences的key用来获取状态

    //public  static final  String  BASE_SERVER_URL="http://183.63.190.43:6600/hbct"; //河北晨通正式服务器

    //	public  static final  String  BASE_SERVER_URL="http://183.63.190.43:6601/ycyt"; //宜昌宜通正式服务器
    public static final String BASE_SERVER_URL = "http://192.168.1.102:8080/android_jkweb/"; //宜昌宜通正式服务器
    //    public static final String BASE_SERVER_URL = "http://192.128.1.108/android_jkweb/"; //宜昌宜通正式服务器


    //public  static final  String  BASE_SERVER_URL="http://10.2.18.32/hbct";  //本机测试
    //public  static final  String  BASE_SERVER_URL="http://192.168.2.102/hbct";  //本机测试

    //	public  static final  String  BASE_SERVER_URL="http://192.168.72.102/hbct";  //本机测试

    //	public static final String URL_GET_USER=BASE_SERVER_URL+"/pda-user!pdaLogin.do";
    /**
     * PDA登录地址
     */
    public static final String URL_PDA_LOGIN = BASE_SERVER_URL + "/mobile-message!pdaLogin.do";
    /**
     * 出入庫文件提交地址
     */
    public static final String URL_PDA_UPLOAD = BASE_SERVER_URL+"/mobile-message!UpdateNet.do";
    /**
     * 网络提交peixiang数据
     */
    public static final String URL_PX_NET_UPLOAD = BASE_SERVER_URL + "/mobile-message!UpdatePX.do";

    /**
     * 网络提交peixiang数据
     */
    public static final String URL_QRCODE_NET_UPLOAD = BASE_SERVER_URL + "/mobile-message!UpdateQRCODE.do";

    /**
     * 获取网点出、入库信息地址
     */
    public static final String URL_NET_OUTIN = BASE_SERVER_URL + "/mobile-message!pdaDownload.do";

    /**
     * PDA交接提交地址
     */
    public static final String URL_NET_IN_COMMIT = BASE_SERVER_URL + "/mobile-message!uploadData.do";
    /**
     * 出入库信息下载地址
     */
    public static final String URL_DOWNINFO = BASE_SERVER_URL + "/mobile-message!downloadWithoutNet.do";

    public static final String URL_GET_TASK_LIST = BASE_SERVER_URL + "/mobile!tasklist.do";

    public static final String URL_GET_BRANCH = BASE_SERVER_URL + "/mobile!branch.do";
    public static final String URL_SAVE_GPS = BASE_SERVER_URL + "/mobile!savegps.do";

    public static final String URL_SAVE_TASK = BASE_SERVER_URL + "/mobile!savetask.do";

    public static final String URL_FILE_UPLOAD = BASE_SERVER_URL + "/mobile!uploadimg.do";


    public static final String URL_UPDATE = BASE_SERVER_URL + "/update/ct1.2.apk";


    public static final String URL_UPDATE_TEXT = BASE_SERVER_URL + "/update/version.txt";

    /**
     * 二维码提交地址
     */
    public static final String URL_QCODE_UPDATE = BASE_SERVER_URL+ "/pei-xiangz!saveData.do";

    /**
     * 二维码查询地址
     */

    public static final String URL_QCODE_QUERY = BASE_SERVER_URL+"/pei-xiangz!queryData.do";

    /**
     * 登录系统后网点出库接口访问参数
     */
    public static final String LOGIN_NET_OUT = "0";
    /**
     * 登录系统后网点入库接口访问参数
     */
    public static final String LOGIN_NET_IN = "1";
    /**
     * 网点任务状态	1:已完成 ； 0：未完成
     */
    public static final String NET_TASK_STATUS_FINISH = "1";
    public static final String NET_TASK_STATUS_UNFINISH = "0";
    /**
     * 押运人员RFID编码前缀
     */
    //	public static final String PRE_RFID_GUARD = "001aa";
    //	public static final String PRE_RFID_GUARD = "BBB2015";
    public static final String PRE_RFID_GUARD = "BBB2016";
    /**
     * 网点人员RFID编码前缀
     */
    //	public static final String PRE_RFID_BANKEMPLOYEE = "001bb";
    //	public static final String PRE_RFID_BANKEMPLOYEE = "AAA2007";
    public static final String PRE_RFID_BANKEMPLOYEE = "AAA2008";


    /**
     * 网点交接类型	1:网点入库 ； 0：网点出库
     */
    public static final String NET_COMMIT_TYPE_IN = "1";
    public static final String NET_COMMIT_TYPE_OUT = "0";
    /**
     * 网点交接状态	0：扫描正确 ;1：扫描错误
     */
    public static final String NET_COMMIT_STATUS_RIGHT = "0";
    /**
     * 网点交接状态	0：扫描正确 ;1：扫描错误
     */
    public static final String NET_COMMIT_STATUS_ERROR = "1";

    /**
     * 定义入库文件名称
     */
    public static final String FILE_NAME_IN = "NETIN";
    /**
     *定义出库文件名称
     */
    public static final String FILE_NAME_OUT = "NETOUT";
    /**
     * 定义文件路径
     */
    public static final String FILE_PATH = "/sdcard/Download/";
    /**
     * 定义文件格式
     */
    public static final String FILE_FORMAT = ".txt";
}
