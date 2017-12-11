package com.thinkgem.jeesite.modules.tech.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务技师家庭成员
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("tech_family_members")
public class TechFamilyMembers extends BaseEntity<TechFamilyMembers> {

    private static final long serialVersionUID = 1L;

    /**
     * 服务技师id
     */
	@TableField("tech_id")
	private String techId;
    /**
     * 与服务技师关系(spouse：夫妻 parent：父母 siblings：兄弟姐妹 relative：亲戚 children：子女)
     */
	private String relation;
    /**
     * 成员名字
     */
	@TableField("member_name")
	private String memberName;
    /**
     * 成员手机号
     */
	@TableField("member_phone")
	private String memberPhone;
    /**
     * 成员单位
     */
	@TableField("member_company")
	private String memberCompany;
    /**
     * 成员职务
     */
	@TableField("member_job")
	private String memberJob;


	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public String getMemberCompany() {
		return memberCompany;
	}

	public void setMemberCompany(String memberCompany) {
		this.memberCompany = memberCompany;
	}

	public String getMemberJob() {
		return memberJob;
	}

	public void setMemberJob(String memberJob) {
		this.memberJob = memberJob;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "TechFamilyMembers{" +
			", techId=" + techId +
			", relation=" + relation +
			", memberName=" + memberName +
			", memberPhone=" + memberPhone +
			", memberCompany=" + memberCompany +
			", memberJob=" + memberJob +
			"}";
	}
}
