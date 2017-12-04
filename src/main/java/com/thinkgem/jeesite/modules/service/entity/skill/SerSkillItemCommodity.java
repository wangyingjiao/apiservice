/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.skill;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 技能服务项目商品关联Entity
 * @author a
 * @version 2017-12-04
 */
public class SerSkillItemCommodity extends DataEntity<SerSkillItemCommodity> {
	
	private static final long serialVersionUID = 1L;
	private String itemId;		// 项目编号
	private String itemName;		// 项目
	private String commodityId;		// 商品编号
	private String commodityName;		// 商品
	
	public SerSkillItemCommodity() {
		super();
	}

	public SerSkillItemCommodity(String id){
		super(id);
	}

	@Length(min=0, max=64, message="技能服务项目关联编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Length(min=0, max=64, message="商品编号长度必须介于 0 和 64 之间")
	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}
	
	@Length(min=0, max=64, message="商品长度必须介于 0 和 64 之间")
	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	
}