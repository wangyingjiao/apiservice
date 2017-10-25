package com.thinkgem.jeesite.common.result;

public class FailResult<String> extends Result<String> {
    public FailResult() {
    }

    public FailResult(String data) {
        super(0, data);
    }

    public FailResult(int code, String data) {
        super(code, data);
    }
}
