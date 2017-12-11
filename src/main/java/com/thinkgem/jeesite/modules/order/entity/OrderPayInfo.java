package com.thinkgem.jeesite.modules.order.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 支付信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("order_pay_info")
public class OrderPayInfo extends BaseEntity<OrderPayInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 支付编号
     */
	@TableField("pay_number")
	private String payNumber;
    /**
     * 订单ID
     */
	@TableField("order_id")
	private String orderId;
    /**
     * 支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
     */
	@TableField("pay_platform")
	private String payPlatform;
    /**
     * 支付方式(online:在线 offline:货到付款)
     */
	@TableField("pay_method")
	private String payMethod;
    /**
     * 支付时间
     */
	@TableField("pay_time")
	private Date payTime;
    /**
     * 支付总额
     */
	@TableField("pay_account")
	private BigDecimal payAccount;
    /**
     * 支付状态(waitpay:待支付 payed:已支付)
     */
	@TableField("pay_status")
	private String payStatus;


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

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public BigDecimal getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(BigDecimal payAccount) {
		this.payAccount = payAccount;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "OrderPayInfo{" +
			", payNumber=" + payNumber +
			", orderId=" + orderId +
			", payPlatform=" + payPlatform +
			", payMethod=" + payMethod +
			", payTime=" + payTime +
			", payAccount=" + payAccount +
			", payStatus=" + payStatus +
			"}";
	}
}
