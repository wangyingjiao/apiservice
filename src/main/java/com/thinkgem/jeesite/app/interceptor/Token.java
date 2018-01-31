package com.thinkgem.jeesite.app.interceptor;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author x
 */
public class Token {
    private String token;
    private String phone;
    private String techId;
    private Map map;

    public Token() {
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
