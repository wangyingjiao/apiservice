package com.thinkgem.jeesite.common.result;


public class Result<T extends Object> {
    private int code;//0:系统异常，内容重复，用户名或者密码错误;1:操作成功;2:session过期;
    private T data;
    private String message;

    public Result() {
    }

    public Result(int code, T data) {
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
