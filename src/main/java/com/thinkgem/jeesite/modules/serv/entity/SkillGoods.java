package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 技能商品关联表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_skill_goods")
public class SkillGoods extends BaseEntity<SkillGoods> {

    private static final long serialVersionUID = 1L;

    /**
     * 技能ID
     */
	@TableField("skill_id")
	private String skillId;
    /**
     * 项目ID
     */
	@TableField("item_id")
	private String itemId;
    /**
     * 商品ID
     */
	@TableField("goods_id")
	private String goodsId;


	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SkillGoods{" +
			", skillId=" + skillId +
			", itemId=" + itemId +
			", goodsId=" + goodsId +
			"}";
	}
}
