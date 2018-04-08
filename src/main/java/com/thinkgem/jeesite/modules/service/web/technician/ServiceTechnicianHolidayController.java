/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.technician;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.service.service.technician.TechScheduleService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
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

	//消息的service
	@Autowired
	private MessageInfoService messageInfoService;

	@Autowired
    private ServiceTechnicianInfoService serviceTechnicianInfoService;

	@Autowired
	private TechScheduleService techScheduleService;

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
	@RequiresPermissions("techni_holiday")
	@ApiOperation("保存服务技师休假时间")
	@RequestMapping(value = "saveData", method = RequestMethod.POST)
	public Result saveData(@RequestBody ServiceTechnicianHoliday info) {
		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		try {
			int i = serviceTechnicianHolidayService.savePc(info);
			ServiceTechnicianInfo serviceTechnicianInfo = serviceTechnicianInfoService.get(info.getTechId());
			//查询休假表
			ServiceTechnicianHoliday holiday = new ServiceTechnicianHoliday();
			holiday.setTechId(info.getTechId());
			holiday.setTechPhone(serviceTechnicianInfo.getPhone());
			if (i>0) {
				messageInfoService.insertHoliday(holiday,"techHolidayFailWeb");
				return new SuccResult("保存成功");

			}
			return new FailResult("保存失败");
		}catch (ServiceException e){
			return new FailResult(e.getMessage());
		}
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
		serviceTechnicianHolidayService.deleteHoliday(serviceTechnicianHoliday);
		return new SuccResult("删除休假成功");
	}

	/**
	 * 审核app休假
	 * @param serviceTechnicianHoliday
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("holiday_review")
	@RequestMapping(value = "/reviewedHoliday", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("审核app休假")
	public Result reviewedHoliday(@RequestBody(required=false) ServiceTechnicianHoliday serviceTechnicianHoliday) {
		//参数 id 状态 未通过原因
		try {
			ServiceTechnicianHoliday holiday = serviceTechnicianHolidayService.get(serviceTechnicianHoliday.getId());
			if ("yes".equals(holiday.getReviewStatus())){
				return new FailResult("该休假已经审核，不可再次审核");
			}
			//审核  增加排期表
			int i = serviceTechnicianHolidayService.reviewedHoliday(serviceTechnicianHoliday);
			//查询休假表
			ServiceTechnicianHoliday serviceTechnicianHoliday1 = serviceTechnicianHolidayService.get(serviceTechnicianHoliday);
			// 查询出技师的手机
            String techId = serviceTechnicianHoliday1.getTechId();
            ServiceTechnicianInfo serviceTechnicianInfo = serviceTechnicianInfoService.get(techId);
            serviceTechnicianHoliday1.setTechPhone(serviceTechnicianInfo.getPhone());
            if (i > 0){
				//发消息 如果是审核通过 带参数techHolidaySuccess 否则techHolidayFail
				if ("yes".equals(serviceTechnicianHoliday1.getReviewStatus())){
					messageInfoService.insertHoliday(serviceTechnicianHoliday1,"techHolidaySuccess");
				}else {
					messageInfoService.insertHoliday(serviceTechnicianHoliday1,"techHolidayFail");
				}
                return new SuccResult("审核成功");
			}else {
				return new FailResult("审核失败");
			}
		}catch (ServiceException e){
			return new FailResult(e.getMessage());
		}
	}

	/**
	 * 审核未通过的休假详情
	 * @param serviceTechnicianHoliday
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("holiday_review")
	@RequestMapping(value = "getHolidayById", method = {RequestMethod.POST})
	@ApiOperation("未通过审核的休假详情")
	public Result getHolidayById(@RequestBody ServiceTechnicianHoliday serviceTechnicianHoliday) {
		ServiceTechnicianHoliday holidayById = serviceTechnicianHolidayService.getHolidayById(serviceTechnicianHoliday);

		return new SuccResult(1,holidayById);
	}
}