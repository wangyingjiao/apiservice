package com.thinkgem.jeesite.common.result;

public class AppSuccResult<T> extends AppResult<T> {
    public AppSuccResult() {
        this.setCode(1);
    }

    public AppSuccResult(T data) {
        super(1, data,"查询列表");
    }


    public AppSuccResult(int code, T data,String message) {
        super(code, data, message);
    }
}
