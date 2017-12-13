/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.item;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import com.thinkgem.jeesite.modules.service.service.item.SerItemInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务项目Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "服务项目", description = "服务项目相关接口")
@RequestMapping(value = "${adminPath}/service/item/serItemInfo")
public class SerItemInfoController extends BaseController {

    @Autowired
    private SerItemInfoService serItemInfoService;

    @ModelAttribute
    public SerItemInfo get(@RequestParam(required = false) String id) {
        SerItemInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serItemInfoService.get(id);
        }
        if (entity == null) {
            entity = new SerItemInfo();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    //@RequiresPermissions("service:station:serItemInfo:edit")
    @ApiOperation("保存服务项目")
    public Result saveData(@RequestBody SerItemInfo serItemInfo) {
        List<String> errList = errors(serItemInfo);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }

        if (!StringUtils.isNotBlank(serItemInfo.getId())) {//新增时验证重复
            User user = UserUtils.getUser();
            serItemInfo.setOrgId(user.getOrganization().getId());//机构ID

            if (0 != serItemInfoService.checkDataName(serItemInfo)) {
                return new FailResult("当前机构已经包含服务项目名称" + serItemInfo.getName() + "");
            }
        }
        serItemInfoService.save(serItemInfo);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务项目列表")
    public Result listData(@RequestBody(required = false) SerItemInfo serItemInfo, HttpServletRequest request, HttpServletResponse response) {
        if(null == serItemInfo){
            serItemInfo = new SerItemInfo();
        }
        Page<SerItemInfo> serItemInfoPage = new Page<>(request, response);
        Page<SerItemInfo> page = serItemInfoService.findPage(serItemInfoPage, serItemInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "formData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("根据ID查找服务项目")
    public Result formData(@RequestBody SerItemInfo serItemInfo) {
        SerItemInfo entity = null;
        if (StringUtils.isNotBlank(serItemInfo.getId())) {
            entity = serItemInfoService.getData(serItemInfo.getId());
        }
        if (entity == null) {
            return new FailResult("未找到此id对应的服务项目。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    //@RequiresPermissions("service:station:serItemInfo:edit")
    @RequestMapping(value = "deleteData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("删除服务项目")
    public Result deleteData(@RequestBody SerItemInfo serItemInfo) {
        serItemInfoService.delete(serItemInfo);
        return new SuccResult("删除成功");
    }

    @ResponseBody
    @RequestMapping(value = "getAllCityCodes", method = {RequestMethod.POST})
    @ApiOperation("机构或分类下定向城市")
    public Result getAllCityCodes(@RequestBody SerItemInfo serItemInfo) {
        if(null == serItemInfo){
            serItemInfo = new SerItemInfo();
        }
        User user = UserUtils.getUser();
        serItemInfo.setOrgId(user.getOrganization().getId());//机构ID
        List<SerCityScope> list = serItemInfoService.getAllCityCodes(serItemInfo);
        return new SuccResult(list);
    }

}