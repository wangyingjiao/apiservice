/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import java.beans.Transient;
import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 角色Entity
 * @author ThinkGem
 * @version 2013-12-05
 * @param <T>
 */
public class Role<T> extends DataEntity<Role> {
	
	private static final long serialVersionUID = 1L;

	@Override
	@NotBlank(message = "删除岗位，不可为空。",groups = DeleteRoleGroup.class)
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	private String id;
	private BasicOrganization organization;	// 归属机构
	private String name; 	// 角色名称
	private String enname;	// 英文名称
	private String roleType;// 权限类型
	private String dataScope;// 数据范围
	private String orgId;
	private String oldName; 	// 原角色名称
	private String oldEnname;	// 原英文名称
	private String sysData; 		//是否是系统数据
	//private String useable; 		//是否是可用
	
	private User user;		// 根据用户ID查询角色列表

	public String updateOwnFlag;//是否编辑自己
	public String getUpdateOwnFlag() {
		return updateOwnFlag;
	}
	public void setUpdateOwnFlag(String updateOwnFlag) {
		this.updateOwnFlag = updateOwnFlag;
	}

//	private List<User> userList = Lists.newArrayList(); // 拥有用户列表
	private List<Menu> menuList = Lists.newArrayList(); // 拥有菜单列表
	private List<BasicOrganization> officeList = Lists.newArrayList(); // 按明细设置数据范围

	// 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
	public static final String DATA_SCOPE_ALL = "1";
	public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";
	public static final String DATA_SCOPE_COMPANY = "3";
	public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";
	public static final String DATA_SCOPE_OFFICE = "5";
	public static final String DATA_SCOPE_SELF = "8";
	public static final String DATA_SCOPE_CUSTOM = "9";

	public static final String DATA_ROLE_SYS = "sys";//全系统
	public static final String DATA_ROLE_ALL = "1";//全平台
	public static final String DATA_ROLE_OFFICE = "2";//全机构、本机构
	public static final String DATA_ROLE_STATION = "3";//本服务站

	public Role() {
		super();
		this.dataScope = DATA_SCOPE_SELF;

	}
	
	public Role(String id){
		super(id);
	}
	
	public Role(User user) {
		this();
		this.user = user;
	}


	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getSysData() {
		return sysData;
	}

	public void setSysData(String sysData) {
		this.sysData = sysData;
	}

	public BasicOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(BasicOrganization organization) {
		this.organization = organization;
	}

	@NotBlank(message = "岗位名称不可为空",groups = SaveRoleGroup.class)
	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=1, max=100)
	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}
	
	@Length(min=1, max=100)
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	@NotBlank(message = "岗位等级不可为空",groups = SaveRoleGroup.class)
	public String getDataScope() {
		return dataScope;
	}

	public void setDataScope(String dataScope) {
		this.dataScope = dataScope;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldEnname() {
		return oldEnname;
	}

	public void setOldEnname(String oldEnname) {
		this.oldEnname = oldEnname;
	}

//	public List<User> getUserList() {
//		return userList;
//	}
//
//	public void setUserList(List<User> userList) {
//		this.userList = userList;
//	}
//	
//	public List<String> getUserIdList() {
//		List<String> nameIdList = Lists.newArrayList();
//		for (User user : userList) {
//			nameIdList.add(user.getId());
//		}
//		return nameIdList;
//	}
//
//	public String getUserIds() {
//		return StringUtils.join(getUserIdList(), ",");
//	}

	public List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}

	public List<String> getMenuIdList() {
		List<String> menuIdList = Lists.newArrayList();
		for (Menu menu : menuList) {
			menuIdList.add(menu.getId());
		}
		return menuIdList;
	}

	public void setMenuIdList(List<String> menuIdList) {
		menuList = Lists.newArrayList();
		for (String menuId : menuIdList) {
			Menu menu = new Menu();
			menu.setId(menuId);
			menuList.add(menu);
		}
	}

	@NotBlank(message = "菜单列表不能为空",groups = SaveRoleGroup.class)
	public String getMenuIds() {
		return StringUtils.join(getMenuIdList(), ",");
	}
	
	public void setMenuIds(String menuIds) {
		menuList = Lists.newArrayList();
		if (menuIds != null){
			String[] ids = StringUtils.split(menuIds, ",");
			setMenuIdList(Lists.newArrayList(ids));
		}
	}
	
	public List<BasicOrganization> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<BasicOrganization> officeList) {
		this.officeList = officeList;
	}

	public List<String> getOfficeIdList() {
		List<String> officeIdList = Lists.newArrayList();
		for (BasicOrganization office : officeList) {
			officeIdList.add(office.getId());
		}
		return officeIdList;
	}

	public void setOfficeIdList(List<String> officeIdList) {
		officeList = Lists.newArrayList();
		for (String officeId : officeIdList) {
			BasicOrganization office = new BasicOrganization();
			office.setId(officeId);
			officeList.add(office);
		}
	}

	public String getOfficeIds() {
		return StringUtils.join(getOfficeIdList(), ",");
	}
	
	public void setOfficeIds(String officeIds) {
		officeList = Lists.newArrayList();
		if (officeIds != null){
			String[] ids = StringUtils.split(officeIds, ",");
			setOfficeIdList(Lists.newArrayList(ids));
		}
	}
	
	/**
	 * 获取权限字符串列表
	 */
	public List<String> getPermissions() {
		List<String> permissions = Lists.newArrayList();
		for (Menu menu : menuList) {
			if (menu.getPermission()!=null && !"".equals(menu.getPermission())){
				permissions.add(menu.getPermission());
			}
		}
		return permissions;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isAdmin(){
		return isAdmin(this.id);
	}

	public static boolean isAdmin(String id){
		return id != null && "1".equals(id);
	}
	
	@Transient
	public String getMenuNames() {
		List<String> menuNameList = Lists.newArrayList();
		for (Menu menu : menuList) {
			menuNameList.add(menu.getName());
		}
		return StringUtils.join(menuNameList, ",");
	}
	//add by wyr判断岗位下是否有员工
	private Boolean flag;

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	//add by wyr 取两个岗位级别不一样的岗位权限集合
	private List<Menu> menuListUnion = Lists.newArrayList();

	public List<Menu> getMenuListUnion() {
		return menuListUnion;
	}

	public void setMenuListUnion(List<Menu> menuListUnion) {
		this.menuListUnion = menuListUnion;
	}
	private T roleUnion;

	public T getRoleUnion() {
		return roleUnion;
	}

	public void setRoleUnion(T roleUnion) {
		this.roleUnion = roleUnion;
	}
	private boolean  flagRoleId;

	public boolean isFlagRoleId() {
		return flagRoleId;
	}

	public void setFlagRoleId(boolean flagRoleId) {
		this.flagRoleId = flagRoleId;
	}

	private List<String> menuIdListEdit;

	public List<String> getMenuIdListEdit() {
		return menuIdListEdit;
	}

	public void setMenuIdListEdit(List<String> menuIdListEdit) {
		this.menuIdListEdit = menuIdListEdit;
	}
	
	
	
	
	
}
