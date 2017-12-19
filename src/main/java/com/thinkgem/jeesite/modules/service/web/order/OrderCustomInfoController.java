/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderCustomInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@Api(tags = "客户信息", description = "客户信息相关接口")
@RequestMapping(value = "${adminPath}/service/order/orderCustomInfo")
public class OrderCustomInfoController extends BaseController {

	@Autowired
	private OrderCustomInfoService orderCustomInfoService;
	
	@ModelAttribute
	public OrderCustomInfo get(@RequestParam(required=false) String id) {
		OrderCustomInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderCustomInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderCustomInfo();
		}
		return entity;
	}


	@ResponseBody
	@RequestMapping(value = "saveData", method = {RequestMethod.POST})
	//@RequiresPermissions("service:station:orderCustomInfo:edit")
	@ApiOperation("新增保存客户")
	public Result saveData(@RequestBody OrderCustomInfo orderCustomInfo) {
		List<String> errList = errors(orderCustomInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		if(StringUtils.isNotBlank(orderCustomInfo.getId())) {
			User user = UserUtils.getUser();
			orderCustomInfo.setOrgId(user.getOrganization().getId());//机构ID
			orderCustomInfo.setStationId(user.getStation().getId());//服务站ID
			orderCustomInfo.setSource("own");// 来源   本机构:own    第三方:other
		}
		orderCustomInfoService.save(orderCustomInfo);
		return new SuccResult("保存成功");
	}

	@ResponseBody
	//@RequiresPermissions("service:station:orderCustomInfo:edit")
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation("编辑客户")
	public Result formData(@RequestBody OrderCustomInfo orderCustomInfo) {
		OrderCustomInfo entity = null;
		if (StringUtils.isNotBlank(orderCustomInfo.getId())) {
			entity = orderCustomInfoService.get(orderCustomInfo.getId());
		}
		if (entity == null) {
			return new FailResult("未找到此id对应的客户。");
		} else {
			return new SuccResult(entity);
		}
	}

	@ResponseBody
	@RequestMapping(value = "upData", method = {RequestMethod.POST})
	//@RequiresPermissions("service:station:orderCustomInfo:edit")
	@ApiOperation("编辑保存客户")
	public Result upData(@RequestBody OrderCustomInfo orderCustomInfo) {
		List<String> errList = errors(orderCustomInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		orderCustomInfoService.save(orderCustomInfo);
		return new SuccResult("保存成功");
	}
	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取客户列表")
	public Result listData(@RequestBody(required=false)  OrderCustomInfo orderCustomInfo, HttpServletRequest request, HttpServletResponse response) {
		if(orderCustomInfo == null){
			orderCustomInfo = new OrderCustomInfo();
		}
		Page<OrderCustomInfo> stationPage = new Page<>(request, response);
		Page<OrderCustomInfo> page = orderCustomInfoService.findPage(stationPage, orderCustomInfo);
		return new SuccResult(page);
	}

	@ResponseBody
	//@RequiresPermissions("service:station:orderCustomInfo:edit")
	@RequestMapping(value = "deleteSortInfo", method = {RequestMethod.POST})
	@ApiOperation("删除客户")
	public Result deleteSortInfo(@RequestBody OrderCustomInfo orderCustomInfo) {
		orderCustomInfoService.delete(orderCustomInfo);
		return new SuccResult("删除客户成功");
	}

}