package com.thinkgem.jeesite.modules.basic.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 机构服务城市列表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("basic_sevice_city")
public class SeviceCity extends BaseEntity<SeviceCity> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 城市代码
     */
	@TableField("city_code")
	private String cityCode;


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SeviceCity{" +
			", orgId=" + orgId +
			", cityCode=" + cityCode +
			"}";
	}
}
