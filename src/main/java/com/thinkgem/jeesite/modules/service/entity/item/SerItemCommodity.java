/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 服务项目商品信息Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemCommodity extends DataEntity<SerItemCommodity> {
	
	private static final long serialVersionUID = 1L;
	private String itemId;		// 服务项目编号
	private String name;		// 商品名称
	private String unit;		// 商品单位
	private String type;		// 计量方式(num：按数量 area：按面积 house：按居室)
	private BigDecimal price;		// 价格
	private Double convertHours;		// 折算时长
	private int minPurchase;		// 起购数量
	private List<SerItemCommodityPersons> persons;//派人数量

	@NotNull(message = "派人数量不可为空")
	public List<SerItemCommodityPersons> getPersons() {return persons;}
	public void setPersons(List<SerItemCommodityPersons> persons) {this.persons = persons;}

	public SerItemCommodity() {
		super();
	}

	public SerItemCommodity(String id){
		super(id);
	}

	@Length(min=0, max=64, message="服务项目编号长度必须介于 0 和 64 之间")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@NotBlank(message = "商品名称不可为空")
	@Length(min=2, max=10, message="商品名称长度必须介于 2 和 10 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank(message = "商品单位不可为空")
	@Length(min=1, max=5, message="商品单位长度必须介于 1 和 5 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@NotBlank(message = "计量方式不可为空")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@NotBlank(message = "价格不可为空")
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@NotBlank(message = "折算时长不可为空")
	public Double getConvertHours() {
		return convertHours;
	}

	public void setConvertHours(Double convertHours) {
		this.convertHours = convertHours;
	}
	
	public int getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(int minPurchase) {
		this.minPurchase = minPurchase;
	}

	private Boolean commodityChecked = false;
	@JsonInclude
	public Boolean getCommodityChecked() {
		return commodityChecked;
	}

	public void setCommodityChecked(Boolean commodityChecked) {
		this.commodityChecked = commodityChecked;
	}
}