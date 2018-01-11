package com.thinkgem.jeesite.common.result;

import org.apache.poi.ss.formula.functions.T;

public class AppFailResult<T> extends AppResult<T> {
    public AppFailResult() {
    }

    public AppFailResult(T data) {
        super(0, data);
    }

    public AppFailResult(int code, T data, java.lang.String message) {
        super(code, data, message);
    }
}
