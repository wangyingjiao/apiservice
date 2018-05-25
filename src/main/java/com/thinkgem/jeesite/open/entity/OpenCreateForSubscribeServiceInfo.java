/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 预约ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenCreateForSubscribeServiceInfo extends DataEntity<OpenCreateForSubscribeServiceInfo> {

	private static final long serialVersionUID = 1L;

	private String service_order_sn;		//自营服务的订单sn
	private String gasq_order_sn;		//国安社区订单sn

	public String getService_order_sn() {
		return service_order_sn;
	}

	public void setService_order_sn(String service_order_sn) {
		this.service_order_sn = service_order_sn;
	}

	public String getGasq_order_sn() {
		return gasq_order_sn;
	}

	public void setGasq_order_sn(String gasq_order_sn) {
		this.gasq_order_sn = gasq_order_sn;
	}
}