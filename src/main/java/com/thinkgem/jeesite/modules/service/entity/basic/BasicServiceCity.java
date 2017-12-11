/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.basic;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 机构服务城市Entity
 * @author a
 * @version 2017-12-11
 */
public class BasicServiceCity extends DataEntity<BasicServiceCity> {
	
	private static final long serialVersionUID = 1L;
	private String orgId;		// 机构id
	private String cityCode;		// 城市代码
	
	public BasicServiceCity() {
		super();
	}

	public BasicServiceCity(String id){
		super(id);
	}

	@Length(min=0, max=32, message="机构id长度必须介于 0 和 32 之间")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	@Length(min=0, max=20, message="城市代码长度必须介于 0 和 20 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
}