/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.app.interceptor.Token;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.thinkgem.jeesite.common.web.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通讯录Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP订单类", description = "APP订单相关接口")
public class AppOrderController extends BaseController {

	@Autowired
	OrderInfoService orderInfoService;
	@Autowired
	ServiceTechnicianInfoService techService;

	//查询订单列表
	@ResponseBody
    @RequestMapping(value = "${appPath}/getOrderListPage",  method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "订单列表", notes = "订单")
    public AppResult getOrderListPage(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		//获取登录用户id 获取用户手机set进去
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		orderInfo.setTechPhone(phone);
		if (orderInfo.getServiceStatus()==null){
			return new AppFailResult("订单服务状态不可为空");
		}
		if (orderInfo.getMajorSort().equals("all")){
			orderInfo.setMajorSort(null);
		}
		Page<OrderInfo> serSortInfoPage = new Page<>(request, response);
		Page<OrderInfo> page = orderInfoService.appFindPage(serSortInfoPage,orderInfo);
		long count = page.getCount();
		int pageSize = page.getPageSize();
		long totalPage=0;
		long l = count % pageSize;//取余
		if (l > 0){
			long l1 = count / pageSize;
			totalPage = l1 + 1;
		}else {
			totalPage = count / pageSize;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("list",page.getList());
		map.put("totalPage",totalPage);
		map.put("pageNo",page.getPageNo());
		if(page.getList().size()==0){
			return new AppSuccResult(1,map,"列表查询");
		}

		return new AppSuccResult(0,map,"列表查询");
    }
	//订单详情
	@ResponseBody
	@RequestMapping(value = "${appPath}/getOrderById",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "订单详情", notes = "订单")
	public AppResult getOrderById(OrderInfo info, HttpServletRequest request, HttpServletResponse response){
		//获取登录用户id
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone(phone);
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		info.setTechPhone(phone);
		info.setNowId(tech1.getId());
		try{
			OrderInfo orderInfo = orderInfoService.appFormData(info);
			return new AppSuccResult(0,orderInfo,"查询订单详情");
		}catch (ServiceException e ){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}

	//技师添加订单备注
	@ResponseBody
	@RequestMapping(value = "${appPath}/saveRemark",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "技师添加订单备注", notes = "订单")
	public AppResult saveRemark(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response){
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		orderInfo.setTechPhone(phone);
		List<String> errList = errors(orderInfo, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		List<String> orderRemarkPics = orderInfo.getOrderRemarkPics();
		if (null != orderRemarkPics){
			String sys = JsonMapper.toJsonString(orderRemarkPics);
			orderInfo.setOrderRemarkPic(sys);
		}
		try{
			int i = orderInfoService.appSaveRemark(orderInfo);
			if (i>0){
				//获取商品对接code 和 机构对接code
//				if (StringUtils.isNotBlank(orderInfo.get)){
//
//				}
				OrderInfo sendOrder = new OrderInfo();
				sendOrder.setId(orderInfo.getId());
				sendOrder.setOrderRemark(orderInfo.getOrderRemark());
				sendOrder.setOrderRemarkPic(orderInfo.getOrderRemarkPic());
				OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
				if (sendResponse == null) {
					return new AppFailResult(-1,null,"对接失败N");
				} else if (!"0".equals(sendResponse.getCode())) {
					return new AppFailResult(-1,null,sendResponse.getMessage());
				}
				return new AppSuccResult(0,null,"添加备注成功");
			}
			return new AppFailResult(-1,null,"添加备注失败");
		}catch (Exception e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}
	//修改服务状态
	@ResponseBody
	@RequestMapping(value = "${appPath}/updateOrderByServiceStatus",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "修改服务状态", notes = "订单")
	public AppResult updateOrderByServiceStatus(OrderInfo info, HttpServletRequest request, HttpServletResponse response){
		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//参数 订单id 服务状态
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		info.setTechPhone(phone);
		int i = orderInfoService.appSaveRemark(info);
		if (i>0){
			return new AppSuccResult(0,null,"修改服务状态成功");
		}
		return new AppFailResult(-1,null,"修改服务状态失败");
	}


	@ResponseBody
	@RequestMapping(value = "${appPath}/techList",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("技师列表")
	public AppResult techList(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		orderInfo.setTechPhone(phone);
		List<OrderDispatch> techList = orderInfoService.appTech(orderInfo);
		Map<String,Object> map=new HashMap<String,Object>();
		try{
			PropertiesLoader loader = new PropertiesLoader("oss.properties");
			String ossHost = loader.getProperty("OSS_HOST");
			List<AppServiceTechnicianInfo> apt=new ArrayList<AppServiceTechnicianInfo>();
			for (OrderDispatch dis:techList){
				ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
				tec.setId(dis.getTechId());
				AppServiceTechnicianInfo technicianById = techService.getTechnicianById(tec);
				technicianById.setImgUrl(ossHost + technicianById.getImgUrlHead());
				apt.add(technicianById);
			}
			map.put("list",apt);
		}catch (ServiceException e){
			return new AppFailResult(e.getMessage());
		}
		return new AppSuccResult(0,map,"技师列表");
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/appDispatchTechSave", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("技师改派保存")
	public AppResult appDispatchTechSave(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
		tech.setPhone(phone);
		ServiceTechnicianInfo tech1 = techService.findTech(tech);
		//传的参数 改派技师的id
		String techId = orderInfo.getTechId();
		//获取订单信息
		orderInfo = orderInfoService.appGet(orderInfo);
		orderInfo.setTechId(techId);
		//改派前技师id
		orderInfo.setDispatchTechId(tech1.getId());
		try {
			int i = orderInfoService.appDispatchTechSave(orderInfo);
			if (i > 0){
				return new AppSuccResult(0,null,"改派成功");
			}
			return new AppFailResult(-1,null,"改派失败");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}

}
