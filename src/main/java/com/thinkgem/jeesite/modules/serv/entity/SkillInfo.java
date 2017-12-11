package com.thinkgem.jeesite.modules.serv.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 技能管理
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("ser_skill_info")
public class SkillInfo extends BaseEntity<SkillInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构ID
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 技能名称
     */
	private String name;
    /**
     * 技师数量
     */
	@TableField("tech_num")
	private Integer techNum;


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTechNum() {
		return techNum;
	}

	public void setTechNum(Integer techNum) {
		this.techNum = techNum;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "SkillInfo{" +
			", orgId=" + orgId +
			", name=" + name +
			", techNum=" + techNum +
			"}";
	}
}
