package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务项目对应的商品信息派人数量
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_item_info_goods_persons")
public class ItemInfoGoodsPersons extends BaseEntity<ItemInfoGoodsPersons> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务商品ID
     */
	@TableField("goods_id")
	private String goodsId;
    /**
     * 临界值
     */
	private Integer critical;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 人数
     */
	private Integer quantity;


	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getCritical() {
		return critical;
	}

	public void setCritical(Integer critical) {
		this.critical = critical;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ItemInfoGoodsPersons{" +
			", goodsId=" + goodsId +
			", critical=" + critical +
			", sort=" + sort +
			", quantity=" + quantity +
			"}";
	}
}
