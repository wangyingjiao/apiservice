/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 退款单Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderRefund extends DataEntity<OrderRefund> {
	
	private static final long serialVersionUID = 1L;
	private String refundNumber;		// 退款单编号
	private String orderId;		// 订单ID
	private Date applyTime;		// 申请时间
	private Date finishTime;		// 退款完成时间
	private String refundAccount;		// 退款金额
	private String refundStatus;		// 退款状态(refunding:申请退款中;cancel:已取消;refunded:退款成功;failure:退款失败;)
	private String refundMethod;		// 退款方式(weixin:微信，alipay:支付宝，bank_card:银行卡)
	private String refundReason;		// 退款原因

	private String refundName; //用户名
	private String refundPhone;//用户电话
	private String orgId;  // 机构
	private String orgName;// 机构
	private String stationId;// 服务站
	private String stationName;// 服务站
	private String orderNumber;// 订单编号
	private String orderSource;// 订单来源


	private String refundDifferenceType; // 退款差额
	private String refundDifference; // 退款差额
	private String refundAccountReality;// 实际退款


	private List<OrderRefundGoods> refundGoodsList;

	public OrderRefund() {
		super();
	}

	public OrderRefund(String id){
		super(id);
	}

	@Length(min=0, max=32, message="退款单编号长度必须介于 0 和 32 之间")
	public String getRefundNumber() {
		return refundNumber;
	}

	public void setRefundNumber(String refundNumber) {
		this.refundNumber = refundNumber;
	}
	
	@Length(min=1, max=32, message="订单ID长度必须介于 1 和 32 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	public String getRefundAccount() {
		return refundAccount;
	}

	public void setRefundAccount(String refundAccount) {
		this.refundAccount = refundAccount;
	}
	
	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getRefundMethod() {
		return refundMethod;
	}

	public void setRefundMethod(String refundMethod) {
		this.refundMethod = refundMethod;
	}

	@Length(min=0, max=255, message="退款原因长度必须介于 0 和 255 之间")
	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getRefundName() {
		return refundName;
	}

	public void setRefundName(String refundName) {
		this.refundName = refundName;
	}

	public String getRefundPhone() {
		return refundPhone;
	}

	public void setRefundPhone(String refundPhone) {
		this.refundPhone = refundPhone;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getRefundDifferenceType() {
		return refundDifferenceType;
	}

	public void setRefundDifferenceType(String refundDifferenceType) {
		this.refundDifferenceType = refundDifferenceType;
	}

	public String getRefundDifference() {
		return refundDifference;
	}

	public void setRefundDifference(String refundDifference) {
		this.refundDifference = refundDifference;
	}

	public List<OrderRefundGoods> getRefundGoodsList() {
		return refundGoodsList;
	}

	public void setRefundGoodsList(List<OrderRefundGoods> refundGoodsList) {
		this.refundGoodsList = refundGoodsList;
	}

	public String getRefundAccountReality() {
		return refundAccountReality;
	}

	public void setRefundAccountReality(String refundAccountReality) {
		this.refundAccountReality = refundAccountReality;
	}
}