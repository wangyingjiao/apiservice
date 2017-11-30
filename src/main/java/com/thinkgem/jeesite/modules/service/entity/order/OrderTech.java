/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 技师技能关联Entity
 * @author a
 * @version 2017-11-15
 */
public class OrderTech extends DataEntity<OrderTech> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9042156612201487923L;
	private String techId;		        // 技师编号
    private String techName;            // 技师姓名
    private String techSex;             // 性别
    private String techPhone;           // 手机号
	private String imgUrl;		        // 图片地址
	
	@Length(min=0, max=64, message="技师编号长度必须介于 0 和 64 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public String getTechSex() {
		return techSex;
	}

	public void setTechSex(String techSex) {
		this.techSex = techSex;
	}

	public String getTechPhone() {
		return techPhone;
	}

	public void setTechPhone(String techPhone) {
		this.techPhone = techPhone;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}