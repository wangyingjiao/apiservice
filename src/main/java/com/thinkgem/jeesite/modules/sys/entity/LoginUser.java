package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "LoginUser", description = "用户登录参数")
public class LoginUser {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    @ApiModelProperty(name = "username",value = "username",example = "aaaa")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @ApiModelProperty(name = "password",value = "password",example = "aaaa")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
