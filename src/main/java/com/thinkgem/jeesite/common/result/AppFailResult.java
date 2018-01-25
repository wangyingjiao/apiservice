package com.thinkgem.jeesite.common.result;

import org.apache.poi.ss.formula.functions.T;

public class AppFailResult<T> extends AppResult<T> {
    public AppFailResult() {
    }

    public AppFailResult(T data) {
        super(-1, data);//0：成功且有数据 1：成功无数据 -1：失败 -2：登录失效
    }

    public AppFailResult(int code, T data, java.lang.String message) {
        super(code, data, message);
    }
}
