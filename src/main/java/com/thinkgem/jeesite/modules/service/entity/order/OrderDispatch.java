/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 派单Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderDispatch extends DataEntity<OrderDispatch> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String techId;		// 技师ID
	private String status;		// 状态(yes：可用 no：不可用)
	
	public OrderDispatch() {
		super();
	}

	public OrderDispatch(String id){
		super(id);
	}

	@Length(min=1, max=32, message="订单ID长度必须介于 1 和 32 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=1, max=32, message="技师ID长度必须介于 1 和 32 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}