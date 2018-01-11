/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.tech;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.web.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通讯录Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP通讯录类", description = "APP通讯录相关接口")
@RequestMapping(value = "${appPath}/appTech/appTechInfo")
public class AppTechController extends BaseController {

	@Autowired
	ServiceTechnicianInfoService techService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ServiceTechnicianHolidayService holidayService;

	//技师的服务信息列表
	 @ResponseBody
	 @RequestMapping(value = "getTechServiceList", method = RequestMethod.POST)
	 @ApiOperation(value = "技师服务信息列表", notes = "技师")
	 public Result getTechServiceList(HttpServletRequest request, HttpServletResponse response) {

		 //获取登陆技师的信息  id 服务站id
		 ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		 tech.setPhone("13508070808");
		 tech.setDelFlag("0");
		 ServiceTechnicianInfo tech1 = techService.findTech(tech);
		 Page<ServiceTechnicianInfo> page = new Page<ServiceTechnicianInfo>(request, response);
		 Page<ServiceTechnicianInfo> pageList = techService.appFindSkillList(page, tech1);

		 return new SuccResult(pageList);
	}

	//技师同服务站的通讯录
	@ResponseBody
	@RequestMapping(value = "appGetFriendByStationId", method = RequestMethod.POST)
	@ApiOperation(value = "通讯录", notes = "通讯录")
	public Result appGetFriendByStationId(HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id 服务站id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		Page<ServiceTechnicianInfo> page = new Page<ServiceTechnicianInfo>(request, response);
		Page<ServiceTechnicianInfo> list = techService.appGetFriendByStationId(page,tech1);

		return new SuccResult(list);
	}

	//技师休假列表
	@ResponseBody
	@RequestMapping(value = "restTechList", method = RequestMethod.POST)
	@ApiOperation(value = "技师休假列表", notes = "技师休假")
	public Result restTechList( HttpServletRequest request, HttpServletResponse response) {

		//获取登陆技师的信息  id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);

		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		holiday.setTechId(tech1.getId());
		Page<ServiceTechnicianHoliday> serSortInfoPage = new Page<>(request, response);
		Page<ServiceTechnicianHoliday> page = holidayService.appFindPage(serSortInfoPage, holiday);

		return new SuccResult(page);
	}

	//新增休假
	@ResponseBody
	@RequestMapping(value = "insertTech", method = RequestMethod.POST)
	@ApiOperation(value = "新增休假", notes = "技师休假")
	public Result insertTech(ServiceTechnicianHoliday info) {

		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		//获取技师id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		info.setTechId(tech.getId());
		//服务人员在请假时间内是否有未完成的订单
		if(holidayService.getOrderTechRelationHoliday(info) > 0){
			return new FailResult("服务人员有未完成订单,不可请假.");
		}
		//服务人员在请假时间内是否有请假
		if(holidayService.getHolidayHistory(info) > 0){
			return new FailResult("请假时间冲突");
		}

		holidayService.save(info);
		return new SuccResult("保存成功");
	}

	@ResponseBody
	@RequiresPermissions("deleteTech")
	@ApiOperation(value = "删除休假", notes = "技师休假")
	public Result deleteTech( ServiceTechnicianHoliday serviceTechnicianHoliday) {
		holidayService.delete(serviceTechnicianHoliday);
		return new SuccResult("删除休假成功");
	}

	//技师改密码
	@ResponseBody
	@RequestMapping(value = "appChangePassword", method = RequestMethod.POST)
	@ApiOperation(value = "技师修改登陆密码", notes = "技师")
	public Result appChangePassword(String oldPassword,String newPassword) {
		//获取登陆技师的信息 id 去数据库查询出加密后的密码password


		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		String oldEncrypt = systemService.entryptPassword(oldPassword);
		//旧密码加密与查询出来的密码验证
		if (!oldEncrypt.equals(tech.getAppLoginPassword())){
			return new FailResult("旧密码输入不对");
		}
		String newEncrypt = systemService.entryptPassword(newPassword);
		tech.setAppLoginPassword(newEncrypt);
		int i = techService.updateTech(tech);
		if (i>0){
			return new SuccResult("修改密码成功");
		}
		return new FailResult("修改密码失败");
	}
}
