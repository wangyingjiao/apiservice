/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * 技能管理Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSkillInfo extends DataEntity<SerSkillInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 技能名称
	private int techNum;  // 技师数量
	private String orgId;//机构ID

	private List<SerSkillTechnician> technicians;
	private List<SerSkillSort> goodsList;

	private List<String> sortIds;

	public SerSkillInfo() {
		super();
	}

	public SerSkillInfo(String id){
		super(id);
	}

	@NotBlank(message = "技能名称不可为空")
	@Length(min=2, max=15, message="技能名称长度必须介于 2 和 15 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTechNum() {
		return techNum;
	}

	public void setTechNum(int techNum) {
		this.techNum = techNum;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<SerSkillTechnician> getTechnicians() {
		return technicians;
	}

	public void setTechnicians(List<SerSkillTechnician> technicians) {
		this.technicians = technicians;
	}

	public List<SerSkillSort> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<SerSkillSort> goodsList) {
		this.goodsList = goodsList;
	}

	private List<SerItemInfo> items;

	public List<SerItemInfo> getItems() {
		return items;
	}

	public void setItems(List<SerItemInfo> items) {
		this.items = items;
	}

	private String itemName;
	private String techName;
	private String techStationId;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getTechStationId() {
		return techStationId;
	}

	public void setTechStationId(String techStationId) {
		this.techStationId = techStationId;
	}

	public List<String> getSortIds() {
		return sortIds;
	}

	public void setSortIds(List<String> sortIds) {
		this.sortIds = sortIds;
	}
}