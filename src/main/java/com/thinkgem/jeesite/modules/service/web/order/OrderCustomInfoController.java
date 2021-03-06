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
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomAddress;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderCustomInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * 用户信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@Api(tags = "用户信息", description = "用户信息相关接口")
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
	@RequiresPermissions("customer_insert")
	@ApiOperation("新增保存用户")
	public Result saveData(@RequestBody OrderCustomInfo orderCustomInfo) {
		List<String> errList = errors(orderCustomInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		if(StringUtils.isBlank(orderCustomInfo.getId())) {
			if (StringUtils.isEmpty(orderCustomInfo.getOrgId())) {
				User user = UserUtils.getUser();
				orderCustomInfo.setOrgId(user.getOrganization().getId());//机构ID
			}
			orderCustomInfo.setSource("own");// 来源   本机构:own    第三方:other
		}
		/*
		//计算服务站
		//用户经纬度
		if(StringUtils.isNotBlank(orderCustomInfo.getAddrLatitude()) && StringUtils.isNotBlank(orderCustomInfo.getAddrLongitude())){
			List<ServiceStation> stations = orderCustomInfoService.getStationsByOrgId(orderCustomInfo.getOrgId());//用户机构下的服务站
			if(null != stations){
				for (ServiceStation station : stations){
					if(StringUtils.isNotBlank(station.getServicePoint())){//服务站范围
						// 判断当前位置是否在多边形区域内
						if(MapUtils.isInPolygon(orderCustomInfo.getAddrLatitude(),orderCustomInfo.getAddrLongitude(),station.getServicePoint())){
							orderCustomInfo.setStationId(station.getId());
							break;
						}
					}
				}
				if(StringUtils.isBlank(orderCustomInfo.getStationId())){
					return new FailResult("该地点没有合适服务站");
				}
			}
		}*/
		List<OrderCustomInfo> techInfoList=orderCustomInfoService.findCusList(orderCustomInfo);
		if (null == techInfoList||techInfoList.size()==0) {
			orderCustomInfoService.save(orderCustomInfo);
			return new SuccResult("保存成功");
		}else {
			return new FailResult("用户手机号不能重复！");
		}
	}

	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
	@RequiresPermissions("customer_view")
	@ApiOperation("获取用户列表")
	public Result listData(@RequestBody(required=false)  OrderCustomInfo orderCustomInfo, HttpServletRequest request, HttpServletResponse response) {
		if(orderCustomInfo == null){
			orderCustomInfo = new OrderCustomInfo();
		}
		Page<OrderCustomInfo> stationPage = new Page<>(request, response);
		Page<OrderCustomInfo> page = orderCustomInfoService.findPage(stationPage, orderCustomInfo);
		HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
		objectObjectHashMap.put("page",page);
		List<BasicOrganization> basicOrganizationList = orderCustomInfoService.findOrganizationList();
		objectObjectHashMap.put("orgList",basicOrganizationList);
		return new SuccResult(objectObjectHashMap);
	}

	@ResponseBody
	@RequiresPermissions("customer_delete")
	@RequestMapping(value = "deleteSortInfo", method = {RequestMethod.POST})
	@ApiOperation("删除用户")
	public Result deleteSortInfo(@RequestBody OrderCustomInfo orderCustomInfo) {
		orderCustomInfoService.delete(orderCustomInfo);
		return new SuccResult("删除用户成功");
	}





	@ResponseBody
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation("用户编辑")
	public Result formData(@RequestBody OrderCustomInfo orderCustomInfo) {
		OrderCustomInfo entity = null;
		if (StringUtils.isNotBlank(orderCustomInfo.getId())) {
			entity = orderCustomInfoService.formData(orderCustomInfo);
		}
		if (entity == null) {
			return new FailResult("未找到此id对应的用户。");
		} else {
			return new SuccResult(entity);
		}
	}

	@ResponseBody
	@RequestMapping(value = "upData", method = { RequestMethod.POST })
	@RequiresPermissions("customer_update")
	@ApiOperation(value = "更新保存用户")
	public Result upData(@RequestBody OrderCustomInfo orderCustomInfo) {
		List<String> errList = errors(orderCustomInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		if (StringUtils.isEmpty(orderCustomInfo.getOrgId())) {
			User user = UserUtils.getUser();
			orderCustomInfo.setOrgId(user.getOrganization().getId());//机构ID
		}
		orderCustomInfo.setSource("own");// 来源   本机构:own    第三方:other
		List<OrderCustomInfo> techInfoList=orderCustomInfoService.findCusList(orderCustomInfo);
		if (null == techInfoList||techInfoList.size()==0) {
			orderCustomInfoService.save(orderCustomInfo);
			return new SuccResult("保存成功");
		}else {
			return new FailResult("用户手机号不能重复！");
		}
	}




	@ResponseBody
	@RequestMapping(value = "listDataAddress", method = {RequestMethod.POST, RequestMethod.GET})
	@RequiresPermissions("customer_view")
	public Result listDataAddress(@RequestBody(required=false) OrderCustomAddress customAddress, HttpServletRequest request, HttpServletResponse response) {
		List<OrderCustomAddress> list = orderCustomInfoService.listCustomAddress(customAddress);
		return new SuccResult(list);
	}
	@ResponseBody
	@RequestMapping(value = "saveDataAddress", method = {RequestMethod.POST})
	@RequiresPermissions("customer_view")
	public Result saveDataAddress(@RequestBody OrderCustomAddress customAddress) {
		orderCustomInfoService.saveDataAddress(customAddress);
		return new SuccResult("保存成功");
	}
	@ResponseBody
	@RequestMapping(value = "formDataAddress", method = {RequestMethod.POST})
	public Result formDataAddress(@RequestBody OrderCustomAddress customAddress) {
		OrderCustomAddress entity = orderCustomInfoService.formDataAddress(customAddress);
		return new SuccResult(entity);
	}
	@ResponseBody
	@RequestMapping(value = "upDataAddress", method = {RequestMethod.POST})
	@RequiresPermissions("customer_view")
	public Result upDataAddress(@RequestBody OrderCustomAddress customAddress) {
		orderCustomInfoService.upDataAddress(customAddress);
		return new SuccResult("保存成功");
	}
	@ResponseBody
	@RequiresPermissions("customer_view")
	@RequestMapping(value = "deleteDataAddress", method = {RequestMethod.POST})
	public Result deleteDataAddress(@RequestBody OrderCustomAddress customAddress) {
		orderCustomInfoService.deleteDataAddress(customAddress);
		return new SuccResult("删除成功");
	}
	@ResponseBody
	@RequestMapping(value = "setDefaultAddress", method = {RequestMethod.POST})
	@RequiresPermissions("customer_view")
	public Result setDefaultAddress(@RequestBody OrderCustomAddress customAddress) {
		orderCustomInfoService.setDefaultAddress(customAddress);
		return new SuccResult("保存成功");
	}

}