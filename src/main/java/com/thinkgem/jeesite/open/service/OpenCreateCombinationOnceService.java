/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderToolsService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.open.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OpenCreateCombinationOnceService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderAddressDao orderAddressDao;
	@Autowired
	OrderCustomInfoDao orderCustomInfoDao;
	@Autowired
	OrderCustomAddressDao orderCustomAddressDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	OrderPayInfoDao orderPayInfoDao;
	@Autowired
	AreaDao areaDao;
	@Autowired
	BasicOrganizationDao basicOrganizationDao;
	@Autowired
	BasicServiceStationDao basicServiceStationDao;
	@Autowired
	BasicStoreDao basicStoreDao;
	@Autowired
	TechScheduleDao techScheduleDao;

	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
	OrderCombinationGasqDao orderCombinationGasqDao;

	@Autowired
	private OrderToolsService orderToolsService;

	/**
	 * 对接接口 订单创建
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> openCreate(OpenCreateRequest info) {
		OpenCreateResponse response = new OpenCreateResponse();
		if(null == info){
			throw new ServiceException("未接收到订单信息!");
		}

		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		try {
			masterInfo = openCreateForMaster(info);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单主表信息失败");
		}

		// order_address  订单地址表---------------------------------------------------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		try{
			orderAddress = openCreateForAddress(info);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单地址表信息失败");
		}

		// customer_info  客户表---------------------------------------------------------------------------------
		OrderCustomInfo orderCustomInfo = new OrderCustomInfo();
		try{
			orderCustomInfo = openCreateForCustom(info);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存客户表信息失败");
		}

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		try{
			orderInfo = openCreateForOrder(info, masterInfo, orderAddress, orderCustomInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存子订单信息表信息失败!");
		}
		if(null == orderInfo){
			throw new ServiceException("保存子订单信息表信息失败!");
		}

		// order_combination_info  组合订单信息 -------------------------------------------------------------------------------
		CombinationOrderInfo combinationInfo = new CombinationOrderInfo();
		try{
			combinationInfo = openCreateForCombination(info, orderInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存组合订单信息表信息失败!");
		}
		if(null == combinationInfo){
			throw new ServiceException("保存组合订单信息表信息失败!");
		}

		// order_dispatch -派单表----------------------------------------------------------------------------------
		List<OrderDispatch> orderDispatches = orderInfo.getTechList();//派单信息
		if(null != orderDispatches && orderDispatches.size() > 0) {
			try{
				openCreateForDispatch(orderInfo, orderDispatches);
			}catch (ServiceException ex){
				throw new ServiceException(ex.getMessage());
			}catch (Exception e){
				throw new ServiceException("保存派单信息表失败!");
			}
		}else {
			throw new ServiceException("未找到派单信息");
		}

		// order_goods-订单服务关联--------------------------------------------------------------------
		List<OrderGoods> orderGoods = orderInfo.getGoodsInfoList();//商品信息
		if(null != orderGoods && orderGoods.size() > 0){
			try{
				openCreateForGoods(orderInfo, orderGoods);
			}catch (ServiceException ex){
				throw new ServiceException(ex.getMessage());
			}catch (Exception e){
				throw new ServiceException("保存订单商品信息表失败!");
			}
		}else {
			throw new ServiceException("未找到商品信息");
		}

		// order_pay_info  支付信息 ------------------------------------------------------------------------------
		try{
			openCreateForPay(orderInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单支付信息表失败!");
		}

		// tech_schedule  服务技师排期 ------------------------------------------------------------------------------
		try{
			openCreateForTechSchedule( orderDispatches, orderInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存排期表失败!");
		}

		//------------------------------------------------------------------------------------------------
		response = new OpenCreateResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_order_id(orderInfo.getOrderNumber());// 自营服务订单ID
		response.setService_group_id(combinationInfo.getMasterId());// 自营服务订单ID

		OrderInfo orderInfoMsg = new OrderInfo();
		orderInfoMsg.setId(orderInfo.getId());
		orderInfoMsg.setOrderNumber(orderInfo.getOrderNumber());
		orderInfoMsg.setTechList(orderDispatches);

		HashMap<String,Object> map = new HashMap<>();
		map.put("response",response);
		map.put("orderInfoMsg",orderInfoMsg);
		return map;
	}

	/**
	 * 订单创建 - 排期表
	 * @param orderDispatches
	 * @param orderInfo
	 */
	private void openCreateForTechSchedule(List<OrderDispatch> orderDispatches, OrderInfo orderInfo) {
		if(orderDispatches != null && orderDispatches.size() > 0){
			for(OrderDispatch dispatch : orderDispatches){
				TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
				techScheduleInfo.setTechId(dispatch.getTechId());//技师ID
				techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(orderInfo.getServiceTime()));//日期
				int weekDay = DateUtils.getWeekNum(orderInfo.getServiceTime());//周几
				techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
				techScheduleInfo.setStartTime(orderInfo.getServiceTime());//起始时段
				techScheduleInfo.setEndTime(orderInfo.getFinishTime());//结束时段
				techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
				techScheduleInfo.setMasterId(orderInfo.getMasterId());
				techScheduleInfo.setType("master");//'holiday：休假  order：订单'

				User user = new User();
				user.setId("gasq001");
				techScheduleInfo.setId(IdGen.uuid());
				techScheduleInfo.setCreateBy(user);
				techScheduleInfo.setCreateDate(new Date());
				techScheduleInfo.setUpdateBy(user);
				techScheduleInfo.setUpdateDate(techScheduleInfo.getCreateDate());

				techScheduleDao.insertSchedule(techScheduleInfo);
			}
		}
	}

	/**
	 * 订单创建 - 支付信息
	 * @param orderInfo
	 */
	private void openCreateForPay(OrderInfo orderInfo) {
		OrderPayInfo payInfo = new OrderPayInfo();
		payInfo.setPayNumber(DateUtils.getDateAndRandomTenNum("02"));//支付编号
		payInfo.setMasterId(orderInfo.getMasterId());//主订单ID',
		payInfo.setOrderId(orderInfo.getId());
		payInfo.setPayPlatform(null);//支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
		payInfo.setPayMethod(null);//支付方式(online:在线 offline:货到付款)',
		payInfo.setPayTime(null);//支付时间',
		payInfo.setPayAccount(orderInfo.getOriginPrice());//支付总额',
		payInfo.setPayStatus("waitpay");//支付状态(waitpay:待支付 payed:已支付)',

		User user = new User();
		user.setId("gasq001");
		payInfo.setId(IdGen.uuid());
		payInfo.setCreateBy(user);
		payInfo.setCreateDate(new Date());
		payInfo.setUpdateBy(user);
		payInfo.setUpdateDate(payInfo.getCreateDate());

		orderPayInfoDao.insert(payInfo);
	}

	/**
	 * 订单创建 - 订单服务关联
	 * @param orderGoods
	 */
	private void openCreateForGoods(OrderInfo orderInfo, List<OrderGoods> orderGoods) {
		for(OrderGoods goods : orderGoods){
			goods.setOrderId(orderInfo.getId());//订单ID

			User user = new User();
			user.setId("gasq001");
			goods.setId(IdGen.uuid());
			goods.setCreateBy(user);
			goods.setCreateDate(new Date());
			goods.setUpdateBy(user);
			goods.setUpdateDate(goods.getCreateDate());

			orderGoodsDao.insert(goods);
		}
	}

	/**
	 * 订单创建 - 派单表
	 * @param orderInfo
	 * @param orderDispatches
	 */
	private void openCreateForDispatch(OrderInfo orderInfo, List<OrderDispatch> orderDispatches) {
		for(OrderDispatch dispatch : orderDispatches){
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setTechId(dispatch.getTechId());//技师ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)

			User user = new User();
			user.setId("gasq001");
			orderDispatch.setId(IdGen.uuid());
			orderDispatch.setCreateBy(user);
			orderDispatch.setCreateDate(new Date());
			orderDispatch.setUpdateBy(user);
			orderDispatch.setUpdateDate(orderDispatch.getCreateDate());

			orderDispatchDao.insert(orderDispatch);
		}
	}

	/**
	 * 订单创建 - 子订单信息
	 * @param info
	 * @param masterInfo
	 * @param orderAddress
	 * @return
	 */
	private OrderInfo openCreateForOrder(OpenCreateRequest info, OrderMasterInfo masterInfo, OrderAddress orderAddress, OrderCustomInfo orderCustomInfo) {
		String store_id = info.getStore_id();//门店ID
		if(null == store_id){
			throw new ServiceException("门店ID不能为空");
		}
		String shop_name = info.getStore_name();//门店名称
		String shop_phone = info.getStore_phone();//门店电话
		String shop_addr = info.getStore_addr();//门店地址

		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}
		String remark = info.getRemark();//订单备注(用户备注)
		List<String> remark_pic = info.getRemark_pic();//订单备注(用户备注)
		String remark_pic_String = "";
		if(null != remark_pic){
			remark_pic_String = JsonMapper.toJsonString(remark_pic);
		}

		List<OpenServiceInfo> serviceInfos = info.getService_info();
		String servie_time = info.getService_time();//服务时间
		if(null == servie_time){
			throw new ServiceException("服务时间不能为空");
		}
		List<String> gasqOrderSnList = info.getGasq_order_sn();
		if(gasqOrderSnList==null || gasqOrderSnList.size()!=1){
			throw new ServiceException("国安社区订单SN不能为空");
		}
		String gasq_order_sn = gasqOrderSnList.get(0);
		if(null == gasq_order_sn){
			throw new ServiceException("国安社区订单SN不能为空");
		}
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度
		String sum_price = info.getSum_price();//订单总支付价格
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
		if(null == order_type){
			throw new ServiceException("订单类型不能为空");
		}

		//通过对接方E店CODE获取机构
		String orgId = orderCustomInfo.getOrgId();
		//通过门店ID获取服务站
		BasicServiceStation stationSerch = new BasicServiceStation();
		stationSerch.setStoreId(store_id);
		stationSerch.setOrgId(orgId);
		List<BasicServiceStation> stations = basicServiceStationDao.getStationListByStoreIdUseable(stationSerch);
		String stationId = "";
		if(null != stations && stations.size() > 0){
			stationId = stations.get(0).getId();
		}else{
			throw new ServiceException("未找到门店ID对应的服务站信息");
		}

		BigDecimal originPrice = new BigDecimal(0);//商品总价
		BigDecimal openPrice = new BigDecimal(0);//对接总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）
		String combinationGoodsId="";//组合商品ID
		String combinationGoodsName="";//组合商品名称
		int combinationGoodsNum=0;//组合商品数量
		String combinationOrderContent="";//组合商品内容

		String orderGoodsMajorSort="";
		String orderGoodsSortId="";

		if(null != serviceInfos && serviceInfos.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OpenServiceInfo openServiceInfo : serviceInfos){

				String key = openServiceInfo.getCate_goods_id();
				String cate_goods_id = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
				if(null == cate_goods_id){
					throw new ServiceException("自营服务服务商品ID不能为空");
				}
				int buy_num = openServiceInfo.getBuy_num();
				if(0 == buy_num){
					throw new ServiceException("购买数量不能为空");
				}
				String pay_price = openServiceInfo.getPay_price();
				if(null == pay_price){
					throw new ServiceException("服务项目单价不能为空");
				}

				//验证对接状态
				SerItemCommodityEshop serchGoodsEshop = new SerItemCommodityEshop();
				serchGoodsEshop.setEshopCode(eshop_code);
				serchGoodsEshop.setGoodsId(cate_goods_id);
				serchGoodsEshop.setOrgId(orgId);
				serchGoodsEshop.setJointStatus("butt_success");
				serchGoodsEshop.setEnabledStatus("yes");
				SerItemCommodityEshop goodsEshop = orderGoodsDao.checkJointStatus(serchGoodsEshop);
				if(null == goodsEshop){
					throw new ServiceException("未找到自营服务服务商品ID对接的商品信息");
				}

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				if(null == commodity){
					throw new ServiceException("未找到自营服务服务商品ID对应的商品信息");
				}
				BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
				originPrice = originPrice.add(price);//商品总价
				sortItemNames = commodity.getSortName();//下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
				goodsNames = goodsNames + "+" + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2

				combinationGoodsId = cate_goods_id;//组合商品ID
				combinationGoodsName = commodity.getName();//组合商品名称
				combinationGoodsNum = buy_num;//组合商品数量
				orderGoodsMajorSort = commodity.getMajorSort();
				orderGoodsSortId = commodity.getSortId();

				int goodsNum = buy_num;		// 订购商品数
				Double convertHours = commodity.getConvertHours();		// 折算时长
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double goodsTime = convertHours * goodsNum;//折算时长 * 订购商品数
				orderTotalTime = orderTotalTime + goodsTime;//订单商品总时长

				if(goodsTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(goodsTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.subtract(b2).divide(b2, 0, BigDecimal.ROUND_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单的最大人数
					techDispatchNum = techNum;
				}
			}
			combinationOrderContent=sortItemNames + goodsNames;//组合商品内容
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("商品信息不能为空");
		}

		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		//根据组合商品ID返回子商品信息
		List<SerItemCommodity> sonGoodsList = orderGoodsDao.listGoodsByCombination(combinationGoodsId);
		for(SerItemCommodity commodity : sonGoodsList){
			OrderGoods goods = new OrderGoods();
			goods.setSortId(commodity.getSortId());//服务分类ID
			goods.setItemId(commodity.getItemId());//服务项目ID
			goods.setItemName(commodity.getItemName());//项目名称
			goods.setGoodsId(commodity.getId());//商品ID
			goods.setGoodsName(commodity.getName());//商品名称
			goods.setGoodsNum(commodity.getGoodsNum() * combinationGoodsNum);//订购商品数
			goods.setGoodsType(commodity.getType());
			goods.setGoodsUnit(commodity.getUnit());
			goods.setPayPrice(commodity.getGoodsPrice().toString());//对接后单价
			goods.setOriginPrice(commodity.getPrice().toString());//原价
			goods.setMajorSort(commodity.getMajorSort());

			goods.setConvertHours(commodity.getConvertHours());		// 折算时长
			goods.setStartPerNum(commodity.getStartPerNum());   		//起步人数（第一个4小时时长派人数量）
			goods.setCappingPerNum(commodity.getCappingPerNum());		//封项人数

			orderGoods.add(goods);
		}

		//--------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(masterInfo.getId());//主订单ID
		orderInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(orgId);  //所属服务机构ID
		orderInfo.setStationId(stationId);        //服务站id
		orderInfo.setMajorSort(orderGoodsMajorSort);               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(sum_price);            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		orderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		orderInfo.setLatitude(latitude);                //服务地址  纬度
		orderInfo.setLongitude(longitude);         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(DateUtils.parseDate(servie_time));     //上门时间（服务时间）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSecondsNotDayE(DateUtils.parseDate(servie_time),serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSecondsNotDayE(DateUtils.parseDate(servie_time),serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource("gasq");  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(orderCustomInfo.getId());    // 客户ID
		orderInfo.setCustomerRemark(remark);   // 客户备注
		orderInfo.setCustomerRemarkPic(remark_pic_String);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);  //下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
		orderInfo.setJointOrderId(gasq_order_sn);//国安社区订单编号
		orderInfo.setShopId(store_id);
		orderInfo.setShopName(shop_name);
		orderInfo.setShopPhone(shop_phone);
		orderInfo.setShopAddr(shop_addr);
		orderInfo.setEshopCode(eshop_code);

		User user = new User();
		user.setId("gasq001");
		orderInfo.setId(IdGen.uuid());
		orderInfo.setCreateBy(user);
		orderInfo.setCreateDate(new Date());
		orderInfo.setUpdateBy(user);
		orderInfo.setUpdateDate(orderInfo.getCreateDate());

		orderInfoDao.insert(orderInfo);

		orderInfo.setGoodsInfoList(orderGoods);//商品信息
		orderInfo.setGoodsSortId(orderGoodsSortId);
		orderInfo.setCombinationGoodsId(combinationGoodsId);
		orderInfo.setCombinationGoodsName(combinationGoodsName);
		orderInfo.setCombinationGoodsNum(combinationGoodsNum);
		orderInfo.setCombinationOrderContent(combinationOrderContent);

		try {
			List<OrderDispatch> techList = openCreateForOrderFindDispatchList(orderInfo,techDispatchNum,serviceSecond);//获取派单技师
			orderInfo.setTechList(techList);//派单技师List
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("派单失败");
		}

		return orderInfo;
	}

	/**
	 * 订单创建 - 子订单信息 - 获取派单技师
	 * @param orderInfo
	 * @return
	 */
	private List<OrderDispatch> openCreateForOrderFindDispatchList(OrderInfo orderInfo,int techDispatchNum,Double serviceSecond){
		Date serviceTime = orderInfo.getServiceTime();//服务时间

		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setOrgId(orderInfo.getOrgId());
		serchOrderInfo.setStationId(orderInfo.getStationId());
		serchOrderInfo.setServiceTime(orderInfo.getServiceTime());
		serchOrderInfo.setFinishTime(orderInfo.getFinishTime());
		serchOrderInfo.setGoodsSortId(orderInfo.getGoodsSortId());
		serchOrderInfo.setSerchFullTech(true);
		List<OrderDispatch> techListRe = orderToolsService.listTechByGoodsAndTime(serchOrderInfo);
		if(techListRe != null && techListRe.size() != 0){
			if(techListRe.size() < techDispatchNum){//技师数量不够
				throw new ServiceException("技师数量不满足当前商品的需求人数");
			}

			//一个自然月内，根据当前服务站内各个技师的订单数量，优先分配给订单数量少的技师
			List<String> serchTechIds = new ArrayList<>();
			for(OrderDispatch tech :techListRe){
				serchTechIds.add(tech.getTechId());
			}
			Date monthFristDay = DateUtils.getMonthFristDay(serviceTime);
			Date monthLastDay = DateUtils.getMonthLastDay(serviceTime);
			OrderDispatch serchTechInfo = new OrderDispatch();
			serchTechInfo.setStartTime(monthFristDay);
			serchTechInfo.setEndTime(monthLastDay);
			serchTechInfo.setTechIds(serchTechIds);
			List<OrderDispatch> techListOrderByNum = orderInfoDao.getTechListOrderByNum(serchTechInfo);//订单数量少的技师
			List<OrderDispatch> dispatchTechs = new ArrayList<>();
			for(int i=0;i<techDispatchNum;i++){//订单数量少的技师  订单需要技师数量
				dispatchTechs.add(techListOrderByNum.get(i));
			}
			return dispatchTechs;
		}else {
			throw new ServiceException("技师数量不满足当前商品的需求人数");
		}
	}

	/**
	 * 订单创建 - 组合订单信息
	 * @param info
	 * @param orderInfo
	 * @return
	 */
	private CombinationOrderInfo openCreateForCombination(OpenCreateRequest info, OrderInfo orderInfo) {
		String group_id = info.getGroup_id();
		if(null == group_id){
			throw new ServiceException("组合订单ID不能为空");
		}
		List<String> gasqOrderSnList = info.getGasq_order_sn();
		if(gasqOrderSnList==null || gasqOrderSnList.size()!=1){
			throw new ServiceException("国安社区订单SN不能为空");
		}
		List<OpenServiceInfo> serviceInfos = info.getService_info();

		//--------------------------------------------------
		CombinationOrderInfo combinationOrderInfo = new CombinationOrderInfo();
		combinationOrderInfo.setMasterId(orderInfo.getMasterId());//主订单ID
		combinationOrderInfo.setJointGroupId(group_id);
		combinationOrderInfo.setOrderType(orderInfo.getOrderType());//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		combinationOrderInfo.setOrgId(orderInfo.getOrgId());  //所属服务机构ID
		combinationOrderInfo.setStationId(orderInfo.getStationId());        //服务站id
		combinationOrderInfo.setMajorSort(orderInfo.getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)  `
		combinationOrderInfo.setCombinationGoodsId(orderInfo.getCombinationGoodsId());//下单组合商品ID
		combinationOrderInfo.setCombinationGoodsName(orderInfo.getCombinationGoodsName());//下单组合商品
		combinationOrderInfo.setCombinationGoodsNum(orderInfo.getCombinationGoodsNum());//下单数量
		combinationOrderInfo.setPayPrice(orderInfo.getPayPrice());            //实际付款价格
		combinationOrderInfo.setOriginPrice(orderInfo.getOriginPrice());              //总价（原价）
		combinationOrderInfo.setOrderAddressId(orderInfo.getOrderAddressId());  // 订单地址ID
		combinationOrderInfo.setLatitude(orderInfo.getLatitude());                //服务地址  纬度
		combinationOrderInfo.setLongitude(orderInfo.getLongitude());         //服务地址  经度
		combinationOrderInfo.setOrderTime(orderInfo.getOrderTime());    //下单时间
		combinationOrderInfo.setServiceHour(orderInfo.getServiceHour());//服务时长（小时）',
		combinationOrderInfo.setServiceStart(orderInfo.getServiceTime());//第一次服务日期',
		combinationOrderInfo.setOrderStatus("dispatched");   // 订单状态(dispatched:已下单;cancel:已取消;success:已成功;close:已关闭)
		combinationOrderInfo.setOrderSource("gasq");  // 订单来源(own:本机构 gasq:国安社区)
		combinationOrderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		combinationOrderInfo.setCustomerId(orderInfo.getCustomerId());    // 客户ID
		combinationOrderInfo.setOrderContent(orderInfo.getCombinationOrderContent());  //下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
		combinationOrderInfo.setShopId(orderInfo.getShopId());
		combinationOrderInfo.setShopName(orderInfo.getShopName());
		combinationOrderInfo.setShopPhone(orderInfo.getShopPhone());
		combinationOrderInfo.setShopAddr(orderInfo.getShopAddr());
		combinationOrderInfo.setEshopCode(orderInfo.getEshopCode());

		User user = new User();
		user.setId("gasq001");
		combinationOrderInfo.setId(IdGen.uuid());
		combinationOrderInfo.setCreateBy(user);
		combinationOrderInfo.setCreateDate(new Date());
		combinationOrderInfo.setUpdateBy(user);
		combinationOrderInfo.setUpdateDate(combinationOrderInfo.getCreateDate());
		combinationOrderDao.insert(combinationOrderInfo);

		OrderCombinationGasqInfo gasqInfo = new OrderCombinationGasqInfo();
		gasqInfo.setMasterId(orderInfo.getMasterId());// 主订单ID
		gasqInfo.setJointOrderSn(orderInfo.getJointOrderId());//国安社区订单编号
		gasqInfo.setOrderGroupId(IdGen.uuid());
		gasqInfo.setOrderNumber(orderInfo.getOrderNumber());
		gasqInfo.setId(IdGen.uuid());
		gasqInfo.setCreateBy(user);
		gasqInfo.setCreateDate(new Date());
		gasqInfo.setUpdateBy(user);
		gasqInfo.setUpdateDate(gasqInfo.getCreateDate());
		orderCombinationGasqDao.insert(gasqInfo);

		return combinationOrderInfo;
	}

	/**
	 * 客户表
	 * @param info
	 * @return
	 */
	private OrderCustomInfo openCreateForCustom(OpenCreateRequest info) {
		String loginMobile = info.getLogin_mobile();
		String loginName = info.getLogin_name();

		String name = info.getName();//用户
		String phone = info.getPhone();//用户电话
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		String area_code = info.getArea_code();//区CODE
		String placename = info.getPlacename();////服务地址：小区
		String detailAddress = info.getDetail_address();//服务地址：门牌号
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度

		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}
		//通过对接方E店CODE获取机构
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(eshop_code);
		String orgId = "";
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
		}else{
			throw new ServiceException("未找到E店CODE对应的机构信息");
		}
		User user = new User();
		user.setId("gasq001");

		// 判断是否在客户表存在
		OrderCustomInfo serchCustomInfo = new OrderCustomInfo();
		serchCustomInfo.setPhone(loginMobile);
		serchCustomInfo.setOrgId(orgId);//机构ID
		serchCustomInfo.setSource("gasq");// 来源   本机构:own    国安社区:gasq
		List<OrderCustomInfo> cusInfoList = orderCustomInfoDao.findCusList(serchCustomInfo);
		OrderCustomInfo orderCustomInfo = new OrderCustomInfo();
		if (null == cusInfoList || cusInfoList.size()==0) {
			orderCustomInfo.setName(loginName);//姓名
			orderCustomInfo.setPhone(loginMobile);//手机号
			orderCustomInfo.setEmail("");//邮编
			orderCustomInfo.setSource("gasq");//来源   本机构:own    国安社区:gasq',
			orderCustomInfo.setOrgId(orgId);

			orderCustomInfo.setId(IdGen.uuid());
			orderCustomInfo.setCreateBy(user);
			orderCustomInfo.setCreateDate(new Date());
			orderCustomInfo.setUpdateBy(user);
			orderCustomInfo.setUpdateDate(orderCustomInfo.getCreateDate());

			orderCustomInfoDao.insert(orderCustomInfo);
		}else {
			// 用户手机号不能重复
			orderCustomInfo = cusInfoList.get(0);
		}

		//判断客户地址是否存在
		OrderCustomAddress serchCustomAddress = new OrderCustomAddress();
		serchCustomAddress.setCustomerId(orderCustomInfo.getId());
		serchCustomAddress.setAddressName(name);
		serchCustomAddress.setAddressPhone(phone);
		serchCustomAddress.setAddrLatitude(latitude);//服务地址：纬度
		serchCustomAddress.setAddrLongitude(longitude);//服务地址：经度
		List<OrderCustomAddress> cusAddrList = orderCustomAddressDao.findCusAddrList(serchCustomAddress);
		OrderCustomAddress orderCustomAddress = new OrderCustomAddress();
		if (null == cusAddrList || cusAddrList.size()==0) {
			// 客户地址表
			orderCustomAddress.setCustomerId(orderCustomInfo.getId());
			orderCustomAddress.setAddressName(name);
			orderCustomAddress.setAddressPhone(phone);
			orderCustomAddress.setProvinceCode(province_code);//省_区号
			orderCustomAddress.setCityCode(city_code);//市_区号
			orderCustomAddress.setAreaCode(area_code);//区_区号
			orderCustomAddress.setPlacename(placename);//小区
			orderCustomAddress.setDetailAddress(detailAddress);//详细地址
			orderCustomAddress.setAddrLatitude(latitude);//服务地址：纬度
			orderCustomAddress.setAddrLongitude(longitude);//服务地址：经度
			orderCustomAddress.setDefaultType("yes");

			orderCustomAddress.setId(IdGen.uuid());
			orderCustomAddress.setCreateBy(user);
			orderCustomAddress.setCreateDate(new Date());
			orderCustomAddress.setUpdateBy(user);
			orderCustomAddress.setUpdateDate(orderCustomAddress.getCreateDate());
			orderCustomAddressDao.insert(orderCustomAddress);
		}else{
			orderCustomAddress = cusAddrList.get(0);
		}
		List<OrderCustomAddress> orderCustomAddressList = new ArrayList<>();
		orderCustomAddressList.add(orderCustomAddress);
		orderCustomInfo.setAddressList(orderCustomAddressList);
		return orderCustomInfo;
	}

	/**
	 * 订单创建 - 订单地址表
	 * @param info
	 * @return
	 */
	private OrderAddress openCreateForAddress(OpenCreateRequest info) {
		String name = info.getName();//用户
		String phone = info.getPhone();//用户电话
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		String area_code = info.getArea_code();//区CODE
		String placename = info.getPlacename();////服务地址：小区
		String detailAddress = info.getDetail_address();//服务地址：门牌号

		//省名称
		String provinceName = info.getProvince_name();
		//市名称
		String cityName = info.getCity_name();
		//区名称
		String areaName = info.getArea_name();

		String address = provinceName + cityName + areaName + placename + detailAddress;

		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName(name);//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setPlacename(placename);//小区
		orderAddress.setDetailAddress(detailAddress);//详细地址
		orderAddress.setAddress(address);//收货人完整地址

		User user = new User();
		user.setId("gasq001");
		orderAddress.setId(IdGen.uuid());
		orderAddress.setCreateBy(user);
		orderAddress.setCreateDate(new Date());
		orderAddress.setUpdateBy(user);
		orderAddress.setUpdateDate(orderAddress.getCreateDate());

		orderAddressDao.insert(orderAddress);

		return orderAddress;
	}

	/**
	 * 订单创建 - 订单主表
	 * @param info
	 * @return
	 */
	private OrderMasterInfo openCreateForMaster(OpenCreateRequest info) {
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
		if(null == order_type){
			throw new ServiceException("订单类型不能为空");
		}
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）

		User user = new User();
		user.setId("gasq001");
		masterInfo.setId(IdGen.uuid());
		masterInfo.setCreateBy(user);
		masterInfo.setCreateDate(new Date());
		masterInfo.setUpdateBy(user);
		masterInfo.setUpdateDate(masterInfo.getCreateDate());

		orderMasterInfoDao.insert(masterInfo);
		return masterInfo;
	}

}