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
	private String commodityId;		// 服务商品编号
	private String critical;		// 临界值
	private Long quantity;		// 人数
	
	public SerItemCommodityPersons() {
		super();
	}

	public SerItemCommodityPersons(String id){
		super(id);
	}

	@Length(min=0, max=64, message="服务商品编号长度必须介于 0 和 64 之间")
	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}
	
	@Length(min=0, max=64, message="临界值长度必须介于 0 和 64 之间")
	public String getCritical() {
		return critical;
	}

	public void setCritical(String critical) {
		this.critical = critical;
	}
	
	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
}