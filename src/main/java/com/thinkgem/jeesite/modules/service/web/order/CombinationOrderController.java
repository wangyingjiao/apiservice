/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
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
	 * 设置固定时间
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "initSetRegularDate", method = {RequestMethod.POST})
	@RequiresPermissions("combination_start_time")
	public Result initSetRegularDate(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
	}
	/**
	 * 设置固定时间
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveRegularTime", method = {RequestMethod.POST})
	@RequiresPermissions("combination_start_time")
	public Result saveRegularTime(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
	}

	/**
	 * 更换固定时间
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateRegularTime", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular_time")
	public Result updateRegularTime(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
	}

	/**
	 * 更换固定技师
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateRegularTech", method = {RequestMethod.POST})
	@RequiresPermissions("combination_regular_tech")
	public Result updateRegularTech(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
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

	/**
	 * 更换子订单技师
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateOrderTech", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order_tech")
	public Result updateOrderTech(@RequestBody CombinationOrderInfo combinationOrderInfo) {

		return new SuccResult("");
	}

	/**
	 * 查看备注
	 * @param combinationOrderInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getOrderRemark", method = {RequestMethod.POST})
	@RequiresPermissions("combination_order_remark")
	public Result getOrderRemark(@RequestBody CombinationOrderInfo combinationOrderInfo) {
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