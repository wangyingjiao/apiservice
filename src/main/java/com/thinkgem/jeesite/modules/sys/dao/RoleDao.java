/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色DAO接口
 * @author ThinkGem
 * @version 2013-12-05
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {

	List<Role> getByName(Role role);
	
	 Role getByEnname(Role role);

	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	 int deleteRoleMenu(Role role);

	 int insertRoleMenu(Role role);
	
	/**
	 * 维护角色与公司部门关系
	 * @param role
	 * @return
	 */
	 int deleteRoleOffice(Role role);

	 int insertRoleOffice(Role role);

	List<Role> searchRoleByName(Role role);

    List<Role> findPageList(Role role);

    List<Role> findRoleListByUser(User user);

	int checkUpdateName(@Param("name")String name, @Param("orgId")String orgId,@Param("roleId")String roleId);

}
