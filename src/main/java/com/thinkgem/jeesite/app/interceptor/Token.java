package com.thinkgem.jeesite.app.interceptor;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author x
 */
public class Token {
    private String token;
    private String mobile;
    private Map map;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
