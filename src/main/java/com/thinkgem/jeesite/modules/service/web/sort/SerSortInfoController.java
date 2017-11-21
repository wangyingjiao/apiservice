/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.sort;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortCity;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortCityService;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务分类Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "服务分类", description = "服务分类相关接口")
@RequestMapping(value = "${adminPath}/service/sort/serSortInfo")
public class SerSortInfoController extends BaseController {

    @Autowired
    private SerSortInfoService serSortInfoService;

    @ModelAttribute
    public SerSortInfo get(@RequestParam(required = false) String id) {
        SerSortInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serSortInfoService.get(id);
        }
        if (entity == null) {
            entity = new SerSortInfo();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    //@RequiresPermissions("service:station:serSortInfo:edit")
    @ApiOperation(value="保存服务分类" ,produces = MediaType.APPLICATION_JSON_VALUE+";charset=utf8",consumes =MediaType.APPLICATION_JSON_VALUE+";charset=utf8" )
    public Result saveData(@RequestBody SerSortInfo serSortInfo) {
        if (!beanValidator(serSortInfo)) {
            return new FailResult("保存服务分类" + serSortInfo.getName() + "失败");
        }
        User user = UserUtils.getUser();
        serSortInfo.setOfficeId(user.getOffice().getId());//机构ID
        serSortInfo.setOfficeName(user.getOffice().getName());//机构名称
        serSortInfo.setStationId(user.getStation().getId());//服务站ID
        serSortInfo.setStationName(user.getStation().getName());//服务站名称
        if (!StringUtils.isNotBlank(serSortInfo.getId())) {//新增时验证重复
            if (0 != serSortInfoService.checkDataName(serSortInfo)) {
                return new FailResult("当前机构已经包含服务分类名称" + serSortInfo.getName() + "");
            }
        }
        serSortInfoService.save(serSortInfo);
        return new SuccResult("保存服务分类" + serSortInfo.getName() + "成功");
    }

    @ResponseBody
    @RequestMapping(value = "/listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务分类列表")
    public Result listData(@RequestBody(required=false) SerSortInfo serSortInfo, HttpServletRequest request, HttpServletResponse response) {
        if(serSortInfo == null){
            serSortInfo = new SerSortInfo();
        }
        Page<SerSortInfo> serSortInfoPage = new Page<>(request, response);
        Page<SerSortInfo> page = serSortInfoService.findPage(serSortInfoPage, serSortInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST})
    @ApiOperation("根据ID查找服务分类")
    public Result getData(@RequestBody SerSortInfo serSortInfo) {
        SerSortInfo entity = null;
        if (StringUtils.isNotBlank(serSortInfo.getId())) {
            entity = serSortInfoService.getData(serSortInfo);
        }
        if (entity == null) {
            return new FailResult("未找到此id：" + serSortInfo.getId() + "对应的服务分类。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    //@RequiresPermissions("service:station:serSortInfo:edit")
    @RequestMapping(value = "deleteSortInfo", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("删除服务分类")
    public Result deleteSortInfo(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkedSortItem(serSortInfo)) {
            return new FailResult("分类" + serSortInfo.getName() + "下有服务项目，不可删除");
        }
        serSortInfoService.delete(serSortInfo);
        return new SuccResult("删除服务分类成功");
    }

/*    @ResponseBody
    @RequestMapping(value = "checkDataName", method = {RequestMethod.POST})
    @ApiOperation("验证服务分类名称是否重复")
    public Result checkDataName(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkDataName(serSortInfo)) {
            return new FailResult("当前机构已经包含服务分类名称" + serSortInfo.getName() + "");
        }
        return new SuccResult("服务分类名称" + serSortInfo.getName() + "可用");
    }*/

    @ResponseBody
    @RequestMapping(value = "checkCityItem", method = {RequestMethod.POST})
    @ApiOperation("验证定向城市是否设置服务项目")
    public Result checkCityItem(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkCityItem(serSortInfo)) {
            return new FailResult("该城市已关联服务项目，不可移除其选中状态");
        }
        return new SuccResult("success");
    }

    @ResponseBody
    @RequestMapping(value = "/getOfficeCitylist", method = {RequestMethod.GET})
    @ApiOperation("获取机构下所有城市")
    public Result getOfficeCitylist(HttpServletRequest request, HttpServletResponse response) {
        SerSortInfo serSortInfo = new SerSortInfo();
        User user = UserUtils.getUser();
        serSortInfo.setOfficeId(user.getOffice().getId());//机构ID
        List<OfficeSeviceAreaList> officeCitys = serSortInfoService.getOfficeCitylist(serSortInfo);
        return new SuccResult(officeCitys);
    }

}