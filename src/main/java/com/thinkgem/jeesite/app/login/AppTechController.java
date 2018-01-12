/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.thinkgem.jeesite.common.web.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private SerSkillInfoService serSkillInfoService;


	//技师的服务信息列表
	 @ResponseBody
	 @RequestMapping(value = "${appPath}/getTechServiceList",method = {RequestMethod.POST, RequestMethod.GET})
	 @ApiOperation(value = "技师服务信息列表", notes = "技师")
	 public AppResult getTechServiceList() {

		 //获取登陆技师的信息  id 服务站id
		 ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		 tech.setPhone("13508070808");
		 tech.setDelFlag("0");
		 ServiceTechnicianInfo tech1 = techService.findTech(tech);
		 ServiceTechnicianInfo serviceTechnicianInfo = techService.appFindSkillList(tech1);
		 List<ServiceTechnicianWorkTime> times = serviceTechnicianInfo.getTimes();
		 List<SerSkillInfo> skills = serviceTechnicianInfo.getSkills();

		 Map<String,List> map=new HashMap<String,List>();
		 map.put("skills",skills);
		 map.put("times",times);

		 return new AppSuccResult(map);
	}

	//技师同服务站的通讯录
	@ResponseBody
	@RequestMapping(value = "${appPath}/appGetFriendByStationId",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "通讯录", notes = "通讯录")
	public AppResult appGetFriendByStationId(HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id 服务站id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		Page<ServiceTechnicianInfo> page = new Page<ServiceTechnicianInfo>(request, response);
		Page<ServiceTechnicianInfo> list = techService.appGetFriendByStationId(page,tech1);

		return new AppSuccResult(list);
	}

	//技师休假列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/restTechList",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "技师休假列表", notes = "技师休假")
	public AppResult restTechList(HttpServletRequest request, HttpServletResponse response) {

		//获取登陆技师的信息  id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);

		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		holiday.setTechId(tech1.getId());
		Page<ServiceTechnicianHoliday> serSortInfoPage = new Page<>(request, response);
		Page<ServiceTechnicianHoliday> page = holidayService.appFindPage(serSortInfoPage, holiday);
		return new AppSuccResult(page);

	}

	//新增休假
	@ResponseBody
	@RequestMapping(value = "${appPath}/insertTech",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "新增休假", notes = "技师休假")
	public AppResult insertTech(ServiceTechnicianHoliday info,HttpServletRequest request, HttpServletResponse response) {

		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//获取技师id
		//获取登陆技师的信息  id
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		info.setTechId(tech1.getId());
		//服务人员在请假时间内是否有未完成的订单
		if(holidayService.getOrderTechRelationHoliday(info) > 0){
			return new AppFailResult(-1,null,"服务人员有未完成订单,不可请假.");
		}
		//服务人员在请假时间内是否有请假
		if(holidayService.getHolidayHistory(info) > 0){
			return new AppFailResult(-1,null,"请假时间冲突");
		}

		holidayService.save(info);
		return new AppSuccResult(0,null,"保存成功");
	}

	@ResponseBody
	//@RequiresPermissions("deleteTech")
	@RequestMapping(value = "${appPath}/deleteTech", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "删除休假", notes = "技师休假")
	public AppResult deleteTech(ServiceTechnicianHoliday serviceTechnicianHoliday,HttpServletRequest request, HttpServletResponse response) {
		int delete = holidayService.delete1(serviceTechnicianHoliday);
		if (delete > 0){
			return new AppSuccResult(0,null,"删除休假成功");
		}
		return new AppFailResult(-1,null,"删除休假失败");
	}

	//技师改密码
	@ResponseBody
	@RequestMapping(value = "${appPath}/appChangePassword",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "技师修改登陆密码", notes = "技师")
	public AppResult appChangePassword(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		List<String> errList = errors(tech, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//获取登陆技师的信息 id 去数据库查询出加密后的密码password
//		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
//		String oldEncrypt = systemService.entryptPassword(oldPassword);
		//旧密码与数据库查询出来的密码验证
		if (!systemService.validatePassword(tech.getOldPassword(),tech1.getAppLoginPassword())){
			return new AppFailResult(-1,null,"旧密码输入不对");
		}
//		if (!oldEncrypt.equals(tech1.getAppLoginPassword())){
//			return new FailResult("旧密码输入不对");
//		}
		String newEncrypt = systemService.entryptPassword(tech.getNewPassword());
		tech1.setAppLoginPassword(newEncrypt);
		int i = techService.updateTech(tech1);
		if (i>0){
			return new AppSuccResult(0,null,"修改密码成功");
		}
		return new AppFailResult(-1,null,"修改密码失败");
	}
}
