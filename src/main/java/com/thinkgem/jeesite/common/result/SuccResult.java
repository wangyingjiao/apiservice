package com.thinkgem.jeesite.common.result;

public class SuccResult<T> extends Result<T> {
    public SuccResult() {
    }

    public SuccResult(T data) {
        super(1, data);
    }


    public SuccResult(int code, T data) {
        super(code, data);
    }
}
