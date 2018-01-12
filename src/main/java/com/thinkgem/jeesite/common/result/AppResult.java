package com.thinkgem.jeesite.common.result;


public class AppResult<T extends Object> {
    private int code;
    private T data;
    private String message;

    public AppResult() {
    }

    public AppResult(int code, T data,String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
    public AppResult(int code, T data) {
        this.code = code;
        this.data = data;
    }

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
