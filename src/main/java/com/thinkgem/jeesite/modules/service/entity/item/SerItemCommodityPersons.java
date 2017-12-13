/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务项目商品派人数量Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemCommodityPersons extends DataEntity<SerItemCommodityPersons> {
	
	private static final long serialVersionUID = 1L;
	private String goodsId;		// 服务商品ID

	private int critical;		// 临界值
	private int quantity;		// 人数
	private int sort;          //排序
	
	public SerItemCommodityPersons() {
		super();
	}

	public SerItemCommodityPersons(String id){
		super(id);
	}

	@Length(min=0, max=64, message="服务商品编号长度必须介于 0 和 64 之间")
	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public int getCritical() {
		return critical;
	}

	public void setCritical(int critical) {
		this.critical = critical;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}