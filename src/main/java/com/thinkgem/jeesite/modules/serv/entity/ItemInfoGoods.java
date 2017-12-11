package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务项目商品信息
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_item_info_goods")
public class ItemInfoGoods extends BaseEntity<ItemInfoGoods> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务项目ID
     */
	@TableField("item_id")
	private String itemId;
    /**
     * 商品名称
     */
	private String name;
    /**
     * 商品单位
     */
	private String unit;
    /**
     * 计量方式(num：按数量 area：按面积 house：按居室)
     */
	private String type;
    /**
     * 价格
     */
	private BigDecimal price;
    /**
     * 折算时长
     */
	@TableField("convert_hours")
	private BigDecimal convertHours;
    /**
     * 起购数量
     */
	@TableField("min_purchase")
	private Integer minPurchase;


	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getConvertHours() {
		return convertHours;
	}

	public void setConvertHours(BigDecimal convertHours) {
		this.convertHours = convertHours;
	}

	public Integer getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(Integer minPurchase) {
		this.minPurchase = minPurchase;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ItemInfoGoods{" +
			", itemId=" + itemId +
			", name=" + name +
			", unit=" + unit +
			", type=" + type +
			", price=" + price +
			", convertHours=" + convertHours +
			", minPurchase=" + minPurchase +
			"}";
	}
}
