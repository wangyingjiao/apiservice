/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.station;

import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.entity.Office;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * 服务站Entity
 * @author x
 * @version 2017-12-01
 */
public class ServiceStation extends DataEntity<ServiceStation> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 服务站名称
	private String type;// 服务站类型

	@Deprecated
	private String area;		// 服务站所在区域

	@Deprecated
	private String address;		// 详细地址

	private User user;		// 站长id
	private String userName;		// 站长名称
	private String phone;		// 联系电话或联系手机号
	private String employees;		// 员工数量
	private String aunts;		// 阿姨数量
	private String servicePoint;		// 服务站座标点
	private Office office;		// office_id
	private String officeId;
	private String officeName;		// office_name
	private String addrProvinceId;		// 现住地址_省_id
	private String addrCityId;		// 现住地址_市_id
	private String addrDistrictId;		// 现地地址_区_id
	private String addrProvinceName;		// 现住地址_省_名称
	private String addrCityName;		// 现住地址_市_名称
	private String addrDistrictName;		// 现住地址_区_名称
	private String addrDetailInfo;		// 现住地址_详细信息
	private String radius;		// 半径距离
	private String circleCenter;		// 圆心点
	private String rangeType;		// 1地图，2门店
	private String officeRangeType; //机构范围类型
	private String useable;		// 是否可以使用
	
	public ServiceStation() {
		super();
	}

	public ServiceStation(String id){
		super(id);
	}

	@NotBlank(message = "服务站名称不能为空",groups = SaveStationGroup.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank(message = "服务站类型不能为空" ,groups = SaveStationGroup.class)
	@Length(min=0, max=12, message="服务站类型长度必须介于 0 和 12 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	//@Length(min=0, max=255, message="服务站所在区域长度必须介于 0 和 255 之间")
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
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
	
	@Length(min=0, max=255, message="站长名称长度必须介于 0 和 255 之间")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Pattern(regexp = RegexTool.REGEX_MOBILE,message = "手机号不符合规则",groups = SaveStationGroup.class)
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
	
	@Length(min=0, max=2000, message="服务站座标点长度必须介于 0 和 2000 之间")
	public String getServicePoint() {
		return servicePoint;
	}

	public void setServicePoint(String servicePoint) {
		this.servicePoint = servicePoint;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=255, message="office_name长度必须介于 0 和 255 之间")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	@NotBlank(message = "服务站省id不能为空",groups = SaveStationGroup.class)
	@Length(min=0, max=64, message="现住地址_省_id长度必须介于 0 和 64 之间")
	public String getAddrProvinceId() {
		return addrProvinceId;
	}

	public void setAddrProvinceId(String addrProvinceId) {
		this.addrProvinceId = addrProvinceId;
	}

	@NotBlank(message = "服务站市id不能为空",groups = SaveStationGroup.class)
	@Length(min=0, max=64, message="现住地址_市_id长度必须介于 0 和 64 之间")
	public String getAddrCityId() {
		return addrCityId;
	}

	public void setAddrCityId(String addrCityId) {
		this.addrCityId = addrCityId;
	}

	@NotBlank(message = "服务站区id不能为空",groups = SaveStationGroup.class)
	@Length(min=0, max=64, message="现地地址_区_id长度必须介于 0 和 64 之间")
	public String getAddrDistrictId() {
		return addrDistrictId;
	}

	public void setAddrDistrictId(String addrDistrictId) {
		this.addrDistrictId = addrDistrictId;
	}
	
	@Length(min=0, max=255, message="现住地址_省_名称长度必须介于 0 和 255 之间")
	public String getAddrProvinceName() {
		return addrProvinceName;
	}

	public void setAddrProvinceName(String addrProvinceName) {
		this.addrProvinceName = addrProvinceName;
	}
	
	@Length(min=0, max=255, message="现住地址_市_名称长度必须介于 0 和 255 之间")
	public String getAddrCityName() {
		return addrCityName;
	}

	public void setAddrCityName(String addrCityName) {
		this.addrCityName = addrCityName;
	}
	
	@Length(min=0, max=255, message="现住地址_区_名称长度必须介于 0 和 255 之间")
	public String getAddrDistrictName() {
		return addrDistrictName;
	}

	public void setAddrDistrictName(String addrDistrictName) {
		this.addrDistrictName = addrDistrictName;
	}

	@NotBlank(message = "服务站详细地址不能为空",groups = SaveStationGroup.class)
	@Length(min=0, max=255, message="现住地址_详细信息长度必须介于 0 和 255 之间")
	public String getAddrDetailInfo() {
		return addrDetailInfo;
	}

	public void setAddrDetailInfo(String addrDetailInfo) {
		this.addrDetailInfo = addrDetailInfo;
	}
	
	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}
	
	@Length(min=0, max=255, message="圆心点长度必须介于 0 和 255 之间")
	public String getCircleCenter() {
		return circleCenter;
	}

	public void setCircleCenter(String circleCenter) {
		this.circleCenter = circleCenter;
	}
	
	@Length(min=0, max=2, message="1地图，2门店长度必须介于 0 和 2 之间")
	public String getRangeType() {
		return rangeType;
	}

	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}
	
	@Length(min=0, max=2, message="是否可以使用长度必须介于 0 和 2 之间")
	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeRangeType() {
		return officeRangeType;
	}

	public void setOfficeRangeType(String officeRangeType) {
		this.officeRangeType = officeRangeType;
	}
}