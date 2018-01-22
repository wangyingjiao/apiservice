package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import io.swagger.models.auth.In;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 服务项目保存RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenSendSaveItemRequest {

    private HashMap<String,Object> product;
    private String eshop_code;// ESHOP_CODE
    private HashMap<String,Object> attachments;

    public HashMap<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(HashMap<String, Object> attachments) {
        this.attachments = attachments;
    }

    public HashMap<String, Object> getProduct() {
        return product;
    }

    public void setProduct(HashMap<String, Object> product) {
        this.product = product;
    }

    public String getEshop_code() {
        return eshop_code;
    }

    public void setEshop_code(String eshop_code) {
        this.eshop_code = eshop_code;
    }

    private String com;
    private String client;
    private String ver;
    private Date requestTimestamp;
    private String method;
    private String app_com;
    private String task;

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