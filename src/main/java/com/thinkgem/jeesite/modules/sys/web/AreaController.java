/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 区域Controller
 *
 * @author ThinkGem
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
@Api(tags = "区域类", description = "区域操作相关接口", value = "这是Api中value的位置！", position = 1)
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ModelAttribute("area")
    public Area get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return areaService.get(id);
        } else {
            return new Area();
        }
    }

    @ApiIgnore
    @RequiresPermissions("sys:area:view")
    @RequestMapping(value = {"list", ""})
    public String list(Area area, Model model) {
        model.addAttribute("list", areaService.findAll());
        return "modules/sys/areaList";
    }

    @ApiIgnore
    @RequiresPermissions("sys:area:view")
    @RequestMapping(value = "form")
    public String form(Area area, Model model) {
        if (area.getParent() == null || area.getParent().getId() == null) {
            area.setParent(UserUtils.getUser().getOffice().getArea());
        }
        area.setParent(areaService.get(area.getParent().getId()));
        model.addAttribute("area", area);
        return "modules/sys/areaForm";
    }

    @ApiIgnore
    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "save")
    public String save(Area area, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/area";
        }
        if (!beanValidator(model, area)) {
            return form(area, model);
        }
        areaService.save(area);
        addMessage(redirectAttributes, "保存区域'" + area.getName() + "'成功");
        return "redirect:" + adminPath + "/sys/area/";
    }

    @ApiIgnore
    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "delete")
    public String delete(Area area, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/area";
        }
//		if (Area.isRoot(id)){
//			addMessage(redirectAttributes, "删除区域失败, 不允许删除顶级区域或编号为空");
//		}else{
        areaService.delete(area);
        addMessage(redirectAttributes, "删除区域成功");
//		}
        return "redirect:" + adminPath + "/sys/area/";
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "生成树形结构的区域", notes = "返回内容有id、pid、name，可以组成用在ztree中")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Area> list = areaService.findAll();
        for (int i = 0; i < list.size(); i++) {
            Area e = list.get(i);
            if (isaBoolean(extId, e)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }

    private boolean isaBoolean(String extId, Area e) {
        return StringUtils.isBlank(extId) ||
                (extId != null &&
                        !extId.equals(e.getId()) &&
                        e.getParentIds().indexOf("," + extId + ",") == -1);
    }

    /**
     * 取得所有区域数据
     *
     * @return
     */
    @ResponseBody
//    @RequiresPermissions("sys:area:view")
    @RequestMapping(value = "listData", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取所有区域", notes = "取得当前用户授权的区域，区域暂没授权，即返回所有区域")
    public Result listData() {
        List<Area> areaList = UserUtils.getAreaList();
        return new SuccResult(areaList);
    }


    @ResponseBody
//    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "saveArea", method = RequestMethod.POST)
    @ApiOperation(value = "保存区哉", notes = "新建区域，保存区域，更新区域均使用此接口<hr>" +
            "新增区属性：>" +
            "必填属性：区域名称<br>" +
            "选填属性：上级区域，区域编码，区域类型，备注")
    public Result saveArea(@RequestBody Area area) {
        areaService.save(area);
        logger.info("保存区域'" + area.getName() + "'成功");
        return new SuccResult<String>("保存成功！");
    }

    @ResponseBody
//  @RequiresPermissions("sys:area:view")
    @RequestMapping(value = "getAreaById", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "按ID查找Area", notes = "参数为Area id，返回区域详细属性")
    public Result getAreaById(@RequestParam String id) {
        Area area = areaService.get(id);
        if (null != area) {
            return new SuccResult(area);
        }
        return new FailResult("区域id不存在！");
    }

    @ResponseBody
    @RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "deleteArea", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "删除区域")
    public Result deleteArea(Area area) {

//		if (Area.isRoot(id)){
//			addMessage(redirectAttributes, "删除区域失败, 不允许删除顶级区域或编号为空");
//		}else{
        areaService.delete(area);
        //addMessage("删除区域成功");
//		}
        return new SuccResult("删除区域成功");
    }


    @ResponseBody
    //@RequiresPermissions("sys:area:view")
    @RequestMapping(value = {"getchildArea"}, method = {RequestMethod.POST, RequestMethod.GET})
    public Result getchildArea(@RequestBody Area area) {
        if (null == area || StringUtils.isBlank(area.getId())) {
            area = new Area("1");
            area.setParentIds("0,1,");
        } else {
            area = areaService.get(area.getId());
            area.setParentIds(area.getParentIds()+area.getId()+",");
        }
        List<Area> areas = areaService.findchildArea(area);
        if (areas.size() < 1){
            return new FailResult(
                    "失败，没有子级区域了。"
            );
        }
        return new SuccResult(areas);
    }
}
