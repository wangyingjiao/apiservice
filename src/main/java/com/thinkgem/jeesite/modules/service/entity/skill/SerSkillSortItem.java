/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技能关联Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSkillSortItem extends DataEntity<SerSkillSortItem> {
	
	private static final long serialVersionUID = 1L;
	private String skillId;		// 技能编号
	private String skillName;   //技能
	private String sortId;		// 分类编号
	private String sortName;		// 分类名称
	private String itemId;		// 项目编号
	private String itemName;		// 项目名称
	
	public SerSkillSortItem() {
		super();
	}

	public SerSkillSortItem(String id){
		super(id);
	}

	@Length(min=0, max=64, message="技能编号长度必须介于 0 和 64 之间")
	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	@Length(min=0, max=64, message="分类编号长度必须介于 0 和 64 之间")
	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}
	
	@Length(min=0, max=64, message="分类名称长度必须介于 0 和 64 之间")
	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	
	@Length(min=0, max=64, message="项目编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
}