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
 * 退款单表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("order_refund")
public class OrderRefund extends BaseEntity<OrderRefund> {

    private static final long serialVersionUID = 1L;

    /**
     * 退款单编号
     */
	@TableField("refund_number")
	private String refundNumber;
    /**
     * 订单ID
     */
	@TableField("order_id")
	private String orderId;
    /**
     * 申请时间
     */
	@TableField("apply_time")
	private Date applyTime;
    /**
     * 退款完成时间
     */
	@TableField("finish_time")
	private Date finishTime;
    /**
     * 退款金额
     */
	@TableField("refund_account")
	private BigDecimal refundAccount;
    /**
     * 退款状态(refunding:申请退款中;cancel:已取消;refunded:退款成功;failure:退款失败;)
     */
	@TableField("refund_status")
	private String refundStatus;
    /**
     * 退款原因
     */
	@TableField("refund_reason")
	private String refundReason;


	public String getRefundNumber() {
		return refundNumber;
	}

	public void setRefundNumber(String refundNumber) {
		this.refundNumber = refundNumber;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public BigDecimal getRefundAccount() {
		return refundAccount;
	}

	public void setRefundAccount(BigDecimal refundAccount) {
		this.refundAccount = refundAccount;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "OrderRefund{" +
			", refundNumber=" + refundNumber +
			", orderId=" + orderId +
			", applyTime=" + applyTime +
			", finishTime=" + finishTime +
			", refundAccount=" + refundAccount +
			", refundStatus=" + refundStatus +
			", refundReason=" + refundReason +
			"}";
	}
}
