/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 预约ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenCreateForSubscribeResponse extends DataEntity<OpenCreateForSubscribeResponse> {

	private static final long serialVersionUID = 1L;
	private boolean success;		//接单状态：true 已接单；false 拒单
	private List<OpenCreateForSubscribeServiceInfo> service_info;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<OpenCreateForSubscribeServiceInfo> getService_info() {
		return service_info;
	}

	public void setService_info(List<OpenCreateForSubscribeServiceInfo> service_info) {
		this.service_info = service_info;
	}
}