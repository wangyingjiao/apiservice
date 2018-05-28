/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 支付信息Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderPayInfo extends DataEntity<OrderPayInfo> {
	
	private static final long serialVersionUID = 1L;
	private String payNumber;		// 支付编号
	private String masterId;		// 主订单ID
	private String orderId;		// 订单ID
	private String payPlatform;		// 支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
	private String payMethod;		// 支付方式(online:在线 offline:货到付款)
	private Date payTime;			// 支付时间
	private String payAccount;		// 支付总额
	private String payStatus;		// 支付状态(waitpay:待支付 payed:已支付)
	private String payTech;			//收款人（技师ID）
	private String payTechName;			//收款人（技师ID）
	private String payPlatformName;		// 支付平台中文app使用

	private String orgId;  // 机构
	private String orgName;// 机构
	private String comOrgName;
	private String stationId;// 服务站
	private String stationName;// 服务站
	private String comStationName;
	private String orderNumber;// 订单编号
	private String orderSource;// 订单来源
	private String orderType;// 订单状态

	public String getComOrgName() {
		return comOrgName;
	}

	public void setComOrgName(String comOrgName) {
		this.comOrgName = comOrgName;
	}

	public String getComStationName() {
		return comStationName;
	}

	public void setComStationName(String comStationName) {
		this.comStationName = comStationName;
	}

	public String getPayPlatformName() {
		return payPlatformName;
	}

	public void setPayPlatformName(String payPlatformName) {
		this.payPlatformName = payPlatformName;
	}

	public String getPayTech() {
		return payTech;
	}

	public void setPayTech(String payTech) {
		this.payTech = payTech;
	}

	public OrderPayInfo() {
		super();
	}

	public OrderPayInfo(String id){
		super(id);
	}

	@Length(min=0, max=32, message="支付编号长度必须介于 0 和 32 之间")
	public String getPayNumber() {
		return payNumber;
	}

	public void setPayNumber(String payNumber) {
		this.payNumber = payNumber;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Length(min=1, max=32, message="主订单ID长度必须介于 1 和 32 之间")
	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	
	public String getPayPlatform() {
		return payPlatform;
	}

	public void setPayPlatform(String payPlatform) {
		this.payPlatform = payPlatform;
	}
	
	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	
	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
	
	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getPayTechName() {
		return payTechName;
	}

	public void setPayTechName(String payTechName) {
		this.payTechName = payTechName;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}