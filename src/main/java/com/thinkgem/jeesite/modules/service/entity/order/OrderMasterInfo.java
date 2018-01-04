/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;


import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单主表Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderMasterInfo extends DataEntity<OrderMasterInfo> {
	
	private static final long serialVersionUID = 1L;
	private String orderType;		// 订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
	
	public OrderMasterInfo() {
		super();
	}

	public OrderMasterInfo(String id){
		super(id);
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
}