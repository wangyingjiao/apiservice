package com.thinkgem.jeesite.modules.basic.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务站
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("basic_service_station")
public class ServiceStation extends BaseEntity<ServiceStation> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务站名称
     */
	private String name;
    /**
     * 服务站类型（self:直营，join:加盟）
     */
	private String type;
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
     * 站长id
     */
	@TableField("user_id")
	private String userId;
    /**
     * 联系电话或联系手机号
     */
	private String phone;
    /**
     * 员工数量
     */
	private Integer employees;
    /**
     * 技师数量
     */
	@TableField("tech_num")
	private Integer techNum;
    /**
     * 服务站座标点
     */
	@TableField("service_point")
	private String servicePoint;
    /**
     * 机构ID
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 是否可用(yes:可用，no:不可用)
     */
	@TableField("is_useable")
	private String isUseable;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getEmployees() {
		return employees;
	}

	public void setEmployees(Integer employees) {
		this.employees = employees;
	}

	public Integer getTechNum() {
		return techNum;
	}

	public void setTechNum(Integer techNum) {
		this.techNum = techNum;
	}

	public String getServicePoint() {
		return servicePoint;
	}

	public void setServicePoint(String servicePoint) {
		this.servicePoint = servicePoint;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getIsUseable() {
		return isUseable;
	}

	public void setIsUseable(String isUseable) {
		this.isUseable = isUseable;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ServiceStation{" +
			", name=" + name +
			", type=" + type +
			", provinceCode=" + provinceCode +
			", cityCode=" + cityCode +
			", areaCode=" + areaCode +
			", address=" + address +
			", userId=" + userId +
			", phone=" + phone +
			", employees=" + employees +
			", techNum=" + techNum +
			", servicePoint=" + servicePoint +
			", orgId=" + orgId +
			", isUseable=" + isUseable +
			"}";
	}
}
