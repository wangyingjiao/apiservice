package com.thinkgem.jeesite.common.result;


public class OpenResult {
    private int code;
    private Object data;
    private String message;

    public OpenResult() {
    }

    public OpenResult(int code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
