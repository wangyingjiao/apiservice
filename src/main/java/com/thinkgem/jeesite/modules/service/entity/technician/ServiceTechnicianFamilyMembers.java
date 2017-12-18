/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.technician;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 服务技师家庭成员Entity
 * @author a
 * @version 2017-11-16
 */
public class ServiceTechnicianFamilyMembers extends DataEntity<ServiceTechnicianFamilyMembers> {
	
	private static final long serialVersionUID = 1L;
	private String techId;		// 服务技师id
	private String techName;		// 服务技师名称
	private String relation;		// 与服务技师关系
	private String memberName;		// 成员名字
	private String memberPhone;		// 成员手机号
	private String memberCompany;		// 成员单位
	private String memberJob;		// 成员职务
	
	public ServiceTechnicianFamilyMembers() {
		super();
	}

	public ServiceTechnicianFamilyMembers(String id){
		super(id);
	}

	@Length(min=0, max=32, message="服务技师id长度必须介于 0 和 32 之间")
	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}
	
	@Length(min=0, max=64, message="服务技师名称长度必须介于 0 和 64 之间")
	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}
	
	@Length(min=0, max=2, message="与服务技师关系长度必须介于 0 和 2 之间")
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	@Length(min=0, max=64, message="成员名字长度必须介于 0 和 64 之间")
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	@Length(min=0, max=32, message="成员手机号长度必须介于 0 和 32 之间")
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

	@Length(min=0, max=64, message="成员职务长度必须介于 0 和 64 之间")
	public String getMemberJob() {
		return memberJob;
	}

	public void setMemberJob(String memberJob) {
		this.memberJob = memberJob;
	}

}