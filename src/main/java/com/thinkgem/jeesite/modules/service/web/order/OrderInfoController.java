/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.service.order.*;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;

import java.util.*;

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
	@Autowired
	private OrderInfoCreateService orderInfoCreateService;
	@Autowired
	private OrderInfoOperateService orderInfoOperateService;
	@Autowired
	private MessageInfoService messageInfoService;
	@Autowired
	private OrderPayInfoService orderPayInfoService;
	@Autowired
	private OrderRefundService orderRefundService;
	
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
	@RequiresPermissions("order_view")
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
	@RequiresPermissions("order_info")
	public Result formData(@RequestBody OrderInfo orderInfo) {
		OrderInfo entity = null;
		try {
			if (StringUtils.isNotBlank(orderInfo.getId())) {
				entity = orderInfoService.formData(orderInfo);
			}
			if (entity == null) {
				return new FailResult("未找到此id对应的订单");
			} else {
				return new SuccResult(entity);
			}
		}catch (ServiceException ex){
			return new FailResult("查看失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("未找到订单详情!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "timeDataList", method = {RequestMethod.POST})
	@ApiOperation("更换时间")
	@RequiresPermissions("order_time")
	public Result timeDataList(@RequestBody OrderInfo orderInfo) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
		if(!flag){
			return new FailResult("当前订单状态或服务状态不允许操作此项内容");
		}
		boolean fullFlag = orderInfoOperateService.checkOrderFullGoods(orderInfo);
		if(!fullFlag){
			return new FailResult("当前订单只有补单商品,不允许操作此项内容");
		}

		try {
			List<OrderTimeList>  timeList = orderInfoOperateService.timeDataList(orderInfo);
			return new SuccResult(timeList);
		}catch (ServiceException ex){
			return new FailResult("获取时间列表失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("获取时间列表失败!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "saveTime", method = {RequestMethod.POST})
	@ApiOperation("更换时间保存")
	@RequiresPermissions("order_time")
	public Result saveTime(@RequestBody OrderInfo orderInfo) {
		try {
			HashMap<String,Object> map = orderInfoOperateService.saveTime(orderInfo);

			try {
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(map.get("orderId").toString());
					sendOrder.setOrderNumber(orderSn);//订单编号

					//sendOrder.setId(map.get("orderId").toString());//订单ID
					sendOrder.setServiceTime((Date) map.get("serviceDate"));//上门服务时间
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendUtil.openSendSaveOrder(sendOrder);
					/*OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						logger.error("更换时间保存-对接失败-返回值为空");
					} else if (sendResponse.getCode() != 0) {
						logger.error("更换时间保存-对接失败-"+sendResponse.getMessage());
					}*/
				}
			}catch (Exception e){
				logger.error("更换时间保存-对接失败-系统异常");
			}

			try{
				// 派单
				List<OrderDispatch> orderCreateMsgList = (List<OrderDispatch>)map.get("orderCreateMsgList");
				// 改派
				List<OrderDispatch> orderDispatchMsgList = (List<OrderDispatch>)map.get("orderDispatchMsgList");
				// 时间变化
				List<OrderDispatch> orderServiceTimeMsgList = (List<OrderDispatch>)map.get("orderServiceTimeMsgList");
				String orderNumber = (String)map.get("orderNumber");
				String orderId = (String)map.get("orderId");

				OrderInfo orderInfo1 = new OrderInfo();
				OrderInfo orderInfo2 = new OrderInfo();
				OrderInfo orderInfo3 = new OrderInfo();

				orderInfo1.setOrderNumber(orderNumber);
				orderInfo2.setOrderNumber(orderNumber);
				orderInfo3.setOrderNumber(orderNumber);

				orderInfo1.setId(orderId);
				orderInfo2.setId(orderId);
				orderInfo3.setId(orderId);

				orderInfo1.setTechList(orderCreateMsgList);
				orderInfo2.setTechList(orderDispatchMsgList);
				orderInfo3.setTechList(orderServiceTimeMsgList);
				//时间
				orderInfo3.setServiceTime((Date)map.get("serviceDate"));

				User user = UserUtils.getUser();
				orderInfo1.setCreateBy(user);
				orderInfo2.setCreateBy(user);
				orderInfo3.setCreateBy(user);

				messageInfoService.insert(orderInfo1,"orderCreate");//新增
				messageInfoService.insert(orderInfo2,"orderDispatch");//改派
				messageInfoService.insert(orderInfo3,"orderServiceTime");//服务时间变更
			}catch (Exception e){
				logger.error("更换时间保存-推送消息失败-系统异常");
			}

			return new SuccResult(map);
		}catch (ServiceException ex){
			return new FailResult("保存失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("保存失败!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "addTech", method = {RequestMethod.POST})
	@ApiOperation("增加技师")
	@RequiresPermissions("order_addTech")
	public Result addTech(@RequestBody OrderInfo orderInfo) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
		if(!flag){
			return new FailResult("当前订单状态或服务状态不允许操作此项内容");
		}
		boolean fullFlag = orderInfoOperateService.checkOrderFullGoods(orderInfo);
		if(!fullFlag){
			return new FailResult("当前订单只有补单商品,不允许操作此项内容");
		}

		try{
			List<OrderDispatch> techList = orderInfoOperateService.addTech(orderInfo);
			return new SuccResult(techList);
		}catch (ServiceException ex){
			return new FailResult("获取技师列表失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("当前没有可服务的技师!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "addTechSave", method = {RequestMethod.POST})
	@ApiOperation("增加技师保存")
	@RequiresPermissions("order_addTech")
	public Result addTechSave(@RequestBody OrderInfo orderInfo) {
		try{
			HashMap<String,Object> map = orderInfoOperateService.addTechSave(orderInfo);

			try {//对接
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(map.get("orderId").toString());
					sendOrder.setOrderNumber(orderSn);//订单编号

					//sendOrder.setId(map.get("orderId").toString());//订单ID
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendUtil.openSendSaveOrder(sendOrder);
					/*OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						logger.error("增加技师保存-对接失败-返回值为空");
					} else if (sendResponse.getCode() != 0) {
						logger.error("增加技师保存-对接失败-"+sendResponse.getMessage());
					}*/
				}
			}catch (Exception e){
				logger.error("增加技师保存-对接失败-系统异常");
			}

			try{
				// 派单
				List<OrderDispatch> orderCreateMsgList = (List<OrderDispatch>)map.get("orderCreateMsgList");
				String orderNumber = (String)map.get("orderNumber");
				String orderId = (String)map.get("orderId");
				OrderInfo orderInfo1 = new OrderInfo();
				orderInfo1.setOrderNumber(orderNumber);
				orderInfo1.setId(orderId);
				orderInfo1.setTechList(orderCreateMsgList);

				User user = UserUtils.getUser();
				orderInfo1.setCreateBy(user);

				messageInfoService.insert(orderInfo1,"orderCreate");//新增
			}catch (Exception e){
				logger.error("增加技师保存-推送消息失败-系统异常");
			}

			return new SuccResult(map);
		}catch (ServiceException ex){
			return new FailResult("保存失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("保存失败!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "dispatchTech", method = {RequestMethod.POST})
	@ApiOperation("技师改派")
	@RequiresPermissions("order_dispatch")
	public Result dispatchTech(@RequestBody OrderInfo orderInfo) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
		if(!flag){
			return new FailResult("当前订单状态或服务状态不允许操作此项内容");
		}
		boolean fullFlag = orderInfoOperateService.checkOrderFullGoods(orderInfo);
		if(!fullFlag){
			return new FailResult("当前订单只有补单商品,不允许操作此项内容");
		}

		try{
			List<OrderDispatch> techList = orderInfoOperateService.addTech(orderInfo);
			return new SuccResult(techList);
		}catch (ServiceException ex){
			return new FailResult("获取技师列表失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("当前没有可服务的技师!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "dispatchTechSave", method = {RequestMethod.POST})
	@ApiOperation("技师改派保存")
	@RequiresPermissions("order_dispatch")
	public Result dispatchTechSave(@RequestBody OrderInfo orderInfo) {
		try{
			HashMap<String,Object> map = orderInfoOperateService.dispatchTechSave(orderInfo);

			try {
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(map.get("orderId").toString());
					sendOrder.setOrderNumber(orderSn);//订单编号

					//sendOrder.setId(map.get("orderId").toString());//订单ID
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendUtil.openSendSaveOrder(sendOrder);
					/*OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						logger.error("技师改派保存-对接失败-返回值为空");
					} else if (sendResponse.getCode() != 0) {
						logger.error("技师改派保存-对接失败-"+sendResponse.getMessage());
					}*/
				}
			}catch (Exception e){
				logger.error("技师改派保存-对接失败-系统异常");
			}

			try{
				// 派单
				List<OrderDispatch> orderCreateMsgList = (List<OrderDispatch>)map.get("orderCreateMsgList");
				// 改派
				List<OrderDispatch> orderDispatchMsgList = (List<OrderDispatch>)map.get("orderDispatchMsgList");
				String orderNumber = (String)map.get("orderNumber");
				String orderId = (String)map.get("orderId");

				OrderInfo orderInfo1 = new OrderInfo();
				OrderInfo orderInfo2 = new OrderInfo();

				orderInfo1.setOrderNumber(orderNumber);
				orderInfo2.setOrderNumber(orderNumber);

				orderInfo1.setId(orderId);
				orderInfo2.setId(orderId);

				orderInfo1.setTechList(orderCreateMsgList);
				orderInfo2.setTechList(orderDispatchMsgList);

				User user = UserUtils.getUser();
				orderInfo1.setCreateBy(user);
				orderInfo2.setCreateBy(user);

				messageInfoService.insert(orderInfo1,"orderCreate");//新增
				messageInfoService.insert(orderInfo2,"orderDispatch");//改派
			}catch (Exception e){
				logger.error("技师改派保存-推送消息失败-系统异常");
			}
			return new SuccResult(map);
		}catch (ServiceException ex){
			return new FailResult("保存失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("保存失败-系统异常");
		}
	}


	/**
	 * 订单创建
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "createOrder", method = {RequestMethod.POST})
	public Result createOrder(@RequestBody OrderInfo info) {
		try {
			HashMap<String,Object> map = orderInfoCreateService.createOrder(info);
			//OpenCreateResponse responseRe = (OpenCreateResponse)map.get("response");

			try {
				OrderInfo orderInfo = (OrderInfo)map.get("orderInfoMsg");
				orderInfo.setCreateBy(UserUtils.getUser());
				messageInfoService.insert(orderInfo, "orderCreate");//新增
			}catch (Exception e){
				logger.error("订单创建-推送消息失败-系统异常");
			}

			return new SuccResult(map);
		}catch (ServiceException ex){
			return new FailResult("订单创建失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("订单创建失败-系统异常");
		}
	}


	/**
	 * 根据ID查找客户(id)
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "findCustomerById", method = {RequestMethod.POST})
	public Result findCustomerById(@RequestBody OrderCustomInfo info) {
		try {
			OrderCustomInfo customInfo = orderInfoCreateService.findCustomerById(info);
			if(customInfo == null){
				return new SuccResult(3,"未找到客户信息");
			}
			return new SuccResult(customInfo);
		}catch (Exception e){
			return new FailResult("未找到客户信息");
		}
	}
	/**
	 * 根据手机号查找客户(phone)
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "findCustomerByPhone", method = {RequestMethod.POST})
	public Result findCustomerByPhone(@RequestBody OrderCustomInfo info) {
		try {
			OrderCustomInfo customInfo = orderInfoCreateService.findCustomerByPhone(info);
			if(customInfo == null){
				return new SuccResult(3,"未找到客户信息");
			}
			return new SuccResult(customInfo);
		}catch (Exception e){
			return new FailResult("未找到客户信息");
		}
	}
	/**
	 * 获取服务项目列表
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "findItemList", method = {RequestMethod.POST})
	public Result findItemList(@RequestBody OrderInfo info) {
		try {
			List<OrderDropdownInfo> list = orderInfoCreateService.findItemList(info);
			return new SuccResult(list);
		}catch (Exception e){
			return new SuccResult(new ArrayList<OrderDropdownInfo>());
		}
	}
	/**
	 * 获取服务项目下的商品列表(itemId)
	 * @param info(itemId)
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "findGoodsListByItem", method = {RequestMethod.POST})
	public Result findGoodsListByItem(@RequestBody OrderGoods info) {
		try {
			List<OrderGoods> list = orderInfoCreateService.findGoodsListByItem(info);
			if(list == null || list.size()==0){
				return new FailResult(3,"未找到商品信息");
			}
			return new SuccResult(list);
		}catch (Exception e){
			return new SuccResult(new ArrayList<OrderGoods>());
		}
	}
	/**
	 * 获取商品的技师列表
	 * @param info(goodsList,stationId)
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("order_insert")
	@RequestMapping(value = "findTechListByGoods", method = {RequestMethod.POST})
	public Result findTechListByGoods(@RequestBody OrderInfo info) {
		try {
			List<OrderDispatch> list = orderInfoCreateService.findTechListByGoods(info);

			return new SuccResult(list);
		}catch (Exception e){
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}
	/**
	 * 获取技师的时间列表
	 * @param info(goodsList,stationId,tech(null))
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "findTimeListByTech", method = {RequestMethod.POST})
	@RequiresPermissions("order_insert")
	public Result findTimeListByTech(@RequestBody OrderInfo info) {
		try {
			List<OrderTimeList>  list = orderInfoCreateService.findTimeListByTech(info);

			return new SuccResult(list);
		}catch (Exception e){
			return new FailResult("获取时间列表失败!");
		}
	}

	/**
	 * 获取商品所需人数和时间
	 * @param info(goodsList)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "findGoodsNeedTech", method = {RequestMethod.POST})
	@RequiresPermissions("order_insert")
	public Result findGoodsNeedTech(@RequestBody OrderInfo info) {
		try {
			Map<String,String> list = orderInfoCreateService.findGoodsNeedTech(info);
			return new SuccResult(list);
		}catch (Exception e){
			return new FailResult("获取提示信息失败!");
		}
	}

	/**
	 * 取消订单
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "orderCancel", method = {RequestMethod.POST})
	@RequiresPermissions("order_cancel")
	public Result orderCancel(@RequestBody OrderInfo info) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderCancelStatus(info);
		if(flag){
			return new FailResult("当前订单状态不允许取消订单");
		}

		try{
			HashMap<String,Object>  map = orderInfoOperateService.orderCancel(info);
			if(map.get("list") != null) {
				try {
					List<OrderDispatch> orderCancelMsgList = (List<OrderDispatch>) map.get("list");
					OrderInfo orderInfo = (OrderInfo) map.get("info");
					String orderNumber = orderInfo.getOrderNumber();
					String orderId = orderInfo.getId();

					OrderInfo orderInfo1 = new OrderInfo();
					orderInfo1.setOrderNumber(orderNumber);
					orderInfo1.setId(orderId);
					orderInfo1.setTechList(orderCancelMsgList);
					User user = UserUtils.getUser();
					orderInfo1.setCreateBy(user);

					messageInfoService.insert(orderInfo1, "orderCancel");//取消
				} catch (Exception e) {
					logger.error("取消订单-推送消息失败-系统异常");
				}

			}
			return new SuccResult("取消订单成功");
		}catch (ServiceException ex){
			return new FailResult(ex.getMessage());
		}catch (Exception e){
			return new FailResult("取消订单失败!");
		}
	}

	/**
	 * 退款订单
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "orderRefundInit", method = {RequestMethod.POST})
	@RequiresPermissions("order_refund")
	public Result orderRefundInit(@RequestBody OrderInfo info) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderRefundStatus(info);
		if(flag){
			return new FailResult("当前订单状态不允许退款");
		}

		OrderInfo orderInfo = orderInfoOperateService.orderRefundInit(info);
		return new SuccResult(orderInfo);
	}

	/**
	 * 退款订单
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "orderRefundSave", method = {RequestMethod.POST})
	@RequiresPermissions("order_refund")
	public Result orderRefundSave(@RequestBody OrderInfo info) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderRefundStatus(info);
		if(flag){
			return new FailResult("当前订单状态不允许退款");
		}
		// 判断订单商品是否已退
		boolean flag2 = orderInfoOperateService.checkOrderRefundGoods(info);
		if(!flag2){
			return new FailResult("当前商品状态不允许退款");
		}

		try{
			HashMap<String,Object>  map = orderInfoOperateService.orderRefundSave(info);
			return new SuccResult("退款成功");
		}catch (ServiceException ex){
			return new FailResult(ex.getMessage());
		}catch (Exception e){
			return new FailResult("退款失败!");
		}
	}

	@ResponseBody
	@RequestMapping(value = "listDataPay", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取订单收款列表")
	@RequiresPermissions("pay_view")
	public Result listDataPay(@RequestBody(required = false) OrderPayInfo orderPayInfo, HttpServletRequest request, HttpServletResponse response) {
		if(null == orderPayInfo){
			orderPayInfo = new OrderPayInfo();
		}
		Page<OrderPayInfo> orderPayInfoPage = new Page<>(request, response);
		Page<OrderPayInfo> page = orderPayInfoService.findPage(orderPayInfoPage, orderPayInfo);
		return new SuccResult(page);
	}

	@ResponseBody
	@RequestMapping(value = "listDataRefund", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取订单退款列表")
	@RequiresPermissions("refund_view")
	public Result listDataRefund(@RequestBody(required = false) OrderRefund orderRefund, HttpServletRequest request, HttpServletResponse response) {
		if(null == orderRefund){
			orderRefund = new OrderRefund();
		}
		Page<OrderRefund> orderRefundPage = new Page<>(request, response);
		Page<OrderRefund> page = orderRefundService.findPage(orderRefundPage, orderRefund);
		return new SuccResult(page);
	}

	@ResponseBody
	@RequestMapping(value = "formDataRefund", method = {RequestMethod.POST})
	@ApiOperation("查看退款")
	@RequiresPermissions("refund_info")
	public Result formDataRefund(@RequestBody OrderRefund orderRefund) {
		OrderRefund entity = null;
		try {
			if (StringUtils.isNotBlank(orderRefund.getId())) {
				entity = orderRefundService.formData(orderRefund);
			}
			if (entity == null) {
				return new FailResult("未找到此id对应的订单");
			} else {
				return new SuccResult(entity);
			}
		}catch (ServiceException ex){
			return new FailResult("查看失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("未找到订单详情!");
		}
	}

}