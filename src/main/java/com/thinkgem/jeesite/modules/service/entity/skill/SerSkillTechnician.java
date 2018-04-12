/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 技师技能关联Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSkillTechnician extends DataEntity<SerSkillTechnician> {

	private static final long serialVersionUID = 1L;
	private String skillId;		// 技能ID
	private String techId;		// 技师ID

	private String techName;		// 技师
	private String techSex;
	private String techStationId;
	private String techStationName;
	private String headPic;
	private String jobStatus;

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public SerSkillTechnician() {
		super();
	}

	public SerSkillTechnician(String id){
		super(id);
	}

	public SerSkillTechnician(ServiceTechnicianInfo serviceInfo, SerSkillInfo skill) {
		this.setSkillId(skill.getId());
		this.setTechId(serviceInfo.getId());
	}
	@Length(min=0, max=64, message="技能编号长度必须介于 0 和 64 之间")
	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getTechSex() {
		return techSex;
	}

	public void setTechSex(String techSex) {
		this.techSex = techSex;
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

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	private Boolean techChecked = false;
	@JsonInclude
	public Boolean getTechChecked() {
		return techChecked;
	}

	public void setTechChecked(Boolean techChecked) {
		this.techChecked = techChecked;
	}

	List<BasicServiceStation> stations;

	public List<BasicServiceStation> getStations() {
		return stations;
	}

	public void setStations(List<BasicServiceStation> stations) {
		this.stations = stations;
	}
}