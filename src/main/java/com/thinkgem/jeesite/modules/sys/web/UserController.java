/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 *
 * @author ThinkGem
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
@Api(tags = "用户类", description = "用户操作相关接口", value = "这是value的位置！")
public class UserController extends BaseController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private OfficeService officeService;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }

    @ApiIgnore
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = {"index"}, method = RequestMethod.GET)
    public String index(User user, Model model) {
        return "modules/sys/userIndex";
    }

    @ApiIgnore
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = {"list", ""}, method = RequestMethod.GET)
    public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        model.addAttribute("page", page);
        return "modules/sys/userList";
    }

    @ResponseBody
    @RequiresPermissions("sys:user:view")
    @ApiOperation(notes = "返回用户列表", value = "获取用户列表")
    @RequestMapping(value = {"listData"}, method = RequestMethod.POST)
    public Result listData(@RequestBody(required = false) User user, HttpServletRequest request, HttpServletResponse response) {
        Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequiresPermissions("sys:user:view")
    @ApiOperation(notes = "返回用户详情", value = "获取用户详情")
    @RequestMapping(value = {"getUserById"}, method = RequestMethod.GET)
    public Result getUserById(@RequestParam String id) {
        User u = systemService.getUser(id);
        if (null == u) {
            return new FailResult<>("未找到用户！");
        }
        return new SuccResult(u);
    }


    @ApiIgnore
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(User user, Model model) {
        if (user.getCompany() == null || user.getCompany().getId() == null) {
            user.setCompany(UserUtils.getUser().getCompany());
        }
        if (user.getOffice() == null || user.getOffice().getId() == null) {
            user.setOffice(UserUtils.getUser().getOffice());
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", systemService.findAllRole());
        return "modules/sys/userForm";
    }


    @ApiIgnore
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/user/list?repage";
        }
        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        user.setCompany(new Office(request.getParameter("company.id")));
        user.setOffice(new Office(request.getParameter("office.id")));
        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
        }
        if (!beanValidator(model, user)) {
            return form(user, model);
        }
        if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
            addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
            return form(user, model);
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        for (Role r : systemService.findAllRole()) {
            if (roleIdList.contains(r.getId())) {
                roleList.add(r);
            }
        }
        user.setRoleList(roleList);
        // 保存用户信息
        systemService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
            //UserUtils.getCacheMap().clear();
        }
        addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    @ApiIgnore
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public String delete(User user, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/user/list?repage";
        }
        if (UserUtils.getUser().getId().equals(user.getId())) {
            addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
        } else if (User.isAdmin(user.getId())) {
            addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
        } else {
            systemService.deleteUser(user);
            addMessage(redirectAttributes, "删除用户成功");
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 导出用户数据
     *
     * @param user
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @ApiIgnore
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
            new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 导入用户数据
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @ApiIgnore
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/user/list?repage";
        }
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<User> list = ei.getDataList(User.class);
            for (User user : list) {
                try {
                    if ("true".equals(checkLoginName("", user.getLoginName()))) {
                        user.setPassword(SystemService.entryptPassword("123456"));
                        BeanValidators.validateWithException(validator, user);
                        systemService.saveUser(user);
                        successNum++;
                    } else {
                        failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
                        failureNum++;
                    }
                } catch (ConstraintViolationException ex) {
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList) {
                        failureMsg.append(message + "; ");
                        failureNum++;
                    }
                } catch (Exception ex) {
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：" + ex.getMessage());
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条用户" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 下载导入用户数据模板
     *
     * @param response
     * @param redirectAttributes
     * @return
     */
    @ApiIgnore
    @RequiresPermissions("sys:user:view")
    @RequestMapping(value = "import/template", method = RequestMethod.GET)
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据导入模板.xlsx";
            List<User> list = Lists.newArrayList();
            list.add(UserUtils.getUser());
            new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 验证登录名是否有效
     *
     * @param oldLoginName
     * @param loginName
     * @return
     */
    @ApiIgnore
    @ResponseBody
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "checkLoginName", method = {RequestMethod.POST, RequestMethod.GET})
    public String checkLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return "true";
        } else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

    @ResponseBody
    @RequiresPermissions("sys:user:edit")
    @ApiOperation(notes = "查询用户名是否有重复",
            value = "重名检查", nickname = "这里是nickName的值")
    @RequestMapping(value = "isDUPofName", method = {RequestMethod.POST, RequestMethod.GET})
    public Result isDUPName(@RequestParam(required = false) String oldLoginName, @RequestParam String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return new SuccResult("名称可用！");
        } else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return new SuccResult("名称可用！");
        }
        return new FailResult("名称不可用");
    }


    /**
     * 用户信息显示及保存
     *
     * @param user
     * @param model
     * @return
     */
    @ApiIgnore
    @RequiresPermissions("user")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String info(User user, HttpServletResponse response, Model model) {
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())) {
            if (Global.isDemoMode()) {
                model.addAttribute("message", "演示模式，不允许操作！");
                return "modules/sys/userInfo";
            }
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setMobile(user.getMobile());
            currentUser.setRemarks(user.getRemarks());
            currentUser.setPhoto(user.getPhoto());
            systemService.updateUserInfo(currentUser);
            model.addAttribute("message", "保存用户信息成功");
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("Global", new Global());
        return "modules/sys/userInfo";
    }

    /**
     * 返回用户信息
     *
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "infoData", method = RequestMethod.GET)
    @ApiOperation(value = "获得用户信息")
    public User infoData() {
        return UserUtils.getUser();
    }


    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "roleData", method = RequestMethod.GET)
    @ApiOperation(value = "获得用户的角色列表")
    public List<Role> roleData() {
        return UserUtils.getUser().getRoleList();
    }


    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "menuData", method = RequestMethod.GET)
    @ApiOperation(value = "获得用户的菜单列表")
    public Result<List<Menu>> menuData() {
        List<Menu> menuList = UserUtils.getMenuList();
        List<Menu> menus = genTreeMenu("1", menuList);
        logger.info(JSON.toJSONString(menus));
        return new SuccResult(menus);
    }

    private List<Menu> genTreeMenu(String id, List<Menu> menus) {
        ArrayList<Menu> list = new ArrayList<>();
        for (Menu menu : menus) {
            //如果对象的父id等于传进来的id，则进行递归，进入下一轮；
            if (menu.getParentId().equals(id)) {
                List<Menu> menus1 = genTreeMenu(menu.getId(), menus);
                menu.setSubMenus(menus1);
                list.add(menu);
            }
        }
        return list;
    }

    /**
     * genTreeMenu(menus.get(i).getId(),menus);
     * 修改个人用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @ApiIgnore
    @RequiresPermissions("user")
    @RequestMapping(value = "modifyPwd", method = RequestMethod.POST)
    //@ApiOperation(value = "这里是value标签", notes = "这是notes点", nickname = "这里是nickName")
    public String modifyPwd(String oldPassword, String newPassword, Model model) {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            if (Global.isDemoMode()) {
                model.addAttribute("message", "演示模式，不允许操作！");
                return "modules/sys/userModifyPwd";
            }
            if (SystemService.validatePassword(oldPassword, user.getPassword())) {
                systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                model.addAttribute("message", "修改密码成功");
            } else {
                model.addAttribute("message", "修改密码失败，旧密码错误");
            }
        }
        model.addAttribute("user", user);
        return "modules/sys/userModifyPwd";
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData", method = RequestMethod.GET)
    @ApiOperation(value = "生成树数据")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = systemService.findUserByOfficeId(officeId);
        for (int i = 0; i < list.size(); i++) {
            User e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", "u_" + e.getId());
            map.put("pId", officeId);
            map.put("name", StringUtils.replace(e.getName(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "modifyPwdData", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户密码", notes = "修改当前用户的密码", nickname = "nick_修改用户密码")
    public Result modifyPwdData(@RequestParam String oldPassword, @RequestParam String newPassword) {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            if (SystemService.validatePassword(oldPassword, user.getPassword())) {
                systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                //model.addAttribute("message", "修改密码成功");
                return new SuccResult("修改密码成功");
            } else {
                return new FailResult("修改密码失败，旧密码错误");
            }
        }
        return new FailResult("参数错误，检查参数");
    }


    /**
     * 用户信息显示及保存
     *
     * @param user
     * @param
     * @return
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "updateInfo", method = RequestMethod.GET)
    @ApiOperation(value = "用户信息显示及保存")
    public Result updateInfo(User user) {
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())) {
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setMobile(user.getMobile());
            currentUser.setRemarks(user.getRemarks());
            currentUser.setPhoto(user.getPhoto());
            systemService.updateUserInfo(currentUser);
            return new SuccResult<User>(currentUser);
        }
        return new FailResult("保存用户信息失败");
    }

    @ResponseBody
    @ApiOperation(value = "删除用户！")
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public Result deleteUser(User user) {

        if (UserUtils.getUser().getId().equals(user.getId())) {
            return new FailResult("删除用户失败, 不允许删除当前用户");
        } else if (User.isAdmin(user.getId())) {
            return new FailResult("删除用户失败, 不允许删除超级管理员用户");
        } else {
            systemService.deleteUser(user);
            return new SuccResult("删除用户成功");
        }
    }

    @Autowired
    private ServiceStationService stationService;

    @ResponseBody
    //@RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ApiOperation(value = "保存用户！")
    public Result saveData(@RequestBody User user) {

        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        user.setCompany(new Office("1"));
        user.setOffice(officeService.get(user.getOfficeId()));
        user.setStation(stationService.get(user.getStationId()));

        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
            user.setNewPassword(null);
        }
        List<String> errors = errors(user);
        if (errors.size() > 0) {
            return new FailResult(errors);
        }
        if (StringUtils.isBlank(user.getId())) {
            if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
                return new FailResult("保存用户'" + user.getLoginName() + "'失败，登录名已存在");
            }
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        String[] roles = user.getRoles();
        List<String> roleIdList = Lists.newArrayList(roles);
        for (Role r : systemService.findAllRole()) {
            if (roleIdList.contains(r.getId())) {
                roleList.add(r);
            }
        }
        user.setRoleList(roleList);
        // 保存用户信息
        systemService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        return new SuccResult(user);

    }


//	@InitBinder
//	public void initBinder(WebDataBinder b) {
//		b.registerCustomEditor(List.class, "roleList", new PropertyEditorSupport(){
//			@Autowired
//			private SystemService systemService;
//			@Override
//			public void setAsText(String text) throws IllegalArgumentException {
//				String[] ids = StringUtils.split(text, ",");
//				List<Role> roles = new ArrayList<Role>();
//				for (String id : ids) {
//					Role role = systemService.getRole(Long.valueOf(id));
//					roles.add(role);
//				}
//				setValue(roles);
//			}
//			@Override
//			public String getAsText() {
//				return Collections3.extractToString((List) getValue(), "id", ",");
//			}
//		});
//	}
}
