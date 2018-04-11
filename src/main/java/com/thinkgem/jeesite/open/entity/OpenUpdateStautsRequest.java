/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 订单状态更新RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenUpdateStautsRequest extends DataEntity<OpenUpdateStautsRequest> {

	private static final long serialVersionUID = 1L;

	private String platform;//对接平台代号  默认值: gasq
	private String service_order_id;//自营服务订单ID->编号
	private String comment;//取消原因
	private String status;//cancel取消；signed已签收；finish完成
	private String gasq_order_id;//国安社区订单ID
	private String gasq_order_sn;//国安社区订单SN

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getService_order_id() {
		return service_order_id;
	}

	public void setService_order_id(String service_order_id) {
		this.service_order_id = service_order_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGasq_order_id() {
		return gasq_order_id;
	}

	public void setGasq_order_id(String gasq_order_id) {
		this.gasq_order_id = gasq_order_id;
	}

	public String getGasq_order_sn() {
		return gasq_order_sn;
	}

	public void setGasq_order_sn(String gasq_order_sn) {
		this.gasq_order_sn = gasq_order_sn;
	}
}