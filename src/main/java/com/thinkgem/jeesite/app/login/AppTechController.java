/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
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
import java.text.SimpleDateFormat;
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

		 return new AppSuccResult(0,map,"获取服务列表");
	}

	//技师同服务站的通讯录
	@ResponseBody
	@RequestMapping(value = "${appPath}/appGetFriendByStationId",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "通讯录", notes = "通讯录")
	public AppResult appGetFriendByStationId(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id 服务站id
//		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		Page<AppTech> page = new Page<AppTech>(request, response);
		Page<AppTech> list = techService.appGetFriendByStationId(page,tech1);
		long count = page.getCount();
		int pageSize = page.getPageSize();
		long totalPage=0;
		long l = count % pageSize;//取余
		if (l > 0){
			long l1 = count / pageSize;
			totalPage = l1 + 1;
		}else {
			totalPage = count / pageSize;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("list",page.getList());
		map.put("totalPage",totalPage);
		map.put("pageNo",page.getPageNo());
		if (list.getList().size() == 0){
			return new AppSuccResult(1,map,"查询通讯录");
		}
		return new AppSuccResult(0,map,"查询通讯录");
	}

	//技师休假列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/restTechList",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "技师休假列表", notes = "技师休假")
	public AppResult restTechList(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {

		//获取登陆技师的信息  id
		//ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);

		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		holiday.setTechId(tech1.getId());
		Page<ServiceTechnicianHoliday> serSortInfoPage = new Page<>(request, response);
		Page<ServiceTechnicianHoliday> page = holidayService.appFindPage(serSortInfoPage, holiday);
		long count = page.getCount();
		int pageSize = page.getPageSize();
		long totalPage=0;
		long l = count % pageSize;//取余
		if (l > 0){
			long l1 = count / pageSize;
			totalPage = l1 + 1;
		}else {
			totalPage = count / pageSize;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("list",page.getList());
		map.put("totalPage",totalPage);
		map.put("pageNo",page.getPageNo());
		if (page.getList().size() == 0){
			return new AppSuccResult(1,map,"技师休假列表");
		}
		return new AppSuccResult(0,map,"技师休假列表");

	}

	//新增休假
	@ResponseBody
	@RequestMapping(value = "${appPath}/insertTech",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "新增休假", notes = "技师休假")
	public AppResult insertTech(ServiceTechnicianHoliday info,HttpServletRequest request, HttpServletResponse response) {

		//不是上班时间不能请假
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

		int i = holidayService.appSave(info);
		if (i==0){
			return new AppFailResult(-1,null,"设置的时间不在工作时间内");
		}
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

}
