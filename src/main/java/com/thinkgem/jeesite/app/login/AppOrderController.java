/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.*;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
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

	//查询订单列表
	@ResponseBody
    @RequestMapping(value = "${appPath}/getOrderListPage",  method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "订单列表", notes = "订单")
    public AppResult getOrderListPage(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		//获取登录用户id 获取用户手机set进去
		orderInfo.setBusinessPhone("13508070808");
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
		map.put("pageNo",page.getPageSize());
		if(page.getList().size()==0){
			return new AppSuccResult(1,map,"列表查询");
		}

		return new AppSuccResult(0,map,"列表查询");
    }
	//订单详情
	@ResponseBody
	@RequestMapping(value = "${appPath}/getOrderById",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "订单详情", notes = "订单")
	public AppResult getOrderById(OrderInfo Info, HttpServletRequest request, HttpServletResponse response){
		//获取登录用户id
		Info.setBusinessPhone("13508070808");
		OrderInfo orderInfo = orderInfoService.appFormData(Info);
		//订单备注
		List<String> orderRemarkPics = orderInfo.getOrderRemarkPics();
		List<String> orp=new ArrayList<String>();
		for (String pic:orderRemarkPics){
			String url="https://openservice.guoanshequ.com/"+pic;
			orp.add(url);
		}
		orderInfo.setOrderRemarkPics(orp);
		String serviceStatus = orderInfo.getServiceStatus();
		if (serviceStatus.equals("wait_service")){
			orderInfo.setServiceStatusName("待服务");
		}else if (serviceStatus.equals("started")){
			orderInfo.setServiceStatusName("已上门");
		}else if (serviceStatus.equals("finish")){
			orderInfo.setServiceStatusName("已完成");
		}
		String orderStatus = orderInfo.getOrderStatus();
		if (orderStatus.equals("waitdispatch")){
			orderInfo.setOrderStatusName("待派单");
		}else if (orderStatus.equals("dispatched")){
			orderInfo.setOrderStatusName("已派单");
		}else if (orderStatus.equals("cancel")){
			orderInfo.setOrderStatusName("已取消");
		}else if (orderStatus.equals("started")){
			orderInfo.setOrderStatusName("已上门");
		}else if (orderStatus.equals("finish")){
			orderInfo.setOrderStatusName("已完成");
		}else if (orderStatus.equals("success")){
			orderInfo.setOrderStatusName("已成功");
		}else if (orderStatus.equals("stop")){
			orderInfo.setOrderStatusName("已暂停");
		}
		String payStatus = orderInfo.getPayStatus();
		if (payStatus.equals("waitpay")){
			orderInfo.setPayStatusName("待支付");
		}else if (payStatus.equals("payed")){
			orderInfo.setPayStatusName("已支付");
		}

		//业务人员信息
		BusinessInfo bus=new BusinessInfo();
		bus.setBusinessName(orderInfo.getBusinessName());
		bus.setBusinessPhone(orderInfo.getBusinessPhone());
		bus.setBusinessRemark(orderInfo.getBusinessRemark());
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		List<String> busiPic = (List<String>) JsonMapper.fromJsonString(businessRemarkPic, ArrayList.class);
		List<String> bp=new ArrayList<String>();
		for (String pic:busiPic){
			String url="https://openservice.guoanshequ.com/"+pic;
			bp.add(url);
		}
		bus.setBusinessRemarkPic(bp);
		orderInfo.setBusinessInfo(bus);
		//门店信息
		ShopInfo shop=new ShopInfo();
		shop.setId(orderInfo.getStationId());
		shop.setShopName(orderInfo.getShopName());
		shop.setShopPhone(orderInfo.getShopPhone());
		shop.setShopAddress(orderInfo.getShopAddr());
		shop.setShopRemark(orderInfo.getShopRemark());
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		List<String> shopPics = (List<String>) JsonMapper.fromJsonString(shopRemarkPic, ArrayList.class);
		List<String> ls=new ArrayList<String>();
		for (String pic:shopPics){
			String url="https://openservice.guoanshequ.com/"+pic;
			ls.add(url);
		}
		shop.setShopRemarkPic(ls);
		orderInfo.setShopInfo(shop);
		//客户信息
		OrderCustomInfo customerInfo=new OrderCustomInfo();
		customerInfo.setCustomerRemark(orderInfo.getCustomerRemark());
		List<String> customerRemarkPics = orderInfo.getCustomerRemarkPics();
		List<String> ll=new ArrayList<String>();
		for (String s:customerRemarkPics){
			String url="https://openservice.guoanshequ.com/"+s;
			ll.add(url);
		}
		customerInfo.setCustomerRemarkPic(ll);
		orderInfo.setCustomerInfo(customerInfo);
		return new AppSuccResult(0,orderInfo,"查询订单详情");
	}

	//技师添加订单备注
	@ResponseBody
	@RequestMapping(value = "${appPath}/saveRemark",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation(value = "技师添加订单备注", notes = "订单")
	public AppResult saveRemark(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response){
		orderInfo.setBusinessPhone("13508070808");
		List<String> errList = errors(orderInfo, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		List<String> orderRemarkPics = orderInfo.getOrderRemarkPics();
		if (null != orderRemarkPics){
			String sys = JsonMapper.toJsonString(orderRemarkPics);
			orderInfo.setOrderRemarkPic(sys);
		}
		int i = orderInfoService.appSaveRemark(orderInfo);
		if (i>0){
			return new AppSuccResult(0,null,"添加备注成功");
		}
		return new AppFailResult(-1,null,"添加备注失败");
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
		info.setBusinessPhone("13508070808");
		int i = orderInfoService.appSaveRemark(info);
		if (i>0){
			return new AppSuccResult(0,null,"修改服务状态成功");
		}
		return new AppFailResult(-1,null,"修改服务状态失败");
	}


	@ResponseBody
	@RequestMapping(value = "${appPath}/appDispatchTech",method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("技师改派")
	public AppResult appDispatchTech(OrderInfo orderInfo) {
		List<OrderDispatch> techList = orderInfoService.addTech(orderInfo);
		return new AppSuccResult(techList);
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/appDispatchTechSave", method = {RequestMethod.POST, RequestMethod.GET})
	@ApiOperation("技师改派保存")
	public AppResult appDispatchTechSave(OrderInfo orderInfo) {
		List<OrderDispatch> techList = orderInfoService.dispatchTechSave(orderInfo);
		return new AppSuccResult(techList);
	}


}
