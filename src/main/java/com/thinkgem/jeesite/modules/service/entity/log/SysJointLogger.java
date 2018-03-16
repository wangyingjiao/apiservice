package com.thinkgem.jeesite.modules.service.entity.log;

import java.util.Date;

import com.thinkgem.jeesite.common.persistence.DataEntity;

public class SysJointLogger extends DataEntity<SysJointLogger>{
	private static final long serialVersionUID = 1L;
	private String url;
	private String requestContent;
	private String responseContent;
	private String isSuccess; 	// 请求结果  yes:成功  no:失败
	private String source;
	private String sendType;
	private String sendFlag;

	public static final String IS_SUCCESS_YES = "yes";
	public static final String IS_SUCCESS_NO = "no";
	private Date createDate;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRequestContent() {
		return requestContent;
	}
	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}
	public String getResponseContent() {
		return responseContent;
	}
	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}
	public String getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public static String getIsSuccessYes() {
		return IS_SUCCESS_YES;
	}
	public static String getIsSuccessNo() {
		return IS_SUCCESS_NO;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
}
