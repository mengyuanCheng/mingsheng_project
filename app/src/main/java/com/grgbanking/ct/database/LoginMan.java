package com.grgbanking.ct.database;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class LoginMan extends Person {
    private String loginId;
    private String login_name;
    private String password;
    private String flag;
    private String line;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public LoginMan(String loginId,
                    String login_name,
                    String password,
                    String flag,
                    String line) {
        this.loginId = loginId;
        this.login_name = login_name;
        this.password = password;
        this.flag = flag;
        this.line = line;
    }

    public LoginMan() {

    }

}
