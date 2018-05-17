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
    private String joint_order_sn;         //国安社区订单编号
	private String order_group_id;		// 自营订单组编号 一次下单生成3个子订单，有相同的组ID
	private String order_number;		// 自营订单编号

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

	public String getJoint_order_sn() {
		return joint_order_sn;
	}

	public void setJoint_order_sn(String joint_order_sn) {
		this.joint_order_sn = joint_order_sn;
	}

	public String getOrder_group_id() {
		return order_group_id;
	}

	public void setOrder_group_id(String order_group_id) {
		this.order_group_id = order_group_id;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
}