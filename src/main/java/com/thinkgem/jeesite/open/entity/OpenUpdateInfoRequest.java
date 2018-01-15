/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 选择服务时间RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenUpdateInfoRequest extends DataEntity<OpenUpdateInfoRequest> {

	private static final long serialVersionUID = 1L;

	private String service_order_id;//自营服务订单ID
	private OpenServiceInfo service_info;//更新服务项目信息及数量
	private OpenGuoanxiaInfo guoanxia_info;//国安侠信息
	private OpenCostomerInfo costomer_info;//用户信息
	private OpenStoreInfo store_info;//门店信息
	private String platform;//对接平台代号

	public String getService_order_id() {
		return service_order_id;
	}

	public void setService_order_id(String service_order_id) {
		this.service_order_id = service_order_id;
	}

	public OpenServiceInfo getService_info() {
		return service_info;
	}

	public void setService_info(OpenServiceInfo service_info) {
		this.service_info = service_info;
	}

	public OpenGuoanxiaInfo getGuoanxia_info() {
		return guoanxia_info;
	}

	public void setGuoanxia_info(OpenGuoanxiaInfo guoanxia_info) {
		this.guoanxia_info = guoanxia_info;
	}

	public OpenCostomerInfo getCostomer_info() {
		return costomer_info;
	}

	public void setCostomer_info(OpenCostomerInfo costomer_info) {
		this.costomer_info = costomer_info;
	}

	public OpenStoreInfo getStore_info() {
		return store_info;
	}

	public void setStore_info(OpenStoreInfo store_info) {
		this.store_info = store_info;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}