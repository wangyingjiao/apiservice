/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 待对接Entity
 * @author ThinkGem
 * @version 2014-8-19
 */
public class SysJointWait extends DataEntity<SysJointWait> {

	private static final long serialVersionUID = 1L;

	private String url; 	// 请求URL
	private String sendType;
	private String requestContent; 		// 请求内容

	public SysJointWait(){
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}
}