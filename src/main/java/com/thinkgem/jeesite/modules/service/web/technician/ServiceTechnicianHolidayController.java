/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.technician;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianHolidayService;

import java.util.List;

/**
 * 服务技师休假时间Controller
 * @author a
 * @version 2017-11-29
 */
@Controller
@RequestMapping(value = "${adminPath}/service/technician/serviceTechnicianHoliday")
public class ServiceTechnicianHolidayController extends BaseController {

	@Autowired
	private ServiceTechnicianHolidayService serviceTechnicianHolidayService;
	
	@ModelAttribute
	public ServiceTechnicianHoliday get(@RequestParam(required=false) String id) {
		ServiceTechnicianHoliday entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serviceTechnicianHolidayService.get(id);
		}
		if (entity == null){
			entity = new ServiceTechnicianHoliday();
		}
		return entity;
	}

	/**
	 * 保存服务技师休假时间
	 *
	 * @param info
	 * @return
	 */
	@ResponseBody
	//@RequiresPermissions("holiday_insert")
	@ApiOperation("保存服务技师休假时间")
	@RequestMapping(value = "saveData", method = RequestMethod.POST)
	public Result saveData(@RequestBody ServiceTechnicianHoliday info) {
		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		//服务人员在请假时间内是否有未完成的订单
		if(serviceTechnicianHolidayService.getOrderTechRelationHoliday(info) > 0){
			return new FailResult("服务人员有未完成订单,不可请假.");
		}
		//服务人员在请假时间内是否有请假
		if(serviceTechnicianHolidayService.getHolidayHistory(info) > 0){
			return new FailResult("请假时间冲突");
		}

		serviceTechnicianHolidayService.save(info);
		return new SuccResult("保存成功");
	}

	@ResponseBody
	@RequiresPermissions("holiday_view")
	@RequestMapping(value = "/listData", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取休假列表")
	public Result listData(@RequestBody(required=false) ServiceTechnicianHoliday serviceTechnicianHoliday, HttpServletRequest request, HttpServletResponse response) {
		if(serviceTechnicianHoliday == null){
			serviceTechnicianHoliday = new ServiceTechnicianHoliday();
		}
		Page<ServiceTechnicianHoliday> serSortInfoPage = new Page<>(request, response);
		Page<ServiceTechnicianHoliday> page = serviceTechnicianHolidayService.findPage(serSortInfoPage, serviceTechnicianHoliday);
		return new SuccResult(page);
	}

	@ResponseBody
	@RequiresPermissions("holiday_delete")
	@RequestMapping(value = "delete", method = {RequestMethod.POST})
	@ApiOperation("删除休假")
	public Result delete(@RequestBody ServiceTechnicianHoliday serviceTechnicianHoliday) {
		serviceTechnicianHolidayService.delete(serviceTechnicianHoliday);
		return new SuccResult("删除休假成功");
	}

}