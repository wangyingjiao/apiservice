package com.thinkgem.jeesite.modules.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_log")
public class Log extends BaseEntity<Log> {

    private static final long serialVersionUID = 1L;

    /**
     * 日志类型 visit：接入日志 error：异常日志
     */
	private String type;
    /**
     * 日志标题
     */
	private String title;
    /**
     * 操作IP地址
     */
	@TableField("remote_addr")
	private String remoteAddr;
    /**
     * 用户代理
     */
	@TableField("user_agent")
	private String userAgent;
    /**
     * 请求URI
     */
	@TableField("request_uri")
	private String requestUri;
    /**
     * 操作方式
     */
	private String method;
    /**
     * 操作提交的数据
     */
	private String params;
    /**
     * 异常信息
     */
	private String exception;


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

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Log{" +
			", type=" + type +
			", title=" + title +
			", remoteAddr=" + remoteAddr +
			", userAgent=" + userAgent +
			", requestUri=" + requestUri +
			", method=" + method +
			", params=" + params +
			", exception=" + exception +
			"}";
	}
}
