/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 服务技师服务信息Entity
 * @author a
 * @version 2017-11-16
 */
public class ServiceTechnicianServiceInfo extends DataEntity<ServiceTechnicianServiceInfo> {
	
	private static final long serialVersionUID = 1L;
	private String techId;		// 技师id
	private String techName;		// 技师名称
	private String serviceCityId;		// 服务城市id
	private String serviceCityName;		// 服务城市名称
	private String jobNatrue;		// 岗位性质（全职，兼职）
	private String stationId;		// 所属服务站id
	private String stationName;		// 所属服务站name
	private String jobStatus;		// 岗位状态(在职，离职)
	private String workTime;		// 工作年限
	private Date inJobTime;		// 入职日期
	private String assessGrade;		// 评价级别
	private String experDesc;		// 经验描述
	private String sort;		// 排序
	private List<SerSkillInfo> skills; //技师技能
	
	public ServiceTechnicianServiceInfo() {
		super();
	}

	public ServiceTechnicianServiceInfo(String id){
		super(id);
	}

	@NotBlank(message = "技师id不可为空")
	@Length(min=0, max=64, message="技师id长度必须介于 0 和 64 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	@Length(min=0, max=255, message="技师名称长度必须介于 0 和 255 之间")
	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	@NotBlank(message = "服务城市id不能为空")
	@Length(min=0, max=64, message="服务城市id长度必须介于 0 和 64 之间")
	public String getServiceCityId() {
		return serviceCityId;
	}

	public void setServiceCityId(String serviceCityId) {
		this.serviceCityId = serviceCityId;
	}
	
	@Length(min=0, max=255, message="服务城市名称长度必须介于 0 和 255 之间")
	public String getServiceCityName() {
		return serviceCityName;
	}

	public void setServiceCityName(String serviceCityName) {
		this.serviceCityName = serviceCityName;
	}

	@NotBlank(message = "岗位性质 不可为空")
	@Length(min=0, max=2, message="岗位性质（全职，兼职）长度必须介于 0 和 2 之间")
	public String getJobNatrue() {
		return jobNatrue;
	}

	public void setJobNatrue(String jobNatrue) {
		this.jobNatrue = jobNatrue;
	}

	@NotBlank(message = "所属服务站id不可为空")
	@Length(min=0, max=64, message="所属服务站id长度必须介于 0 和 64 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	@Length(min=0, max=255, message="所属服务站name长度必须介于 0 和 255 之间")
	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	@NotBlank(message = "岗位状态 不可为空")
	@Length(min=0, max=2, message="岗位状态(在职，离职)长度必须介于 0 和 2 之间")
	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@NotBlank(message = "工作年限 不可为空")
	@Length(min=0, max=64, message="工作年限长度必须介于 0 和 64 之间")
	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getInJobTime() {
		return inJobTime;
	}

	public void setInJobTime(Date inJobTime) {
		this.inJobTime = inJobTime;
	}
	
	@Length(min=0, max=2, message="评价级别长度必须介于 0 和 2 之间")
	public String getAssessGrade() {
		return assessGrade;
	}

	public void setAssessGrade(String assessGrade) {
		this.assessGrade = assessGrade;
	}
	
	@Length(min=0, max=255, message="经验描述长度必须介于 0 和 255 之间")
	public String getExperDesc() {
		return experDesc;
	}

	public void setExperDesc(String experDesc) {
		this.experDesc = experDesc;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<SerSkillInfo> getSkills() {
		return skills;
	}

	public void setSkills(List<SerSkillInfo> skills) {
		this.skills = skills;
	}
}