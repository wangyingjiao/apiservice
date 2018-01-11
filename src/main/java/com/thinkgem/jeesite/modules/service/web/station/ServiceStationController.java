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
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.service.entity.station.SaveStationGroup;
import com.thinkgem.jeesite.modules.service.service.station.BasicStoreService;
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
import java.util.ArrayList;
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

    @Autowired
    private BasicStoreService basicStoreService;


    @ModelAttribute
    public BasicServiceStation get(@RequestParam(required = false) String id) {
        BasicServiceStation entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serviceStationService.get(id);
        }
        if (entity == null) {
            entity = new BasicServiceStation();
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务站列表")
    @RequiresPermissions("station_view")
    public Result listData(@RequestBody(required = false) BasicServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response) {
        Page<BasicServiceStation> stationPage = new Page<>(request, response);

        Page<BasicServiceStation> page = serviceStationService.findPage(stationPage, serviceStation);
        return new SuccResult(page);

    }

    @ResponseBody
    @RequestMapping(value = "listByOffice", method = {RequestMethod.POST, RequestMethod.GET})
    //@RequiresPermissions("service:station:serviceStation:view")
    @RequiresPermissions("user")
    public Result listByOffice(@RequestBody(required = false) BasicServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response) {
        List<BasicServiceStation> list = new ArrayList<>();
        if (StringUtils.isNotBlank(serviceStation.getOrgId())) {
            list = serviceStationService.findList(serviceStation);
        }
        return new SuccResult(list);
    }


    @ResponseBody
    @RequiresPermissions("station_view")
    @RequestMapping(value = "getData", method = {RequestMethod.POST, RequestMethod.GET})
    public Result getData(@RequestBody BasicServiceStation serviceStation) {
        BasicServiceStation entity = null;
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
    @RequiresPermissions("station_insert")
    @ApiOperation("保存服务站")
    public Result saveData(@RequestBody BasicServiceStation serviceStation) {

        List<String> errors = errors(serviceStation, SaveStationGroup.class);
        if (errors.size() > 0) {
            return new FailResult(errors);
        }
        User user = UserUtils.getUser();
        if (user.getOrganization() != null && user.getOrganization().getId() != "0") {
            serviceStation.setOrgId(user.getOrganization().getId());
        } else {
            return new FailResult("用户没有具体的所属机构(全平台权限)");
        }
        serviceStationService.save(serviceStation);
        return new SuccResult("保存服务站" + serviceStation.getName() + "成功");
    }

    @ResponseBody
    @RequestMapping(value = "upData", method = {RequestMethod.POST})
    @RequiresPermissions("station_update")
    @ApiOperation("保存服务站")
    public Result upData(@RequestBody BasicServiceStation serviceStation) {

        List<String> errors = errors(serviceStation, SaveStationGroup.class);
        if (errors.size() > 0) {
            return new FailResult(errors);
        }
        User user = UserUtils.getUser();

        if (user.getOrganization() != null && user.getOrganization().getId() != "0") {
            serviceStation.setOrgId(user.getOfficeId());
        } else {
            return new FailResult("用户没有具体的所属机构(全平台权限)");
        }
        serviceStationService.save(serviceStation);
        return new SuccResult("保存服务站" + serviceStation.getName() + "成功");
    }


    @ResponseBody
    @RequestMapping(value = "setScope", method = {RequestMethod.POST, RequestMethod.GET})
    @RequiresPermissions("station_scope")
    @ApiOperation("设置座标范围")
    public Result setScope(@RequestBody BasicServiceStation serviceStation) {

        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站ID。");
        }
        BasicServiceStation station = serviceStationService.get(serviceStation.getId());
        station.setServicePoint(serviceStation.getServicePoint());
        serviceStationService.save(station);
        return new SuccResult("保存服务站：" + station.getName() + " 座标信息成功。");
    }

    @ResponseBody
    @RequestMapping(value = "setManager", method = {RequestMethod.POST, RequestMethod.GET})
    @RequiresPermissions("station_manager")
    @ApiOperation("设置站长")
    public Result setManager(@RequestBody BasicServiceStation serviceStation) {
        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站的ID。");
        }
        //BasicServiceStation station = serviceStationService.get(serviceStation.getId());
        //station.setUser(serviceStation.getUser());
        serviceStationService.save(serviceStation);
        return new SuccResult("保存服务站：" + serviceStation.getName() + " 站长信息成功。");
    }
    @ResponseBody
    @RequestMapping(value = "getManager", method = {RequestMethod.POST, RequestMethod.GET})
    @RequiresPermissions("station_manager")
    @ApiOperation("设置站长")
    public Result getManager(@RequestBody BasicServiceStation serviceStation) {
        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站的ID。");
        }
        List<User> list = serviceStationService.getUserListByStationId(serviceStation);
        return new SuccResult(list);
    }


    @ResponseBody
    @RequiresPermissions("station_delete")
    @RequestMapping(value = "deleteStation")
    @ApiOperation("删除服务站")
    public Result deleteStation(@RequestBody BasicServiceStation serviceStation) {
        int count = serviceStationService.getCount(serviceStation);
        if (count > 0){
            return new FailResult("该服务站已有员工，不可删除！");
        }
        serviceStationService.delete(serviceStation);
        return new SuccResult("删除服务站成功");
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "getStationByArea")
    public Result getStationByArea(@RequestBody BasicServiceStation serviceStation) {
//        if (StringUtils.isBlank(serviceStation.getCityCode())) {
//            return new FailResult("城市code 不能为空！");
//        } else {
            BasicServiceStation basicServiceStation = new BasicServiceStation();
//            basicServiceStation.setCityCode(serviceStation.getCityCode());
            basicServiceStation.setOrgId(UserUtils.getUser().getOrganization().getId());
            List<BasicServiceStation> list = serviceStationService.findList(basicServiceStation);
            return new SuccResult(list);
//        }
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "getStationByAreaTech")
    public Result getStationByAreaTech(@RequestBody BasicServiceStation serviceStation) {
        if (StringUtils.isBlank(serviceStation.getCityCode())) {
            return new FailResult("城市code 不能为空！");
        } else {
            BasicServiceStation basicServiceStation = new BasicServiceStation();
            basicServiceStation.setCityCode(serviceStation.getCityCode());
            basicServiceStation.setOrgId(UserUtils.getUser().getOrganization().getId());
            List<BasicServiceStation> list = serviceStationService.findListTech(basicServiceStation);
            return new SuccResult(list);
        }
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "getStoreList")
    public Result getStoreList(@RequestBody BasicStore basicStore) {
        List<BasicStore> list = basicStoreService.findList(basicStore);
        if (list.size() > 0) {
            return new SuccResult(list);
        }
        return new FailResult("未找到数据");
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "saveStationStore")
    public Result saveStationStore(@RequestBody BasicServiceStation station) {
        return serviceStationService.saveStore(station);
    }


}