/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单服务关联Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderServiceRelation extends DataEntity<OrderServiceRelation> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String itemId;		// 服务项目商品ID
	
	public OrderServiceRelation() {
		super();
	}

	public OrderServiceRelation(String id){
		super(id);
	}

	@Length(min=0, max=64, message="订单ID长度必须介于 0 和 64 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=0, max=64, message="服务项目商品ID长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
}