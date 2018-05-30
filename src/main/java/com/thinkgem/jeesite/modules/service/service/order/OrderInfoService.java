/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortInfoDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderInfoService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	SerItemInfoDao serItemInfoDao;
	@Autowired
	OrderPayInfoDao orderPayInfoDao;
	@Autowired
	BasicServiceStationDao basicServiceStationDao;
	@Autowired
	TechScheduleDao techScheduleDao;
	@Autowired
	OrderRefundGoodsDao orderRefundGoodsDao;
	@Autowired
	OrderRefundDao orderRefundDao;
	@Autowired
	OrderInfoOperateService orderInfoOperateService;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	SerItemCommodityDao serItemCommodityDao;
	@Autowired
	SerSortInfoDao serSortInfoDao;
	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
	OrderCombinationGasqDao orderCombinationGasqDao;

	public OrderInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderInfo> findList(OrderInfo orderInfo) {
		return super.findList(orderInfo);
	}

	public Page<OrderInfo> findPage(Page<OrderInfo> page, OrderInfo orderInfo) {
		User user = UserUtils.getUser();
		orderInfo.getSqlMap().put("dsf", dataRoleFilterForOrder(user, "a"));
		Page<OrderInfo> pageResult = super.findPage(page, orderInfo);
		return pageResult;
	}

	/**
	 * 订单详情
	 * @param info
	 * @return
	 */
	public OrderInfo formData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		if(orderInfo == null){
			return null;
		}
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			OrderGoods goodsInfo = new OrderGoods();
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			//goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			//goodsInfo.setItemName(goodsInfoList.get(0).getItemName());

			for(OrderGoods orderGoods : goodsInfoList){
				String dj = orderGoods.getPayPrice();//商品单价
				if(StringUtils.isEmpty(dj)){
					dj = "0";
				}
				int num = orderGoods.getGoodsNum();//商品数量
				BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
				orderGoods.setPayPriceSum(price.toString());//总价
			}

			goodsInfo.setGoods(goodsInfoList);
			orderInfo.setGoodsInfo(goodsInfo);
		}

		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		orderInfo.setTechList(techList);

		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(null != customerRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		if(null != businessRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic,ArrayList.class);
			orderInfo.setBusinessRemarkPics(pictureDetails);
		}
		/*
		门店备注图片暂时不展示
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			orderInfo.setShopRemarkPics(pictureDetails);
		}*/
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if(null != orderRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			orderInfo.setOrderRemarkPics(pictureDetails);
		}

		// 地址模糊显示
		String orgVisable = orderInfo.getOrgVisable();
		OrderAddress addressInfo = orderInfo.getAddressInfo();

		if("gasq".equals(orderInfo.getOrderSource()) && "no".equals(orgVisable)){
			if(addressInfo != null){
				OrderAddress address = new OrderAddress();
				String name = addressInfo.getName();
				if(StringUtils.isNotBlank(name) && name.length() > 0){
					String newName = name.substring(0,1);
					for(int i=1;i<name.length();i++){
						newName = newName.concat("*");
					}
					address.setName(newName);
				}else{
					address.setName(name);
				}
				String phone = addressInfo.getPhone();
				if(StringUtils.isNotBlank(phone) && phone.length() == 11){
					address.setPhone(phone.substring(0, 3) + "****" + phone.substring(7, phone.length()));
				}else{
					address.setPhone(phone);
				}
				address.setDetailAddress(addressInfo.getPlacename() + "***");
				orderInfo.setAddressInfo(address);
			}

			String customerName = orderInfo.getCustomerName();
			if(StringUtils.isNotBlank(customerName) && customerName.length() > 0){
				String newCustomerName = customerName.substring(0,1);
				for(int i=1;i<customerName.length();i++){
					newCustomerName = newCustomerName.concat("*");
				}
				orderInfo.setCustomerName(newCustomerName);
			}
			String customerPhone = orderInfo.getCustomerPhone();
			if(StringUtils.isNotBlank(customerPhone) && customerPhone.length() == 11){
				orderInfo.setCustomerPhone(customerPhone.substring(0, 3) + "****" + customerPhone.substring(7, customerPhone.length()));
			}

		}else{
			if(addressInfo != null){
				OrderAddress address = new OrderAddress();
				address.setName(addressInfo.getName());
				address.setPhone(addressInfo.getPhone());
				address.setDetailAddress(addressInfo.getAddress());
				orderInfo.setAddressInfo(address);
			}
		}

        List<OrderRefundGoods> refundList = orderRefundGoodsDao.listRefundGoodsByOrderId(info);
        if(refundList!=null && refundList.size()>0){
            orderInfo.setOrderRefundFlag(true);
        }else{
            orderInfo.setOrderRefundFlag(false);
        }

        if(goodsInfoList.size() == refundList.size()){
            orderInfo.setOrderAllRefundFlag(true);
        }else{
            orderInfo.setOrderAllRefundFlag(false);
        }
		return orderInfo;
	}

	//app查询详情
	public OrderInfo appFormData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		//图片路径
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		if (null == orderInfo){
			throw new ServiceException("没有该订单");
		}
		orderInfo.setNowId(info.getNowId());
		OrderGoods goodsInfo = new OrderGoods();
		//服务时长
		Double serviceHour = orderInfo.getServiceHour();
		orderInfo.setServiceHourStr(DateUtils.dateToStrDays(serviceHour));
		//订单的类型
		String orderType = orderInfo.getOrderType();
		if ("common".equals(orderType)){
			orderInfo.setOrderTypeName("普通订单");
			//如果是普通订单 应付是订单中的价格
			orderInfo.setPayable(orderInfo.getPayPrice());
		}
		if ("group_split_yes".equals(orderType)){
			orderInfo.setOrderTypeName("组合订单（多次服务）");
		}
		if ("group_split_no".equals(orderType)){
			orderInfo.setOrderTypeName("组合订单（单次服务）");
		}
		//如果是组合订单 应付是组合订单中的价格 用masterId去查组合订单表
		//展示订单类型、组合商品名称、订单组ID、对接订单组ID
		if (!"common".equals(orderType)){
			CombinationOrderInfo combinationByMasterId = combinationOrderDao.getCombinationByMasterId(orderInfo.getMasterId());
			orderInfo.setCombinationGoodsName(combinationByMasterId.getCombinationGoodsName());
			orderInfo.setJointGroupId(combinationByMasterId.getJointGroupId());
			orderInfo.setPayable(combinationByMasterId.getPayPrice());
		}
		//保持支付表与订单表中支付状态一致  支付按钮的出现时机
		String payStatus1 = orderInfo.getPayInfo().getPayStatus();
		orderInfo.setPayStatus(payStatus1);
		if ("payed".equals(orderInfo.getPayInfo().getPayStatus())){
			orderInfo.setRealPayment(orderInfo.getPayInfo().getPayAccount());
		}else {
			orderInfo.setRealPayment(Integer.toString(0));
		}
		//服务信息
		List<OrderGoods> goodsInfoList = null;
		//获取服务项目的id集合
		List<String> ids = new ArrayList<>();
		if ("group_split_no".equals(orderType)){
			// 如果是组合不拆单 在order_combination_info获取数量和id 再去ser_item_info_goods找价格 masterId
			goodsInfoList = dao.getOrderGoodsListByMasterId(orderInfo);
		}else {
			//普通订单和组合并拆单 去order_goods查子商品信息   orderId
			goodsInfoList = dao.getOrderGoodsList(info);
		}
		List<OrderGoods> tem=new ArrayList<OrderGoods>();
		if(goodsInfoList != null && goodsInfoList.size() > 0){
			//获取服务项目id集合
			for (int i=0;i<goodsInfoList.size();i++) {
				if(!ids.contains(goodsInfoList.get(i).getItemId())) {
					ids.add(goodsInfoList.get(i).getItemId());
				}
			}
			//循环 ids
			for(String id:ids){
				//根据id获取服务项目的名称 图片 服务时间
				OrderGoods orderGoods = serItemInfoDao.getById(id);
				List<OrderGoods> orderGoodsList1 = new ArrayList<>();
				for (int i=0;i<goodsInfoList.size();i++) {
					//如果服务项目的id与商品的服务项目id相同
					if(id.equals(goodsInfoList.get(i).getItemId())){
						//加入到集合中
						orderGoodsList1.add(goodsInfoList.get(i));
						orderGoods.setItemName(goodsInfoList.get(i).getItemName());
					}
				}
				orderGoods.setGoods(orderGoodsList1);
				String picture = orderGoods.getPicture();
				if (StringUtils.isNotBlank(picture)) {
					List<String> picl = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
					if (picl !=null && picl.size()>0){
						orderGoods.setPicture(ossHost+picl.get(0));
					}
				}
				tem.add(orderGoods);
			}
		}else {
			throw new ServiceException("没有商品信息");
		}
		orderInfo.setGoodsInfoList(tem);
		//app的退款详情
		OrderRefund orderRefundInit = orderInfoOperateService.getOrderRefundInit(orderInfo);
		if (orderRefundInit.getGoodsInfoList() != null && orderRefundInit.getGoodsInfoList().size() > 0){
            orderInfo.setRefundInfo(orderRefundInit);
        }
		//根据订单id查询出
		//app的技师列表 appTechList
		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		if (techList==null || techList.size()==0){
			throw new ServiceException("没有技师");
		}
		orderInfo.setGoodsInfo(goodsInfo);
		orderInfo.setTechList(techList);
		List<String> idList=new ArrayList<String>();
		//app其他技师
		List<AppServiceTechnicianInfo> appTechList=new ArrayList<AppServiceTechnicianInfo>();
		for (OrderDispatch apt:techList){
			ServiceTechnicianInfo temInfo=new ServiceTechnicianInfo();
			temInfo.setId(apt.getTechId());
			//当前登陆用户信息
			AppServiceTechnicianInfo technicianById = serviceTechnicianInfoDao.getTechnicianById(temInfo);
			//当前登陆用户的id是否与当前订单的拥有人id不相同 取出不相同的技师放入其他技师list
			if (technicianById !=null){
				if (!technicianById.getId().equals(orderInfo.getNowId())){
					appTechList.add(technicianById);
				}
				//将订单下的技师id取出来
				idList.add(technicianById.getId());
			}
		}
		//如果包含这个id 可以操作 不包含 不能对订单进行操作
		if (idList != null && idList.size()>0) {
			if (idList.contains(orderInfo.getNowId())) {
				orderInfo.setIsTech("yes");
			}
		}
		orderInfo.setAppTechList(appTechList);
		//客户信息
		OrderCustomInfo customerInfo=new OrderCustomInfo();
		customerInfo.setCustomerRemark(orderInfo.getCustomerRemark());
		List<String> ll=new ArrayList<String>();
		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(StringUtils.isNotBlank(customerRemarkPic)){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			if (pictureDetails.size()>0){
				for (String s:pictureDetails){
					String url=ossHost+s;
					ll.add(url);
				}
			}
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		customerInfo.setCustomerRemarkPic(ll);
		orderInfo.setCustomerInfo(customerInfo);
		String orderSource = orderInfo.getOrderSource();
		if (StringUtils.isNotBlank(orderSource)) {
			if (orderSource.equals("own")) {
				orderInfo.setOrderSourceName("本机构");
			} else if (orderSource.equals("gasq")) {
				orderInfo.setOrderSourceName("国安社区");
			}
		}
		//业务人员信息 订单来源是国安社区的展示
		if ("gasq".equals(orderSource)) {
			BusinessInfo bus = new BusinessInfo();
			bus.setBusinessName(orderInfo.getBusinessName());
			bus.setBusinessPhone(orderInfo.getBusinessPhone());
			bus.setBusinessRemark(orderInfo.getBusinessRemark());
			List<String> bp = new ArrayList<String>();
			String businessRemarkPic = orderInfo.getBusinessRemarkPic();
			if (StringUtils.isNotBlank(businessRemarkPic)) {
				List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic, ArrayList.class);
				orderInfo.setBusinessRemarkPics(pictureDetails);
				if (pictureDetails.size() > 0) {
					for (String pic : pictureDetails) {
						String url = ossHost + pic;
						bp.add(url);
					}
				}
			}
			bus.setBusinessRemarkPic(bp);
			orderInfo.setBusinessInfo(bus);
		//门店信息 订单来源是国安社区的展示
			ShopInfo shop = new ShopInfo();
			shop.setId(orderInfo.getStationId());
			shop.setShopName(orderInfo.getShopName());
			shop.setShopPhone(orderInfo.getShopPhone());
			shop.setShopAddress(orderInfo.getShopAddr());
			shop.setShopRemark(orderInfo.getShopRemark());
			orderInfo.setShopInfo(shop);
		}
		String serviceStatus = orderInfo.getServiceStatus();
		if (StringUtils.isNotBlank(serviceStatus)) {
			if (serviceStatus.equals("wait_service")) {
				orderInfo.setServiceStatusName("待服务");
			} else if (serviceStatus.equals("started")) {
				orderInfo.setServiceStatusName("已上门");
			} else if (serviceStatus.equals("finish")) {
				orderInfo.setServiceStatusName("已完成");
			} else if (serviceStatus.equals("cancel")) {
				orderInfo.setServiceStatusName("已取消");
			}
		}
		String orderStatus = orderInfo.getOrderStatus();
		if (StringUtils.isNotBlank(orderStatus)) {
			if (orderStatus.equals("waitdispatch")) {
				orderInfo.setOrderStatusName("待派单");
			} else if (orderStatus.equals("dispatched")) {
				orderInfo.setOrderStatusName("已派单");
			} else if (orderStatus.equals("cancel")) {
				orderInfo.setOrderStatusName("已取消");
			} else if (orderStatus.equals("started")) {
				orderInfo.setOrderStatusName("已上门");
			} else if (orderStatus.equals("finish")) {
				orderInfo.setOrderStatusName("已完成");
			} else if (orderStatus.equals("success")) {
				orderInfo.setOrderStatusName("已成功");
			} else if (orderStatus.equals("stop")) {
				orderInfo.setOrderStatusName("已暂停");
			}else if (orderStatus.equals("close")) {
                orderInfo.setOrderStatusName("已关闭");
            }
		}
		String payStatus = orderInfo.getPayStatus();
		if (StringUtils.isNotBlank(payStatus)) {
			if ("waitpay".equals(payStatus)) {
				orderInfo.setPayStatusName("待支付");
			} else if ("payed".equals(payStatus)) {
				orderInfo.setPayStatusName("已支付");
			}
		}
		//订单取消原因  如果不为空
		String cancelReason = orderInfo.getCancelReason();
		if (StringUtils.isNotBlank(cancelReason)){
			if ("other".equals(cancelReason)){
				orderInfo.setCancelReasonName("其它原因");
			}
			if ("tech".equals(cancelReason)){
				orderInfo.setCancelReasonName("无可派技师");
			}
			if ("customer".equals(cancelReason)){
				orderInfo.setCancelReasonName("客户来电取消");
			}
		}

		//订单备注 数据库中的json 存的是list
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		List<String> orp=new ArrayList<String>();
		if (StringUtils.isNotBlank(orderRemarkPic)){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			if(pictureDetails.size()>0){
				for (String pic:pictureDetails){
					String url=ossHost+pic;
					orp.add(url);
				}
			}
		}
		orderInfo.setOrderRemarkPics(orp);
		//如果是来源是本机构的已支付的显示支付方式
		if ("payed".equals(payStatus) && "own".equals(orderInfo.getOrderSource())) {
			//订单详情中对应的支付信息表
			String masterId = orderInfo.getMasterId();
			//如果主订单ID为空
			if (StringUtils.isBlank(masterId)) {
				throw new ServiceException("主订单ID不可为空");
			}
			//根据masterId 查询支付表
			OrderPayInfo byMasterId = orderPayInfoDao.getByMasterId(masterId);
			if (byMasterId == null) {
				throw new ServiceException("支付信息为空");
			}
			if ("cash".equals(byMasterId.getPayPlatform())) {
				byMasterId.setPayPlatform("现金");
			}
			orderInfo.setOrderPayInfo(byMasterId);
		}

		String orgVisable = orderInfo.getOrgVisable();
		OrderAddress addressInfo = orderInfo.getAddressInfo();

		if("gasq".equals(orderInfo.getOrderSource()) && "no".equals(orgVisable)){
			if(addressInfo != null){
				OrderAddress address = new OrderAddress();
				String name = addressInfo.getName();
				if(StringUtils.isNotBlank(name) && name.length() > 0){
					String newName = name.substring(0,1);
					for(int i=1;i<name.length();i++){
						newName = newName.concat("*");
					}
					address.setName(newName);
				}else{
					address.setName(name);
				}
				String phone = addressInfo.getPhone();
				if(StringUtils.isNotBlank(phone) && phone.length() == 11){
					address.setPhone(phone.substring(0, 3) + "****" + phone.substring(7, phone.length()));
				}else{
					address.setPhone(phone);
				}
				address.setAddress(addressInfo.getPlacename() + "***");
				orderInfo.setAddressInfo(address);
			}
		}else{
			if(addressInfo != null){
				OrderAddress address = new OrderAddress();
				address.setName(addressInfo.getName());
				address.setPhone(addressInfo.getPhone());
				address.setAddress(addressInfo.getAddress());
				orderInfo.setAddressInfo(address);
			}
		}
		return orderInfo;
	}


	//app订单支付 修改订单的支付状态
	@Transactional(readOnly = false)
	public int savePayStatus(OrderInfo orderInfo){
		int i=0;
		//当前登陆技师id
		String nowId = orderInfo.getNowId();
		//根据订单id获取当前订单
		OrderInfo info = orderInfoDao.appGet(orderInfo);
		if (info == null){
			throw new ServiceException("订单不可为空");
		}
		//订单支付状态
		String payStatus = info.getPayStatus();
		//订单服务状态
		String serviceStatus = info.getServiceStatus();
		//订单的状态是已取消 也不可点击支付
		String orderStatus = info.getOrderStatus();
		//订单来源
        String orderSource = info.getOrderSource();
        //订单来源为国安社区的 或者 为空的 不可点击支付
        if (StringUtils.isBlank(orderSource) || "gasq".equals(orderSource)){
            throw new ServiceException("订单来源不可为国安社区");
        }
        String masterId = info.getMasterId();
		//如果主订单ID为空
		if (StringUtils.isBlank(masterId)){
            throw new ServiceException("主订单ID不可为空");
        }
		//订单的状态是已取消 也不可点击支付
		if (StringUtils.isBlank(orderStatus) || "cancel".equals(orderStatus)){
			throw new ServiceException("订单已取消不可点击支付");
		}
		//修改订单表中支付状态
		//服务状态为已上门后，才可以支付
		if (StringUtils.isBlank(serviceStatus) || !"started".equals(serviceStatus)){
			throw new ServiceException("订单状态不正确，不可支付");
		}
		//订单状态必须为未支付
		if (StringUtils.isBlank(payStatus) || "payed".equals(payStatus)){
			throw new ServiceException("订单已经支付，无需再次支付");
		}
		//判断价格 master_id查询出对应的支付信息
		OrderPayInfo byMasterId = orderPayInfoDao.getByMasterId(masterId);
		if (byMasterId == null){
			throw new ServiceException("支付信息为空");
		}
		if (!orderInfo.getPayPrice().equals(byMasterId.getPayAccount())){
			throw new ServiceException("当前支付价格与订单实际价格不一致，请重新支付");
		}
		//支付信息中的支付状态
		String payStatus1 = byMasterId.getPayStatus();
		if (StringUtils.isBlank(payStatus1) || "payed".equals(payStatus1)){
			throw new ServiceException("订单已经支付，无需再次支付");
		}
		info.appPreUpdate();
		info.setPayStatus("payed");
		i = dao.appUpdatePay(info);
		//修改支付信息中的订单状态
		if (i>0) {
			byMasterId.appPreUpdate();
			byMasterId.setPayPlatform("cash");
			byMasterId.setPayMethod("offline");
			byMasterId.setPayTime(new Date());
			byMasterId.setPayTech(nowId);
			byMasterId.setPayStatus("payed");
			byMasterId.appPreUpdate();
			i = orderPayInfoDao.update(byMasterId);
		}else {
			throw new ServiceException("支付失败");
		}

		return i;
	}

	@Transactional(readOnly = false)
	public void delete(OrderInfo orderInfo) {
		super.delete(orderInfo);
	}

	//app获取补单商品表 根据订单id获取多个sortId位数小于3的商品
	public List<OrderGoods> getItemGoods(OrderInfo orderInfo) {
		//图片路径
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		List<OrderGoods> list= orderGoodsDao.listItemGoods(orderInfo);

        if (list != null && list.size() > 0) {
            for (OrderGoods orderGoods : list) {
                String picture = orderGoods.getPicture();
                if (StringUtils.isNotBlank(picture)) {
                    List<String> picl = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
                    if (picl !=null && picl.size()>0){
                        orderGoods.setPicture(ossHost+picl.get(0));
                    }
                }
                //如果是未选择的商品 将起购数量赋值给数量
                List<OrderGoods> goods = orderGoods.getGoods();
                for (OrderGoods good:goods) {
                    if ("false".equals(good.getIsChecked())) {
                        good.setGoodsNum(good.getMinPurchase());
                    }
                }
            }
        }
		return list;
	}
	//app补单保存 参数 order的id 商品goodsId  数量goodsNum
	@Transactional(readOnly = false)
	public int saveSupp(OrderInfo info) {
		int change=0;
		//首先 验证参数
		OrderInfo orderInfo = orderInfoDao.appGet(info);
		if (orderInfo == null){
			throw new ServiceException("该订单未找到，未传订单id");
		}
        String orderStatus = orderInfo.getOrderStatus();
        if (("cancel".equals(orderStatus)) || ("finish".equals(orderStatus)) || ("success".equals(orderStatus)) || ("stop".equals(orderStatus)) || ("close".equals(orderStatus))){
            throw new ServiceException("该订单的状态不允许补单");
        }
        if ("payed".equals(orderInfo.getPayStatus())){
            throw new ServiceException("该订单已支付，不可补单");
        }
        if (!"own".equals(orderInfo.getOrderSource())){
            throw new ServiceException("订单来源不是本机构，不可补单");
        }
        //查询支付表 masterId
        OrderPayInfo payInfoByOrderId = orderPayInfoDao.getByMasterId(orderInfo.getMasterId());
        if (payInfoByOrderId == null){
            throw new ServiceException("未找到支付表");
        }
        if ("payed".equals(payInfoByOrderId.getPayStatus())){
            throw new ServiceException("订单已支付，不可补单");
        }
		//订单原总价
		String originPrice = orderInfo.getOriginPrice();
		//传参的补单商品集合
		List<OrderGoods> goodsInfoList = info.getGoodsInfoList();
		//1.根据订单id先去修改orderGood表 将订单商品已有的补单商品删除
		//根据订单id查询所有的订单商品表中分类id小于3的商品集合
		List<OrderGoods> orderOldSuppGoods = orderGoodsDao.getbyOrderId(info);

        //如果订单商品中没有补单商品  且没有传补单商品
        if (goodsInfoList == null || goodsInfoList.size() < 1){
            if (orderOldSuppGoods == null || orderOldSuppGoods.size() <1) {
                return 1;
            }
        }
		BigDecimal countOld =new BigDecimal(0);
		BigDecimal paypriceSum =new BigDecimal(0);
		if (orderOldSuppGoods != null && orderOldSuppGoods.size() > 0){
			for (OrderGoods good:orderOldSuppGoods){
				//已有补单的商品总价
				//单个商品总价
				paypriceSum = new BigDecimal(good.getPayPrice()).multiply(new BigDecimal(good.getGoodsNum()));
				countOld = countOld.add(paypriceSum);
				// 删除 good
				int i = orderGoodsDao.deleteById(good);
				if (i < 0){
					throw new ServiceException("删除订单原商品失败");
				}
			}
		}
		//2.循环商品表 将补单商品新增至数据库中
		BigDecimal countNew =new BigDecimal(0);
		BigDecimal paypriceSumNew =new BigDecimal(0);
        if (goodsInfoList != null || goodsInfoList.size() > 0) {
            for (OrderGoods goods : goodsInfoList) {
                //根据商品id查询出商品
                SerItemCommodity serItemCommodity = serItemCommodityDao.get(goods.getGoodsId());
                //商品的最低起购数量
				int minPurchase = serItemCommodity.getMinPurchase();
				if (minPurchase > goods.getGoodsNum()) {
					throw new ServiceException("选中的商品"+serItemCommodity.getName()+"数量不可低于商品的起购数量");
				}
                //查询商品的项目名
                if (serItemCommodity == null) {
                    throw new ServiceException("未找到该商品");
                }
                OrderGoods byId = serItemInfoDao.getById(serItemCommodity.getItemId());
                //传入的补单商品总价
                // 单种商品总价 数量* 单价
				paypriceSumNew = serItemCommodity.getPrice().multiply(new BigDecimal(goods.getGoodsNum()));
				countNew = countNew.add(paypriceSumNew);
                // 插入 订单商品表
                goods.setOrderId(info.getId());
                goods.setSortId(serItemCommodity.getSortId());
                goods.setItemId(serItemCommodity.getItemId());
                goods.setItemName(byId.getItemName());
                goods.setGoodsName(serItemCommodity.getName());
                goods.setGoodsType(serItemCommodity.getType());
                goods.setGoodsUnit(serItemCommodity.getUnit());
                goods.setPayPrice(serItemCommodity.getPrice().toString());
                goods.setOriginPrice(serItemCommodity.getPrice().toString());
                goods.appreInsert();
                change = orderGoodsDao.insert(goods);
            }
        }
		if (change < 0){
			throw new ServiceException("新增补单商品失败");
		}
		//3. 计算新的商品总价
        //新增补单商品总价-已有补单总价
		BigDecimal subtract = countNew.subtract(countOld);
		//原订单总价
		BigDecimal bigDecimal = new BigDecimal(originPrice);
		//补单后订单商品总价
		BigDecimal add = bigDecimal.add(subtract);
		//将商品分类 商品名称拼接
		List<String> sortList=new ArrayList<String>();
		List<OrderGoods> orderGoodsList = orderInfoDao.getOrderGoodsList(info);

		if (orderGoodsList != null && orderGoodsList.size() > 0) {
			for (OrderGoods orderGoods:orderGoodsList) {
				SerSortInfo serSortInfo = serSortInfoDao.get(orderGoods.getSortId());
				sortList.add(serSortInfo.getId());
			}
		}
		//将分类id集合去重
		Set set = new  HashSet();
		//最后的 项目id集合
		List<String> newList = new  ArrayList<String>();
		for (String cd:sortList) {
			if(set.add(cd)){
				newList.add(cd);
			}
		}
		List<String> strList = new  ArrayList<String>();
		if (newList != null && newList.size() > 0) {
			for (String id : newList) {
				String orderContent = "";
				SerSortInfo serSortInfo = serSortInfoDao.get(id);
				for (OrderGoods orderGoods:orderGoodsList) {
					if (id.equals(orderGoods.getSortId())) {
						SerItemCommodity serItemCommodity = serItemCommodityDao.get(orderGoods.getGoodsId());
						orderContent =orderContent+ "+"+serItemCommodity.getName();
					}
				}
				orderContent=serSortInfo.getName()+orderContent;
				strList.add(orderContent);
			}
		}
		// 修改数据库
		String content="";
		if (strList != null && strList.size() > 0) {
			for (int i=0 ;i< strList.size(); i++ ) {
				if("".equals(content)){
					content =  strList.get(i);
				}else {
					content = content  + "、" +  strList.get(i);
				}
			}
		}
		orderInfo.setOrderContent(content);
		orderInfo.appPreUpdate();
		//4.如果订单总价改变 修改订单表和订单支付表
		if (!originPrice.equals(add.toString())){
			orderInfo.setPayPrice(add.toString());
			orderInfo.setOriginPrice(add.toString());
			int update =0;
			try{
            	update = orderInfoDao.update(orderInfo);
			}catch (Exception e){
				//长度过长捕获异常信息
				throw new ServiceException("修改数据库失败可能是长度过长");
			}
            if(update < 0){
                throw new ServiceException("更新订单总额失败");
            }
            payInfoByOrderId.setPayAccount(add.toString());
			payInfoByOrderId.appPreUpdate();
			change=orderPayInfoDao.update(payInfoByOrderId);
		}else {
			try{
				change = orderInfoDao.update(orderInfo);
			}catch (Exception e){
				//长度过长捕获异常信息
				throw new ServiceException("修改数据库失败可能是长度过长");
			}
		}
		return change;
	}
	/**
	 * 请求订单列表时返回查询条件服务机构下拉列表
	 * @return
	 */
	public List<BasicOrganization> findOrganizationList() {
		BasicOrganization organization = new BasicOrganization();
		organization.getSqlMap().put("dsf", dataOrganFilter(UserUtils.getUser(), "a"));
		return dao.findOrganizationList(organization);
	}

	/**
	 * 取消订单
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public void cancelData(OrderInfo orderInfo) {
		dao.cancelData(orderInfo);
	}

	/**
	 * 新增、改派判断库存
	 * @param techIdList
	 * @param serviceTime
	 * @param finishTime
	 * @return
	 */
	private boolean checkTechTime(List<String> techIdList, Date serviceTime, Date finishTime) {
		boolean flag = true;
		String stationId = "";
		List<String> techIdListFull = new ArrayList<>();
		List<String> techIdListPart = new ArrayList<>();
		if(techIdList!=null && techIdList.size()>0) {
			for (String techId : techIdList) {
				ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoDao.get(techId);
				if (technicianInfo != null) {
					if("no".equals(technicianInfo.getStatus())){
						return false;
					}
					if("full_time".equals(technicianInfo.getJobNature())) {//岗位性质（full_time:全职，part_time:兼职）
						techIdListFull.add(techId);
					}else{
						techIdListPart.add(techId);
					}
					stationId= technicianInfo.getStationId();
				}
			}
		}else{
			return false;
		}
		int techNum = techIdList.size();
		List<String> techListRe = new ArrayList<>();
		if(techIdListPart!=null && techIdListPart.size()>0) {
			for(String techId : techIdListPart){
				techListRe.add(techId);
			}
		}
		if(techIdListFull!=null && techIdListFull.size()>0) {
			OrderDispatch serchInfo = new OrderDispatch();
			serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
			serchInfo.setStartTime(serviceTime);
			serchInfo.setEndTime(finishTime);
			serchInfo.setStationId(stationId);
			List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
			//（4）考虑技师的休假时间
			List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

			List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
			List<String> beforTimeCheckTechIdList = new ArrayList<String>();

			for(String techId : techIdListFull){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(techId))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(techId))) ){
					beforTimeCheckTechIdList.add(techId);
				}
			}

			if(beforTimeCheckTechIdList.size() != 0){
				serchInfo.setTechIds(beforTimeCheckTechIdList);
				//今天的订单
				serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
				serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
				//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
				List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//技师列表

				List<String> timeCheckDelTechIdList = new ArrayList<String>();

				int intervalTimeS =  Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
				int intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

				Date checkServiceTime = DateUtils.addSecondsNotDayB(serviceTime, -intervalTimeS);
				Date checkFinishTime = DateUtils.addSecondsNotDayE(serviceTime, intervalTimeE);

				for(OrderDispatch orderTech : orderTechList){
					//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
					//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
					//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
					//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
					//（4）富余时间定为10分钟"
					if (!DateUtils.checkDatesRepeat(checkServiceTime, checkFinishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
						timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
					}
				}
				for(String techId : beforTimeCheckTechIdList){
					if(!timeCheckDelTechIdList.contains(techId)){
						techListRe.add(techId);
					}
				}
			}
		}
		if(techListRe.size() != techNum){
			return false;
		}

		return flag;
	}

	//app 通过id获取订单详情
	public OrderInfo appGet(OrderInfo info){
		return dao.appGet(info);
	}
	//app改派保存调用 获取技师列表
	public List<OrderDispatch> getOrderDispatchList(OrderInfo info){
		List<OrderDispatch> orderDispatchList = dao.getOrderDispatchList(info);
		return orderDispatchList;
	}
	//app改派保存
	@Transactional(readOnly = false)
	public Map<String,Object> appDispatchTechSave(OrderInfo orderInfo) {
		int insert =0;
		//改派前技师ID
		String dispatchTechId = orderInfo.getDispatchTechId();
		//改派后技师id
		String techId = orderInfo.getTechId();
		List<OrderInfo> orderInfoList=new ArrayList<OrderInfo>();
		String orderGroupId = null;
		Map<String,Object> map=new HashMap<String,Object>();
		if (!"group_split_yes".equals(orderInfo.getOrderType())) {
			orderInfoList.add(orderInfo);
		}else {
			//根据orderNumber masterId获取组合订单的orderGroupId
			OrderCombinationGasqInfo listByOrderNumber = orderCombinationGasqDao.getListByOrderNumber(orderInfo);
			orderGroupId = listByOrderNumber.getOrderGroupId();
			//根据groupId获取组合订单的订单集合
			orderInfoList = orderInfoDao.getOrderListByOrderGroupId(listByOrderNumber);
		}
		if (orderInfoList == null || orderInfoList.size() < 1){
			throw new ServiceException("未找到该订单！");
		}
		orderInfo = orderInfoList.get(0);
		String orderId = orderInfo.getId();
		ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
		Date serviceTime = orderInfo.getServiceTime();
		Double serviceHour = orderInfo.getServiceHour();
		Date finishTime = orderInfo.getSuggestFinishTime();
		List techIdList=new ArrayList();
		techIdList.add(techId);
		//新增、改派判断库存
		boolean flag = checkTechTime(techIdList,serviceTime,finishTime);
		if(!flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}
		//改派技师信息
		tec.setId(techId);
		ServiceTechnicianInfo newTech = serviceTechnicianInfoDao.getById(tec);
		//订单的服务站id 与技师的服务站id匹配
		if (newTech!=null){
			if (!newTech.getStationId().equals(orderInfo.getStationId())){
				throw new ServiceException("选择技师不属于该服务站！");
			}
		}else{
			throw new ServiceException("选择技师不可为空！");
		}
		// 将数据库中改派前技师排期表设为无效
		TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
		if (!"group_split_yes".equals(orderInfo.getOrderType())){
			techScheduleInfo.setTypeId(orderInfo.getId());
		}else {
			techScheduleInfo.setTypeId(orderGroupId);
		}
		if ("common".equals(orderInfo.getOrderType())){
			techScheduleInfo.setType("order");
		}else{
			techScheduleInfo.setType("master");
		}
		techScheduleInfo.setTechId(dispatchTechId);
		techScheduleInfo.appPreUpdate();
		techScheduleDao.deleteScheduleByTypeIdTechId(techScheduleInfo);
		if (StringUtils.isNotBlank(orderInfo.getServiceStatus()) && StringUtils.isNotBlank(orderInfo.getOrderStatus())) {
			//订单服务状态为 wait_service:待服务 started:已上门 订单状态 waitdispatch:待派单;dispatched:已派单 可以改派
			if ((orderInfo.getServiceStatus().equals("wait_service") || orderInfo.getServiceStatus().equals("started"))
					&& (orderInfo.getOrderStatus().equals("waitdispatch") || orderInfo.getOrderStatus().equals("dispatched")) ) {
				//普通订单改派
					for (OrderInfo info:orderInfoList){
						info.setDispatchTechId(dispatchTechId);
						//根据订单技师  获取老派单 订单id和改派前技师id去查
						OrderDispatch orderDispatch = dao.appGetOrderDispatch(info);
						//如果派单为空 说明订单不属于该技师
						if(null == orderDispatch){
							throw new ServiceException("订单不属于该技师！");
						}
					//1. 数据库将改派前技师派单 设为不可用
						OrderDispatch orderDispatchUpdate = new OrderDispatch();
						orderDispatchUpdate.setId(orderDispatch.getId());//ID
						orderDispatchUpdate.setStatus("no");//状态(yes：可用 no：不可用)
						orderDispatchUpdate.appPreUpdate();
						int update1 = orderDispatchDao.update(orderDispatchUpdate);
						if (update1 < 1){
							throw new ServiceException("删除改派前技师派单失败");
						}
					//2. 新增派单
						OrderDispatch newDis = new OrderDispatch();
						newDis.setTechId(techId);//技师ID
						newDis.setOrderId(info.getId());//订单ID
						newDis.setStatus("yes");//状态(yes：可用 no：不可用)
						newDis.appreInsert();
						insert = orderDispatchDao.insert(newDis);
						if (insert < 1){
							throw new ServiceException("新增技师派单失败");
						}
					}
				// 新增改派后的排期表
				TechScheduleInfo scheduleInfo = new TechScheduleInfo();
				scheduleInfo.setTechId(techId);
				if ("common".equals(orderInfo.getOrderType())){
					scheduleInfo.setType("order");
					scheduleInfo.setTypeId(orderId);
				}else if ("group_split_no".equals(orderInfo.getOrderType())){
					scheduleInfo.setType("master");
					scheduleInfo.setTypeId(orderId);
				}else {
					scheduleInfo.setType("master");
					scheduleInfo.setTypeId(orderGroupId);
				}
				//计算排期表的开始 结束时间
				int size = orderInfoList.size();//serviceHour
				Double serviceSecondTem = (serviceHour * size * 3600);
				Date date = DateUtils.addSeconds(serviceTime, serviceSecondTem.intValue());
				scheduleInfo.setStartTime(serviceTime);
				scheduleInfo.setEndTime(date);
				int weekNum = DateUtils.getWeekNum(serviceTime);
				scheduleInfo.setScheduleWeek(weekNum);
				Date dateFirstTime = DateUtils.getDateFirstTime(serviceTime);
				scheduleInfo.setScheduleDate(dateFirstTime);
				scheduleInfo.setMasterId(orderInfo.getMasterId());
				scheduleInfo.appreInsert();
				insert = techScheduleDao.insertSchedule(scheduleInfo);
				if (insert < 0){
					throw new ServiceException("新增改派后技师排期失败");
				}
				map.put("orderInfoList",orderInfoList);
				return  map;
			}else {
				throw new ServiceException("订单状态与服务状态不符合改派要求！");
			}
		}else {
			throw new ServiceException("订单状态与服务状态不可为空！");
		}
	}

	//app订单列表
	public Page<OrderInfo> appFindPage(Page<OrderInfo> page, OrderInfo orderInfo) {
		orderInfo.setPage(page);
		//查询已完成订单列表
		if (orderInfo.getServiceTime()!=null && orderInfo.getFinishTime()!=null){
			Date serviceTime = orderInfo.getServiceTime();
			Date finishTime = orderInfo.getFinishTime();
			Date end = DateUtils.parseDate(DateUtils.formatDate(finishTime, "yyyy-MM-dd") + " 23:59:59");
			orderInfo.setOrderTimeStart(new Timestamp(serviceTime.getTime()));
			orderInfo.setOrderTimeEnd(new Timestamp(end.getTime()));
		}
		List<OrderInfo> orderInfos = dao.appFindList(orderInfo);
		if (orderInfos!= null && orderInfos.size()>0) {
			for (OrderInfo info : orderInfos) {
				String majorSort = info.getMajorSort();
				if (majorSort.equals("clean")) {
					info.setMajorSortName("保洁");
				} else if (majorSort.equals("repair")) {
					info.setMajorSortName("家修");
				}
				String serviceStatus = info.getServiceStatus();
				if (serviceStatus.equals("wait_service")) {
					info.setServiceStatusName("待服务");
				} else if (serviceStatus.equals("started")) {
					info.setServiceStatusName("已上门");
				} else if (serviceStatus.equals("finish")) {
					info.setServiceStatusName("已完成");
				}
			}
		}
		page.setList(orderInfos);
		return page;
	}

    //根据权限获取对应的服务站
    public List<BasicServiceStation> getServiceStationList(BasicServiceStation station) {
        station.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
        return basicServiceStationDao.getServiceStationList(station);
    }

	//订单的编辑（订单备注修改）
	@Transactional(readOnly = false)
	public int appSaveRemark(OrderInfo orderInfo){
		//判断订单是否属于该技师
		OrderDispatch dis=new OrderDispatch();
		dis.setTechId(orderInfo.getNowId());
		dis.setOrderId(orderInfo.getId());
		OrderDispatch byOrderTechId = orderDispatchDao.getByOrderTechId(dis);
		//派单表为空 不属于
		if (null == byOrderTechId){
			throw new ServiceException("订单不属于该技师");
		}
		//订单备注图片  JSON串存的数组
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if (StringUtils.isNotBlank(orderRemarkPic)){
			List<String> pics = (List<String>) JsonMapper.fromJsonString(orderRemarkPic, ArrayList.class);
			List<String> tem=new ArrayList<String>();
			for (String pic:pics){
				if (pic.contains("https://")){
					//获取第三个/下标
					int index = index(pic);
					//截取新数组
					pic = pic.substring(index + 1);
				}
				tem.add(pic);
			}
			String s = JsonMapper.toJsonString(tem);
			orderInfo.setOrderRemarkPic(s);
		}
		orderInfo.appPreUpdate();
		int i =0;
		try{
			i = dao.appUpdateRemark(orderInfo);
		}catch (Exception e){
			//更改订单备注时 长度过长捕获异常信息
			throw new ServiceException("修改数据库失败可能是长度过长");
		}
		return i;
	}

	//订单的编辑（订单的服务状态修改）
	@Transactional(readOnly = false)
	public int appSaveOrder(OrderInfo orderInfo){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//判断订单是否属于该技师
		OrderDispatch dis=new OrderDispatch();
		dis.setTechId(orderInfo.getNowId());
		dis.setOrderId(orderInfo.getId());
		OrderDispatch byOrderTechId = orderDispatchDao.getByOrderTechId(dis);
		//派单表为空 不属于
		if (null == byOrderTechId){
			throw new ServiceException("订单不属于该技师");
		}
		//查询数据库 获取 完成时间和服务状态
		OrderInfo info = dao.appGet(orderInfo);
		//根据服务状态 更改订单的服务状态
		if (StringUtils.isNotBlank(orderInfo.getServiceStatus())){
			//如果查询出来的订单的服务状态是取消的 返回订单已取消
			if (StringUtils.isBlank(info.getServiceStatus()) || "cancel".equals(info.getServiceStatus())){
				throw new ServiceException("订单已取消");
			}
			//完成服务  如果未支付 不能点击
			if ("finish".equals(orderInfo.getServiceStatus())){
				if (StringUtils.isBlank(info.getPayStatus())){
					throw new ServiceException("订单服务状态不可为空");
				}
				//本机构的订单未支付不可点击完成 国安社区的可以
				if ("own".equals(info.getOrderSource()) && "waitpay".equals(info.getPayStatus())){
					throw new ServiceException("订单尚未支付，请完成支付");
				}
				//订单来源为本机构的 完成服务后，同时将订单的状态改为已成功
				if ("own".equals(info.getOrderSource())){
					info.setOrderStatus("success");
				}
				Date finishTime = info.getFinishTime();
				Date date=new Date();
				//如果提前完成  更新完成时间
				if (date.before(finishTime)) {
					//比数据库中结束时间早1个小时的时间
					String format = df.format(new Date(finishTime.getTime() - (long) 1 * 60 * 60 * 1000));
					Date date1 = DateUtils.parseDate(format);
					//如果提前一个多小时 不能点击 抛异常
					if (date.before(date1) || date.before(info.getServiceTime())) {
						throw new ServiceException("最多提前一个小时点击完成，且不能早于服务时间");
					}
					info.setFinishTime(date);
					//修改订单对应的排期表  先查出派单表集合
					List<OrderDispatch> byOrderId= orderDispatchDao.getByOrderId(orderInfo);
					List<ServiceTechnicianInfo> tech = new ArrayList<ServiceTechnicianInfo>();
					if (byOrderId != null && byOrderId.size()>0) {
						for (OrderDispatch dispatch : byOrderId) {
							ServiceTechnicianInfo info1 = new ServiceTechnicianInfo();
							info1.setId(dispatch.getTechId());
							ServiceTechnicianInfo byId = serviceTechnicianInfoDao.getById(info1);
							tech.add(byId);
						}
					}else {
						throw new ServiceException("没有派单表");
					}
					//查出每个技师对应的排期表 修改
					for (ServiceTechnicianInfo serviceTechnicianInfo : tech) {
						TechScheduleInfo scheduleInfo = new TechScheduleInfo();
						if ("common".equals(info.getOrderType())){
							scheduleInfo.setType("order");
						}else {
							scheduleInfo.setType("master");
						}
						scheduleInfo.setTechId(serviceTechnicianInfo.getId());
						if ("group_split_yes".equals(info.getOrderType())){
							OrderCombinationGasqInfo listByOrderNumber = orderCombinationGasqDao.getListByOrderNumber(info);
							scheduleInfo.setTypeId(listByOrderNumber.getOrderGroupId());
						}else {
							scheduleInfo.setTypeId(orderInfo.getId());
						}
						//订单再排期表中只有一条数据
						List<TechScheduleInfo> orderScheduleList = techScheduleDao.getOrderSchedule(scheduleInfo);
						if (orderScheduleList == null || orderScheduleList.size()<=0 ) {
							throw new ServiceException("该技师没有排期表");
						}
						TechScheduleInfo orderSchedule=orderScheduleList.get(0);
						orderSchedule.setEndTime(date);
						orderSchedule.setType("order");
						orderSchedule.setTypeId(orderInfo.getId());
						orderSchedule.setTechId(serviceTechnicianInfo.getId());
						orderSchedule.appPreUpdate();
						//修改数据库
						techScheduleDao.updateScheduleByTypeIdTech(orderSchedule);
					}
				}
 			}
			//上门服务 把点击时间加入数据库
			if (orderInfo.getServiceStatus().equals("started")){
				//数据库查询出来的状态不是上门 将第一次上门时间添加到数据库 不是第一次不添加数据库
				if (!"started".equals(info.getServiceStatus())){
					info.setRealServiceTime(new Date());
				}
				Date serviceTime = info.getServiceTime();
				Date date=new Date();
				String beforeService = df.format(new Date(serviceTime.getTime() - (long) 30 * 60 * 1000));
				Date before = DateUtils.parseDate(beforeService);
				if (date.before(before)){
					throw new ServiceException("最多提前半个小时点击上门服务");
				}
			}
		}
		info.setServiceStatus(orderInfo.getServiceStatus());
		info.appPreUpdate();
		int i =0;
		try{
			i = dao.appUpdate(info);
		}catch (Exception e){
			//更改订单备注时 长度过长捕获异常信息
			throw new ServiceException("修改数据库失败可能是长度过长");
		}
		return i;
	}
	//获取订单对应机构的对接code
	public BasicOrganization getBasicOrganizationByOrgId(OrderInfo orderInfo){
		return null;
	}
	//app根据商品id获取订单对应商品的对接code
	public List<String> getGoodsCode(OrderInfo orderInfo){
		List<String> goods = dao.getGoods(orderInfo);
		List<String> code = new ArrayList<String>();
		if (goods != null && goods.size() > 0) {
			for (String goodId : goods) {
				String goodsCode = dao.getGoodsCode(goodId);
				code.add(goodsCode);
			}
		}
		return code;
	}


	//获取字符串中第三个/的下标 App修改订单备注使用 截取图片路径
	public static int index(String s) {
		Pattern p = Pattern.compile("/");
		Matcher m = p.matcher(s);
		int c=0,index=-1;
		while(m.find()){
			c++;
			index=m.start();
			if(c==3){
				return index;
			}
		}
		return index;
	}

	/**
	 * 通过ID返回订单编号
	 * @param orderId
	 * @return
	 */
	public String getOrderSnById(String orderId) {
    	OrderInfo orderInfo = get(orderId);
		return orderInfo.getOrderNumber();
	}

}