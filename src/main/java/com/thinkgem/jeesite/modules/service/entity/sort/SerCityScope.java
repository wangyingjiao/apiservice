/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 服务分类服务项目对应的定向城市Entity
 * @author a
 * @version 2017-11-15
 */
public class SerCityScope extends DataEntity<SerCityScope> {

	private static final long serialVersionUID = 1L;
	private String masterId;		// 服务分类ID（服务项目ID）
	private String type;		// 0:服务分类 1:服务项目
	private String cityCode;		// 市_区号

	public SerCityScope() {
		super();
	}

	public SerCityScope(String id){
		super(id);
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}


	private boolean haveItem;
	private boolean sortChecked;
	private String cityName;		// 市

	public boolean getHaveItem() {
		return haveItem;
	}

	public void setHaveItem(boolean haveItem) {
		this.haveItem = haveItem;
	}

	public boolean getSortChecked() {
		return sortChecked;
	}

	public void setSortChecked(boolean sortChecked) {
		this.sortChecked = sortChecked;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
}