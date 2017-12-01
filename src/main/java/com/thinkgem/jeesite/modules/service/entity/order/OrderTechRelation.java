/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单技师关联Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderTechRelation extends DataEntity<OrderTechRelation> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String techId;		// 技师ID
	private String techStatus;	// 状态
    private String techName;    // 技师姓名
    private String techSex;     // 性别
    private String techPhone;   // 手机号
	private String imgUrl;		// 图片地址
	
	public OrderTechRelation() {
		super();
	}

	public OrderTechRelation(String id){
		super(id);
	}

	@Length(min=0, max=64, message="订单ID长度必须介于 0 和 64 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=0, max=64, message="技师ID长度必须介于 0 和 64 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	@Length(min=0, max=1, message="状态长度必须介于 0 和 1 之间")
	public String getTechStatus() {
		return techStatus;
	}

	public void setTechStatus(String techStatus) {
		this.techStatus = techStatus;
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