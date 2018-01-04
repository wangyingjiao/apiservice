/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 技能服务项目关联Entity
 * @author a
 * @version 2017-12-04
 */
public class SerSkillItem extends DataEntity<SerSkillItem> {

	private static final long serialVersionUID = 1L;
	private String skillId;		// 技能编号
	private String itemId;		// 项目编号
	private String goodsId;

	public SerSkillItem() {
		super();
	}

	public SerSkillItem(String id){
		super(id);
	}

	@Length(min=0, max=64, message="技能编号长度必须介于 0 和 64 之间")
	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	@Length(min=0, max=64, message="项目编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	private String itemName;		// 项目编号
	private String goodsName;


	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

}