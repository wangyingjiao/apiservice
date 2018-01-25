package com.thinkgem.jeesite.open.entity;

import java.util.List;

/**
 * 服务项目删除ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendDeleteItemResponse {

    private static final long serialVersionUID = 1L;

    private int code;
    private List<String> datafail;
    private List<String> datainventoryfail;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getDatafail() {
        return datafail;
    }

    public void setDatafail(List<String> datafail) {
        this.datafail = datafail;
    }

    public List<String> getDatainventoryfail() {
        return datainventoryfail;
    }

    public void setDatainventoryfail(List<String> datainventoryfail) {
        this.datainventoryfail = datainventoryfail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}