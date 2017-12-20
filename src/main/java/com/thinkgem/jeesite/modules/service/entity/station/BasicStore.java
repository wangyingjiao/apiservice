/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.station;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 门店数据Entity
 * @author 门店数据
 * @version 2017-12-20
 */
public class BasicStore extends DataEntity<BasicStore> {
	
	private static final long serialVersionUID = 1L;
	private String orgId;		// 机构id
	private String storeId;		// 门店id
	private String storeName;		// 门店名称
	private String white;		// 门店类型
	private String provinceCode;		// 省_区号
	private String cityCode;		// 市_区号
	private String areaCode;		// 区_区号
	
	public BasicStore() {
		super();
	}

	public BasicStore(String id){
		super(id);
	}

	@Length(min=1, max=32, message="机构id长度必须介于 1 和 32 之间")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	@Length(min=1, max=32, message="门店id长度必须介于 1 和 32 之间")
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	@Length(min=1, max=32, message="门店名称长度必须介于 1 和 32 之间")
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}
	
	@Length(min=1, max=20, message="省_区号长度必须介于 1 和 20 之间")
	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	@Length(min=1, max=20, message="市_区号长度必须介于 1 和 20 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Length(min=1, max=20, message="区_区号长度必须介于 1 和 20 之间")
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
}