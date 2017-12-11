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
import com.thinkgem.jeesite.modules.sys.entity.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
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
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = {"list", ""})
    public String list(Role role, Model model) {
        List<Role> list = systemService.findAllRole();
        model.addAttribute("list", list);
        return "modules/sys/roleList";
    }

    @ApiIgnore
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "form")
    public String form(Role role, Model model) {
        if (role.getOffice() == null) {
            role.setOffice(UserUtils.getUser().getOffice());
        }
        model.addAttribute("role", role);
        model.addAttribute("menuList", systemService.findAllMenu());
        model.addAttribute("officeList", officeService.findAll());
        return "modules/sys/roleForm";
    }

    @ApiIgnore
    @RequiresPermissions("sys:role:edit")
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
    @RequiresPermissions("sys:role:edit")
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
//		if (Role.isAdmin(id)){
//			addMessage(redirectAttributes, "删除角色失败, 不允许内置角色或编号空");
////		}else if (UserUtils.getUser().getRoleIdList().contains(id)){
////			addMessage(redirectAttributes, "删除角色失败, 不能删除当前用户所在角色");
//		}else{
        systemService.deleteRole(role);
        addMessage(redirectAttributes, "删除角色成功");
//		}
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
    @RequiresPermissions("sys:role:edit")
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
    @RequiresPermissions("sys:role:view")
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
    @RequiresPermissions("sys:role:view")
    @ResponseBody
    @RequestMapping(value = "users", method = {RequestMethod.POST, RequestMethod.GET})
    public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        User user = new User();
        user.setOffice(new BasicOrganization(officeId));
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
                addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
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
    @RequestMapping(value = "checkName", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "验证角色名是否有效")
    public String checkName(@RequestParam(required = false) String oldName, @RequestParam String name) {
        if (name != null && name.equals(oldName)) {
            return "true";
            //return new SuccResult("名称可用");
        } else if (name != null && systemService.getRoleByName(name) == null) {
            //return new SuccResult("名称可用");
            return "true";
        }
        //return new FailResult("名称不可用");
        return "false";
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "chkName", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "验证角色名是否有效")
    public Result chkName(@RequestParam(required = false) String oldName, @RequestParam String name) {
        if (name != null && name.equals(oldName)) {
            //return "true";
            return new SuccResult("名称可用");
        } else if (name != null && systemService.getRoleByName(name) == null) {
            return new SuccResult("名称可用");
            //return "true";
        }
        return new FailResult("名称不可用");
        //return "false";
    }

    /**
     * 验证角色英文名是否有效
     *
     * @param oldName
     * @param name
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "checkEnname", method = {RequestMethod.POST, RequestMethod.GET})
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
    @RequiresPermissions("sys:role:edit")
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ApiOperation(value = "新建，更新岗位")
    public Result saveData(@RequestBody Role role) {

        Set<ConstraintViolation<Role>> validate = validator.validate(role, SaveRoleGroup.class);
        if (validate.size() > 0) {
            ArrayList<String> errs = new ArrayList<>();
            for (ConstraintViolation<Role> violation : validate) {
                errs.add(violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return new FailResult(errs);
        }

        if (StringUtils.isBlank(role.getId())) {

            Role roleByName = systemService.getRoleByName(role.getName());
            if (roleByName != null) {
                return new FailResult("保存角色'" + role.getName() + "'失败, 角色名已存在");
            }

        }
        role.setOffice(UserUtils.getUser().getOffice());
        systemService.saveRole(role);

        return new SuccResult(role);

    }

    @ResponseBody
    @RequiresPermissions("sys:user:delete")
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
                return new FailResult("有" + users.size() + "个用户属于要删除的角色，不能删除此角色");
            } else {
                systemService.deleteRole(role);
                UserUtils.clearCache();
                return new SuccResult("删除角色成功");
            }
        }
    }

    @ResponseBody
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "listData", method = RequestMethod.GET)
    @ApiOperation(value = "得到当前用户能看到的（默认当前机构）角色列表")
    public Result listData(Role role) {
        List<Role> list = systemService.findAllRole();
        return new SuccResult(list);
    }

    @ResponseBody
    @RequiresPermissions("sys:role:view")
    @RequestMapping(value = "listPageData", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "得到当前用户能看到的（默认当前机构）角色列表")
    public Result listPageData(@RequestBody(required = false) Role role, HttpServletRequest request, HttpServletResponse response) {
        Page<Role> page = new Page<>(request, response);
        Page<Role> list = systemService.findRole(page, role);
        return new SuccResult(list);
    }


    @ResponseBody
    //@RequiresPermissions("sys:role:view")
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
    @RequestMapping(value = "getRoleDetail", method = RequestMethod.GET)
    public Result getRoleDetail(@RequestParam String id) {
        Role role = systemService.getRole(id);
        if (role != null) {
            return new SuccResult(role);
        }
        return new FailResult("岗位信息未找到");
    }


}
