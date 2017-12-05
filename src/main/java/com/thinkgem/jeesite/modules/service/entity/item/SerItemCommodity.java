/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.item;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 服务项目商品信息Entity
 * @author a
 * @version 2017-11-15
 */
public class SerItemCommodity extends DataEntity<SerItemCommodity> {
	
	private static final long serialVersionUID = 1L;
	private String itemId;		// 服务项目编号
	private String itemName;		// 服务项目名称
	public String getItemName() {return itemName;}
	public void setItemName(String itemName) {this.itemName = itemName;}
	private String name;		// 商品名称
	private String unit;		// 商品单位
	private String meterage;		// 计量方式
	private String price;		// 价格
	private String convertTime;		// 折算时长
	private Long minimum;		// 起购数量
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
	@Length(min=0, max=64, message="计量方式长度必须介于 0 和 64 之间")
	public String getMeterage() {
		return meterage;
	}

	public void setMeterage(String meterage) {
		this.meterage = meterage;
	}

	@NotBlank(message = "价格不可为空")
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@NotBlank(message = "折算时长不可为空")
	public String getConvertTime() {
		return convertTime;
	}

	public void setConvertTime(String convertTime) {
		this.convertTime = convertTime;
	}
	
	public Long getMinimum() {
		return minimum;
	}

	public void setMinimum(Long minimum) {
		this.minimum = minimum;
	}
	
}