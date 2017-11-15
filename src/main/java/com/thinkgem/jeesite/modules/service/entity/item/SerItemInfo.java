/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemInfo extends DataEntity<SerItemInfo> {
	
	private static final long serialVersionUID = 1L;
	private String majorSort;		// 分类：保洁、家修
	private String sortId;		// 所属分类编号
	private String name;		// 服务项目名称
	private String picture;		// 服务图片
	private String description;		// 服务描述
	private String sale;		// 是否上架
	private Long sortNum;		// 排序号
	
	public SerItemInfo() {
		super();
	}

	public SerItemInfo(String id){
		super(id);
	}

	@Length(min=0, max=1, message="分类：保洁、家修长度必须介于 0 和 1 之间")
	public String getMajorSort() {
		return majorSort;
	}

	public void setMajorSort(String majorSort) {
		this.majorSort = majorSort;
	}
	
	@Length(min=0, max=64, message="所属分类编号长度必须介于 0 和 64 之间")
	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}
	
	@Length(min=0, max=64, message="服务项目名称长度必须介于 0 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=255, message="服务图片长度必须介于 0 和 255 之间")
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@Length(min=0, max=255, message="服务描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=1, message="是否上架长度必须介于 0 和 1 之间")
	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}
	
	public Long getSortNum() {
		return sortNum;
	}

	public void setSortNum(Long sortNum) {
		this.sortNum = sortNum;
	}
	
}