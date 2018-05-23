/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 退货单Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderRefundGoods extends DataEntity<OrderRefundGoods> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;		// 订单ID
	private String masterId;		// 主订单ID
	private String refundId;		// 退款ID
	private String itemId;		// 服务项目ID
	private String itemName;		// 项目名称
	private String goodsId;		// 商品ID
	private String goodsName;		// 商品名称
	private String goodsNum;		// 订购商品数
	private String payPrice;		// 单价(折后价，无折扣时和原价相同)
	private String goodsUnit;  //单位
	private String payPriceSum; //小计
	
	public OrderRefundGoods() {
		super();
	}

	public OrderRefundGoods(String id){
		super(id);
	}

	@Length(min=1, max=32, message="订单ID长度必须介于 1 和 32 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	@Length(min=1, max=32, message="退款ID长度必须介于 1 和 32 之间")
	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	
	@Length(min=1, max=32, message="服务项目ID长度必须介于 1 和 32 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	@Length(min=0, max=32, message="项目名称长度必须介于 0 和 32 之间")
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	@Length(min=1, max=32, message="商品ID长度必须介于 1 和 32 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	@Length(min=0, max=32, message="商品名称长度必须介于 0 和 32 之间")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	@Length(min=0, max=11, message="订购商品数长度必须介于 0 和 11 之间")
	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}
	
	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	public String getGoodsUnit() {
		return goodsUnit;
	}

	public void setGoodsUnit(String goodsUnit) {
		this.goodsUnit = goodsUnit;
	}

	public String getPayPriceSum() {
		return payPriceSum;
	}

	public void setPayPriceSum(String payPriceSum) {
		this.payPriceSum = payPriceSum;
	}
}