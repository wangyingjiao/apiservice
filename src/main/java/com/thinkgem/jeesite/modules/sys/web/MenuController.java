/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
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

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thinkgem.jeesite.modules.sys.utils.UserUtils.genTreeMenu;

/**
 * 菜单Controller
 *
 * @author ThinkGem
 * @version 2013-3-23
 */
@Controller
@Api(tags = "菜单管理", description = "菜单管理相关接口")
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private SystemService systemService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getMenu(id);
        } else {
            return new Menu();
        }
    }

    @ResponseBody
    @RequestMapping(value = "formData" , method = { RequestMethod.POST })
    public Result formData(@RequestBody Menu menu){
        if (StringUtils.isBlank(menu.getId())){
            return new FailResult("没有相关信息");
        }
        return new SuccResult(systemService.getMenu(menu.getId()));
    }

    @ResponseBody
    @RequestMapping(value = "upData", method = { RequestMethod.POST })
    //@RequiresPermissions("skill_update")
    @ApiOperation("修改保存菜单信息")
    public Result upData(@RequestBody Menu menu) {
        List<String> errList = errors(menu);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }
        systemService.saveMenu(menu);
        return new SuccResult("保存成功");
    }

    //@RequiresPermissions("sys:menu:view")
    @RequestMapping(value = {"list", ""})
    public String list(Model model) {
        List<Menu> list = Lists.newArrayList();
        List<Menu> sourcelist = systemService.findAllMenu();
        Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
        return "modules/sys/menuList";
    }

    //@RequiresPermissions("sys:menu:view")
    @RequestMapping(value = "form")
    public String form(Menu menu, Model model) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(systemService.getMenu(menu.getParent().getId()));
        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = systemService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        model.addAttribute("menu", menu);
        return "modules/sys/menuForm";
    }

    @ResponseBody
    //@RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "save")
    public Result save(@RequestBody Menu menu, Model model, RedirectAttributes redirectAttributes) {
        if (!UserUtils.getUser().isAdmin()) {
            //addMessage(redirectAttributes, "越权操作，只有超级管理员才能添加或修改数据！");
            //return "redirect:" + adminPath + "/sys/role/?repage";
            return new FailResult("越权操作，只有超级管理员才能添加或修改数据");
        }
        if (Global.isDemoMode()) {
            //addMessage(redirectAttributes, "演示模式，不允许操作！");
            //return "redirect:" + adminPath + "/sys/menu/";
            return new FailResult("演示模式，不允许操作");
        }
        /*if (!beanValidator(model, menu)) {
            return form(menu, model);
        }*/
        systemService.saveMenu(menu);
        //addMessage(redirectAttributes, "保存菜单'" + menu.getName() + "'成功");
        //return "redirect:" + adminPath + "/sys/menu/";
        return new SuccResult("保存成功");
    }

    @ResponseBody
    //@RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "delete")
    public Result delete(@RequestBody Menu menu, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            //addMessage(redirectAttributes, "演示模式，不允许操作！");
            //return "redirect:" + adminPath + "/sys/menu/";
            return new FailResult("演示模式，不允许操作");
        }
//		if (Menu.isRoot(id)){
//			addMessage(redirectAttributes, "删除菜单失败, 不允许删除顶级菜单或编号为空");
//		}else{
        systemService.deleteMenu(menu);
        //addMessage(redirectAttributes, "删除菜单成功");
//		}
        //return "redirect:" + adminPath + "/sys/menu/";
        return new SuccResult("删除菜单成功");
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "tree")
    public String tree() {
        return "modules/sys/menuTree";
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "treeselect")
    public String treeselect(String parentId, Model model) {
        model.addAttribute("parentId", parentId);
        return "modules/sys/menuTreeselect";
    }

    /**
     * 批量修改菜单排序
     */
    //@RequiresPermissions("sys:menu:edit")
    @RequestMapping(value = "updateSort")
    public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/menu/";
        }
        for (int i = 0; i < ids.length; i++) {
            Menu menu = new Menu(ids[i]);
            menu.setSort(sorts[i]);
            systemService.updateMenuSort(menu);
        }
        addMessage(redirectAttributes, "保存菜单排序成功!");
        return "redirect:" + adminPath + "/sys/menu/";
    }

    /**
     * isShowHide是否显示隐藏菜单
     *
     * @param extId
     *
     * @param response
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = systemService.findAllMenu();
        for (int i = 0; i < list.size(); i++) {
            Menu e = list.get(i);
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                if (isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")) {
                    continue;
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }

    @ResponseBody
    @RequestMapping(value = "getMenuList")
    public Result getMenuList() {
        List<Menu> menuList = UserUtils.getMenuListForPlatform();
        List<Menu> menus = genTreeMenu("1", menuList);
        logger.info(JSON.toJSONString(menus));
        return new SuccResult(menus);
    }
}
