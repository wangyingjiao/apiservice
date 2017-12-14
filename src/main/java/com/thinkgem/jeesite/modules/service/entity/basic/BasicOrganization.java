/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.basic;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 机构Entity
 * @author a
 * @version 2017-12-11
 */
public class BasicOrganization extends DataEntity<BasicOrganization> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 机构名称
	private String telephone;		// 机构电话
	private String masterName;		// 负责人
	private String masterPhone;		// 负责人电话
	private String provinceCode;		// 省_区号
	private String cityCode;		// 市_区号
	private String areaCode;		// 区_区号
	private String address;		// 详细地址
	private String scopeType;		// 服务范围类型(store:门店;map:地图)
	private String visable;		// 用户是否可见(yes:可见;no:不可见;)
	private String url;		// 机构网址
	private String fax;		// 机构传真
	private String tel400;		// 机构400电话
	private String remark;		// 备注信息
	
	public BasicOrganization() {
		super();
	}

	public BasicOrganization(String id){
		super(id);
	}

	@Length(min=1, max=32, message="机构名称长度必须介于 1 和 32 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=16, message="机构电话长度必须介于 0 和 16 之间")
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	@Length(min=0, max=32, message="负责人长度必须介于 0 和 32 之间")
	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	
	@Length(min=0, max=11, message="负责人电话长度必须介于 0 和 11 之间")
	public String getMasterPhone() {
		return masterPhone;
	}

	public void setMasterPhone(String masterPhone) {
		this.masterPhone = masterPhone;
	}
	
	@Length(min=0, max=20, message="省_区号长度必须介于 0 和 20 之间")
	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	@Length(min=0, max=20, message="市_区号长度必须介于 0 和 20 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Length(min=0, max=20, message="区_区号长度必须介于 0 和 20 之间")
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	@Length(min=0, max=100, message="详细地址长度必须介于 0 和 100 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}
	
	public String getVisable() {
		return visable;
	}

	public void setVisable(String visable) {
		this.visable = visable;
	}
	
	@Length(min=0, max=256, message="机构网址长度必须介于 0 和 256 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=0, max=16, message="机构传真长度必须介于 0 和 16 之间")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	
	@Length(min=0, max=16, message="机构400电话长度必须介于 0 和 16 之间")
	public String getTel400() {
		return tel400;
	}

	public void setTel400(String tel400) {
		this.tel400 = tel400;
	}
	
	@Length(min=0, max=255, message="备注信息长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	List<String> cityCodes;

	public List<String> getCityCodes() {
		return cityCodes;
	}

	public void setCityCodes(List<String> cityCodes) {
		this.cityCodes = cityCodes;
	}
}