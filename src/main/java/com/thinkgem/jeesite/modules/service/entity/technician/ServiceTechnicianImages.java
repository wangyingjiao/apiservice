/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技师关联图片Entity
 * @author a
 * @version 2017-11-16
 */
public class ServiceTechnicianImages extends DataEntity<ServiceTechnicianImages> {
	
	private static final long serialVersionUID = 1L;
	private String techId;		// 技师id
	private String techName;		// 技师名称
	private String imgType;		// 图片分类（1头像，2身份证，4生活照，3证件照）
	private String imgUrl;		// 图片地址
	private String sort;		// 排序
	
	public ServiceTechnicianImages() {
		super();
	}

	public ServiceTechnicianImages(String id){
		super(id);
	}

	@Length(min=0, max=64, message="技师id长度必须介于 0 和 64 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	@Length(min=0, max=64, message="技师名称长度必须介于 0 和 64 之间")
	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}
	
	@Length(min=0, max=2, message="图片分类（1头像，2身份证，4生活照，3证件照）长度必须介于 0 和 2 之间")
	public String getImgType() {
		return imgType;
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}
	
	@Length(min=0, max=255, message="图片地址长度必须介于 0 和 255 之间")
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}