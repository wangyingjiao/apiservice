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
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;
import com.thinkgem.jeesite.modules.sys.service.*;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
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
	@Autowired
	private MessageInfoService messageInfoService;
	@Autowired
	private VersionInfoService versionInfoService;



	//技师的服务信息列表
	 @ResponseBody
	 @RequestMapping(value = "${appPath}/getTechServiceList",method = {RequestMethod.POST, RequestMethod.GET})
	 @ApiOperation(value = "技师服务信息列表", notes = "技师")
	 public AppResult getTechServiceList() {

		 //获取登陆技师的信息  id 服务站id
		 Token token = (Token) Servlets.getRequest().getAttribute("token");
		 String phone = token.getPhone();
		 ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		 tech.setDelFlag("0");
		 tech.setPhone(phone);
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
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
//		System.out.println(token);
		tech.setPhone(phone);
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
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		//获取登陆技师的信息  id
		tech.setPhone(phone);
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
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone(phone);
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		info.setTechId(tech1.getId());
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
	@RequestMapping(value = "${appPath}/deleteTech", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "删除休假", notes = "技师休假")
	public AppResult deleteTech(ServiceTechnicianHoliday serviceTechnicianHoliday,HttpServletRequest request, HttpServletResponse response) {

		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone(phone);
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		serviceTechnicianHoliday.setTechId(tech1.getId());
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
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		tech.setPhone(phone);
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

		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		ServiceTechnicianInfo tech2=new ServiceTechnicianInfo();
		tech2.setPhone(phone);
		//从数据库查询用户
		ServiceTechnicianInfo tech1 = techService.findTech(tech2);
		appTech.setId(tech1.getId());
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		//将apptech转成技师
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
		//籍贯code
		tech.setNativeProvinceCode(appTech.getTechNativePlace());
		//民族code
		tech.setNation(appTech.getTechNation());
		tech.setDescription(appTech.getExperDesc());
		tech.setLifePic(appTech.getImgUrlLife());
		tech.setIdCardPicBefor(appTech.getImgUrlCardBefor());
		tech.setIdCardPicAfter(appTech.getImgUrlCardAfter());
		//省市区code
		tech.setProvinceCode(appTech.getProvinceCode());
		tech.setCityCode(appTech.getCityCode());
		tech.setAreaCode(appTech.getAreaCode());
		//身份证正反面
		Map<String,String> map=new HashMap<String,String>();
		map.put("befor",appTech.getImgUrlCardBefor());
		map.put("after",appTech.getImgUrlCardAfter());
		String s = JsonMapper.toJsonString(map);
		if (StringUtils.isNotBlank(appTech.getImgUrlCardBefor()) || StringUtils.isNotBlank(appTech.getImgUrlCardAfter())){
			tech.setIdCardPic(s);
		}
		int i = techService.appUpdate(tech);
		if (i > 0){
			//查询出来的appTech
			AppServiceTechnicianInfo technicianById = techService.getTechnicianById(tech);
			technicianById.setImgUrl(ossHost+technicianById.getImgUrl());
			technicianById.setImgUrlHead(ossHost+technicianById.getImgUrlHead());
			technicianById.setImgUrlLife(ossHost+technicianById.getImgUrlLife());
			//身份证正反面
			String imgUrlCard = technicianById.getImgUrlCard();
			technicianById.setImgUrlCard(imgUrlCard);
			if (StringUtils.isNotBlank(imgUrlCard)){
				Map<String, String> getIdCardMap = (Map<String, String>) JsonMapper.fromJsonString(imgUrlCard, Map.class);
				technicianById.setImgUrlCardBefor(ossHost+getIdCardMap.get("befor"));
				technicianById.setImgUrlCardAfter(ossHost+getIdCardMap.get("after"));
			}

			//民族
			if (StringUtils.isNotBlank(technicianById.getTechNationValue())){
				Dict dict=new Dict();
				dict.setType("ethnic");
				dict.setValue(technicianById.getTechNationValue());
				Dict name = dictService.findName(dict);
				technicianById.setTechNation(name.getLabel());
			}
			//籍贯
			if (StringUtils.isNotBlank(technicianById.getTechNativePlaceValue())){
				List<Area> nameByCode = areaService.getNameByCode(technicianById.getTechNativePlaceValue());
				technicianById.setTechNativePlace(nameByCode.get(0).getName());
			}
			//省市区
			if (StringUtils.isNotBlank(technicianById.getProvinceCode())){
				List<Area> nameByCode = areaService.getNameByCode(technicianById.getProvinceCode());
				technicianById.setProvinceCodeName(nameByCode.get(0).getName());
			}
			if (StringUtils.isNotBlank(technicianById.getCityCode())){
				List<Area> nameByCode = areaService.getNameByCode(technicianById.getCityCode());
				technicianById.setCityCodeName(nameByCode.get(0).getName());
			}
			if (StringUtils.isNotBlank(technicianById.getAreaCode())){
				List<Area> nameByCode = areaService.getNameByCode(technicianById.getAreaCode());
				technicianById.setAreaCodeName(nameByCode.get(0).getName());
			}
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
			nationMap.put("value",nation.getValue());
			naList.add(nationMap);
		}

		Area area=new Area();
		area.setLevel(1);
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
	//现住地址下拉列表

	@ResponseBody
	@RequestMapping(value = "${appPath}/selectListPro", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "现住地址下拉列表省", notes = "地址下拉列表")
	public AppResult selectListPro(ServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token)request.getAttribute("token");
		tech.setPhone(token.getPhone());
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		Map map=new HashMap();
		//根据省号查询省
		Area area=new Area();
		area.setLevel(1);
		List<Area> pro = areaService.appFindAllList(area);
		List<Map<String,String>> proList=new ArrayList<Map<String,String>>();
		if (pro.size()>0){
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
	@RequestMapping(value = "${appPath}/selectListCity", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "现住地址下拉列表市", notes = "地址下拉列表")
	public AppResult selectListCity(AppServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		Map map=new HashMap();
		//根据省号查询所有市
		Area area=new Area();
		area.setLevel(2);
		area.setParentCode(tech.getProvinceCode());
		List<Area> pro = areaService.appFindAllList(area);
		List<Map<String,String>> cityList=new ArrayList<Map<String,String>>();
		if (pro.size()>0){
			for (Area area1:pro){
				Map<String,String> ps=new HashMap<String,String>();
				ps.put("cityCode",area1.getCode());
				ps.put("cityCodeName",area1.getName());
				cityList.add(ps);
			}
		}
		map.put("city",cityList);
		return new AppSuccResult(0,map,"下拉列表");
	}
	@ResponseBody
	@RequestMapping(value = "${appPath}/selectListArea", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "现住地址下拉列表区", notes = "地址下拉列表")
	public AppResult selectListArea(AppServiceTechnicianInfo tech,HttpServletRequest request, HttpServletResponse response) {
		Map map=new HashMap();
		//根据市号查询所有区
		Area area=new Area();
		area.setLevel(3);
		area.setParentCode(tech.getCityCode());
		List<Area> qu = areaService.appFindAllList(area);
		List areaList=new ArrayList();
		if (qu.size()>0) {
			for (Area area2 : qu) {
				Map<String, String> cs = new HashMap<String, String>();
				cs.put("areaCode", area2.getCode());
				cs.put("areaCodeName", area2.getName());
				areaList.add(cs);
			}
		}
		map.put("area",areaList);
		return new AppSuccResult(0,map,"下拉列表");
	}

	//查看单个消息
	@ResponseBody
	@RequestMapping(value = "${appPath}/get", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "查看消息详情", notes = "查看消息详情")
	public AppResult get(MessageInfo messageInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		//根据消息的id 收件人查看
		messageInfo.setReceivePhone(phone);
		MessageInfo messageInfo1 = messageInfoService.get(messageInfo);
		if (messageInfo1 == null){
			return new AppFailResult(-1,null,"没有该消息");
		}
		return new AppSuccResult(0,messageInfo1,"查看消息");
	}
		//查看消息列表
	@ResponseBody
	@RequestMapping(value = "${appPath}/getMessageList", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "消息列表", notes = "消息列表")
	public AppResult getMessageList(MessageInfo messageInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		//根据消息的id 收件人查看
		messageInfo.setReceivePhone(phone);
		Page<MessageInfo> page=new Page<MessageInfo>(request, response);
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
		if (list.getList().size() == 0){
			return new AppFailResult(1,null,"没有消息");
		}
		return new AppSuccResult(0,map,"查看消息列表");
	}
	//增加消息到数据库
	@ResponseBody
	@RequestMapping(value = "${appPath}/insertMessage", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "添加消息", notes = "添加消息")
	public AppResult insertMessage(MessageInfo messageInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取登陆技师的信息  id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		int insert = messageInfoService.insert(messageInfo);
		if (insert > 0){
			return new AppSuccResult(0,null,"添加消息成功");
		}
		return new AppFailResult(-1,null,"添加消息失败");
	}
	//消息已读
	@ResponseBody
	@RequestMapping(value = "${appPath}/updateMessage", method = {RequestMethod.POST, RequestMethod.GET})
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
	@RequestMapping(value = "${appPath}/updateVersion", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "版本更新", notes = "版本更新")
	public AppResult updateVersion(VersionInfo versionInfo,HttpServletRequest request, HttpServletResponse response) {
		//获取传过来的code
		String build = (String)request.getAttribute("appBuild");
		versionInfo.setBuild(build);
		VersionInfo byTime = versionInfoService.getByTime(versionInfo);
		if(byTime ==null){
			return new AppSuccResult(-1,null,"版本已是最新版本");
		}
		return new AppSuccResult(0,byTime,"版本更新");
	}
}
