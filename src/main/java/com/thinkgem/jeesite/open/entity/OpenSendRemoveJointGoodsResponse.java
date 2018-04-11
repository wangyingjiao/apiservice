package com.thinkgem.jeesite.open.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 服务项目解除 Response
 * @author a
 * @version 2017-12-11
 */
public class OpenSendRemoveJointGoodsResponse<T extends Object> {

    private static final long serialVersionUID = 1L;

    private int code;
    private T data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}