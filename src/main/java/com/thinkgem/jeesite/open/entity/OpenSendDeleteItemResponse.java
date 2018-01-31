package com.thinkgem.jeesite.open.entity;

import java.util.List;

/**
 * 服务项目删除ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendDeleteItemResponse<T extends Object> {

    private static final long serialVersionUID = 1L;

    private int code;
    private T datafail;
    private T datainventoryfail;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getDatafail() {
        return datafail;
    }

    public void setDatafail(T datafail) {
        this.datafail = datafail;
    }

    public T getDatainventoryfail() {
        return datainventoryfail;
    }

    public void setDatainventoryfail(T datainventoryfail) {
        this.datainventoryfail = datainventoryfail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}