/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.app.interceptor.Token;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.Servlets;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.thinkgem.jeesite.common.web.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技师相关Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP通讯录类", description = "APP通讯录相关接口")
public class AppTechController extends BaseController {
	//技师info的service
	@Autowired
	private ServiceTechnicianInfoService techService;
	//技师假期的service
	@Autowired
	private ServiceTechnicianHolidayService holidayService;
	//字典的service
	@Autowired
	private DictService dictService;
	//改密码调用的系统的service
	@Autowired
	private SystemServicePassword systemService;
	//区域的service
	@Autowired
	private AreaService areaService;
	//消息的service
	@Autowired
	private MessageInfoService messageInfoService;
	//版本的service
	@Autowired
	private VersionInfoService versionInfoService;



	//技师的服务信息列表
	 @ResponseBody
	 @RequestMapping(value = "${appPath}/getTechServiceList",method = {RequestMethod.POST})
	 @ApiOperation(value = "技师服务信息列表", notes = "技师")
	 public AppResult getTechServiceList() {

		 //获取登陆技师的信息  id 服务站id
		 Token token = (Token) Servlets.getRequest().getAttribute("token");
		 ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		 tech.setId(token.getTechId());
		 ServiceTechnicianInfo serviceTechnicianInfo = techService.appFindSkillList(tech);
		 List<ServiceTechnicianWorkTime> times = serviceTechnicianInfo.getTimes();
		 List<SerSkillInfo> skills = serviceTechnicianInfo.getSkills();

		 Map<String,List> map=new HashMap<String,List>();
		 map.put("skills",skills);
		 map.put("times",times);

		 return new AppSuccResult(0,map,"获取服务列表");
	}

	//技师同服务站的通讯录
	@ResponseBody
	@RequestMapping(value = "${appPath}/appGetFriendByStationId",method = {RequestMethod.POST})
	@ApiOperation(value = "通讯录", notes = "通讯录")
	public AppResult appGetFriendByStationId(Page page,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id 服务站id
		Token token = (Token) request.getAttribute("token");
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setId(token.getTechId());
		ServiceTechnicianInfo tech1 = techService.appFindTech(tech);
		if (null == tech1){
			return new AppSuccResult(1, null, "未找到该用户");
		}
		try {
			Page<AppServiceTechnicianInfo> list = techService.appGetFriendByStationId(page, tech1);
			long count = page.getCount();
			int pageSize = page.getPageSize();
			long totalPage = 0;
			long l = count % pageSize;//取余
			if (l > 0) {
				long l1 = count / pageSize;
				totalPage = l1 + 1;
			} else {
				totalPage = count / pageSize;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", page.getList());
			map.put("totalPage", totalPage);
			map.put("pageNo", page.getPageNo());
			if (list.getList()==null || list.getList().size() == 0) {
				return new AppSuccResult(1, null, "查询通讯录");
			}
			return new AppSuccResult(0, map, "查询通讯录");
		}catch (ServiceException e){
			return new AppSuccResult(1, null, e.getMessage());
		}
	}

	//技师休假列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/restTechList",method = {RequestMethod.POST})
	@ApiOperation(value = "技师休假列表", notes = "技师休假")
	public AppResult restTechList(Page serSortInfoPage,HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token) request.getAttribute("token");
		//获取登陆技师的信息  id
		ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
		holiday.setTechId(token.getTechId());
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
		if (page.getList() == null || page.getList().size() == 0){
			return new AppSuccResult(1,null,"技师休假列表");
		}
		return new AppSuccResult(0,map,"技师休假列表");

	}

