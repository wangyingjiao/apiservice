/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.service.basic.BasicOrganizationService;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.interceptor.SameUrlData;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thinkgem.jeesite.modules.sys.utils.UserUtils.genTreeMenu;

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
    private BasicOrganizationService organizationService;


    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }

    @ResponseBody
    @RequiresPermissions("user_view")
    @ApiOperation(notes = "返回用户列表", value = "获取用户列表")
    @RequestMapping(value = {"listData"}, method = RequestMethod.POST)
    public Result listData(@RequestBody(required = false) User user, HttpServletRequest request, HttpServletResponse response) {
        Page<User> page = new Page<>(request, response);
        if (StringUtils.isNotBlank(request.getParameter("pageSize"))
                && request.getParameter("pageSize").equals("-1")) {
            page = new Page<>(request, response, -1);
        }
        page = systemService.findUser(page, user);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequiresPermissions("user_view")
    @ApiOperation(notes = "返回用户详情", value = "获取用户详情")
    @RequestMapping(value = {"getUserById"}, method = RequestMethod.GET)
    public Result getUserById(@RequestParam String id) {
        User u = systemService.getUser(id);
        if (null == u) {
            return new FailResult<>("未找到用户！");
        }
        return new SuccResult(u);
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
    @RequiresPermissions("user")
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
    @RequiresPermissions("user_view")
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

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "getButtons", method = RequestMethod.GET)
    @ApiOperation(value = "获得用户的菜单列表")
    public Result getButtons() {
        List<String> buttons = new ArrayList<>();
        List<Menu> menuList = UserUtils.getMenuList();
        for (Menu menu : menuList) {
            if (StringUtils.isNotBlank(menu.getPermission())) {
                buttons.add(menu.getPermission());
            }
        }
        return new SuccResult(buttons);
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
    @RequiresPermissions("user_delete")
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public Result deleteUser(@RequestBody User user) {

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
    @RequiresPermissions("user_insert")
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ApiOperation(value = "保存用户！")
    public Result saveData(@RequestBody User user) {

        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        user.setOrganization(organizationService.get(user.getOfficeId()));
        user.setStation(stationService.get(user.getStationId()));
        user.setLoginName(user.getMobile());


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
        }else{
            //编辑员工
            User temUser = new User();
            temUser.setMobile(user.getMobile());
            User midUser = systemService.getByMobile(temUser);
            //如果根据手机号查询出用户
            if (midUser != null){
                //如果id不相同不是同一用户 不能添加
                if (!midUser.getId().equals(user.getId())){
                    return new FailResult("保存用户失败，手机号已存在");
                }
            }
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        String[] roles = user.getRoles();
        List<String> roleIdList = Lists.newArrayList(roles);

        List<Role> role = new ArrayList<>();
        User currUser = UserUtils.getUser();
        if (currUser.getOrganization().getId().equals("0")) {
            logger.info("全平台用户！");
            role = systemService.findRole(new Role());
        } else {
            role = systemService.findRole(currUser);
        }

        for (Role r : role) {
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
}
