package com.thinkgem.jeesite.modules.basic.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 门店表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("basic_store")
public class Store extends BaseEntity<Store> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 门店id
     */
	@TableField("store_id")
	private String storeId;
    /**
     * 门店名称
     */
	@TableField("store_name")
	private String storeName;
    /**
     * 门店类型
     */
	private String white;
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


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Store{" +
			", orgId=" + orgId +
			", storeId=" + storeId +
			", storeName=" + storeName +
			", white=" + white +
			", provinceCode=" + provinceCode +
			", cityCode=" + cityCode +
			", areaCode=" + areaCode +
			"}";
	}
}
