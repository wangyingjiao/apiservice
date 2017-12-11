package com.thinkgem.jeesite.modules.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 角色(岗位)表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_role")
public class Role extends BaseEntity<Role> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
	@TableField("org_id")
	private String orgId;
    /**
     * 角色(岗位)名称
     */
	private String name;
    /**
     * 等级(1:一级 ..... 10:十级)
     */
	private Integer level;


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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Role{" +
			", orgId=" + orgId +
			", name=" + name +
			", level=" + level +
			"}";
	}
}
