package com.thinkgem.jeesite.modules.basic.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 机构设置
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("basic_organization")
public class Organization extends BaseEntity<Organization> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构名称
     */
	private String name;
    /**
     * 机构电话
     */
	private String telephone;
    /**
     * 负责人
     */
	@TableField("master_name")
	private String masterName;
    /**
     * 负责人电话
     */
	@TableField("master_phone")
	private String masterPhone;
    /**
     * 省_区号
     */
	@TableField("province_code")
	private String provinceCode;
    /**
     * 市_区号
     */
	@TableField("city_code")
	private String cityCode;
    /**
     * 区_区号
     */
	@TableField("area_code")
	private String areaCode;
    /**
     * 详细地址
     */
	private String address;
    /**
     * 服务范围类型(store:门店;map:地图)
     */
	@TableField("scope_type")
	private String scopeType;
    /**
     * 用户是否可见(yes:可见;no:不可见;)
     */
	private String visable;
    /**
     * 机构网址
     */
	private String url;
    /**
     * 机构传真
     */
	private String fax;
    /**
     * 机构400电话
     */
	@TableField("tel_400")
	private String tel400;
    /**
     * 备注信息
     */
	private String remark;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public String getMasterPhone() {
		return masterPhone;
	}

	public void setMasterPhone(String masterPhone) {
		this.masterPhone = masterPhone;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getTel400() {
		return tel400;
	}

	public void setTel400(String tel400) {
		this.tel400 = tel400;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Organization{" +
			", name=" + name +
			", telephone=" + telephone +
			", masterName=" + masterName +
			", masterPhone=" + masterPhone +
			", provinceCode=" + provinceCode +
			", cityCode=" + cityCode +
			", areaCode=" + areaCode +
			", address=" + address +
			", scopeType=" + scopeType +
			", visable=" + visable +
			", url=" + url +
			", fax=" + fax +
			", tel400=" + tel400 +
			", remark=" + remark +
			"}";
	}
}
