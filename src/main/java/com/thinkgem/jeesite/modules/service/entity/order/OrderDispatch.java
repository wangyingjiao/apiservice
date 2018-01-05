/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 派单Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderDispatch extends DataEntity<OrderDispatch> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String techId;		// 技师ID
	private String status;		// 状态(yes：可用 no：不可用)

	private String techName;   //技师姓名
	private String headPic;    //头像
	private String techSex;    //性别
	private String techPhone;   //电话
	private String jobNature; //岗位性质（full_time:全职，part_time:兼职）

	//查询条件
	private String stationId;
	private String skillId;//技能ID
	private String techStatus;		// 状态(yes:上线，no:暂停)
	private String jobStatus;//岗位状态(online:在职，leave:离职)
	private Date startTime;		// 起始时段
	private Date endTime;		// 结束时段
	private int week;         //周几
	private Date serviceTime;//服务时间
	private Date finishTime;//完成时间
	private List<String> techIds;//技能List

	//显示用
	private String latitude;//纬度
	private String longitude;//经度

	public OrderDispatch() {
		super();
	}

	public OrderDispatch(String id){
		super(id);
	}

	@Length(min=1, max=32, message="订单ID长度必须介于 1 和 32 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=1, max=32, message="技师ID长度必须介于 1 和 32 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public String getTechSex() {
		return techSex;
	}

	public void setTechSex(String techSex) {
		this.techSex = techSex;
	}

	public String getTechPhone() {
		return techPhone;
	}

	public void setTechPhone(String techPhone) {
		this.techPhone = techPhone;
	}

	public String getJobNature() {
		return jobNature;
	}

	public void setJobNature(String jobNature) {
		this.jobNature = jobNature;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getTechStatus() {
		return techStatus;
	}

	public void setTechStatus(String techStatus) {
		this.techStatus = techStatus;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
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

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Date serviceTime) {
		this.serviceTime = serviceTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public List<String> getTechIds() {
		return techIds;
	}

	public void setTechIds(List<String> techIds) {
		this.techIds = techIds;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}