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
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
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
	@Autowired
	private MessageInfoService messageInfoService;
	
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
		Page<OrderInfo> data = orderDispatchService.findPage(dispatchInfoPage, dispatchInfo);
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
		//判断订单状态
		boolean flag = orderInfoService.checkOrderStatus(orderInfo);
		if(!flag){
			return new FailResult("当前订单状态或服务状态不允许操作此项内容");
		}

		try{
			List<OrderDispatch> techList = orderInfoService.addTech(orderInfo);
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
	@RequiresPermissions("dispatch_insert")
	public Result dispatchTechSave(@RequestBody OrderInfo orderInfo) {
		try{
			HashMap<String,Object> map = orderInfoService.dispatchTechSave(orderInfo);

			try {
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(StringUtils.isNotEmpty(map.get("jointGoodsCodes").toString()) &&
						StringUtils.isNotEmpty(map.get("jointEshopCode").toString()) &&
						!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(map.get("orderId").toString());
					sendOrder.setOrderNumber(orderSn);//订单编号

					//sendOrder.setId(map.get("orderId").toString());//订单ID
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						logger.error("技师改派保存-对接失败-返回值为空");
					} else if (sendResponse.getCode() != 0) {
						logger.error("技师改派保存-对接失败-"+sendResponse.getMessage());
					}
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
}