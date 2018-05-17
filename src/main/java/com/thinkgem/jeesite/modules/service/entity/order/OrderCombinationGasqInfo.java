/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;

import java.util.Date;
import java.util.List;

/**
 * 组合订单对接编号信息Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderCombinationGasqInfo extends DataEntity<OrderCombinationGasqInfo> {

	private static final long serialVersionUID = 1L;
	private String masterId;		// 主订单ID
    private String jointOrderSn;         //国安社区订单编号
	private String orderGroupId;		// 自营订单组编号 一次下单生成3个子订单，有相同的组ID
	private String orderNumber;		// 自营订单编号
	private List<OrderInfo> orderList;
	public OrderCombinationGasqInfo() {
	}

	public OrderCombinationGasqInfo(String id) {
		super(id);
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getJointOrderSn() {
		return jointOrderSn;
	}

	public void setJointOrderSn(String jointOrderSn) {
		this.jointOrderSn = jointOrderSn;
	}

	public String getOrderGroupId() {
		return orderGroupId;
	}

	public void setOrderGroupId(String orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<OrderInfo> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderInfo> orderList) {
		this.orderList = orderList;
	}
}