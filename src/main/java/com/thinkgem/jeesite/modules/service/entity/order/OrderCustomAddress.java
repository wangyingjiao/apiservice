/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 客户地址信息Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderCustomAddress extends DataEntity<OrderCustomAddress> {

	private static final long serialVersionUID = 1L;

	private String customerId;		// 客户
	private String addressName;		// 客户姓名
	private String addressPhone;		// 客户电话
	private String provinceCode;		// 省_区号
	private String cityCode;		// 市_区号
	private String areaCode;		// 区_区号
	private String placename;//小区
	private String detailAddress;		// 详细地址 门牌号
	private String addrLongitude;		// 经度
	private String addrLatitude;		// 纬度
	private String defaultType; // 是否默认地址(yes:可用，no:不可用)

	public OrderCustomAddress() {
		super();
	}

	public OrderCustomAddress(String id){
		super(id);
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddressPhone() {
		return addressPhone;
	}

	public void setAddressPhone(String addressPhone) {
		this.addressPhone = addressPhone;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getAddrLongitude() {
		return addrLongitude;
	}

	public void setAddrLongitude(String addrLongitude) {
		this.addrLongitude = addrLongitude;
	}

	public String getAddrLatitude() {
		return addrLatitude;
	}

	public void setAddrLatitude(String addrLatitude) {
		this.addrLatitude = addrLatitude;
	}

	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
}