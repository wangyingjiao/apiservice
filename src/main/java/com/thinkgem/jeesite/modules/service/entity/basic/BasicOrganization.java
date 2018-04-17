/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.Date;
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
	private Date workStartTime;//工作开始时间
	private Date workEndTime;//工作结束时间

	private String remark;		// 备注信息
	private String dockType;   //平台
	private List<BasicOrganizationEshop> basicOrganizationEshops; //E店集合
	private String eshopNames;
	private String eshopCode;
	private String type;  //web端员工新增时 角色与机构联动 仅作为收参使用

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEshopCode() {
		return eshopCode;
	}
	public void setEshopCode(String eshopCode) {
		this.eshopCode = eshopCode;
	}

	public String updateOwnFlag;//是否编辑自己
	public String getUpdateOwnFlag() {
		return updateOwnFlag;
	}
	public void setUpdateOwnFlag(String updateOwnFlag) {
		this.updateOwnFlag = updateOwnFlag;
	}
	public String allPlatformFlag;//是否全平台
	public String getAllPlatformFlag() {
		return allPlatformFlag;
	}
	public void setAllPlatformFlag(String allPlatformFlag) {
		this.allPlatformFlag = allPlatformFlag;
	}

	/*private String jointEshopCode;//对接方E店CODE
	public String getJointEshopCode() {
		return jointEshopCode;
	}

	public void setJointEshopCode(String jointEshopCode) {
		this.jointEshopCode = jointEshopCode;
	}
*/

	public String getEshopNames() {
		return eshopNames;
	}

	public void setEshopNames(String eshopNames) {
		this.eshopNames = eshopNames;
	}

	public BasicOrganization() {
		super();
	}

	public BasicOrganization(String id){
		super(id);
	}

	@NotBlank(message = "机构名称不可为空")
	@Length(min=2, max=15, message="机构名称长度必须介于 2 和 15 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank(message = "机构电话不可为空")
	@Length(min=0, max=16, message="机构电话长度必须介于 0 和 16 之间")
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@NotBlank(message = "负责人不可为空")
	@Length(min=2, max=15, message="负责人长度必须介于 2 和 15 之间")
	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	@NotBlank(message = "负责人手机号不可为空")
	@Pattern(regexp = RegexTool.REGEX_MOBILE,message = "负责人手机号格式不正确！")
	@Length(min=11, max=11, message="负责人手机号长度必须是 11 位")
	public String getMasterPhone() {
		return masterPhone;
	}

	public void setMasterPhone(String masterPhone) {
		this.masterPhone = masterPhone;
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
	@Length(min=6, max=100, message="详细地址长度必须介于 6 和 100 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@NotBlank(message = "服务范围类型不可为空")
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

	@JsonFormat(pattern = "HH:mm:ss")
	public Date getWorkStartTime() {
		return workStartTime;
	}

	public void setWorkStartTime(Date workStartTime) {
		this.workStartTime = workStartTime;
	}

	@JsonFormat(pattern = "HH:mm:ss")
	public Date getWorkEndTime() {
		return workEndTime;
	}

	public void setWorkEndTime(Date workEndTime) {
		this.workEndTime = workEndTime;
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

	private int haveStation;

	public int getHaveStation() {
		return haveStation;
	}

	public void setHaveStation(int haveStation) {
		this.haveStation = haveStation;
	}
	private String workStartTimeNew;
	private String workEndTimeNew;

	public String getWorkStartTimeNew() {
		return workStartTimeNew;
	}

	public void setWorkStartTimeNew(String workStartTimeNew) {
		this.workStartTimeNew = workStartTimeNew;
	}

	public String getWorkEndTimeNew() {
		return workEndTimeNew;
	}

	public void setWorkEndTimeNew(String workEndTimeNew) {
		this.workEndTimeNew = workEndTimeNew;
	}

	public String getDockType() {
		return dockType;
	}

	public void setDockType(String dockType) {
		this.dockType = dockType;
	}

	public List<BasicOrganizationEshop> getBasicOrganizationEshops() {
		return basicOrganizationEshops;
	}

	public void setBasicOrganizationEshops(List<BasicOrganizationEshop> basicOrganizationEshops) {
		this.basicOrganizationEshops = basicOrganizationEshops;
	}
}