package com.thinkgem.jeesite.app.token.manager.entity;

import java.io.Serializable;

public class Token implements Serializable {

    private String mobile;
    private String token;

    public Token() {
    }

    public Token(String mobile, String token) {
        this.setMobile(mobile);
        this.setToken(token);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
