/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * E店信息RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenEshopInfoRequest extends DataEntity<OpenEshopInfoRequest> {

	private static final long serialVersionUID = 1L;

	private String name;//E店名称
	private String business_model_id;//业务模式ID
	private String seller_id;//商家ID
	private String eshop_code;//E店编码
	private String operation_base_status;//运营基本信息审核状态：no未审核；submit审核中；fail审核未通过；yes审核通过'
	private String third_part;//第三方对接平台：''未选择、XMGJ小马管家、selfService自营服务
	private String publish;//店铺审核状态：yes 审核通过 no 未审核 fail 未通过

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusiness_model_id() {
		return business_model_id;
	}

	public void setBusiness_model_id(String business_model_id) {
		this.business_model_id = business_model_id;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getEshop_code() {
		return eshop_code;
	}

	public void setEshop_code(String eshop_code) {
		this.eshop_code = eshop_code;
	}

	public String getOperation_base_status() {
		return operation_base_status;
	}

	public void setOperation_base_status(String operation_base_status) {
		this.operation_base_status = operation_base_status;
	}

	public String getThird_part() {
		return third_part;
	}

	public void setThird_part(String third_part) {
		this.third_part = third_part;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}
}