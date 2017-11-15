/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 技能管理Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSkillInfo extends DataEntity<SerSkillInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 技能名称
	private String code;		// 技能编码
	
	public SerSkillInfo() {
		super();
	}

	public SerSkillInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="技能名称长度必须介于 0 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=64, message="技能编码长度必须介于 0 和 64 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}