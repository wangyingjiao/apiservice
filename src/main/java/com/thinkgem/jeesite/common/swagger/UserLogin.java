package com.thinkgem.jeesite.common.swagger;

import java.io.Serializable;

/**
 * @author x
 */
public class UserLogin implements Serializable{
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
