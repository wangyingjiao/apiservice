/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.app.interceptor.Token;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.AppFailResult;
import com.thinkgem.jeesite.common.result.AppResult;
import com.thinkgem.jeesite.common.result.AppSuccResult;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.SavePersonalGroup;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoOperateService;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单相关Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP订单类", description = "APP订单相关接口")
public class AppOrderController extends BaseController {

	//订单info的service
	@Autowired
	OrderInfoService orderInfoService;
	//技师info的service
	@Autowired
	ServiceTechnicianInfoService techService;
	//消息的service
	@Autowired
	private MessageInfoService messageInfoService;
	@Autowired
	private OrderInfoOperateService orderInfoOperateService;

	//查询订单列表
	@ResponseBody
    @RequestMapping(value = "${appPath}/getOrderListPage",  method = {RequestMethod.POST})
    @ApiOperation(value = "订单列表", notes = "订单")
    public AppResult getOrderListPage(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		//获取登录用户id 获取用户手机set进去
		Token token = (Token) request.getAttribute("token");
		orderInfo.setTechId(token.getTechId());
		if (StringUtils.isBlank(orderInfo.getServiceStatus())){
			return new AppFailResult(1,null,"订单服务状态不可为空");
		}
		if (orderInfo.getMajorSort().equals("all")){
			orderInfo.setMajorSort(null);
		}
		String pageSize1 = orderInfo.getPageSize();
		String pageNo = orderInfo.getPageNo();
		Page<OrderInfo> serSortInfoPage = new Page<>(request, response);
		serSortInfoPage.setPageSize(Integer.parseInt(pageSize1));
		serSortInfoPage.setPageNo(Integer.parseInt(pageNo));
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
		if(page.getList() ==null || page.getList().size()==0){
			return new AppSuccResult(1,null,"列表查询");
		}

		return new AppSuccResult(0,map,"列表查询");
    }
	//订单详情
	@ResponseBody
	@RequestMapping(value = "${appPath}/getOrderById",method = {RequestMethod.POST})
	@ApiOperation(value = "订单详情", notes = "订单")
	public AppResult getOrderById(OrderInfo info, HttpServletRequest request, HttpServletResponse response){
		//获取登录用户id
		Token token = (Token) request.getAttribute("token");
		info.setNowId(token.getTechId());
		try{
			OrderInfo orderInfo = orderInfoService.appFormData(info);
			return new AppSuccResult(0,orderInfo,"查询订单详情");
		}catch (ServiceException e ){
			return new AppFailResult(1,null,e.getMessage());
		}
	}

