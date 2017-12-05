/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.station;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.station.SaveStationGroup;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 服务站Controller
 *
 * @author x
 * @version 2017-11-06
 */
@Controller
@RequestMapping(value = "${adminPath}/service/station/serviceStation")
@Api(tags = "服务站管理", description = "服务站管理相关接口")
public class ServiceStationController extends BaseController {

    @Autowired
    private ServiceStationService serviceStationService;

    @ModelAttribute
    public ServiceStation get(@RequestParam(required = false) String id) {
        ServiceStation entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serviceStationService.get(id);
        }
        if (entity == null) {
            entity = new ServiceStation();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务站列表")
    //@RequiresPermissions("service:station:serviceStation:view")
    public Result listData(@RequestBody(required = false) ServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response) {
        Page<ServiceStation> stationPage = new Page<>(request, response);
        Page<ServiceStation> page = serviceStationService.findPage(stationPage, serviceStation);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "listByOffice", method = {RequestMethod.POST, RequestMethod.GET})
    //@RequiresPermissions("service:station:serviceStation:view")
    public Result listByOffice(@RequestBody(required = false) ServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response) {
        List<ServiceStation> list = serviceStationService.findList(serviceStation);
        return new SuccResult(list);
    }


    @ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getData(@RequestBody ServiceStation serviceStation) {
        ServiceStation entity = null;
        if (StringUtils.isNotBlank(serviceStation.getId())) {
            entity = serviceStationService.get(serviceStation.getId());
        }
        if (entity == null) {
            return new FailResult("未找到此id：" + serviceStation.getId() + "对应的服务站。");

        } else {
            return new SuccResult(entity);
        }
    }


    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    //@RequiresPermissions("service:station:serviceStation:edit")
    @ApiOperation("保存服务站")
    public Result saveData(@RequestBody ServiceStation serviceStation) {

        List<String> errors = errors(serviceStation, SaveStationGroup.class);
        if (errors.size() > 0) {
            return new FailResult(errors);
        }
        User user = UserUtils.getUser();

        if (user.getOffice() != null && user.getOffice().getId() != "0") {
            serviceStation.setOffice(user.getOffice());
            serviceStation.setOfficeId(user.getOffice().getId());
            serviceStation.setOfficeName(user.getOffice().getName());
        } else {
            return new FailResult("用户没有具体的所属机构(全平台)");
        }
        serviceStationService.save(serviceStation);
        return new SuccResult("保存服务站" + serviceStation.getName() + "成功");
    }

    @ResponseBody
    @RequestMapping(value = "setScope", method = {RequestMethod.POST, RequestMethod.GET})
    @RequiresPermissions("service:station:serviceStation:edit")
    @ApiOperation("设置座标范围")
    public Result setScope(@RequestBody ServiceStation serviceStation) {

        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站ID。");
        }
        ServiceStation station = serviceStationService.get(serviceStation.getId());
        station.setServicePoint(serviceStation.getServicePoint());
        serviceStationService.save(station);
        return new SuccResult("保存服务站：" + station.getName() + " 座标信息成功。");
    }

    @ResponseBody
    @RequestMapping(value = "setManager", method = {RequestMethod.POST, RequestMethod.GET})
    @RequiresPermissions("service:station:serviceStation:edit")
    @ApiOperation("设置站长")
    public Result setManager(@RequestBody ServiceStation serviceStation) {
        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站的ID。");
        }
        ServiceStation station = serviceStationService.get(serviceStation.getId());

        serviceStationService.save(station);
        return new SuccResult("保存服务站：" + station.getName() + " 站长信息成功。");
    }


    @ResponseBody
    @RequiresPermissions("service:station:serviceStation:edit")
    @RequestMapping(value = "deleteStation")
    @ApiOperation("删除服务站")
    public Result deleteStation(@RequestBody ServiceStation serviceStation) {
        serviceStationService.delete(serviceStation);
        return new SuccResult("删除服务站成功");
    }
}