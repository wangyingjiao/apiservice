package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

public class OpenSendVerificationCombo extends DataEntity<OpenSendVerificationCombo> {

    private static final long serialVersionUID = 1L;

    private OpenSendVerificationComboGoods product;

    private String com;
    private String client;
    private String ver;
    private Date requestTimestamp;
    private String method;
    private String app_com;
    private String task;

    public OpenSendVerificationComboGoods getProduct() {
        return product;
    }

    public void setProduct(OpenSendVerificationComboGoods product) {
        this.product = product;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Date getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Date requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getApp_com() {
        return app_com;
    }

    public void setApp_com(String app_com) {
        this.app_com = app_com;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
