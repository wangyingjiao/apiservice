/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

/**
 * 下单服务项目信息Entity
 * @author a
 * @version 2017-12-11
 */
public class OpenServiceInfo extends DataEntity<OpenServiceInfo> {
	private static final long serialVersionUID = 1L;

	private String cate_goods_id;//自营服务服务项目ID
	private String gasq_product_id;//国安社区服务项目ID
	private int buy_num;//购买数量
	private String pay_price;//服务项目单价

	public String getCate_goods_id() {
		return cate_goods_id;
	}

	public void setCate_goods_id(String cate_goods_id) {
		this.cate_goods_id = cate_goods_id;
	}

	public String getGasq_product_id() {
		return gasq_product_id;
	}

	public void setGasq_product_id(String gasq_product_id) {
		this.gasq_product_id = gasq_product_id;
	}

	public int getBuy_num() {
		return buy_num;
	}

	public void setBuy_num(int buy_num) {
		this.buy_num = buy_num;
	}

	public String getPay_price() {
		return pay_price;
	}

	public void setPay_price(String pay_price) {
		this.pay_price = pay_price;
	}
}