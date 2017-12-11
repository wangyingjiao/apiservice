package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 技能技师关联表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_skill_tech")
public class SkillTech extends BaseEntity<SkillTech> {

    private static final long serialVersionUID = 1L;

    /**
     * 技能ID
     */
	@TableField("skill_id")
	private String skillId;
    /**
     * 技师ID
     */
	@TableField("tech_id")
	private String techId;


	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SkillTech{" +
			", skillId=" + skillId +
			", techId=" + techId +
			"}";
	}
}