	//新增休假
	@ResponseBody
	@RequestMapping(value = "${appPath}/insertTech",method = {RequestMethod.POST})
	@ApiOperation(value = "新增休假", notes = "技师休假")
	public AppResult insertTech(ServiceTechnicianHoliday info,HttpServletRequest request, HttpServletResponse response) {

		//不是上班时间不能请假
		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		info.setTechId(token.getTechId());
		int i ;
		try{
			i = holidayService.appSave(info);
			if (i>0){
				return new AppSuccResult(0,null,"保存成功");
			}
			return new AppFailResult(-1,null,"保存失败，可能是设置的时间不在工作时间内");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/deleteTech", method = {RequestMethod.POST})
	@ApiOperation(value = "删除休假", notes = "技师休假")
	public AppResult deleteTech(ServiceTechnicianHoliday serviceTechnicianHoliday,HttpServletRequest request, HttpServletResponse response) {

		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		serviceTechnicianHoliday.setTechId(token.getTechId());
		int delete = holidayService.delete1(serviceTechnicianHoliday);
		if (delete > 0){
			return new AppSuccResult(0,null,"删除休假成功");
		}
		return new AppFailResult(-1,null,"删除休假失败");
	}


	//技师改密码
	@ResponseBody
	@RequestMapping(value = "${appPath}/appChangePassword",method = {RequestMethod.POST})
	@ApiOperation(value = "技师修改登陆密码", notes = "技师")
	public AppResult appChangePassword(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		List<String> errList = errors(tech, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		tech.setId(token.getTechId());
		ServiceTechnicianInfo tech1 = techService.appFindTech(tech);
		if (tech1==null){
			return new AppFailResult(-1,null,"没有该用户");
		}
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
	@RequestMapping(value = "${appPath}/saveTech", method = {RequestMethod.POST})
	@ApiOperation(value = "修改个人资料保存", notes = "修改个人资料")
	public AppResult saveTech(AppServiceTechnicianInfo appTech,HttpServletRequest request, HttpServletResponse response) {

		Token token = (Token) request.getAttribute("token");
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		appTech.setId(token.getTechId());
		try {
			int i = techService.appUpdate(appTech);
			if (i > 0) {
				ServiceTechnicianInfo tech = new ServiceTechnicianInfo();
				tech.setId(appTech.getId());
				//查询出来的appTech
				AppServiceTechnicianInfo technicianById = techService.getTechnicianById(tech);
				technicianById.setImgUrl(ossHost + technicianById.getImgUrl());
				technicianById.setImgUrlHead(ossHost + technicianById.getImgUrlHead());
				technicianById.setImgUrlLife(ossHost + technicianById.getImgUrlLife());
				//身份证正反面
				String imgUrlCard = technicianById.getImgUrlCard();
				technicianById.setImgUrlCard(imgUrlCard);
				if (StringUtils.isNotBlank(imgUrlCard)) {
					Map<String, String> getIdCardMap = (Map<String, String>) JsonMapper.fromJsonString(imgUrlCard, Map.class);
					technicianById.setImgUrlCardBefor(ossHost + getIdCardMap.get("befor"));
					technicianById.setImgUrlCardAfter(ossHost + getIdCardMap.get("after"));
				}

				//民族
				if (StringUtils.isNotBlank(technicianById.getTechNationValue())) {
					Dict dict = new Dict();
					dict.setType("ethnic");
					dict.setValue(technicianById.getTechNationValue());
					Dict name = dictService.findName(dict);
					technicianById.setTechNation(name.getLabel());
				}
				//籍贯
				if (StringUtils.isNotBlank(technicianById.getTechNativePlaceValue())) {
					List<Area> nameByCode = areaService.getNameByCode(technicianById.getTechNativePlaceValue());
					technicianById.setTechNativePlace(nameByCode.get(0).getName());
				}
				//省市区
				if (StringUtils.isNotBlank(technicianById.getProvinceCode())) {
					List<Area> nameByCode = areaService.getNameByCode(technicianById.getProvinceCode());
					technicianById.setProvinceCodeName(nameByCode.get(0).getName());
				}
				if (StringUtils.isNotBlank(technicianById.getCityCode())) {
					List<Area> nameByCode = areaService.getNameByCode(technicianById.getCityCode());
					technicianById.setCityCodeName(nameByCode.get(0).getName());
				}
				if (StringUtils.isNotBlank(technicianById.getAreaCode())) {
					List<Area> nameByCode = areaService.getNameByCode(technicianById.getAreaCode());
					technicianById.setAreaCodeName(nameByCode.get(0).getName());
				}
				return new AppSuccResult(0, technicianById, "保存成功");
			}
			return new AppFailResult(-1,null,"保存失败");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}

	}

	//修改资料列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/selectList", method = {RequestMethod.POST})
	@ApiOperation(value = "个人信息下拉列表", notes = "下拉列表")
	public AppResult selectList(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {

		Map<String,Object> map=new HashMap<String,Object>();
		List<Map> sexList=new ArrayList<Map>();
		Dict dict=new Dict();
		dict.setType("sex");
		List<Dict> sexs = dictService.appFindList(dict);
		if (sexs != null && sexs.size()>0) {
			for (Dict sex : sexs) {
				Map<String, Object> sexMap = new HashMap<String, Object>();
				sexMap.put("label", sex.getLabel());
				sexMap.put("value", sex.getValue());
				sexList.add(sexMap);
			}
		}
		dict.setType("ethnic");
		List<Dict> nations = dictService.appFindList(dict);
		List naList=new ArrayList();
		if (nations!=null && nations.size()>0) {
			for (Dict nation : nations) {
				Map<String, Object> nationMap = new HashMap<String, Object>();
				nationMap.put("label", nation.getLabel());
				nationMap.put("value", nation.getValue());
				naList.add(nationMap);
			}
		}
		Area area=new Area();
		area.setLevel(1);
		List<Area> areas = areaService.appFindAllList(area);
		List proList=new ArrayList();
		if (areas !=null && areas.size()>0) {
			for (Area area1 : areas) {
				Map<String, Object> ps = new HashMap<String, Object>();
				ps.put("nativeProvinceCode", area1.getCode());
				ps.put("name", area1.getName());
				proList.add(ps);
			}
		}
		map.put("sex",sexList);
		map.put("nation",naList);
		map.put("provinces",proList);
		return new AppSuccResult(0,map,"下拉列表");
	}
	//现住地址下拉列表

	@ResponseBody
	@RequestMapping(value = "${appPath}/selectListPro", method = {RequestMethod.POST})
	@ApiOperation(value = "现住地址下拉列表省", notes = "地址下拉列表")
	public AppResult selectListPro(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		Map map=new HashMap();
		//根据省号查询省
		Area area=new Area();
		area.setLevel(1);
		List<Area> pro = areaService.appFindAllList(area);
		List<Map<String,String>> proList=new ArrayList<Map<String,String>>();
		if (pro!=null && pro.size()>0){
			for (int i=0;i<pro.size();i++) {
				Map<String, String> ps = new HashMap<String, String>();
				ps.put("provinceCode", pro.get(i).getCode());
				ps.put("provinceCodeName", pro.get(i).getName());
				proList.add(ps);
			}
		}
		map.put("province",proList);
		return new AppSuccResult(0,map,"下拉列表");
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/selectListCity", method = {RequestMethod.POST})
	@ApiOperation(value = "现住地址下拉列表市", notes = "地址下拉列表")
	public AppResult selectListCity(AppServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotBlank(tech.getProvinceCode())){
			Map map=new HashMap();
			//根据省号查询所有市
			Area area=new Area();
			area.setLevel(2);
			area.setParentCode(tech.getProvinceCode());
			List<Area> pro = areaService.appFindAllList(area);
			List<Map<String,String>> cityList=new ArrayList<Map<String,String>>();
			if (pro !=null && pro.size()>0){
				for (Area area1:pro){
					Map<String,String> ps=new HashMap<String,String>();
					ps.put("cityCode",area1.getCode());
					ps.put("cityCodeName",area1.getName());
					cityList.add(ps);
				}
			}
			map.put("city",cityList);
			if (cityList !=null && cityList.size()>0){
				return new AppSuccResult(0,map,"下拉列表");
			}
		}
		return new AppSuccResult(1,null,"下拉列表");
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/selectListArea", method = {RequestMethod.POST})
	@ApiOperation(value = "现住地址下拉列表区", notes = "地址下拉列表")
	public AppResult selectListArea(AppServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		Map map=new HashMap();
		//根据市号查询所有区
		Area area=new Area();
		area.setLevel(3);
		if (StringUtils.isNotBlank(tech.getCityCode())){
			area.setParentCode(tech.getCityCode());
			List<Area> qu = areaService.appFindAllList(area);
			List areaList=new ArrayList();
			if (qu !=null && qu.size()>0) {
				for (Area area2 : qu) {
					Map<String, String> cs = new HashMap<String, String>();
					cs.put("areaCode", area2.getCode());
					cs.put("areaCodeName", area2.getName());
					areaList.add(cs);
				}
			}
			map.put("area",areaList);
			if (areaList != null && areaList.size()>0){
				return new AppSuccResult(0,map,"下拉列表");
			}
		}
		return new AppSuccResult(1,null,"下拉列表");
	}

	//查看消息列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/getMessageList", method = {RequestMethod.POST})
	@ApiOperation(value = "消息列表", notes = "消息列表")
	public AppResult getMessageList(Page page,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		//根据消息的id 收件人查看
		MessageInfo messageInfo=new MessageInfo();
		messageInfo.setTechId(token.getTechId());
		Page<MessageInfo> list = messageInfoService.findList(page, messageInfo);
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
		Map map=new HashMap();
		map.put("list",list.getList());
		map.put("totalPage",totalPage);
		map.put("pageNo",page.getPageNo());
		if (list.getList() ==null || list.getList().size() == 0){
			return new AppFailResult(1,null,"没有消息");
		}
		return new AppSuccResult(0,map,"查看消息列表");
	}

	//app未读消息数量
	@ResponseBody
	@RequestMapping(value = "${appPath}/getCount", method = {RequestMethod.POST})
	@ApiOperation(value = "未读消息数量", notes = "未读消息数量")
	public AppResult getCount(ServiceTechnicianInfo info,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		//根据登陆技师的id去查询对应的未读消息数量
		MessageInfo messageInfo=new MessageInfo();
		messageInfo.setTechId(token.getTechId());
		int count = messageInfoService.getCount(messageInfo);
		return new AppFailResult(0,count,"未读消息数量");
	}

	//消息已读
	@ResponseBody
	@RequestMapping(value = "${appPath}/updateMessage", method = {RequestMethod.POST})
	@ApiOperation(value = "消息已读", notes = "消息已读")
	public AppResult updateMessage(MessageInfo messageInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		//需要传过来消息的id
		Token token = (Token) request.getAttribute("token");
		messageInfo.setReceivePhone(token.getPhone());
		try{
			int i = messageInfoService.updateMessage(messageInfo);
			if (i > 0){
				return new AppSuccResult(0,null,"编辑消息已读成功");
			}
			return new AppFailResult(-1,null,"编辑已读失败");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}

	//版本
	@ResponseBody
	@RequestMapping(value = "${appPath}/updateVersion", method = {RequestMethod.POST})
	@ApiOperation(value = "版本更新", notes = "版本更新")
	public AppResult updateVersion(VersionInfo versionInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取传过来的code
		String build = request.getHeader("appBuild");
		versionInfo.setReceiveBuild(build);
		VersionInfo byTime = versionInfoService.getByTime(versionInfo);
		if(byTime ==null){
			return new AppSuccResult(1,null,"版本已是最新版本");
		}
		return new AppSuccResult(0,byTime,"版本更新");
	}
}
