package com.thinkgem.jeesite.common.result;

public class AppSuccResult<T> extends AppResult<T> {
    public AppSuccResult() {
        this.setCode(0);
    }

    public AppSuccResult(T data) {
        super(1, data,"查询列表");//0：成功且有数据 1：成功无数据 -1：失败 -2：登录失效
    }


    public AppSuccResult(int code, T data,String message) {
        super(code, data, message);
    }
}
