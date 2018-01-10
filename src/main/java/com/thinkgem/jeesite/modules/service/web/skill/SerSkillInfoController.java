/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.skill;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillSortService;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortInfoService;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import sun.print.resources.serviceui_ko;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 技能管理Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "技能管理", description = "技能管理相关接口")
@RequestMapping(value = "${adminPath}/service/skill/serSkillInfo")
public class SerSkillInfoController extends BaseController {

	@Autowired
	private SerSkillInfoService serSkillInfoService;
	@Autowired
	private SerSkillSortService serSkillSortService;
	@Autowired
	private SerSortInfoService serSortInfoService;

	@ModelAttribute
	public SerSkillInfo get(@RequestParam(required = false) String id) {
		SerSkillInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = serSkillInfoService.get(id);
		}
		if (entity == null) {
			entity = new SerSkillInfo();
		}
		return entity;
	}

	@ResponseBody
	@RequestMapping(value = "saveData", method = { RequestMethod.POST })
	@RequiresPermissions("skill_insert")
	@ApiOperation("保存技能")
	public Result saveData(@RequestBody SerSkillInfo serSkillInfo) {
		List<String> errList = errors(serSkillInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}

		if (!StringUtils.isNotBlank(serSkillInfo.getId())) {// 新增时验证重复
			User user = UserUtils.getUser();
			serSkillInfo.setOrgId(user.getOrganization().getId());// 机构ID
			if (0 != serSkillInfoService.checkDataName(serSkillInfo)) {
				return new FailResult("当前机构已经包含技能名称" + serSkillInfo.getName() + "");
			}
		}
		serSkillInfoService.save(serSkillInfo);
		return new SuccResult("保存成功");
	}

	@ResponseBody
	@RequestMapping(value = "upData", method = { RequestMethod.POST })
	@RequiresPermissions("skill_update")
	@ApiOperation("保存技能")
	public Result upData(@RequestBody SerSkillInfo serSkillInfo) {
		List<String> errList = errors(serSkillInfo);
		// 验证名字是否可用
		User user = UserUtils.getUser();
		serSkillInfo.setOrgId(user.getOrganization().getId());// 机构ID
		if (0 != serSkillInfoService.checkDataName(serSkillInfo)) {
			return new FailResult("当前机构已经包含技能名称:" + serSkillInfo.getName() + "");
		}
		/*
		 * SerSkillInfo serSkillInfo1=new SerSkillInfo();
		 * serSkillInfo1.setName(serSkillInfo.getName()); SerSkillInfo byName =
		 * serSkillInfoService.getByName(serSkillInfo1); //不同id if (byName !=
		 * null){ if (byName.getId().equals(serSkillInfo.getId())){ //同机构 if
		 * (byName.getOrgId().equals(serSkillInfo.getOrgId())){ return new
		 * FailResult("当前机构已经包含技能名称" + serSkillInfo.getName() + ""); } } }
		 */
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		serSkillInfoService.save(serSkillInfo);
		return new SuccResult("保存成功");

	}

	@ResponseBody
	@RequiresPermissions("skill_view")
	@RequestMapping(value = "listData", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation("获取技能列表")
	public Result listData(@RequestBody(required = false) SerSkillInfo serSkillInfo, HttpServletRequest request,
			HttpServletResponse response) {
		if (serSkillInfo == null) {
			serSkillInfo = new SerSkillInfo();
		}
		Page<SerSkillInfo> serSkillPage = new Page<>(request, response);
		Page<SerSkillInfo> page = serSkillInfoService.findPage(serSkillPage, serSkillInfo);
		return new SuccResult(page);
	}

	@ResponseBody
	@RequestMapping(value = "formData", method = { RequestMethod.POST })
	@ApiOperation("根据ID查找技能")
	public Result formData(@RequestBody SerSkillInfo serSkillInfo) {
		SerSkillInfo entity = null;
		if (StringUtils.isNotBlank(serSkillInfo.getId())) {
			entity = serSkillInfoService.getData(serSkillInfo.getId());
		}
		if (entity == null) {
			return new FailResult("未找到此id对应的技能。");
		} else {
			// 获取分类表
			SerSortInfo serSortInfo = new SerSortInfo();
			serSortInfo.setSortIds(entity.getSortIds());
			List<SerSortInfo> list = serSortInfoService.findList(serSortInfo);
			// List<SerItemInfo> items =
			// serSkillInfoService.findSerPage(serSkillInfo);
			List<SerSkillTechnician> techs = serSkillInfoService.findTechnicianPage(serSkillInfo);
			String orgId = serSkillInfo.getOrgId();
			BasicServiceStation station = new BasicServiceStation();
			station.setId(orgId);
			List<BasicServiceStation> stations = serSkillInfoService.getServiceStationList(station);
			HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
			objectObjectHashMap.put("info", entity);
			objectObjectHashMap.put("list", list);
			// objectObjectHashMap.put("items",items);
			objectObjectHashMap.put("techs", techs);
			objectObjectHashMap.put("stations", stations);

			return new SuccResult(objectObjectHashMap);
		}
	}

	@ResponseBody
	@RequestMapping(value = "insertData", method = { RequestMethod.POST })
	@ApiOperation("新增技能")
	public Result insertData(@RequestBody SerSkillInfo serSkillInfo) {
		// 服务项目
		// List<SerItemInfo> items =
		// serSkillInfoService.findSerPage(serSkillInfo);
		// 根据权限查看对应下的技师列表
		List<SerSkillTechnician> techs = serSkillInfoService.findTechnicianPage(serSkillInfo);
		// 根据权限 查看对应的服务站列表
		BasicServiceStation station = new BasicServiceStation();
		List<BasicServiceStation> stations = serSkillInfoService.getServiceStationList(station);
		SerSortInfo serSortInfo = new SerSortInfo();
		// 获取分类表
		List<SerSortInfo> list = serSortInfoService.findList(serSortInfo);

		HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
		// objectObjectHashMap.put("items",items);
		objectObjectHashMap.put("list", list);
		objectObjectHashMap.put("techs", techs);
		objectObjectHashMap.put("stations", stations);

		return new SuccResult(objectObjectHashMap);
	}

	@ResponseBody
	@RequiresPermissions("skill_delete")
	@RequestMapping(value = "deleteSortInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation("删除技能")
	public Result deleteSortInfo(@RequestBody SerSkillInfo serSkillInfo) {
		serSkillInfoService.delete(serSkillInfo);
		return new SuccResult("删除成功");
	}
}