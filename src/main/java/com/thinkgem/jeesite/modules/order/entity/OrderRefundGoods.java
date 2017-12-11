package com.thinkgem.jeesite.modules.order.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 退货单表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("order_refund_goods")
public class OrderRefundGoods extends BaseEntity<OrderRefundGoods> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
	@TableField("order_id")
	private String orderId;
    /**
     * 退款ID
     */
	@TableField("refund_id")
	private String refundId;
    /**
     * 服务项目ID
     */
	@TableField("item_id")
	private String itemId;
    /**
     * 商品ID
     */
	@TableField("goods_id")
	private String goodsId;
    /**
     * 订购商品数
     */
	@TableField("goods_num")
	private Integer goodsNum;
    /**
     * 单价(折后价，无折扣时和原价相同)
     */
	@TableField("pay_price")
	private BigDecimal payPrice;


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}

	public BigDecimal getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(BigDecimal payPrice) {
		this.payPrice = payPrice;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "OrderRefundGoods{" +
			", orderId=" + orderId +
			", refundId=" + refundId +
			", itemId=" + itemId +
			", goodsId=" + goodsId +
			", goodsNum=" + goodsNum +
			", payPrice=" + payPrice +
			"}";
	}
}
