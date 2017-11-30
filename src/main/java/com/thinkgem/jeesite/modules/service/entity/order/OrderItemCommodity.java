/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技能关联Entity
 * @author a
 * @version 2017-11-15
 */
public class OrderItemCommodity extends DataEntity<OrderItemCommodity> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3023634469845823080L;
	private String sortId;		    // 分类编号
	private String sortName;		// 分类名称
	private String itemId;		    // 项目编号
	private String itemName;		// 项目名称
	private String commodityId;		// 商品编号
	private String commodityName;	// 商品名称
	private String meterage;	    // 计量方式
	private double price;		    // 商品单价
	private long orderNum;		    // 商品数量
	
	public OrderItemCommodity() {
		super();
	}

	public OrderItemCommodity(String id){
		super(id);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public long getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}

	@Length(min=0, max=64, message="项目编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getMeterage() {
		return meterage;
	}

	public void setMeterage(String meterage) {
		this.meterage = meterage;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}