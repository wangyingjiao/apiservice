/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.skill;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 技能管理Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "技能管理", description = "技能管理相关接口")
@RequestMapping(value = "${adminPath}/service/skill/serSkillInfo")
public class SerSkillInfoController extends BaseController {

    @Autowired
    private SerSkillInfoService serSkillInfoService;

    @ModelAttribute
    public SerSkillInfo get(@RequestParam(required = false) String id) {
        SerSkillInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serSkillInfoService.get(id);
        }
        if (entity == null) {
            entity = new SerSkillInfo();
        }
        return entity;
    }


    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    //@RequiresPermissions("service:station:serSkillInfo:edit")
    @ApiOperation("保存技能")
    public Result saveData(@RequestBody SerSkillInfo serSkillInfo) {
        if (!beanValidator(serSkillInfo)) {
            return new FailResult("保存技能" + serSkillInfo.getName() + "失败");
        }
        User user = UserUtils.getUser();
        serSkillInfo.setOfficeId(user.getOffice().getId());//机构ID
        serSkillInfo.setOfficeName(user.getOffice().getName());//机构名称
        serSkillInfo.setStationId(user.getStation().getId());//服务站ID
        serSkillInfo.setStationName(user.getStation().getName());//服务站名称
        if (!StringUtils.isNotBlank(serSkillInfo.getId())) {//新增时验证重复
            if (0 != serSkillInfoService.checkDataName(serSkillInfo)) {
                return new FailResult("当前机构已经包含技能名称" + serSkillInfo.getName() + "");
            }
        }
        serSkillInfoService.save(serSkillInfo);
        return new SuccResult("保存技能" + serSkillInfo.getName() + "成功");
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取技能列表")
    public Result listData(@RequestBody SerSkillInfo serSkillInfo, HttpServletRequest request, HttpServletResponse response) {
        Page<SerSkillInfo> stationPage = new Page<>(request, response);
        Page<SerSkillInfo> page = serSkillInfoService.findPage(stationPage, serSkillInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("根据ID查找技能")
    public Result getData(@RequestBody SerSkillInfo serSkillInfo) {
        SerSkillInfo entity = null;
        if (StringUtils.isNotBlank(serSkillInfo.getId())) {
            entity = serSkillInfoService.getData(serSkillInfo.getId());
        }
        if (entity == null) {
            return new FailResult("未找到此id：" + serSkillInfo.getId() + "对应的技能。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    //@RequiresPermissions("service:station:serSkillInfo:edit")
    @RequestMapping(value = "deleteSortInfo", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("删除技能")
    public Result deleteSortInfo(@RequestBody SerSkillInfo serSkillInfo) {
        serSkillInfoService.delete(serSkillInfo);
        return new SuccResult("删除技能成功");
    }
/*
    @ResponseBody
    @RequestMapping(value = "checkDataName", method = {RequestMethod.POST})
    @ApiOperation("验证技能名称是否重复")
    public Result checkDataName(@RequestBody SerSkillInfo serSkillInfo) {
        if (0 != serSkillInfoService.checkDataName(serSkillInfo)) {
            return new FailResult("当前机构已经包含技能名称" + serSkillInfo.getName() + "");
        }
        return new SuccResult("技能名称" + serSkillInfo.getName() + "可用");
    }*/


}