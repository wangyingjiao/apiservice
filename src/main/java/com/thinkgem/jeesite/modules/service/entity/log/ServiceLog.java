package com.thinkgem.jeesite.modules.service.entity.log;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

/**
 * 日志Entity
 * @author a
 * @version 2018-01-26
 */
public class ServiceLog extends DataEntity<ServiceLog> {

    private static final long serialVersionUID = 1L;
    private String type;   //日志类型
    private String title;   //日志标题
    private String remoteAddr;  //操作IP地址
    private String userAgent;   //用户代理
    private String requestUri;   //请求URL
    private String method;    //操作方式
    private String params;    //操作提交的数据
    private String exceptions;   //异常信息
    private String startTime;   //开始时间
    private String endTime;     //结束时间
    private String requestContent;//参数值


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }
}
