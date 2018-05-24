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
public class OpenServiceTimesForGroupRequest extends DataEntity<OpenServiceTimesForGroupRequest> {

	private static final long serialVersionUID = 1L;

	private String store_id;//门店ID
	private String eshop_code;//E店编码
	private List<OpenServiceInfo> service_info;//下单服务项目信息
	private String platform;//对接平台代号   默认值 : gasq
	private String group_id;
	private String gasq_order_num;

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getEshop_code() {
		return eshop_code;
	}

	public void setEshop_code(String eshop_code) {
		this.eshop_code = eshop_code;
	}

	public List<OpenServiceInfo> getService_info() {
		return service_info;
	}

	public void setService_info(List<OpenServiceInfo> service_info) {
		this.service_info = service_info;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getGasq_order_num() {
		return gasq_order_num;
	}

	public void setGasq_order_num(String gasq_order_num) {
		this.gasq_order_num = gasq_order_num;
	}
}