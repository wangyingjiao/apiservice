package com.thinkgem.jeesite.modules.order.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 派单表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("order_dispatch")
public class OrderDispatch extends BaseEntity<OrderDispatch> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
	@TableField("order_id")
	private String orderId;
    /**
     * 技师ID
     */
	@TableField("tech_id")
	private String techId;
    /**
     * 状态(yes：可用 no：不可用)
     */
	private String status;


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "OrderDispatch{" +
			", orderId=" + orderId +
			", techId=" + techId +
			", status=" + status +
			"}";
	}
}
