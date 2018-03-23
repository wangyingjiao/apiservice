/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 客户信息Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderCustomInfo extends DataEntity<OrderCustomInfo> {

	private static final long serialVersionUID = 1L;
	private String name;		// 客户姓名
	private String sex;		// 性别（male：男 female：女）
	private String phone;		// 客户电话
	private String provinceCode;		// 省_区号
	private String cityCode;		// 市_区号
	private String areaCode;		// 区_区号
	private String placename;//小区
	private String address;		// 详细地址 门牌号
	private String addrLongitude;		// 经度
	private String addrLatitude;		// 纬度
	private String email;		// 客户邮箱
	private String orgId;		// 所属服务机构ID
	private String source;		// 来源   本机构:own    第三方:other
	private String stationId;		// 所属服务站ID
	private List<OrderDropdownInfo> stationList;
	private String orgName;
	private String customerRemark;		//备注
	private List<String> customerRemarkPic;		//备注 图片

	public OrderCustomInfo() {
		super();
	}

	public OrderCustomInfo(String id){
		super(id);
	}

	@NotBlank(message = "姓名不可为空")
	@Length(min=2, max=15, message="姓名长度必须介于 2 和 15 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank(message = "性别不可为空")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@NotBlank(message = "手机号不可为空")
	@Pattern(regexp = RegexTool.REGEX_MOBILE,message = "手机号格式不正确！")
	@Length(min=11, max=11, message="手机号长度必须是 11 位")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@NotBlank(message = "省不可为空")
	@Length(min=0, max=20, message="省_区号长度必须介于 0 和 20 之间")
	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	@NotBlank(message = "市不可为空")
	@Length(min=0, max=20, message="市_区号长度必须介于 0 和 20 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@NotBlank(message = "区不可为空")
	@Length(min=0, max=20, message="区_区号长度必须介于 0 和 20 之间")
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	@NotBlank(message = "详细地址不可为空")
	@Length(min=0, max=100, message="详细地址长度必须介于 0 和 100 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	//@Pattern(regexp = RegexTool.REGEX_EMAIL,message = "邮箱格式不正确！")
	@Length(min=0, max=64, message="客户邮箱长度必须介于 0 和 64 之间")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Length(min=0, max=32, message="所属服务站ID长度必须介于 0 和 32 之间")
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCustomerRemark() {
		return customerRemark;
	}

	public void setCustomerRemark(String customerRemark) {
		this.customerRemark = customerRemark;
	}

	public List<String> getCustomerRemarkPic() {
		return customerRemarkPic;
	}

	public void setCustomerRemarkPic(List<String> customerRemarkPic) {
		this.customerRemarkPic = customerRemarkPic;
	}

	public List<OrderDropdownInfo> getStationList() {
		return stationList;
	}

	public void setStationList(List<OrderDropdownInfo> stationList) {
		this.stationList = stationList;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}
}