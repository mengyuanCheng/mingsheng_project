package com.grgbanking.ct.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ：     cmy
 * @version :     2017/4/23.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class SerializableMap implements Serializable {
    private Map<String,Object> map;
    public Map<String,Object> getMap()
    {
        return map;
    }
    public void setMap(Map<String,Object> map)
    {
        this.map=map;
    }
}