	//技师添加订单备注
	@ResponseBody
	@RequestMapping(value = "${appPath}/saveRemark",method = {RequestMethod.POST})
	@ApiOperation(value = "技师添加订单备注", notes = "订单")
	public AppResult saveRemark(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response){
		//获取技师id
		Token token = (Token) request.getAttribute("token");
		orderInfo.setNowId(token.getTechId());
		List<String> errList = errors(orderInfo, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		try{
			int i = orderInfoService.appSaveRemark(orderInfo);
			if (i>0){
				OrderInfo orderInfo1 = orderInfoService.appGet(orderInfo);
				String orderSource = orderInfo1.getOrderSource();
				//订单来源是国安社区的 需要对接
				if (StringUtils.isNotBlank(orderSource) && orderSource.equals("gasq")) {
					// //查询数据库获取订单对应的机构  获取对接code
					// OrderInfo info = orderInfoService.appGet(orderInfo);
					// BasicOrganization basicCode = orderInfoService.getBasicOrganizationByOrgId(info);
					// String orderSource1 = info.getOrderSource();
					// //获取商品的对接code
					// List<String> goodsCode = orderInfoService.getGoodsCode(orderInfo);
					// if (StringUtils.isNotBlank(basicCode.getJointEshopCode()) && goodsCode.size() > 0) {
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(orderInfo.getId());
					sendOrder.setOrderNumber(orderSn);//订单编号
					sendOrder.setOrderRemark(orderInfo.getOrderRemark());
					String orderRemarkPic = orderInfo.getOrderRemarkPic();
					if (StringUtils.isNotBlank(orderRemarkPic)) {
						List<String> strings = (List<String>) JsonMapper.fromJsonString(orderRemarkPic, ArrayList.class);
						orderInfo.setOrderRemarkPics(strings);
					}
					sendOrder.setOrderRemarkPics(orderInfo.getOrderRemarkPics());
					OpenSendUtil.openSendSaveOrder(sendOrder);
					/*OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						return new AppFailResult(-1, null, "对接失败N");
					} else if (sendResponse.getCode() != 0) {
						return new AppFailResult(-1, null, sendResponse.getMessage());
					}*/
					// }
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
	@RequestMapping(value = "${appPath}/updateOrderByServiceStatus",method = {RequestMethod.POST})
	@ApiOperation(value = "修改服务状态", notes = "订单")
	public AppResult updateOrderByServiceStatus(OrderInfo info, HttpServletRequest request, HttpServletResponse response){
		List<String> errList = errors(info, SavePersonalGroup.class);
		if (errList != null && errList.size() > 0) {
			return new AppFailResult(errList);
		}
		//参数 订单id 服务状态
		Token token = (Token) request.getAttribute("token");
        OrderInfo orderInfo = orderInfoService.appGet(info);
		info.setNowId(token.getTechId());
		try{
            //如果数据库中订单的状态是完成状态 不管是否成功 全返回成功
			if (!"finish".equals(orderInfo.getServiceStatus())){
                int i = orderInfoService.appSaveOrder(info);
				if (i>0){
					return new AppSuccResult(0,null,"修改服务状态成功");
				}
				return new AppFailResult(-1,null,"修改服务状态失败");
			}
			return new AppSuccResult(0,null,"修改服务状态成功");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}


	@ResponseBody
	@RequestMapping(value = "${appPath}/techList",method = {RequestMethod.POST})
	@ApiOperation("技师列表")
	public AppResult techList(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token) request.getAttribute("token");
		String phone = token.getPhone();
		orderInfo.setTechPhone(phone);
		Map<String,Object> map=new HashMap<String,Object>();
		//判断订单状态
		boolean flag = orderInfoOperateService.checkOrderStatus(orderInfo);
		if(!flag){
			return new AppFailResult(1,null,"当前订单状态或服务状态不允许操作此项内容");
		}
		try{
			List<OrderDispatch> techList = orderInfoOperateService.appTech(orderInfo);
			PropertiesLoader loader = new PropertiesLoader("oss.properties");
			String ossHost = loader.getProperty("OSS_THUMB_HOST");
			List<AppServiceTechnicianInfo> apt=new ArrayList<AppServiceTechnicianInfo>();
			if (techList !=null && techList.size()>0){
				for (OrderDispatch dis:techList){
					ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
					tec.setId(dis.getTechId());
					AppServiceTechnicianInfo technicianById = techService.getTechnicianById(tec);
					technicianById.setImgUrl(ossHost + technicianById.getImgUrlHead());
					apt.add(technicianById);
				}
				map.put("list",apt);
				return new AppSuccResult(0,map,"技师列表");
			}
			return new AppSuccResult(1,null,"技师列表");
		}catch (ServiceException e){
			return new AppFailResult(1,null,e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "${appPath}/appDispatchTechSave", method = {RequestMethod.POST})
	@ApiOperation("技师改派保存")
	public AppResult appDispatchTechSave(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response) {
		Token token = (Token) request.getAttribute("token");
		//传的参数 改派技师的id
		String techId = orderInfo.getTechId();
		//获取订单信息
		orderInfo = orderInfoService.appGet(orderInfo);
		orderInfo.setTechId(techId);
		//改派前技师id
		orderInfo.setDispatchTechId(token.getTechId());
		try {
			//改派并发送消息
			int i = orderInfoService.appDispatchTechSave(orderInfo);
			if (i > 0){
				// 给改派后技师发送消息
				ServiceTechnicianInfo technicianInfo=new ServiceTechnicianInfo();
				technicianInfo.setId(techId);
				ServiceTechnicianInfo byId = techService.getById(technicianInfo);
				//给改派的技师发送消息
				OrderDispatch new1 = new OrderDispatch();
				new1.setTechId(techId);
				new1.setOrderId(orderInfo.getId());
				new1.setTechPhone(byId.getPhone());
				List<OrderDispatch> list1=new ArrayList<OrderDispatch>();
				list1.add(new1);
				orderInfo.setTechList(list1);
				messageInfoService.insert(orderInfo,"orderCreate");

				//查询数据库获取订单对应的机构  获取对接code
				OrderInfo info = orderInfoService.get(orderInfo.getId());
				String orderSource = info.getOrderSource();
				//订单来源是国安社区的 需要对接
				if (StringUtils.isNotBlank(orderSource) && orderSource.equals("gasq")) {
					// BasicOrganization basicCode = orderInfoService.getBasicOrganizationByOrgId(info);
					// //获取商品的对接code
					// List<String> goodsCode = orderInfoService.getGoodsCode(orderInfo);
					// if (StringUtils.isNotBlank(basicCode.getJointEshopCode()) && goodsCode.size() > 0) {
					OrderInfo sendOrder = new OrderInfo();

					String orderSn = orderInfoService.getOrderSnById(orderInfo.getId());
					sendOrder.setOrderNumber(orderSn);//订单编号

					//sendOrder.setId(orderInfo.getId());
					List<OrderDispatch> orderDispatchList = orderInfoService.getOrderDispatchList(info);
					sendOrder.setTechList(orderDispatchList);
					OpenSendUtil.openSendSaveOrder(sendOrder);
					/*OpenSendSaveOrderResponse sendResponse = OpenSendUtil.openSendSaveOrder(sendOrder);
					if (sendResponse == null) {
						return new AppFailResult(-1, null, "对接失败N");
					} else if (sendResponse.getCode() != 0) {
						return new AppFailResult(-1, null, sendResponse.getMessage());
					}*/
					// }
				}
				return new AppSuccResult(0,null,"改派成功");
			}
			return new AppFailResult(-1,null,"改派失败");
		}catch (ServiceException e){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}

	/**
	 * 订单支付 修改订单的支付状态
	 * 参数： 订单id 订单支付状态（要去协商是否需要）
	 * @param info
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "${appPath}/savePayStatus",method = {RequestMethod.POST})
	@ApiOperation(value = "订单支付", notes = "订单")
	public AppResult savePayStatus(OrderInfo info, HttpServletRequest request, HttpServletResponse response){
		//获取登录用户id
		Token token = (Token) request.getAttribute("token");
		//根据id获取订单
		info.setNowId(token.getTechId());
		try{
			int i= orderInfoService.savePayStatus(info);
			if (i>0){
				return new AppSuccResult(0,null,"支付成功");
			}
			return new AppFailResult(-1,null,"支付失败");
		}catch (ServiceException e ){
			return new AppFailResult(-1,null,e.getMessage());
		}
	}
}
