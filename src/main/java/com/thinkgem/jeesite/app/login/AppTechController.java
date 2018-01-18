/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.DictService;
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
import java.util.ArrayList;
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
	private ServiceTechnicianInfoService techService;
	@Autowired
	private ServiceTechnicianHolidayService holidayService;
	@Autowired
	private DictService dictService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private AreaService areaService;


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
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		Page<AppServiceTechnicianInfo> page = new Page<AppServiceTechnicianInfo>(request, response);
		Page<AppServiceTechnicianInfo> list = techService.appGetFriendByStationId(page,tech1);
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
		tech.setId("d30d2e68ae1a48b3b8a80625b0abc39f");
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
	@RequestMapping(value = "${appPath}/deleteTech", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "删除休假", notes = "技师休假")
	public AppResult deleteTech(ServiceTechnicianHoliday serviceTechnicianHoliday,HttpServletRequest request, HttpServletResponse response) {
		serviceTechnicianHoliday.setTechId("d30d2e68ae1a48b3b8a80625b0abc39f");
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
		tech.setPhone("13508070808");
		tech.setDelFlag("0");
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		//旧密码与数据库查询出来的密码验证
		if (!systemService.validatePassword(tech.getOldPassword(),tech1.getAppLoginPassword())){
			return new AppFailResult(-1,null,"旧密码输入不对");
		}
		String newEncrypt = systemService.entryptPassword(tech.getNewPassword());
		tech1.setAppLoginPassword(newEncrypt);
		int i = techService.updateTech(tech1);
		if (i>0){
			return new AppSuccResult(0,null,"修改密码成功");
		}
		return new AppFailResult(-1,null,"修改密码失败");
	}

	//修改资料保存
	@ResponseBody
	@RequestMapping(value = "${appPath}/saveTech", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "修改个人资料保存", notes = "修改个人资料")
	public AppResult saveTech(AppServiceTechnicianInfo appTech,HttpServletRequest request, HttpServletResponse response) {
		appTech.setId("d30d2e68ae1a48b3b8a80625b0abc39f");

		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_HOST");

		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setId(appTech.getId());
		tech.setHeadPic(appTech.getImgUrlHead());
		tech.setName(appTech.getTechName());
		tech.setPhone(appTech.getTechPhone());
		tech.setSex(appTech.getTechSex());
		tech.setBirthDate(appTech.getTechBirthDate());
		tech.setAddress(appTech.getAddrDetailInfo());
		tech.setIdCard(appTech.getTechIdCard());
		tech.setIdCardPicBefor(appTech.getImgUrlCard());
		tech.setEmail(appTech.getTechEmail());
		if (appTech.getTechHeight() != null){
			tech.setHeight(Integer.valueOf(appTech.getTechHeight()));
		}
		if (appTech.getTechWeight() != null){
			tech.setWeight(Integer.valueOf(appTech.getTechWeight()));
		}
		tech.setNativeProvinceCode(appTech.getTechNativePlace());
		tech.setNation(appTech.getTechNation());
		tech.setDescription(appTech.getExperDesc());
		tech.setLifePic(appTech.getImgUrlLife());
		int i = techService.appUpdate(tech);
		if (i > 0){
			AppServiceTechnicianInfo technicianById = techService.getTechnicianById(tech);
			technicianById.setImgUrl(ossHost+technicianById.getImgUrl());
			technicianById.setImgUrlHead(ossHost+technicianById.getImgUrlHead());
			technicianById.setImgUrlLife(ossHost+technicianById.getImgUrlLife());
			technicianById.setImgUrlCard(ossHost+technicianById.getImgUrlCard());
			return new AppSuccResult(0,technicianById,"保存成功");
		}
		return new AppFailResult(-1,null,"保存失败");
	}

	//修改资料列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/selectList", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "个人信息下拉列表", notes = "下拉列表")
	public AppResult selectList(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {

		Map<String,Object> map=new HashMap<String,Object>();

		List<Map> sexList=new ArrayList<Map>();
		Dict dict=new Dict();
		dict.setType("sex");
		List<Dict> sexs = dictService.appFindList(dict);
		for (Dict sex:sexs){
			Map<String,Object> sexMap=new HashMap<String,Object>();
			sexMap.put("label",sex.getLabel());
			sexMap.put("value",sex.getValue());
			sexList.add(sexMap);
		}

		dict.setType("ethnic");
		List<Dict> nations = dictService.appFindList(dict);
		List naList=new ArrayList();
		for (Dict nation:nations){
			Map<String,Object> nationMap=new HashMap<String,Object>();
			nationMap.put("label",nation.getLabel());
			naList.add(nationMap);
		}

		Area area=new Area();
		List<Area> areas = areaService.appFindAllList(area);
		List proList=new ArrayList();
		for (Area area1:areas){
			Map<String,Object> ps=new HashMap<String,Object>();
			ps.put("nativeProvinceCode",area1.getCode());
			ps.put("name",area1.getName());
			proList.add(ps);
		}
		map.put("sex",sexList);
		map.put("nation",naList);
		map.put("provinces",proList);
		return new AppSuccResult(0,map,"下拉列表");
	}
}
