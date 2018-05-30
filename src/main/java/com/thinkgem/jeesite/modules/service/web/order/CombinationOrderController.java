/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.service.order.*;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 组合订单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/combination")
public class CombinationOrderController extends BaseController {

	@Autowired
	private CombinationOrderService combinationOrderService;
	@Autowired
	private CombinationSaveRegularDateService combinationSaveRegularDateService;
	@Autowired
	private CombinationSaveRegularTechService combinationSaveRegularTechService;
	@Autowired
	private CombinationSaveOrderTechService combinationSaveOrderTechService;
	@Autowired
	private CombinationSaveOrderTimeService combinationSaveOrderTimeService;
	@Autowired
	private CombinationSubscribeService combinationSubscribeService;

	@Autowired
	private MessageInfoService messageInfoService;
	@Autowired
	private OrderInfoOperateService orderInfoOperateService;
	@Autowired
	private OrderInfoService orderInfoService;

	/**
	 * 组合订单列表
	 * orgId  stationId  orderTime  masterId  orderContent
	 * @param combinationOrderInfo
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listDataCombination", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("获取组合订单列表")
	@RequiresPermissions("combination_view")
	public Result listDataCombination(@RequestBody(required = false) CombinationOrderInfo combinationOrderInfo, HttpServletRequest request, HttpServletResponse response) {
		if(null == combinationOrderInfo){
			combinationOrderInfo = new CombinationOrderInfo();
		}
		Page<CombinationOrderInfo> orderInfoPage = new Page<>(request, response);
		Page<CombinationOrderInfo> page = combinationOrderService.listDataCombination(orderInfoPage, combinationOrderInfo);
		return new SuccResult(page);
	}

	/**
	 * 根据masterId查询组合订单详情
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getCombinationById", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("组合订单详情")
	//@RequiresPermissions("combination_info")
	public Result getCombinationById(@RequestBody(required = false) CombinationOrderInfo combinationOrderInfo) {
		if(null == combinationOrderInfo){
			combinationOrderInfo = new CombinationOrderInfo();
		}
		CombinationOrderInfo combinationById = combinationOrderService.getCombinationById(combinationOrderInfo);
		return new SuccResult(combinationById);
	}


	/**
	 * 设置固定时间- 查询服务日期
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveRegularDateDateList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result saveRegularDateDateList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			List<OrderTimeList>  list = combinationSaveRegularDateService.saveRegularDateDateList(combinationOrderInfo);
			HashMap data = new HashMap();
			data.put("weekList",list);
			List<OrderTimeList> dateList = combinationSaveRegularDateService.saveRegularDateWeekList();
			data.put("dateList",dateList);
			return new SuccResult(data);
		}catch (Exception e){
			//return new FailResult("获取时间列表失败!");
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderTimeList>());
		}
	}

	/**
	 * 设置固定时间- 查询服务技师
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveRegularDateTechList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result saveRegularDateTechList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			List<OrderDispatch> list = combinationSaveRegularDateService.saveRegularDateTechList(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}
	/**
	 * 设置固定时间 - 保存
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveRegularDate", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result saveRegularDate(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			boolean flag = combinationSaveRegularDateService.checkRegularDateTech(combinationOrderInfo);
			if(flag){
				return new FailResult("时间或服务技师目前暂不可用!");
			}
			List<OrderInfo> list = combinationSaveRegularDateService.saveRegularDate(combinationOrderInfo);

			try {
				if(list!=null){
					for(OrderInfo info : list){
						info.setCreateBy(UserUtils.getUser());
						messageInfoService.insert(info, "orderCreate");//新增
					}
				}
			}catch (Exception e){
				logger.error("订单创建-推送消息失败-系统异常");
			}
			try {
				if(list!=null && list.size()>0) {
					CombinationOrderInfo combinationInfo = new CombinationOrderInfo();
					combinationInfo.setMasterId(list.get(0).getMasterId());
					combinationInfo.setJointGroupId(list.get(0).getJointGroupId());
					combinationInfo.setOrderInfoList(list);
					OpenSendUtil.updateGroupOrderInfo(combinationInfo);
				}
			}catch (Exception e){
				logger.error("订单创建-对接失败-系统异常");
			}

			return new SuccResult("设置成功");
		}catch (Exception e){
			e.printStackTrace();
			return new FailResult("设置失败!");
		}
	}

	/**
	 * 更换固定时间 - 保存
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateRegularDate", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result updateRegularDate(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			boolean flag = combinationSaveRegularDateService.checkRegularDateTech(combinationOrderInfo);
			if(flag){
				return new FailResult("时间或服务技师目前暂不可用!");
			}
			List<OrderInfo> list = combinationSaveRegularDateService.updateRegularDate(combinationOrderInfo);

			return new SuccResult("更换成功");
		}catch (Exception e){
			e.printStackTrace();
			return new FailResult("更换失败!");
		}
	}

	/**
	 * 更换固定技师 - 技师列表
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateRegularTechTechList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result updateRegularTechTechList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			List<OrderDispatch> list = combinationSaveRegularTechService.updateRegularTechTechList(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}

	/**
	 * 更换固定技师 - 保存
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateRegularTechSave", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular")
	public Result updateRegularTechSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			if(StringUtils.isBlank(combinationOrderInfo.getTechId())){
				return new FailResult("请选择服务技师!");
			}
			boolean flag = combinationSaveRegularTechService.checkRegularDateTech(combinationOrderInfo);
			if(flag){
				return new FailResult("服务技师目前暂不可用!");
			}
			List<OrderInfo> list = combinationSaveRegularTechService.updateRegularTechSave(combinationOrderInfo);

			return new SuccResult("更换成功");
		}catch (Exception e){
			e.printStackTrace();
			return new FailResult("更换失败!");
		}
	}
	/**
	 * 子订单  更换技师  按钮
	 * @param combinationOrderInfo
	 * @return 子订单技师列表
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTechInit", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTechInit(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(combinationOrderInfo.getOrderId());
			//判断订单状态
			boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
			if(!flag){
				return new FailResult("当前订单状态或服务状态不允许操作此项内容");
			}
			boolean fullFlag = orderInfoOperateService.checkOrderFullGoods(orderInfo);
			if(!fullFlag){
				return new FailResult("当前订单只有补单商品,不允许操作此项内容");
			}

			List<OrderDispatch> list = combinationSaveOrderTechService.updateOrderTechInit(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}

	/**
	 * 子订单  更换技师 增加 改派 技师列表
	 * @param combinationOrderInfo
	 * @return 子订单增加 改派 技师列表
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTechTechList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTechTechList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			List<OrderDispatch> list = combinationSaveOrderTechService.updateOrderTechTechList(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}

	/**
	 * 子订单  更换技师 增加保存按钮
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTechAddSave", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTechAddSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try{
			HashMap<String,Object> map = combinationSaveOrderTechService.updateOrderTechAddSave(combinationOrderInfo);

			try {//对接
				//非自营订单
				if(!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();
					String orderSn = map.get("orderNumber").toString();
					sendOrder.setOrderNumber(orderSn);//订单编号
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendUtil.openSendSaveOrder(sendOrder);
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
	/**
	 * 子订单  更换技师  改派保存按钮
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTechDispatchSave", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTechDispatchSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try{
			HashMap<String,Object> map = null;
			OrderInfo typeInfo = combinationSaveOrderTechService.getOrderTypeByOrderId(combinationOrderInfo);
			if("group_split_yes".equals(typeInfo.getOrderType())){
				map = combinationSaveOrderTechService.updateOrderTechDispatchSaveForMany(combinationOrderInfo);
				try {
					//订单商品有对接方商品CODE  机构有对接方E店CODE
					if(!"own".equals(map.get("orderSource").toString())){
						List<OrderInfo> orderSendList = (List<OrderInfo>) map.get("orderSendList");
						if(orderSendList != null ){
							for(OrderInfo orderSend : orderSendList){
								OpenSendUtil.openSendSaveOrder(orderSend);
							}
						}
					}
				}catch (Exception e){
					logger.error("技师改派保存-对接失败-系统异常");
				}

				try{
					User user = UserUtils.getUser();
					// 派单
					List<OrderInfo> orderCreateMsgList = (List<OrderInfo>) map.get("orderCreateMsgList");
					if(orderCreateMsgList != null ){
						for(OrderInfo orderCreateMsg : orderCreateMsgList){
							orderCreateMsg.setCreateBy(user);
							messageInfoService.insert(orderCreateMsg,"orderCreate");//新增
						}
					}
					// 改派
					List<OrderInfo> orderDispatchMsgList = (List<OrderInfo>) map.get("orderDispatchMsgList");
					if(orderDispatchMsgList != null ){
						for(OrderInfo orderDispatchMsg : orderDispatchMsgList){
							orderDispatchMsg.setCreateBy(user);
							messageInfoService.insert(orderDispatchMsg,"orderDispatch");//改派
						}
					}
				}catch (Exception e){
					logger.error("技师改派保存-推送消息失败-系统异常");
				}
				return new SuccResult(map);
			}else if("group_split_no".equals(typeInfo.getOrderType())){
				map = combinationSaveOrderTechService.updateOrderTechDispatchSaveForOnce(combinationOrderInfo);
			}else {
				return null;
			}

			try {
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(!"own".equals(map.get("orderSource").toString())){
					OrderInfo sendOrder = new OrderInfo();
					String orderSn = map.get("orderNumber").toString();
					sendOrder.setOrderNumber(orderSn);//订单编号
					sendOrder.setTechList((List<OrderDispatch>) map.get("list"));//技师信息
					OpenSendUtil.openSendSaveOrder(sendOrder);
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
	 * 查看备注
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "formOrderRemark", method = {RequestMethod.POST})
	@RequiresPermissions("combination_remark")
	public Result formOrderRemark(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		// 订单ID不为空
		if(combinationOrderInfo == null || StringUtils.isBlank(combinationOrderInfo.getOrderId())){
			return new FailResult("未找到订单信息");
		}
		try {
			return new SuccResult(combinationOrderService.getOrderRemark(combinationOrderInfo));
		}catch (ServiceException ex){
			return new FailResult("查看失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("未找到订单信息!");
		}
	}


	/**
	 * 更换子订单时间 - 时间列表
	 * @param orderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTimeDateList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTimeDateList(@RequestBody OrderInfo orderInfo) {
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
		if(!flag){
			return new FailResult("当前订单状态或服务状态不允许操作此项内容");
		}
		boolean fullFlag = orderInfoOperateService.checkOrderFullGoods(orderInfo);
		if(!fullFlag){
			return new FailResult("当前订单只有补单商品,不允许操作此项内容");
		}
		OrderInfo info = orderInfoService.get(orderInfo.getId());
		Date serviceTime = info.getServiceTime();
		//当前时间后一个半小时
		Double serviceNextSecond = (1.5 * 3600);
		Date finishTime = DateUtils.addSeconds(new Date(), serviceNextSecond.intValue());
		if (serviceTime.compareTo(finishTime) < 0){
			return new FailResult("距离服务开始时间只剩下一个半小时，不可再更换时间!");
		}
		try {
			List<OrderTimeList> orderTimeLists = combinationSaveOrderTimeService.updateOrderTimeDateList(orderInfo);
			return new SuccResult(orderTimeLists);
		}catch (ServiceException ex){
			return new FailResult("获取时间列表失败-"+ex.getMessage());
		}catch (Exception e){
			return new FailResult("获取时间列表失败!");
		}
	}

	/**
	 * 更换子订单时间 - 技师列表
	 * 参数 订单id serviceTime
	 * @param orderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTimeTechList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTimeTechList(@RequestBody OrderInfo orderInfo) {
		Map<String, Object> stringObjectMap = combinationSaveOrderTimeService.updateOrderTimeTechList(orderInfo);
		return new SuccResult(stringObjectMap);
	}

	/**
	 * 更换子订单时间 - 保存
	 * 参数 orderId serviceTime techId
	 * @param orderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTimeSave", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order")
	public Result updateOrderTimeSave(@RequestBody OrderInfo orderInfo) {
		OrderInfo info = orderInfoService.get(orderInfo.getId());
		HashMap<String,Object> map = null;
		Date serviceTime = info.getServiceTime();
		//当前时间后一个半小时
		Double serviceNextSecond = (1.5 * 3600);
		Date finishTime = DateUtils.addSeconds(new Date(), serviceNextSecond.intValue());
		if (serviceTime.compareTo(finishTime) < 0){
			return new FailResult("距离服务开始时间只剩下一个半小时，不可再更换时间!");
		}
		//根据订单id 查询排期表 时间是否冲突
		boolean checkTech = combinationSaveOrderTimeService.checkTech(orderInfo);
		if (!checkTech){
			return new FailResult("选中技师在此时间段已有排期，不可再更换时间!");
		}
		try {
			if ("group_split_yes".equals(info.getOrderType())){
				map = combinationSaveOrderTimeService.updateOrderTimeSave(orderInfo);
			}else if ("group_split_no".equals(info.getOrderType())){
				map = orderInfoOperateService.saveComTime(orderInfo);
			}
			try {
				//订单商品有对接方商品CODE  机构有对接方E店CODE
				if(!"own".equals(map.get("orderSource").toString())){
					CombinationOrderInfo combinationOrderInfo = (CombinationOrderInfo) map.get("combinationByMasterId");
					OpenSendUtil.updateGroupDate(combinationOrderInfo);
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
			return new SuccResult("保存成功");
        }catch (ServiceException e){
            return new FailResult(e.getMessage());
        }
	}

    /**
	 * 后台预约- 查询服务日期
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "subscribeDateList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_subscribe")
	public Result subscribeDateList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
            boolean flag = combinationSubscribeService.checkCombinationStatus(combinationOrderInfo);
            if (!flag){
                return new FailResult("组合订单当前状态不允许预约");
            }
			List<OrderTimeList>  list = combinationSubscribeService.subscribeDateList(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			//return new FailResult("获取时间列表失败!");
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderTimeList>());
		}
	}

	/**
	 * 后台预约 - 查询服务技师
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "subscribeTechList", method = {RequestMethod.POST})
	@RequiresPermissions("combination_subscribe")
	public Result subscribeTechList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			List<OrderDispatch> list = combinationSubscribeService.subscribeTechList(combinationOrderInfo);
			return new SuccResult(list);
		}catch (Exception e){
			e.printStackTrace();
			return new SuccResult(new ArrayList<OrderDispatch>());
		}
	}
	/**
	 * 后台预约 - 保存
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "subscribeSave", method = {RequestMethod.POST})
	@RequiresPermissions("combination_subscribe")
	public Result subscribeSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
            boolean statusFlag = combinationSubscribeService.checkCombinationStatus(combinationOrderInfo);
            if (!statusFlag){
                return new FailResult("组合订单当前状态不允许预约");
            }
			boolean flag = combinationSubscribeService.checkSubscribeTech(combinationOrderInfo);
			if(flag){
				return new FailResult("时间或服务技师目前暂不可用!");
			}
			List<OrderInfo> list = combinationSubscribeService.subscribeSave(combinationOrderInfo);

			try {
				for(OrderInfo info : list){
					info.setCreateBy(UserUtils.getUser());
					messageInfoService.insert(info, "orderCreate");//新增
				}
			}catch (Exception e){
				logger.error("订单创建-推送消息失败-系统异常");
			}

			try {
				if(list!=null && list.size()>0) {
					CombinationOrderInfo combinationInfo = new CombinationOrderInfo();
					combinationInfo.setMasterId(list.get(0).getMasterId());
					combinationInfo.setJointGroupId(list.get(0).getJointGroupId());
					combinationInfo.setOrderInfoList(list);
					OpenSendUtil.updateGroupOrderInfo(combinationInfo);
				}
			}catch (Exception e){
				logger.error("订单创建-对接失败-系统异常");
			}

			return new SuccResult("预约成功");
		}catch (Exception e){
			e.printStackTrace();
			return new FailResult("预约失败!");
		}
	}

}