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
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
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
	private MessageInfoService messageInfoService;

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
	@RequiresPermissions("combination_info")
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
	//@RequiresPermissions("combination_start_time")
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
	//@RequiresPermissions("combination_start_time")
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
	//@RequiresPermissions("combination_start_time")
	public Result saveRegularDate(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
			boolean flag = combinationSaveRegularDateService.checkRegularDateTech(combinationOrderInfo);
			if(flag){
				return new FailResult("时间或服务技师目前暂不可用!");
			}
			List<OrderInfo> list = combinationSaveRegularDateService.saveRegularDate(combinationOrderInfo);

			try {
				for(OrderInfo info : list){
					info.setCreateBy(UserUtils.getUser());
					messageInfoService.insert(info, "orderCreate");//新增
				}
			}catch (Exception e){
				logger.error("订单创建-推送消息失败-系统异常");
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
	//@RequiresPermissions("combination_regular_time")
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
	//@RequiresPermissions("combination_regular_tech")
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
	//@RequiresPermissions("combination_regular_tech")
	public Result updateRegularTechSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
		try {
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
	 * 更换子订单时间
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTime", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order_time")
	public Result updateOrderTime(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
	}

//	/**
//	 * 子订单  更换技师  按钮
//	 * @param combinationOrderInfo
//	 * @return 子订单技师列表
//	 */
//	@ResponseBody
//	@RequestMapping(value = "updateOrderTechInit", method = {RequestMethod.POST})
//	//@RequiresPermissions("combination_order_tech")
//	public Result updateOrderTechInit(@RequestBody CombinationOrderInfo combinationOrderInfo) {
//
//	}
//
//	/**
//	 * 子订单  更换技师 增加 改派 技师列表
//	 * @param combinationOrderInfo
//	 * @return 子订单增加 改派 技师列表
//	 */
//	@ResponseBody
//	@RequestMapping(value = "updateOrderTechTechList", method = {RequestMethod.POST})
//	//@RequiresPermissions("combination_order_tech")
//	public Result updateOrderTechTechList(@RequestBody CombinationOrderInfo combinationOrderInfo) {
//
//	}
//
//	/**
//	 * 子订单  更换技师 增加保存按钮
//	 * @param combinationOrderInfo
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "updateOrderTechAddSave", method = {RequestMethod.POST})
//	//@RequiresPermissions("combination_order_tech")
//	public Result updateOrderTechAddSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
//
//	}
//	/**
//	 * 子订单  更换技师  改派保存按钮
//	 * @param combinationOrderInfo
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "updateOrderTechDispatchSave", method = {RequestMethod.POST})
//	//@RequiresPermissions("combination_order_tech")
//	public Result updateOrderTechDispatchSave(@RequestBody CombinationOrderInfo combinationOrderInfo) {
//
//	}


	/**
	 * 查看备注
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "formOrderRemark", method = {RequestMethod.POST})
	//@RequiresPermissions("combination_order_remark")
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

}