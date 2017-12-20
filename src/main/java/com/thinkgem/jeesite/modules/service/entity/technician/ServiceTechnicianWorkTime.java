/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技师工作时间Entity
 * @author a
 * @version 2017-11-16
 */
public class ServiceTechnicianWorkTime extends DataEntity<ServiceTechnicianWorkTime> {
	
	private static final long serialVersionUID = 1L;
	private String techId;		// tech_id
	private String techName;		// 名称
	private String week;		// 工作日期（周一，周二。。。）
	private Timestamp startTime;		// 起始时段
	private Timestamp endTime;		// 结束时段
	private List<ServiceTechnicianWorkTimeWeek> weeks;		// 工作日期（周一，周二。。。）

	public ServiceTechnicianWorkTime() {
		super();
	}

	public ServiceTechnicianWorkTime(String id){
		super(id);
	}

	@Length(min=0, max=32, message="tech_id长度必须介于 0 和 32 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	@Length(min=1, max=100, message="名称长度必须介于 1 和 100 之间")
	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	@JsonFormat(pattern = "HH:mm:ss")
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "HH:mm:ss")
	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public List<ServiceTechnicianWorkTimeWeek> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<ServiceTechnicianWorkTimeWeek> weeks) {
		this.weeks = weeks;
	}
}