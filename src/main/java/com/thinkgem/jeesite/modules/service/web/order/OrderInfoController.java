/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import java.util.Date;

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
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.OrderServiceRelation;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTech;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTechRelation;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
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
		if (!beanValidator(orderInfo)) {
			return new FailResult("保存订单失败");
		}
		if (orderInfo.getId() == null || orderInfo.getId().equals("")) {
			if (orderInfo.getOrderItems().size() == 0) {
				return new FailResult("未选择商品");
			}
			if (orderInfo.getOrderTechs().size() == 0) {
				return new FailResult("未选择技师");
			}
		}
		User user = UserUtils.getUser();
		orderInfo.setOfficeId(user.getOffice().getId());//机构ID
		orderInfo.setOfficeName(user.getOffice().getName());//机构名称
		orderInfo.setStationId(user.getStation().getId());//服务站ID
		orderInfo.setStationName(user.getStation().getName());//服务站名称
		orderInfo.setOrderTime(new Date());
//		orderInfo.setStatus("0");//订单状态
		orderInfoService.save(orderInfo);
		
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
//				orderTechRelation.setStatus(0); //设置技师当前的状态
				orderTechRelationService.save(orderTechRelation);
			}
		}
		
		return new SuccResult("保存订单成功");
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
		return new SuccResult("删除订单成功");
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
		return new SuccResult("编辑商品成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "editTech", method = {RequestMethod.POST})
	@ApiOperation("编辑技师")
	public Result editTech(@RequestParam String orderId, @RequestParam String id0, @RequestParam String techId) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(orderId)){
			entity = orderInfoService.get(orderId);
		}
        if (entity == null) {
            return new FailResult("未找到对应的订单。");
        } else {
        	if (id0 != null && !id0.equals("")) {
        		orderTechRelationService.delete(new OrderTechRelation(id0));
        	}
        	OrderTechRelation orderTechRelation = new OrderTechRelation();
			orderTechRelation.setOrderId(orderId);
			orderTechRelation.setTechId(techId);
//			orderTechRelation.setStatus(0); //设置技师当前的状态
			orderTechRelationService.save(orderTechRelation);
        }
		return new SuccResult("编辑技师成功");
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@ResponseBody
//	@RequestMapping(value = "editTechs", method = {RequestMethod.POST})
//	@ApiOperation("编辑技师")
//	public Result editTechs(@RequestBody OrderInfo orderInfo) {
//		String id = orderInfo.getId();
//		OrderInfo entity = null;
//		if (StringUtils.isNotBlank(id)){
//			entity = orderInfoService.get(id);
//		}
//		if (entity == null) {
//			return new FailResult("未找到对应的订单。");
//		} else {
//			for (OrderTech orderTech : entity.getOrderTechs()) {
//				orderTechRelationService.delete(new OrderTechRelation(orderTech.getId()));
//			}
//			//保存订单技师的关联信息
//			if (orderInfo.getOrderTechs() != null) {
//				for (OrderTech orderTech : orderInfo.getOrderTechs()) {
//					OrderTechRelation orderTechRelation = new OrderTechRelation();
//					orderTechRelation.setOrderId(id);
//					orderTechRelation.setTechId(orderTech.getTechId());
////    				orderTechRelation.setStatus(0); //设置技师当前的状态
//					orderTechRelationService.save(orderTechRelation);
//				}
//			}
//			else {
//				return new FailResult("没有技师数据。");
//			}
//		}
//		return new SuccResult("编辑技师成功");
//	}
}