package com.thinkgem.jeesite.modules.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 区域表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_area")
public class Area extends BaseEntity<Area> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
	private String name;
    /**
     * 区域编码
     */
	private String code;
    /**
     * 邮政编码
     */
	@TableField("zip_code")
	private String zipCode;
    /**
     * 级别
     */
	private Integer level;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 父级编码
     */
	@TableField("parent_code")
	private String parentCode;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Area{" +
			", name=" + name +
			", code=" + code +
			", zipCode=" + zipCode +
			", level=" + level +
			", sort=" + sort +
			", parentCode=" + parentCode +
			"}";
	}
}
