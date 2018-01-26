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
    private Date startTime;   //开始时间
    private Date endTime;     //结束时间


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
