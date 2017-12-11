package com.thinkgem.jeesite.modules.customer.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 客户信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("customer_info")
public class CustomerInfo extends BaseEntity<CustomerInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 客户姓名
     */
	private String name;
    /**
     * 性别（male：男 female：女）
     */
	private String sex;
    /**
     * 客户电话
     */
	private String phone;
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
     * 经度
     */
	@TableField("addr_longitude")
	private String addrLongitude;
    /**
     * 纬度
     */
	@TableField("addr_latitude")
	private String addrLatitude;
    /**
     * 客户邮箱
     */
	private String email;
    /**
     * 所属服务机构ID
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 所属服务站ID
     */
	@TableField("station_id")
	private String stationId;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "CustomerInfo{" +
			", name=" + name +
			", sex=" + sex +
			", phone=" + phone +
			", provinceCode=" + provinceCode +
			", cityCode=" + cityCode +
			", areaCode=" + areaCode +
			", address=" + address +
			", addrLongitude=" + addrLongitude +
			", addrLatitude=" + addrLatitude +
			", email=" + email +
			", orgId=" + orgId +
			", stationId=" + stationId +
			"}";
	}
}
