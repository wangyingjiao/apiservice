package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务分类对应的定向城市
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_city_scope")
public class CityScope extends BaseEntity<CityScope> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务分类ID（服务项目ID）
     */
	@TableField("master_id")
	private String masterId;
    /**
     * 0:服务分类 1:服务项目
     */
	private String type;
    /**
     * 市_区号
     */
	@TableField("city_code")
	private String cityCode;


	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		return "CityScope{" +
			", masterId=" + masterId +
			", type=" + type +
			", cityCode=" + cityCode +
			"}";
	}
}
