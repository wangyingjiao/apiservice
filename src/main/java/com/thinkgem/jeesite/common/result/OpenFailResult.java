package com.thinkgem.jeesite.common.result;

public class OpenFailResult extends OpenResult {
    public OpenFailResult() {
    }

    public OpenFailResult(String msg) {
        super(0,null, msg);
    }

    public OpenFailResult(int code, String msg) {
        super(code,null, msg);
    }
}
