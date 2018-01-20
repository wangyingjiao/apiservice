/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.Collections3;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.sys.entity.DeleteRoleGroup;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.SaveRoleGroup;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;
import sun.security.util.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import static com.thinkgem.jeesite.modules.sys.utils.UserUtils.genTreeMenu;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色Controller
 *
 * @author ThinkGem
 * @version 2013-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
@Api(tags = "角色（岗位）类", description = "角色（岗位）管理相关接口", value = "这是Api中value的位置！", position = 0)
public class RoleController extends BaseController {

	@Autowired
	private SystemService systemService;

	@Autowired
	private OfficeService officeService;

	@ModelAttribute("role")
	public Role get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return systemService.getRole(id);
		} else {
			return new Role();
		}
	}

	@ApiIgnore
	@RequiresPermissions("role_view")
	@RequestMapping(value = { "list", "" })
	public String list(Role role, Model model) {
		List<Role> list = systemService.findAllRole();
		model.addAttribute("list", list);
		return "modules/sys/roleList";
	}

	@ApiIgnore
	@RequiresPermissions("sys:role:view")
	@RequestMapping(value = "form")
	public String form(Role role, Model model) {
		if (role.getOrganization() == null) {
			role.setOrganization(UserUtils.getUser().getOrganization());
		}
		model.addAttribute("role", role);
		model.addAttribute("menuList", systemService.findAllMenu());
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/roleForm";
	}

	@ApiIgnore
	@RequiresPermissions("role_insert")
	@RequestMapping(value = "save")
	public String save(Role role, Model model, RedirectAttributes redirectAttributes) {
		if (!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)) {
			addMessage(redirectAttributes, "越权操作，只有超级管理员才能修改此数据！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if (!beanValidator(model, role)) {
			return form(role, model);
		}
		if (!"true".equals(checkName(role.getOldName(), role.getName()))) {
			addMessage(model, "保存角色'" + role.getName() + "'失败, 角色名已存在");
			return form(role, model);
		}
		if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))) {
			addMessage(model, "保存角色'" + role.getName() + "'失败, 英文名已存在");
			return form(role, model);
		}
		systemService.saveRole(role);
		addMessage(redirectAttributes, "保存角色'" + role.getName() + "'成功");
		return "redirect:" + adminPath + "/sys/role/?repage";
	}

	@ApiIgnore
	@RequiresPermissions("role_delete")
	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String delete(String id, RedirectAttributes redirectAttributes) {

		Role role = systemService.getRole(id);
		if (null == role) {
			addMessage(redirectAttributes, "岗位已经不存在！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if (!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)) {
			addMessage(redirectAttributes, "越权操作，只有超级管理员才能修改此数据！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		// if (Role.isAdmin(id)){
		// addMessage(redirectAttributes, "删除角色失败, 不允许内置角色或编号空");
		//// }else if (UserUtils.getUser().getRoleIdList().contains(id)){
		//// addMessage(redirectAttributes, "删除角色失败, 不能删除当前用户所在角色");
		// }else{
		systemService.deleteRole(role);
		addMessage(redirectAttributes, "删除角色成功");
		// }
		return "redirect:" + adminPath + "/sys/role/?repage";
	}

	/**
	 * 角色分配页面
	 *
	 * @param role
	 * @param model
	 * @return
	 */
	@ApiIgnore
	@RequestMapping(value = "assign")
	public String assign(Role role, Model model) {
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("userList", userList);
		return "modules/sys/roleAssign";
	}

	/**
	 * 角色分配 -- 打开角色分配对话框
	 *
	 * @param role
	 * @param model
	 * @return
	 */
	@ApiIgnore
	@RequiresPermissions("role_view")
	@RequestMapping(value = "usertorole")
	public String selectUserToRole(Role role, Model model) {
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("role", role);
		model.addAttribute("userList", userList);
		model.addAttribute("selectIds", Collections3.extractToString(userList, "name", ","));
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/selectUserToRole";
	}

	/**
	 * 角色分配 -- 根据部门编号获取用户列表
	 *
	 * @param officeId
	 * @param response
	 * @return
	 */
	@RequiresPermissions("role_view")
	@ResponseBody
	@RequestMapping(value = "users", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		User user = new User();
		user.setOrganization(new BasicOrganization(officeId));
		Page<User> page = systemService.findUser(new Page<User>(1, -1), user);
		for (User e : page.getList()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", 0);
			map.put("name", e.getName());
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 角色分配 -- 从角色中移除用户
	 *
	 * @param userId
	 * @param roleId
	 * @param redirectAttributes
	 * @return
	 */
	@ApiIgnore
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "outrole")
	public String outrole(String userId, String roleId, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id=" + roleId;
		}
		Role role = systemService.getRole(roleId);
		User user = systemService.getUser(userId);
		if (UserUtils.getUser().getId().equals(userId)) {
			addMessage(redirectAttributes, "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		} else {
			if (user.getRoleList().size() <= 1) {
				addMessage(redirectAttributes,
						"用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
			} else {
				Boolean flag = systemService.outUserInRole(role, user);
				if (!flag) {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
				} else {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
				}
			}
		}
		return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
	}

	/**
	 * 角色分配
	 *
	 * @param role
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@ApiIgnore
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "assignrole")
	public String assignRole(Role role, String[] idsArr, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < idsArr.length; i++) {
			User user = systemService.assignUserToRole(role, systemService.getUser(idsArr[i]));
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		addMessage(redirectAttributes, "已成功分配 " + newNum + " 个用户" + msg);
		return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
	}

	/**
	 * 验证角色名是否有效
	 *
	 * @param oldName
	 * @param name
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "checkName", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "验证角色名是否有效")
	public String checkName(@RequestParam(required = false) String oldName, @RequestParam String name) {
		if (name != null && name.equals(oldName)) {
			return "true";
			// return new SuccResult("名称可用");
		} else if (name != null && systemService.getRoleByName(name) == null) {
			// return new SuccResult("名称可用");
			return "true";
		}
		// return new FailResult("名称不可用");
		return "false";
	}
	// 加机构的id  若机构id为空直接返回不做操作
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "chkName", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "验证角色名是否有效")
	public Result chkName(@RequestParam(required = false) String id, @RequestParam(required = false) String oldName, @RequestParam String name) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(id)) {
			return new FailResult("机构id不能为空");
		}
		//同一下的机构，岗位名不可重复add by wyr
		int i=systemService.checkAddName(name, id);
		if (i>0) {
			return new FailResult("同一机构下岗位名不可重复");
		}else{
			return new SuccResult("名称可用");
		}
		/*if (name != null && name.equals(oldName)) {
			return new SuccResult("名称可用");
		} else if (name != null && systemService.getRoleByName(name).size() == 0) {
			return new SuccResult("名称可用");
		}
		
		return new FailResult("名称不可用");*/
	}

	// add by wyr 编辑岗位时校验岗位名重复
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "chkNameUpdate", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "验证角色名是否有效")
	public Result chkNameUpdate(@RequestParam String id,@RequestParam(required = false) String oldName, @RequestParam String name,
			@RequestParam String roleId) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(id)) {
			return new FailResult("机构id不能为空");
		}
		int i = systemService.checkUpdateName(name, id, roleId);
		if (0 != i) {
			return new FailResult("名称不可用");
		}
		/*if (name != null && name.equals(oldName)) {
			return new SuccResult("名称可用");
		} else if (name != null && systemService.getRoleByName(name).size() == 0) {
			return new SuccResult("名称可用");
		}*/
		// return new FailResult("名称不可用");
		return new SuccResult("名称可用");
	}

	/**
	 * 验证角色英文名是否有效
	 *
	 * @param oldEnname
	 * @param enname
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "checkEnname", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation(value = "验证角色英文名是否有效")
	public String checkEnname(@RequestParam(required = false) String oldEnname, @RequestParam String enname) {
		if (enname != null && enname.equals(oldEnname)) {
			return "true";
		} else if (enname != null && systemService.getRoleByEnname(enname) == null) {
			return "true";
		}
		return "false";
	}

	@ResponseBody
	@RequiresPermissions("role_insert")
	@RequestMapping(value = "saveData", method = RequestMethod.POST)
	@ApiOperation(value = "新建，更新岗位")
	public Result saveData(@RequestBody Role role) {
		List<String> errList = errors(role, SaveRoleGroup.class);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}

		/*if (StringUtils.isBlank(role.getId())) {
			List<Role> roleByName = systemService.getRoleByName(role.getName());
			if (roleByName != null && roleByName.size() > 0) {
				for (Role role2 : roleByName) {
					if (role2 != null) {
						return new FailResult("保存角色'" + role.getName() + "'失败, 角色名已存在");
					}
				}
			}
		} else {
			// 编辑岗位 是否修改名称
			List<Role> roleByName = systemService.getRoleByName(role.getName());
			// 根据名称查询出的岗位为空 没有该岗位
			if (roleByName != null && roleByName.size() > 0) {
				for (Role role2 : roleByName) {
					if (role2 != null) {
						if (!role2.getId().equals(role.getId())) {
							return new FailResult("保存角色'" + role.getName() + "'失败, 角色名已存在");
						} else {

						}
					}
				}
			}
		}*/
		User user = UserUtils.getUser();
		// 获取岗位机构
		BasicOrganization organization = user.getOrganization();
		if (role.getOrganization() == null) {
			role.setOrganization(organization);
		}
		systemService.saveRole(role);

		return new SuccResult(role);

	}

	@ResponseBody
	@RequiresPermissions("role_update")
	@RequestMapping(value = "upData", method = RequestMethod.POST)
	@ApiOperation(value = "新建，更新岗位")
	public Result upData(@RequestBody Role role) {
		List<String> errList = errors(role, SaveRoleGroup.class);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		// add by wyr重复岗位名的校验已经调用chkNameUpdate接口，以下注释掉
		/*
		 * if (StringUtils.isBlank(role.getId())) {
		 * 
		 * Role roleByName = systemService.getRoleByName(role.getName()); if
		 * (roleByName != null) { return new FailResult("保存角色'" + role.getName()
		 * + "'失败, 角色名已存在"); } }else{ //编辑岗位 是否修改名称 Role roleByName =
		 * systemService.getRoleByName(role.getName()); //根据名称查询出的岗位为空 没有该岗位 if
		 * (roleByName != null) { //如果查询出来的岗位与传输的岗位名称相同 id不同 则不可以修改 if
		 * (!roleByName.getId().equals(role.getId())) { return new
		 * FailResult("保存角色'" + role.getName() + "'失败, 角色名已存在"); }else{
		 * 
		 * } } }
		 */
		User user = UserUtils.getUser();
		//获取岗位机构
		BasicOrganization organization = user.getOrganization();
		if (role.getOrganization() == null) {
			role.setOrganization(organization);
		}
		systemService.saveRole(role);

		return new SuccResult(role);

	}

	@ResponseBody
	@RequiresPermissions("role_delete")
	@RequestMapping(value = "deleteRole", method = RequestMethod.POST)
	@ApiOperation(value = "删除角色（岗位）")
	public Result deleteRole(@RequestBody Role role) {

		List<String> errors = errors(role, DeleteRoleGroup.class);
		if (errors.size() > 0) {
			return new FailResult(errors);
		}
		if (Role.isAdmin(role.getId())) {
			return new FailResult("删除角色失败, 不允许内置角色或编号空");
		} else if (UserUtils.getUser().getRoleIdList().contains(role.getId())) {
			return new FailResult("删除角色失败, 不能删除当前用户所在角色");
		} else {
			List<User> users = systemService.findUserByRole(role);
			if (users.size() > 0) {
				return new FailResult("该岗位已有员工，暂时无法删除");
			} else {
				systemService.deleteRole(role);
				//UserUtils.clearCache();
				return new SuccResult("删除角色成功");
			}
		}
	}

	@ResponseBody
	@RequiresPermissions("role_view")
	@RequestMapping(value = "listData", method = RequestMethod.POST)
	@ApiOperation(value = "得到当前用户能看到的（默认当前机构）角色列表")
	public Result listData(@RequestBody Role role) {
		List<Role> list = new ArrayList<>();
		if (StringUtils.isNotBlank(role.getOrganization().getId())) {
			User user = UserUtils.getUser();
			user.setOrganization(role.getOrganization());
			list = systemService.findRole(user);
			return new SuccResult(list);
		}
		return new FailResult("未找到岗位");
	}
	

	@ResponseBody
	@RequestMapping(value = "listDataWithoutPermission", method = RequestMethod.POST)
	@ApiOperation(value = "得到当前用户能看到的（默认当前机构）角色列表")
	public Result listDataWithoutPermission(@RequestBody Role role) {
		List<Role> list = new ArrayList<>();
		if (StringUtils.isNotBlank(role.getOrganization().getId())) {
			User user = UserUtils.getUser();
			user.setOrganization(role.getOrganization());
			list = systemService.findRole(user);
			return new SuccResult(list);
		}
		return new FailResult("未找到岗位");
	}

	@ResponseBody
	@RequiresPermissions("role_view")
	@RequestMapping(value = "listPageData", method = { RequestMethod.GET, RequestMethod.POST })
	@ApiOperation(value = "得到当前用户能看到的（默认当前机构）角色列表")
	public Result listPageData(@RequestBody(required = false) Role role, HttpServletRequest request,
			HttpServletResponse response) {
		Page<Role> page = new Page<>(request, response);
		Page<Role> list = systemService.findRole(page, role);
		return new SuccResult(list);
	}

	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "search", method = RequestMethod.POST)
	@ApiOperation("岗位搜索")
	public Result search(@RequestBody Role role) {
		if (StringUtils.isNotBlank(role.getName())) {
			List<Role> roles = systemService.searchRoleByName(role.getName());
			if (roles.size() > 0) {
				return new SuccResult(roles);
			} else {
				return new FailResult("未找到岗位");
			}
		}
		return new FailResult("未传值");
	}

	/**
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "getRoleDetail", method = RequestMethod.GET)
	public Result getRoleDetail(@RequestParam String id) {
		//Role role = systemService.getRoleUnion(id);
		Role role = systemService.getRole(id);
		// add by wyr 判断岗位下是否有员工
		int count = systemService.getUserCount(id);
		if (count > 0) {
			role.setFlag(true);
		} else {
			role.setFlag(false);
		}
		//方案一
		/*List<Menu> menuList = UserUtils.getMenuListForPlatform();
		List<Menu> menuRoleList = role.getMenuList();
		menuRoleList.removeAll(menuList);
		List<Menu> menusRoleDis = genTreeMenusRole("1", menuRoleList);
		List<Menu> menus = genTreeMenu("1", menuList);
		menus.addAll(menusRoleDis);*/
		//方案二
		//add by wyr:获取apiservice/a/sys/menu/getMenuList的岗位集合
		/*List<Menu> menuList = UserUtils.getMenuListForPlatform();
		List<Menu> menus = genTreeMenu("1", menuList);
		
		List<Menu> menuRoleList = role.getMenuList();
		List<Menu> menusRole = genTreeMenu("1", menuRoleList);
		
		//与结果role的岗位集合menuRoleList去重取并集
		menusRole.removeAll(menus);
		//List<Menu> menusRoleDis = genTreeMenusRole("1", menusRole);
		for (Menu menu : menusRole) {
			menu.setDisable(true);
		}
		menus.addAll(menusRole);*/
		//role.setMenuListUnion(menus);
		
		if (role != null) {
			return new SuccResult(role);
		}
		return new FailResult("岗位信息未找到");
	}
	
	
	private  List<Menu> genTreeMenusRole(String id, List<Menu> menusRole) {
        ArrayList<Menu> list = new ArrayList<>();
        for (Menu menu : menusRole) {
        	menu.setDisable(true);
            //如果对象的父id等于传进来的id，则进行递归，进入下一轮；
            if (menu.getParentId().equals(id)) {
                List<Menu> menus1 = genTreeMenusRole(menu.getId(), menusRole);
                menu.setSubMenus(menus1);
                menu.setDisable(true);
                list.add(menu);
            }
        }
        return list;
    } 

}
