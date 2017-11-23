/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianServiceInfo;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 技师技能关联Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSkillTechnician extends DataEntity<SerSkillTechnician> {
	
	private static final long serialVersionUID = 1L;
	private String skillId;		// 技能编号
	private String skillName;		// 技能
	private String technicianId;		// 技师编号
	private String technicianName;		// 技师
	
	public SerSkillTechnician() {
		super();
	}

	public SerSkillTechnician(String id){
		super(id);
	}

    public SerSkillTechnician(ServiceTechnicianServiceInfo serviceInfo, SerSkillInfo skill) {
		this.setSkillId(skill.getId());
		this.setSkillName(skill.getName());
		this.setTechnicianId(serviceInfo.getTechId());
		this.setTechnicianName(serviceInfo.getTechName());
    }

    @Length(min=0, max=64, message="技能编号长度必须介于 0 和 64 之间")
	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
	
	@Length(min=0, max=64, message="技师编号长度必须介于 0 和 64 之间")
	public String getTechnicianId() {
		return technicianId;
	}

	public void setTechnicianId(String technicianId) {
		this.technicianId = technicianId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getTechnicianName() {
		return technicianName;
	}

	public void setTechnicianName(String technicianName) {
		this.technicianName = technicianName;
	}
}