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
	private List<String> techIdList;
	private String techName;		// 名称
	private String week;		// 工作日期（周一，周二。。。）
	private Date startTime;		// 起始时段
	private Date endTime;		// 结束时段
	private String startTimeStr;  //起始时间 技师信息回显
	private String endTimeStr;  //结束时间 技师信息回显
	private List<ServiceTechnicianWorkTimeWeek> weeks;		// 工作日期（周一，周二。。。）
	private List<String> weekList;
	private String startTimes;
	private String endTimes;

	public String getStartTimes() {
		return startTimes;
	}

	public void setStartTimes(String startTimes) {
		this.startTimes = startTimes;
	}

	public String getEndTimes() {
		return endTimes;
	}

	public void setEndTimes(String endTimes) {
		this.endTimes = endTimes;
	}

	public List<String> getWeekList() {
		return weekList;
	}

	public void setWeekList(List<String> weekList) {
		this.weekList = weekList;
	}

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

	@JsonFormat(pattern = "HH:mm")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "HH:mm")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public List<ServiceTechnicianWorkTimeWeek> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<ServiceTechnicianWorkTimeWeek> weeks) {
		this.weeks = weeks;
	}

	public List<String> getTechIdList() {
		return techIdList;
	}

	public void setTechIdList(List<String> techIdList) {
		this.techIdList = techIdList;
	}
}