/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Date;
import java.util.Map;

/**
 * 日志Entity
 * @author ThinkGem
 * @version 2014-8-19
 */
public class SysJointLog extends DataEntity<SysJointLog> {

	private static final long serialVersionUID = 1L;
	private String sendType; //请求类型 "save_order": "更新订单信息","del_goods": "删除商品","save_goods": "保存商品",org_del_goods:机构删除E店关联商品
	private String url; 	// 请求URL
	private String requestContent; 		// 请求内容
	private String responseContent; 		// 响应内容
	private String source; //请求方 gasq国安社区 own自营平台
	private String isSuccess; 	// 请求结果  yes:成功  no:失败

	public static final String IS_SUCCESS_YES = "yes";
	public static final String IS_SUCCESS_NO = "no";

	public SysJointLog(){
		super();
	}

	public SysJointLog(String id){
		super(id);
	}

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

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}