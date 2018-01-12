/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.OpenService;
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
 * 对接Controller
 * @author a
 * @version 2018-1-11
 */
@Controller
@RequestMapping(value = "${openPath}/open/order")
public class OpenController extends BaseController {

	@Autowired
	private OpenService openService;

	/**
	 * 选择服务时间
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "serviceTimes", method = {RequestMethod.POST})
	public Result serviceTimes(@RequestBody OpenServiceTimesRequest info) {
		List<OpenServiceTimesResponse> list = openService.openServiceTimes(info);
		HashMap<Object,Object> response = new HashMap();
		response.put("available_times",list);
		return new SuccResult(response);
	}

	/**
	 * 订单创建
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create", method = {RequestMethod.POST})
	public Result create(@RequestBody OpenCreateRequest info) {
		OpenCreateResponse response = openService.openCreate(info);
		return new SuccResult(response);
	}

	/**
	 * 订单状态更新
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateStauts", method = {RequestMethod.POST})
	public Result updateStauts(@RequestBody OpenUpdateStautsRequest info) {
		OpenUpdateStautsResponse response = openService.openUpdateStauts(info);
		return new SuccResult(response);
	}

	/**
	 * 更新订单信息
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateInfo", method = {RequestMethod.POST})
	public Result updateInfo(@RequestBody OpenUpdateInfoRequest info) {
		OpenUpdateInfoResponse response = openService.openUpdateInfo(info);
		return new SuccResult(response);
	}
}