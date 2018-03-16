/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 待对接Entity
 * @author ThinkGem
 * @version 2014-8-19
 */
public class SysJointWait extends DataEntity<SysJointWait> {

	private static final long serialVersionUID = 1L;

	private String url; 	// 请求URL
	private String sendType; //对接类型 "save_order": "更新订单信息","del_goods": "删除商品","save_goods": "保存商品",org_del_goods:机构删除E店关联商品
	private String requestContent; 		// 请求内容
	private String orderNumber;  // 对接ID
	private String eshopCode;
	private String jointGoodsCodes;

	private String many;  //是否多次请求
	private int num;  //请求次数

	public SysJointWait(){
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getMany() {
		return many;
	}

	public void setMany(String many) {
		this.many = many;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getEshopCode() {
		return eshopCode;
	}

	public void setEshopCode(String eshopCode) {
		this.eshopCode = eshopCode;
	}

	public String getJointGoodsCodes() {
		return jointGoodsCodes;
	}

	public void setJointGoodsCodes(String jointGoodsCodes) {
		this.jointGoodsCodes = jointGoodsCodes;
	}
}