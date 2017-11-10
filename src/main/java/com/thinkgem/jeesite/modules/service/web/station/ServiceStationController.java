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
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
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


//
//	@RequiresPermissions("service:station:serviceStation:view")
//	@RequestMapping(value = {"list", ""})
//	public String list(ServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response, Model model) {
//		Page<ServiceStation> page = serviceStationService.findPage(new Page<ServiceStation>(request, response), serviceStation);
//		model.addAttribute("page", page);
//		return "modules/service/station/serviceStationList";
//	}

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    //@RequiresPermissions("service:station:serviceStation:view")
    public Result listData(ServiceStation serviceStation, HttpServletRequest request, HttpServletResponse response) {
        Page<ServiceStation> stationPage = new Page<>(request, response);
        Page<ServiceStation> page = serviceStationService.findPage(stationPage, serviceStation);
        return new SuccResult(page);
    }


//	@RequiresPermissions("service:station:serviceStation:view")
//	@RequestMapping(value = "form")
//	public String form(ServiceStation serviceStation, Model model) {
//		model.addAttribute("serviceStation", serviceStation);
//		return "modules/service/station/serviceStationForm";
//	}

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

    //
//	@RequiresPermissions("service:station:serviceStation:edit")
//	@RequestMapping(value = "save")
//	public String save(ServiceStation serviceStation, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, serviceStation)){
//			return form(serviceStation, model);
//		}
//		serviceStationService.save(serviceStation);
//		addMessage(redirectAttributes, "保存服务站成功");
//		return "redirect:"+Global.getAdminPath()+"/service/station/serviceStation/?repage";
//	}
    @ResponseBody
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    @RequiresPermissions("service:station:serviceStation:edit")
    public Result saveData(@RequestBody ServiceStation serviceStation) {
        if (!beanValidator(serviceStation)) {
            return new FailResult("保存服务站" + serviceStation.getName() + "失败");
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
        station.setServicePoint(serviceStation.getServicePoint());
        serviceStationService.save(station);
        return new SuccResult("保存服务站：" + station.getName() + " 站长信息成功。");
    }


//
//	@RequiresPermissions("service:station:serviceStation:edit")
//	@RequestMapping(value = "delete")
//	public String delete(ServiceStation serviceStation, RedirectAttributes redirectAttributes) {
//		serviceStationService.delete(serviceStation);
//		addMessage(redirectAttributes, "删除服务站成功");
//		return "redirect:"+Global.getAdminPath()+"/service/station/serviceStation/?repage";
//	}

    @ResponseBody
    @RequiresPermissions("service:station:serviceStation:edit")
    @RequestMapping(value = "deleteStation")
    @ApiOperation("删除服务站")
    public Result deleteStation(@RequestBody ServiceStation serviceStation) {
        serviceStationService.delete(serviceStation);
        return new SuccResult("删除服务站成功");
    }

    @ResponseBody
    @RequiresPermissions("service:station:serviceStation:view")
    @RequestMapping()
    @ApiOperation("当前服务站所有员工")
    public Result listUserData(@RequestBody ServiceStation serviceStation,HttpServletRequest request, HttpServletResponse response){
        if (null == serviceStation.getId()) {
            return new FailResult("未指定设置的服务站的ID。");
        }
        //ServiceStation station = serviceStationService.get(serviceStation.getId());

        //serviceStationService.findPage(stationPage, serviceStation);
        Page<User> userList =  serviceStationService.findUserData(serviceStation.getId());

        return new SuccResult(userList);
    }



}