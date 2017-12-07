/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.OrderPayInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderReturn;
import com.thinkgem.jeesite.modules.service.entity.order.OrderServiceRelation;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTech;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTechRelation;
import com.thinkgem.jeesite.modules.service.service.order.OrderCustomInfoService;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.modules.service.service.order.OrderPayInfoService;
import com.thinkgem.jeesite.modules.service.service.order.OrderReturnService;
import com.thinkgem.jeesite.modules.service.service.order.OrderServiceRelationService;
import com.thinkgem.jeesite.modules.service.service.order.OrderTechRelationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 订单信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@Api(tags = "订单信息", description = "订单管理相关接口")
@RequestMapping(value = "${adminPath}/service/order/orderInfo")
public class OrderInfoController extends BaseController {

	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private OrderTechRelationService orderTechRelationService;
	@Autowired
	private OrderServiceRelationService orderServiceRelationService;
	@Autowired
	private OrderPayInfoService orderPayInfoService;
	@Autowired
	private OrderReturnService orderReturnService;
	@Autowired
	private OrderCustomInfoService orderCustomInfoService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("订单详情")
	public Result getData(@RequestParam(required=false) String id) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderInfoService.get(id);
		}
        if (entity == null) {
            return new FailResult("未找到对应的订单。");
        } else {
            return new SuccResult(entity);
        }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "saveData", method = {RequestMethod.POST})
	@ApiOperation("保存订单")
	@Transactional
	public Result saveData(@RequestBody OrderInfo orderInfo) {
		List<String> errors = errors(orderInfo);
        if (errors.size() > 0) {
        	return new FailResult(errors);
        }
//		if (!beanValidator(orderInfo)) {
//			return new FailResult("保存订单失败");
//		}
		if (orderInfo.getId() == null || orderInfo.getId().equals("")) {
			if (orderInfo.getOrderItems().size() == 0) {
				return new FailResult("未选择商品");
			}
//			if (orderInfo.getOrderTechs().size() == 0) {
//				return new FailResult("未选择技师");
//			}
		}
		
		//查询订单对应的客户
		if (orderInfo.getCustomId() == null || !orderInfo.getCustomId().equals("")) {
			return new FailResult("找不到该订单的客户信息");
		}
		OrderCustomInfo entity = new OrderCustomInfo(orderInfo.getCustomId());
		OrderCustomInfo customInfo = orderCustomInfoService.get(entity);
		orderInfo.setOfficeId(customInfo.getOfficeId());//机构ID
		orderInfo.setOfficeName(customInfo.getOfficeName());//机构名称
		orderInfo.setStationId(customInfo.getStationId());//服务站ID
		orderInfo.setStationName(customInfo.getStationName());//服务站名称
		orderInfo.setOrderTime(new Date());
		orderInfo.setOrderStatus("2");//订单状态:已派单
		orderInfoService.save(orderInfo);
		
		//保存该订单的支付信息
		OrderPayInfo orderPayInfo = new OrderPayInfo();
		orderPayInfo.setOrderId(orderInfo.getId());
		orderInfo.setPayStatus("1");//初始化支付状态：待支付
		orderPayInfoService.save(orderPayInfo);
		
		//保存订单商品的关联信息
		if (orderInfo.getOrderItems() != null) {
			for (OrderItemCommodity orderItem : orderInfo.getOrderItems()) {
				OrderServiceRelation orderServiceRelation = new OrderServiceRelation(orderItem.getId());
				orderServiceRelation.setItemId(orderItem.getCommodityId());
				orderServiceRelation.setOrderId(orderInfo.getId());
				orderServiceRelation.setOrderNum(orderItem.getOrderNum());
				orderServiceRelationService.save(orderServiceRelation);
			}
		}
		
		//保存订单技师的关联信息
		if (orderInfo.getOrderTechs() != null) {
			for (OrderTech orderTech : orderInfo.getOrderTechs()) {
				OrderTechRelation orderTechRelation = new OrderTechRelation(orderTech.getId());
				orderTechRelation.setOrderId(orderInfo.getId());
				orderTechRelation.setTechId(orderTech.getTechId());
				orderTechRelation.setTechStatus("1");//设置技师当前的状态：派单
				orderTechRelationService.save(orderTechRelation);
			}
		}
		return new SuccResult("保存成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "editData", method = {RequestMethod.POST})
	@ApiOperation("编辑订单（更新服务时间，取消订单等）")
	public Result editData(@RequestParam String orderId, @RequestParam(required = false) String serTime, @RequestParam(required = false) String orderStatus) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderId)){
			entity = orderInfoService.get(orderId);
		}
        if (entity == null) {
            return new FailResult("未找到对应的订单。");
        } else {
        	try {
        		OrderInfo orderInfo = new OrderInfo(orderId);
				if (serTime != null && !serTime.equals("")) {
					orderInfo.setSerTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serTime));
				}
				if (orderStatus != null && !orderStatus.equals("")) {
					//取消订单时，先查询该订单的支付状态是否为已支付
					if (orderStatus.equals("3")) {
						OrderPayInfo payInfo = orderPayInfoService.getByOrderId(orderId);
						//订单已支付
						if (payInfo != null && payInfo.getPayStatus().equals("2")) {
							return new FailResult("该订单已支付，不能取消");
						}
					}
					orderInfo.setOrderStatus(orderStatus);
				}
				orderInfoService.save(orderInfo);
			} catch (ParseException e) {
				e.printStackTrace();
				return new FailResult("数据处理错误！请调整服务时间数据格式");
			}
        }
		return new SuccResult("保存成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST})
	@ApiOperation("获取订单列表")
	public Result listData(@RequestBody(required=false)  OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		if(orderInfo == null){
			orderInfo = new OrderInfo();
		}
		Page<OrderInfo> stationPage = new Page<>(request, response);
		Page<OrderInfo> page = orderInfoService.findPage(stationPage, orderInfo);
		return new SuccResult(page);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "delData", method = {RequestMethod.POST})
	@ApiOperation("删除订单")
	public Result delData(@RequestBody OrderInfo orderInfo) {
		orderInfoService.delete(orderInfo);
		return new SuccResult("删除成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "editItems", method = {RequestMethod.POST})
	@ApiOperation("编辑商品")
	public Result editItems(@RequestBody OrderInfo orderInfo) {
		String id = orderInfo.getId();
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderInfoService.get(id);
		}
        if (entity == null) {
            return new FailResult("未找到对应的订单。");
        } else {
        	for (OrderItemCommodity orderItem : entity.getOrderItems()) {
        		orderServiceRelationService.delete(new OrderServiceRelation(orderItem.getId()));
        	}
        	//保存订单商品的关联信息
    		if (orderInfo.getOrderItems() != null) {
    			for (OrderItemCommodity orderItem : orderInfo.getOrderItems()) {
    				OrderServiceRelation orderServiceRelation = new OrderServiceRelation();
    				orderServiceRelation.setItemId(orderItem.getCommodityId());
    				orderServiceRelation.setOrderId(id);
    				orderServiceRelation.setOrderNum(orderItem.getOrderNum());
    				orderServiceRelationService.save(orderServiceRelation);
    			}
    		}
    		else {
    			return new FailResult("没有商品数据。");
    		}
        }
		return new SuccResult("保存成功");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "editTechs", method = {RequestMethod.POST})
	@ApiOperation("派单或改派")
	public Result editTechs(@RequestParam String orderId, @RequestParam(required = false) String id0, @RequestParam String techIds) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderId)){
			entity = orderInfoService.get(orderId);
		}
		if (entity == null) {
			return new FailResult("未找到对应的订单。");
		} else {
			String orderStatus = entity.getOrderStatus();
			String statusStr = "";
			if (!orderStatus.equals("1") && !orderStatus.equals("2")) {
				switch (orderStatus) {
				case "3":
					statusStr = "已取消";
					break;
				case "4":
					statusStr = "已上门";
					break;
				case "5":
					statusStr = "已完成";
					break;
				case "6":
					statusStr = "已关闭";
					break;

				default:
					statusStr = "状态未知";
					break;
				}
				return new FailResult("该订单不能派单。原因是该订单状态为：" + statusStr + "。");
			}
			if (id0 != null && !id0.equals("")) {
				OrderTechRelation orderTechRelation = new OrderTechRelation(id0);
				orderTechRelation.setTechStatus("2");//设置技师当前的状态：未派单
				orderTechRelationService.updateStatus(orderTechRelation);
			}
			//保存订单技师的关联信息
			if (techIds != null && !techIds.equals("")) {
				for (String techId : techIds.split(",")) {
					OrderTechRelation orderTechRelation = new OrderTechRelation();
					orderTechRelation.setOrderId(orderId);
					orderTechRelation.setTechId(techId);
    				orderTechRelation.setTechStatus("1"); //设置技师当前的状态：派单
					orderTechRelationService.save(orderTechRelation);
				}
			}
			else {
				return new FailResult("没有技师数据。");
			}
			
			//订单状态为未派单时，更新订单状态为已派单
			if (entity.getOrderStatus().equals("1")) {
				OrderInfo orderInfo = new OrderInfo(entity.getId());
				orderInfo.setOrderStatus("2");//更新订单状态：已派单
				orderInfoService.save(orderInfo);
			}
		}
		return new SuccResult("保存成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "getItems", method = {RequestMethod.POST})
	@ApiOperation("查询商品列表")
	public Result getItems() {
		OrderServiceRelation orderServiceRelation = new OrderServiceRelation();
		//获取当前用户的机构
		User user = UserUtils.getUser();
		orderServiceRelation.setOfficeId(user.getOffice().getId());
        List<OrderItemCommodity> items = orderServiceRelationService.findListByOffice(orderServiceRelation);
        return new SuccResult(items);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "getTechs", method = {RequestMethod.POST})
	@ApiOperation("查询技师列表")
	public Result getTechs(@RequestBody OrderTechRelation orderTechRelation, HttpServletRequest request, HttpServletResponse response) {
		Page<OrderTechRelation> stationPage = new Page<>(request, response);
		Page<OrderTechRelation> page = orderTechRelationService.findPage(stationPage, orderTechRelation);
		return new SuccResult(page);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "upPayStatus", method = {RequestMethod.POST})
	@ApiOperation("更新支付状态")
	public Result upPayStatus(@RequestParam String orderId, @RequestParam(required = false) String payId, @RequestParam String payStatus) {
		//查询订单信息
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderId)){
			entity = orderInfoService.get(orderId);
		}
		if (entity == null) {
			return new FailResult("未找到对应的订单。");
		}
		//设置支付状态
		if (payStatus != null && !payStatus.equals("")) {
			OrderPayInfo orderPayInfo = null;
			//查询支付信息
			if (StringUtils.isNotBlank(payId)){
				orderPayInfo = orderPayInfoService.get(payId);
				//不存在支付信息或订单号与支付号不匹配，返回错误信息
				if (orderPayInfo == null || !orderPayInfo.getOrderId().equals(orderId)) {
					return new FailResult("未找到对应订单的支付信息。");
				}
			}
			else {//新建支付信息
				orderPayInfo = new OrderPayInfo();
				orderPayInfo.setOrderId(orderId);
			}
			orderPayInfo.setPayStatus(payStatus);
			orderPayInfoService.save(orderPayInfo);
		}
		else {
			return new FailResult("请输入订单状态。");
		}
		return new SuccResult("保存成功");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "saveReturn", method = {RequestMethod.POST})
	@ApiOperation("生成退款信息")
	public Result saveReturn(@RequestBody OrderReturn orderReturn) {
		//保存该订单的支付信息
		orderReturn.setReturnStatus("1");//初始化退款状态：申请退款中
		orderReturnService.save(orderReturn);
		return new SuccResult("保存成功");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "upReturnStatus", method = {RequestMethod.POST})
	@ApiOperation("更新退款状态")
	public Result upReturnStatus(@RequestParam String orderId, @RequestParam(required = false) String returnId, @RequestParam String returnStatus) {
		//查询订单信息
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderId)){
			entity = orderInfoService.get(orderId);
		}
		if (entity == null) {
			return new FailResult("未找到对应的订单。");
		}
		//设置支付状态
		if (returnStatus != null && !returnStatus.equals("")) {
			OrderReturn orderReturn = null;
			//查询支付信息
			if (StringUtils.isNotBlank(returnId)){
				orderReturn = orderReturnService.get(returnId);
				//不存在支付信息或订单号与支付号不匹配，返回错误信息
				if (orderReturn == null || !orderReturn.getOrderId().equals(orderId)) {
					return new FailResult("未找到对应订单的退款信息。");
				}
			}
			else {//新建支付信息
				orderReturn = new OrderReturn();
				orderReturn.setOrderId(orderId);
			}
			orderReturn.setReturnStatus(returnStatus);
			orderReturnService.save(orderReturn);
		}
		else {
			return new FailResult("请输入退款状态。");
		}
		return new SuccResult("保存成功");
	}
	

}