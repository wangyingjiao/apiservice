package com.thinkgem.jeesite.common.result;

public class OpenSuccResult extends OpenResult {
    public OpenSuccResult() {
        this.setCode(1);
    }

    public OpenSuccResult(Object data, String msg) {
        super(1, data, msg);
    }

    public OpenSuccResult(int code, Object data,String msg) {
        super(code, data, msg);
    }
}
