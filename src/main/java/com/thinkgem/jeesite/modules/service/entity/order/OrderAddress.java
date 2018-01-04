/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单地址Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderAddress extends DataEntity<OrderAddress> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 姓名
	private String phone;		// 手机号
	private String zipcode;		// 邮编
	private String provinceCode;		// 省_区号
	private String cityCode;		// 市_区号
	private String areaCode;		// 区_区号
	private String detailAddress;		// 详细地址
	private String address;		// 收货人完整地址
	
	public OrderAddress() {
		super();
	}

	public OrderAddress(String id){
		super(id);
	}

	@Length(min=0, max=32, message="姓名长度必须介于 0 和 32 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=11, message="手机号长度必须介于 0 和 11 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=10, message="邮编长度必须介于 0 和 10 之间")
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
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
	
	@Length(min=0, max=200, message="详细地址长度必须介于 0 和 200 之间")
	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	
	@Length(min=0, max=200, message="收货人完整地址长度必须介于 0 和 200 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}