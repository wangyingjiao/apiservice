package com.thinkgem.jeesite.modules.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 角色(岗位)-菜单 关联表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_role_menu")
public class RoleMenu extends BaseEntity<RoleMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色(岗位)id
     */
    @TableId("role_id")
	private String roleId;
    /**
     * 菜单id
     */
	@TableField("menu_id")
	private String menuId;


	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	@Override
	protected Serializable pkVal() {
		return this.roleId;
	}

	@Override
	public String toString() {
		return "RoleMenu{" +
			", roleId=" + roleId +
			", menuId=" + menuId +
			"}";
	}
}
