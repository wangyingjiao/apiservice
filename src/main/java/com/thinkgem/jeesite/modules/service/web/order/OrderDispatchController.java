/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
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
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.service.order.OrderDispatchService;

import java.util.HashMap;
import java.util.List;

/**
 * 派单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderDispatch")
public class OrderDispatchController extends BaseController {
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private OrderDispatchService orderDispatchService;
	
	@ModelAttribute
	public OrderDispatch get(@RequestParam(required=false) String id) {
		OrderDispatch entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderDispatchService.get(id);
		}
		if (entity == null){
			entity = new OrderDispatch();
		}
		return entity;
	}

	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取改派列表")
	@RequiresPermissions("dispatch_view")
	public Result listData(@RequestBody(required = false) OrderInfo dispatchInfo, HttpServletRequest request, HttpServletResponse response) {
		if(null == dispatchInfo){
			dispatchInfo = new OrderInfo();
		}
		Page<OrderInfo> dispatchInfoPage = new Page<>(request, response);
		//add by wyr 更改改派管理列表的总条数显示为订单的条数
		User user = UserUtils.getUser();
		dispatchInfo.setOrgId(user.getOrganization().getId());// 机构ID
		//Long count =orderDispatchService.findOrderCount(dispatchInfoPage, dispatchInfo);
		//dispatchInfoPage.setCount(count);
				
				
		Page<OrderInfo> data = orderDispatchService.findPage(dispatchInfoPage, dispatchInfo);
		//data.setCount(count);
		return new SuccResult(data);
	}

	@ResponseBody
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation("查看订单改派记录")
	@RequiresPermissions("dispatch_info")
	public Result formData(@RequestBody OrderDispatch dispatchInfo) {
		List<OrderDispatch> list = orderDispatchService.formData(dispatchInfo);
		return new SuccResult(list);
	}

	@ResponseBody
	@RequestMapping(value = "dispatchTech", method = {RequestMethod.POST})
	@ApiOperation("技师改派")
	@RequiresPermissions("dispatch_insert")
	public Result dispatchTech(@RequestBody OrderInfo orderInfo) {
		List<OrderDispatch> techList = orderInfoService.addTech(orderInfo);
		return new SuccResult(techList);
	}

	@ResponseBody
	@RequestMapping(value = "dispatchTechSave", method = {RequestMethod.POST})
	@ApiOperation("技师改派保存")
	@RequiresPermissions("dispatch_insert")
	public Result dispatchTechSave(@RequestBody OrderInfo orderInfo) {
		HashMap<String,Object> map = orderInfoService.dispatchTechSave(orderInfo);
		return new SuccResult(map.get("list"));
	}
}