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
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
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
 * 机构Controller
 *
 * @author ThinkGem
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
@Api(tags = "机构类", description = "机构管理相关接口", value = "这是Api中value的位置！", position = 0)
public class OfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;

    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

    @ApiIgnore
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {""})
    public String index(Office office, Model model) {
        //model.addAttribute("list", officeService.findAll());
        return "modules/sys/officeIndex";
    }

    @ApiIgnore
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {"list"})
    public String list(Office office, Model model) {
        model.addAttribute("list", officeService.findList(office));
        return "modules/sys/officeList";
    }

    @ApiIgnore
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "form")
    public String form(Office office, Model model) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        model.addAttribute("office", office);
        return "modules/sys/officeForm";
    }

    @ApiIgnore
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "save")
    public String save(Office office, Model model, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/";
        }
        if (!beanValidator(model, office)) {
            return form(office, model);
        }
        officeService.save(office);

        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
                childOffice.setParent(office);
                childOffice.setArea(office.getArea());
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(Global.YES);
                officeService.save(childOffice);
            }
        }

        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
        String id = "0".equals(office.getParentId()) ? "" : office.getParentId();
        return "redirect:" + adminPath + "/sys/office/list?id=" + id + "&parentIds=" + office.getParentIds();
    }


    @ApiIgnore
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "delete")
    public String delete(Office office, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/office/list";
        }
//		if (Office.isRoot(id)){
//			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
//		}else{
        officeService.delete(office);
        addMessage(redirectAttributes, "删除机构成功");
//		}
        return "redirect:" + adminPath + "/sys/office/list?id=" + office.getParentId() + "&parentIds=" + office.getParentIds();
    }

    /**
     * ø
     * 获取机构JSON数据。
     *
     * @param extId    排除的ID
     * @param type     类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade    显示级别
     * @param response
     * @return
     */

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "获得机构组织树形结构")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.findList(isAll);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) ||
                    (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                if (type != null && "3".equals(type)) {
                    map.put("isParent", true);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }

    @ResponseBody
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "/listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "获得机构列表")
    public Result listData(Office office) {
        if (null == office) {
            office = UserUtils.getUser().getOffice();
        }
        List<Office> list = officeService.findList(office);
        return new SuccResult(list);
    }

    @ResponseBody
    //@RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ApiOperation(value = "新建，更新机构")
    public Result saveData(@RequestBody Office office) {

        if (null == office.getParent()) {
            office.setParent(new Office("1"));
        }
        //设置area
        office.setArea(new Area(office.getAreaId()));
        //默认为1级
        office.setGrade("1");
        //默认可用
        office.setUseable("1");


        if (!beanValidator(office)) {
            return new FailResult("参数有误:");
        }
        //检查重名
        if (officeService.getByName(office.getName())) {
            return new FailResult("机构名称重复");
        }

        officeService.save(office);

//        if (office.getChildDeptList() != null) {
//            Office childOffice = null;
//            for (String id : office.getChildDeptList()) {
//                childOffice = new Office();
//                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
//                childOffice.setParent(office);
//                childOffice.setArea(office.getArea());
//                childOffice.setType("2");
//                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
//                childOffice.setUseable(Global.YES);
//                officeService.save(childOffice);
//            }
//        }

        return new SuccResult<String>("保存机构'" + office.getName() + "'成功");
    }

    @ResponseBody
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "deleteOffice", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "删除机构及子机构")
    public Result deleteOffice(Office office) {
        officeService.delete(office);
        return new SuccResult("删除机构成功");
    }

    @ResponseBody
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "formData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "机构详情!")
    public Result formData(@RequestBody Office office) {
        office = officeService.get(office);
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        return new SuccResult<>(office);
    }

}
