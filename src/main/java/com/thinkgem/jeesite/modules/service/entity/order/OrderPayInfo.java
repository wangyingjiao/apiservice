/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;
import java.util.Date;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 支付信息Entity
 * @author a
 * @version 2017-11-23
 */
public class OrderPayInfo extends DataEntity<OrderPayInfo> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String payMode;		// 支付方式(1:cash(现金);2:wx_pub_qr(微信扫码);3:wx(微信);4:alipay_qr(支付宝扫码);5:alipay(支付宝);6:pos(银行卡);7:balance(余额);)
	private Date payTime;		// 支付时间
	private String payAccount;	// 支付总额
	private String payStatus;	// 支付状态(1:待支付;2:已支付;)
	
	public OrderPayInfo() {
		super();
	}

	public OrderPayInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="订单ID长度必须介于 0 和 64 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=0, max=1, message="支付方式必须为【1-7】之间的1位数字")
    @Pattern(regexp = "^[1-7]{1}$", message = "支付方式必须为【1-7】之间的1位数字")
	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

    @Pattern(regexp = "^(-?\\\\d+)(\\\\.\\\\d+)?$", message = "支付金额请填写数字") 
	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
	
	@Length(min=0, max=1, message="支付状态必须为【1-2】之间的1位数字")
    @Pattern(regexp = "^[1-2]{1}$", message = "支付方式必须为【1-2】之间的1位数字")
	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	
}