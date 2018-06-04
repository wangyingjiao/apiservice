/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.web;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.*;
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
    private OpenCreateCombinationSubscribeService openCreateCombinationSubscribeService;

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
     * 多次预约订单保存
     * @param info
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "createForGroup", method = {RequestMethod.POST})
    public OpenResult createForGroup(OpenCreateForSubscribeRequest info, HttpServletRequest request, HttpServletResponse response) {
        try {
            HashMap<String,Object> map = openCreateCombinationSubscribeService.openCreate(info);
			OpenCreateForSubscribeResponse responseRe = (OpenCreateForSubscribeResponse)map.get("response");

            try {
				List<OrderInfo> orderInfoList = (List<OrderInfo>)map.get("orderInfoMsg");
				if(orderInfoList != null){
					for(OrderInfo orderInfo : orderInfoList){
						User user = new User();
						user.setId("gasq001");
						orderInfo.setCreateBy(user);
						messageInfoService.insert(orderInfo, "orderCreate");//新增
					}
				}
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
	 * 订单退款取消
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateGroupStatus", method = {RequestMethod.POST})
	public OpenResult updateGroupStatus(OpenUpdateStautsRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			HashMap<String,Object> map = openService.updateGroupStatus(info);
			OpenUpdateStautsResponse responseRe = (OpenUpdateStautsResponse)map.get("response");
			try {
				List<OrderInfo> orderMsgList = (List<OrderInfo>) map.get("orderMsgList");
				if(orderMsgList!=null){
					for(OrderInfo orderInfo : orderMsgList){
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
				List<OrderInfo> orderMsgList = (List<OrderInfo>) map.get("orderMsgList");
				if(orderMsgList!=null){
					for(OrderInfo orderInfo : orderMsgList){
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
			HashMap<String, Object> map = openCombinationService.cancelForGroup(info);
			Object responseRe = map.get("response");
			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");

			try {
				List<OrderDispatch> dis = (List<OrderDispatch>)map.get("dis");
				OrderInfo orderInfo1 = new OrderInfo();
				orderInfo1.setId((String)map.get("orderId"));
				orderInfo1.setOrderNumber((String)map.get("orderNumber"));
				orderInfo1.setTechList(dis);
				User user = UserUtils.getUser();
				user.setId("gasq001");
				orderInfo1.setCreateBy(user);
				orderInfo1.setUpdateBy(user);
				messageInfoService.insert(orderInfo1,"orderCancel");//取消
			}catch (Exception e){
				logger.error("更换时间保存-推送消息失败-系统异常");
			}
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