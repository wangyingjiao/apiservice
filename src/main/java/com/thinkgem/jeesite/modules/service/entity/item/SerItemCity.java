/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目对应的定向城市Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemCity extends DataEntity<SerItemCity> {
	
	private static final long serialVersionUID = 1L;
	private String itemId;		// 服务项目编号
	private String itemName;		// 服务项目名称
	public String getItemName() {return itemName;}
	public void setItemName(String itemName) {this.itemName = itemName;}

	private String cityId;		// 城市编号
	private String cityName;		// 城市名称
	
	public SerItemCity() {
		super();
	}

	public SerItemCity(String id){
		super(id);
	}

	@Length(min=0, max=64, message="服务项目编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	@Length(min=0, max=64, message="城市编号长度必须介于 0 和 64 之间")
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	@Length(min=0, max=64, message="城市名称长度必须介于 0 和 64 之间")
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
}