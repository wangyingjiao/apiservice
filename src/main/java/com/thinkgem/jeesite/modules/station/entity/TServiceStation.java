/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.station.entity;

import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务站Entity
 * @author x
 * @version 2017-10-27
 */
public class TServiceStation extends DataEntity<TServiceStation> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 服务站名称
	private String serviceStationType;		// 服务站类型,对应字典中的字段
	private Area area;		// 服务站所在区域id
	private String address;		// 详细地址
	private User user;		// 站长id
	private String phone;		// 联系电话或联系手机号
	private String employees;		// 员工数量
	private String aunts;		// 阿姨数量
	private String status;		// 是否可以使用
	private String areaName;		// 区域名称
	
	public TServiceStation() {
		super();
	}

	public TServiceStation(String id){
		super(id);
	}

	@Length(min=0, max=255, message="服务站名称长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=12, message="服务站类型,对应字典中的字段长度必须介于 0 和 12 之间")
	public String getServiceStationType() {
		return serviceStationType;
	}

	public void setServiceStationType(String serviceStationType) {
		this.serviceStationType = serviceStationType;
	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	@Length(min=0, max=255, message="详细地址长度必须介于 0 和 255 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=32, message="联系电话或联系手机号长度必须介于 0 和 32 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=11, message="员工数量长度必须介于 0 和 11 之间")
	public String getEmployees() {
		return employees;
	}

	public void setEmployees(String employees) {
		this.employees = employees;
	}
	
	@Length(min=0, max=11, message="阿姨数量长度必须介于 0 和 11 之间")
	public String getAunts() {
		return aunts;
	}

	public void setAunts(String aunts) {
		this.aunts = aunts;
	}
	
	@Length(min=0, max=11, message="是否可以使用长度必须介于 0 和 11 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=255, message="区域名称长度必须介于 0 和 255 之间")
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
}