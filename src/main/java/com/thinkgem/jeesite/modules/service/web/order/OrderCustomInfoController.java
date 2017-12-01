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
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderCustomInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@Api(tags = "客户信息", description = "客户信息相关接口")
@RequestMapping(value = "${adminPath}/service/order/orderCustomInfo")
public class OrderCustomInfoController extends BaseController {

	@Autowired
	private OrderCustomInfoService orderCustomInfoService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST})
    @ApiOperation("客户详情")
	public Result getData(@RequestParam(required=false) String id) {
		OrderCustomInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderCustomInfoService.get(id);
		}
        if (entity == null) {
            return new FailResult("未找到此id：" + id + "对应的客户。");
        } else {
            return new SuccResult(entity);
        }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "saveData", method = {RequestMethod.POST})
	@ApiOperation("保存客户")
	public Result saveData(@RequestBody OrderCustomInfo orderCustomInfo) {
		if (!beanValidator(orderCustomInfo)) {
			return new FailResult("保存客户" + orderCustomInfo.getCustomName() + "失败");
		}
		User user = UserUtils.getUser();
		orderCustomInfo.setOfficeId(user.getOffice().getId());//机构ID
		orderCustomInfo.setOfficeName(user.getOffice().getName());//机构名称
		orderCustomInfo.setStationId(user.getStation().getId());//服务站ID
		orderCustomInfo.setStationName(user.getStation().getName());//服务站名称
		orderCustomInfoService.save(orderCustomInfo);
		return new SuccResult("保存客户" + orderCustomInfo.getCustomName() + "成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "listData", method = {RequestMethod.POST})
	@ApiOperation("获取客户列表")
	public Result listData(@RequestBody(required=false)  OrderCustomInfo orderCustomInfo, HttpServletRequest request, HttpServletResponse response) {
		if(orderCustomInfo == null){
			orderCustomInfo = new OrderCustomInfo();
		}
		Page<OrderCustomInfo> stationPage = new Page<>(request, response);
		Page<OrderCustomInfo> page = orderCustomInfoService.findPage(stationPage, orderCustomInfo);
		return new SuccResult(page);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "delData", method = {RequestMethod.POST})
	@ApiOperation("删除客户")
	public Result deleteSortInfo(@RequestBody OrderCustomInfo orderCustomInfo) {
		orderCustomInfoService.delete(orderCustomInfo);
		return new SuccResult("删除客户成功");
	}

}