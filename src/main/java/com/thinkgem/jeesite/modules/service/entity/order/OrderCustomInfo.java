/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 客户信息Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderCustomInfo extends DataEntity<OrderCustomInfo> {
	
	private static final long serialVersionUID = 1L;
	private String customName;		// 客户姓名
	private String customSex;		// 客户性别
	private String customPhone;		// 客户电话
	private String customArea;		// 客户所在区域
	private String customAddr;		// 客户详细地址
	private String addrLongitude;		// 经度
	private String addrLatitude;		// 纬度
	private String customEmail;		// 客户邮箱
	private String officeId;
	private String officeName;		// 所属服务机构名称
	private String stationId;		// 所属服务站ID
	private String stationName;		// 所属服务站名称
	private String cusProvId;		// 客户所在省ID
	private String cusProvName;		// 客户所在省名称
	private String cusCityId;		// 客户所在市ID
	private String cusCityName;		// 客户所在市名称
	private String cusTownId;		// 客户所在县ID
	private String cusTownName;		// 客户所在县名称
	
	public OrderCustomInfo() {
		super();
	}

	public OrderCustomInfo(String id){
		super(id);
	}

	@Length(min=0, max=255, message="客户姓名长度必须介于 0 和 255 之间")
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	@Length(min=0, max=1, message="客户性别长度必须介于 0 和 1 之间")
	public String getCustomSex() {
		return customSex;
	}

	public void setCustomSex(String customSex) {
		this.customSex = customSex;
	}
	
	@Length(min=0, max=32, message="客户电话长度必须介于 0 和 32 之间")
	public String getCustomPhone() {
		return customPhone;
	}

	public void setCustomPhone(String customPhone) {
		this.customPhone = customPhone;
	}
	
	public String getCustomArea() {
		return customArea;
	}

	public void setCustomArea(String customArea) {
		this.customArea = customArea;
	}

	@Length(min=0, max=255, message="客户地址长度必须介于 0 和 255 之间")
	public String getCustomAddr() {
		return customAddr;
	}

	public void setCustomAddr(String customAddr) {
		this.customAddr = customAddr;
	}
	
	@Length(min=0, max=64, message="经度长度必须介于 0 和 64 之间")
	public String getAddrLongitude() {
		return addrLongitude;
	}

	public void setAddrLongitude(String addrLongitude) {
		this.addrLongitude = addrLongitude;
	}
	
	@Length(min=0, max=64, message="纬度长度必须介于 0 和 64 之间")
	public String getAddrLatitude() {
		return addrLatitude;
	}

	public void setAddrLatitude(String addrLatitude) {
		this.addrLatitude = addrLatitude;
	}
	
	@Length(min=0, max=64, message="客户邮箱长度必须介于 0 和 64 之间")
	public String getCustomEmail() {
		return customEmail;
	}

	public void setCustomEmail(String customEmail) {
		this.customEmail = customEmail;
	}
	
/*	public Office getOrganization() {
		return office;
	}

	public void setOrganization(Office office) {
		this.office = office;
	}*/
	
	@Length(min=0, max=255, message="所属服务机构名称长度必须介于 0 和 255 之间")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	@Length(min=0, max=64, message="所属服务站ID长度必须介于 0 和 64 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	@Length(min=0, max=255, message="所属服务站名称长度必须介于 0 和 255 之间")
	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getCusProvId() {
		return cusProvId;
	}

	public void setCusProvId(String cusProvId) {
		this.cusProvId = cusProvId;
	}

	public String getCusProvName() {
		return cusProvName;
	}

	public void setCusProvName(String cusProvName) {
		this.cusProvName = cusProvName;
	}

	public String getCusCityId() {
		return cusCityId;
	}

	public void setCusCityId(String cusCityId) {
		this.cusCityId = cusCityId;
	}

	public String getCusCityName() {
		return cusCityName;
	}

	public void setCusCityName(String cusCityName) {
		this.cusCityName = cusCityName;
	}

	public String getCusTownId() {
		return cusTownId;
	}

	public void setCusTownId(String cusTownId) {
		this.cusTownId = cusTownId;
	}

	public String getCusTownName() {
		return cusTownName;
	}

	public void setCusTownName(String cusTownName) {
		this.cusTownName = cusTownName;
	}
}