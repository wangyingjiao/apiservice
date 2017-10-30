/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.junit.internal.runners.statements.Fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.service.DictService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 字典Controller
 *
 * @author ThinkGem
 * @version 2014-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/dict")
@Api(tags = "字典类", description = "字典相关接口", value = "字典相关接口")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    @ModelAttribute
    public Dict get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return dictService.get(id);
        } else {
            return new Dict();
        }
    }

    @ApiIgnore
    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = {"list", ""})
    public String list(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<String> typeList = dictService.findTypeList();
        model.addAttribute("typeList", typeList);
        Page<Dict> page = dictService.findPage(new Page<Dict>(request, response), dict);
        model.addAttribute("page", page);
        return "modules/sys/dictList";
    }

    @ApiIgnore
    @RequiresPermissions("sys:dict:view")
    @RequestMapping(value = "form")
    public String form(Dict dict, Model model) {
        model.addAttribute("dict", dict);
        return "modules/sys/dictForm";
    }

    @ApiIgnore
    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "save")//@Valid
    public String save(Dict dict, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/dict/?repage&type=" + dict.getType();
        }
        if (!beanValidator(model, dict)) {
            return form(dict, model);
        }
        dictService.save(dict);
        addMessage(redirectAttributes, "保存字典'" + dict.getLabel() + "'成功");
        return "redirect:" + adminPath + "/sys/dict/?repage&type=" + dict.getType();
    }

    @ApiIgnore
    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "delete")
    public String delete(Dict dict, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/dict/?repage";
        }
        dictService.delete(dict);
        addMessage(redirectAttributes, "删除字典成功");
        return "redirect:" + adminPath + "/sys/dict/?repage&type=" + dict.getType();
    }

    @ApiIgnore
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String type, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Dict dict = new Dict();
        dict.setType(type);
        List<Dict> list = dictService.findList(dict);
        for (int i = 0; i < list.size(); i++) {
            Dict e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", e.getParentId());
            map.put("name", StringUtils.replace(e.getLabel(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取类型数据", notes = "按类型取得字典数据")
    public List<Dict> listData(@RequestParam(required = true) String type) {
        Dict dict = new Dict();
        dict.setType(type);
        return dictService.findList(dict);
    }

    @ResponseBody
    @RequestMapping(value = "listType", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取字典数据", notes = "取得所有类型数据")
    public List<String> listData() {
        return dictService.findTypeList();
    }

    @ResponseBody
    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "saveData", method = {RequestMethod.POST, RequestMethod.GET})//@Valid
    @ApiOperation(value = "新建字典")
    public Result save(@RequestBody Dict dict) {
        if (!beanValidator(dict)) {
            return new FailResult("参数错误！");
        }
        dictService.save(dict);
        return new SuccResult<String>("保存字典'" + dict.getLabel() + "'成功");
    }

    @ResponseBody
    @RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "deleteDict", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "删除字典")
    public Result deleteDict(Dict dict) {
        dictService.delete(dict);
        return new SuccResult("删除字典成功");
    }
}
