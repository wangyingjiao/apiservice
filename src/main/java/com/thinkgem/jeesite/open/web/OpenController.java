/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.web;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.OpenCombinationService;
import com.thinkgem.jeesite.open.service.OpenCreateCombinationManyService;
import com.thinkgem.jeesite.open.service.OpenCreateCombinationOnceService;
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
import java.util.Map;

/**
 * 对接Controller
 * @author a
 * @version 2018-1-11
 */
@Controller
@RequestMapping(value = "${openPath_gasq}/order")
public class OpenController extends BaseController {

	@Autowired
	private OpenService openService;
	@Autowired
	private OpenCombinationService openCombinationService;
	@Autowired
	private OpenCreateCombinationManyService openCreateCombinationManyService;
	@Autowired
	private OpenCreateCombinationOnceService openCreateCombinationOnceService;

	@Autowired
	private MessageInfoService messageInfoService;

	/**
	 * 选择服务时间
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "serviceTimes", method = {RequestMethod.POST})
	public OpenResult serviceTimes(OpenServiceTimesRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String,Object> value = openService.openServiceTimes(info);
			OpenSuccResult result =  new OpenSuccResult(value, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			request.setAttribute("openResponseSendType","");
			return result;
		}catch (ServiceException ex){
			OpenFailResult result =  new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * 多次预约订单选择服务时间
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "serviceTimesForGroup", method = {RequestMethod.POST})
	public OpenResult serviceTimesForGroup(OpenServiceTimesForGroupRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String,Object> value = openCombinationService.serviceTimesForGroup(info);
			OpenSuccResult result =  new OpenSuccResult(value, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			request.setAttribute("openResponseSendType","");
			return result;
		}catch (ServiceException ex){
			OpenFailResult result =  new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * 订单创建
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "create", method = {RequestMethod.POST})
	public OpenResult create(OpenCreateRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			HashMap<String,Object> map = null;
			String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
			if(null == order_type){
				OpenFailResult result =  new OpenFailResult("订单类型不能为空");
				String openResponseJson = JsonMapper.toJsonString(result);
				request.setAttribute("openResponseJson",openResponseJson);
				request.setAttribute("openResponseCode","0");
				return result;
			}

			if("common".equals(order_type)) {
				map = openService.openCreate(info);
			}else if("group_split_yes".equals(order_type)) {
				map = openCreateCombinationManyService.openCreate(info);
			}else if("group_split_no".equals(order_type)) {
				map = openCreateCombinationOnceService.openCreate(info);
			}else{
				OpenFailResult result =  new OpenFailResult("订单类型有误");
				String openResponseJson = JsonMapper.toJsonString(result);
				request.setAttribute("openResponseJson",openResponseJson);
				request.setAttribute("openResponseCode","0");
				return result;
			}

			OpenCreateResponse responseRe = (OpenCreateResponse)map.get("response");

			try {
				OrderInfo orderInfo = (OrderInfo)map.get("orderInfoMsg");
				User user = new User();
				user.setId("gasq001");
				orderInfo.setCreateBy(user);
				messageInfoService.insert(orderInfo, "orderCreate");//新增
			}catch (Exception e){
				logger.error("订单创建-推送消息失败-系统异常");
			}

			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (ServiceException ex){
			OpenFailResult result =  new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * 订单状态更新
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateStatus", method = {RequestMethod.POST})
	public OpenResult updateStatus(OpenUpdateStautsRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			HashMap<String,Object> map = openService.openUpdateStauts(info);
			OpenUpdateStautsResponse responseRe = (OpenUpdateStautsResponse)map.get("response");
			try {
				String status = info.getStatus();
				if("cancel".equals(status)) {
					OrderInfo orderInfo = (OrderInfo) map.get("orderInfoMsg");
					if(null != orderInfo) {
						User user = new User();
						user.setId("gasq001");
						orderInfo.setCreateBy(user);
						messageInfoService.insert(orderInfo, "orderCancel");//取消
					}
				}
			}catch (Exception e){
				logger.error("订单状态更新-推送消息失败-系统异常");
			}

			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (ServiceException ex){
			OpenFailResult result =  new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * 更新订单信息
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateInfo", method = {RequestMethod.POST})
	public OpenResult updateInfo(OpenUpdateInfoRequest info, HttpServletRequest request, HttpServletResponse response) {
		try{
			OpenUpdateInfoResponse responseRe = openService.openUpdateInfo(info);

			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (ServiceException ex){
			OpenFailResult result =  new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * 对接取消订单
	 * @param info
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "cancelForGroup", method = {RequestMethod.POST})
	public OpenResult cancelForGroup(OpenCancleStautsRequest info, HttpServletRequest request, HttpServletResponse response) {
		try{
			OpenUpdateInfoResponse responseRe = openCombinationService.cancelForGroup(info);

			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (ServiceException ex) {
			OpenFailResult result = new OpenFailResult(ex.getMessage());
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson", openResponseJson);
			request.setAttribute("openResponseCode", "0");
			return result;
		}
	}


}