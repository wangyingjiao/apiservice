/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
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
	private Date workDate;		// 工作日期（周一，周二。。。）
	private String startTime;		// 起始时段
	private String endTime;		// 结束时段
	private String sort;		// 排序
	
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
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}