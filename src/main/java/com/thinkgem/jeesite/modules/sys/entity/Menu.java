package com.thinkgem.jeesite.modules.sys.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("sys_menu")
public class Menu extends BaseEntity<Menu> {

    private static final long serialVersionUID = 1L;

    /**
     * 父级编号
     */
	@TableField("parent_id")
	private String parentId;
    /**
     * 所有父级编号
     */
	@TableField("parent_ids")
	private String parentIds;
    /**
     * 名称
     */
	private String name;
    /**
     * 排序
     */
	private BigDecimal sort;
    /**
     * 链接
     */
	private String href;
    /**
     * 目标
     */
	private String target;
    /**
     * 图标
     */
	private String icon;
    /**
     * 是否在菜单中显示
     */
	@TableField("is_show")
	private String isShow;
    /**
     * 权限标识
     */
	private String permission;


	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getSort() {
		return sort;
	}

	public void setSort(BigDecimal sort) {
		this.sort = sort;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Menu{" +
			", parentId=" + parentId +
			", parentIds=" + parentIds +
			", name=" + name +
			", sort=" + sort +
			", href=" + href +
			", target=" + target +
			", icon=" + icon +
			", isShow=" + isShow +
			", permission=" + permission +
			"}";
	}
}
