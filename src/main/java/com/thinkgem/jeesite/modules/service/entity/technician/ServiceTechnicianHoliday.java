/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技师休假时间Entity
 * @author a
 * @version 2017-11-29
 */
public class ServiceTechnicianHoliday extends DataEntity<ServiceTechnicianHoliday> {
	
	private static final long serialVersionUID = 1L;
	private String techId;		// tech_id
	private String techName;		// 名称
	private String holiday;		// 休假日期（周一，周二。。。）
	private Date startTime;		// 起始时段
	private Date endTime;		// 结束时段
	private String sort;		// 排序
	private String techPhone; //手机号
	private String techStationId;//服务站ID
	private String techStationName;//服务站名称
	private String remark;
	private String isExpire="yes";//app是否过期
	private String reviewStatus; //审核状态 no：审核人未通过 submit：审核中 yes：审核通过
	private String failReason; //未通过原因
	private String source; //来源 app：技师APP端 sys：后台系统
	private String jobStatus;		// 岗位状态(online:在职，leave:离职)
	private String status; //按钮时候出现
	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsExpire() {
		return isExpire;
	}

	public void setIsExpire(String isExpire) {
		this.isExpire = isExpire;
	}

	public ServiceTechnicianHoliday() {
		super();
	}

	public ServiceTechnicianHoliday(String id){
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

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
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
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getTechPhone() {
		return techPhone;
	}

	public void setTechPhone(String techPhone) {
		this.techPhone = techPhone;
	}

	public String getTechStationId() {
		return techStationId;
	}

	public void setTechStationId(String techStationId) {
		this.techStationId = techStationId;
	}

	public String getTechStationName() {
		return techStationName;
	}

	public void setTechStationName(String techStationName) {
		this.techStationName = techStationName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}