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
 * 组合订单-多次服务-服务时间Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderCombinationFrequencyInfo extends DataEntity<OrderCombinationFrequencyInfo> {

	private static final long serialVersionUID = 1L;
	private String masterId;		// 主订单ID
    private int week;         //工作日期（周一，周二。。。）
    private Date startTime;		// 起始时段
    private Date endTime;		// 结束时段

	public OrderCombinationFrequencyInfo() {}

	public OrderCombinationFrequencyInfo(String id) {
		super(id);
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}