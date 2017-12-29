/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
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
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;

import java.util.HashMap;
import java.util.List;

/**
 * 子订单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderInfo")
public class OrderInfoController extends BaseController {

	@Autowired
	private OrderInfoService orderInfoService;
	
	@ModelAttribute
	public OrderInfo get(@RequestParam(required=false) String id) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderInfo();
		}
		return entity;
	}

	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取订单列表")
	//@RequiresPermissions("order")
	public Result listData(@RequestBody(required = false) OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		if(null == orderInfo){
			orderInfo = new OrderInfo();
		}
		Page<OrderInfo> orderInfoPage = new Page<>(request, response);
		Page<OrderInfo> page = orderInfoService.findPage(orderInfoPage, orderInfo);
		HashMap data = new HashMap();
		data.put("page",page);
		List<BasicOrganization> basicOrganizationList = orderInfoService.findOrganizationList();
		data.put("orgList",basicOrganizationList);
		return new SuccResult(data);
	}

	@ResponseBody
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation("查看订单")
	public Result formData(@RequestBody OrderInfo orderInfo) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderInfo.getId())) {
			entity = orderInfoService.formData(orderInfo);
		}
		if (entity == null) {
			return new FailResult("未找到此id对应的订单");
		} else {
			HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
			objectObjectHashMap.put("info",entity);
			List<ServiceTechnicianWorkTime>  serviceTimeList = orderInfoService.findServiceTimeList(orderInfo);
			objectObjectHashMap.put("serviceTimeList",serviceTimeList);
			return new SuccResult(objectObjectHashMap);
		}
	}
	@ResponseBody
	@RequestMapping(value = "cancelData", method = {RequestMethod.POST})
	@ApiOperation("取消订单")
	public Result cancelData(@RequestBody OrderInfo orderInfo) {
		orderInfoService.cancelData(orderInfo);
		return new SuccResult("取消订单成功");
	}

	@ResponseBody
	@RequestMapping(value = "saveTime", method = {RequestMethod.POST})
	@ApiOperation("更换时间保存")
	public Result saveTime(@RequestBody OrderInfo orderInfo) {
		orderInfoService.saveTime(orderInfo);
		return new SuccResult("更换时间成功");
	}

	@ResponseBody
	@RequestMapping(value = "editGoodsInit", method = {RequestMethod.POST})
	@ApiOperation("服务项目编辑")
	public Result editGoodsInit(@RequestBody OrderInfo orderInfo) {
		List<OrderGoods> goodsInfoList = orderInfoService.editGoodsInit(orderInfo);
		return new SuccResult(goodsInfoList);
	}

	@ResponseBody
	@RequestMapping(value = "editGoodsSave", method = {RequestMethod.POST})
	//@RequiresPermissions("order_insert")
	@ApiOperation("服务项目编辑保存")
	public Result editGoodsSave(@RequestBody OrderInfo orderInfo) {
		List<String> errList = errors(orderInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		orderInfoService.save(orderInfo);
		return new SuccResult("保存成功");
	}


	@ResponseBody
	@RequestMapping(value = "saveData", method = {RequestMethod.POST})
	@RequiresPermissions("order_insert")
	@ApiOperation("保存订单")
	public Result saveData(@RequestBody OrderInfo orderInfo) {
		List<String> errList = errors(orderInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		orderInfoService.save(orderInfo);
		return new SuccResult("保存成功");
	}

}