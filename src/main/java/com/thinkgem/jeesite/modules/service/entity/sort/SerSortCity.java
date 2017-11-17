/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务分类对应的定向城市Entity
 * @author a
 * @version 2017-11-15
 */
public class SerSortCity extends DataEntity<SerSortCity> {
	
	private static final long serialVersionUID = 1L;
	private String sortId;		// 服务分类编号
	private String cityId;		// 城市编号
	private String cityName;		// 城市名称

	public SerSortCity() {
		super();
	}

	public SerSortCity(String id){
		super(id);
	}

	@Length(min=0, max=64, message="服务分类编号长度必须介于 0 和 64 之间")
	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
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