/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.BeanUtils;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderInfoCreateService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderCustomInfoDao orderCustomInfoDao;
	@Autowired
	OrderCustomAddressDao orderCustomAddressDao;
	@Autowired
	OrderAddressDao orderAddressDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	OrderPayInfoDao orderPayInfoDao;
	@Autowired
	AreaDao areaDao;
	@Autowired
	TechScheduleDao techScheduleDao;

	@Autowired
	private OrderToolsService orderToolsService;

	@Transactional(readOnly = false)
	public HashMap<String,Object> createOrder(OrderInfo info) {
		// 一期暂定-普通订单
		String order_type = "common";//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单

		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		try {
			masterInfo = openCreateForMaster(info,order_type);
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

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		try{
			orderInfo = openCreateForOrder(info, masterInfo, orderAddress);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存子订单信息表信息失败!");
		}
		if(null == orderInfo){
			throw new ServiceException("保存子订单信息表信息失败!");
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
		OpenCreateResponse response = new OpenCreateResponse();

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
				techScheduleInfo.setType("order");//'holiday：休假  order：订单'
				techScheduleInfo.preInsert();
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
		payInfo.preInsert();

		orderPayInfoDao.insert(payInfo);
	}

	/**
	 * 订单创建 - 订单服务关联
	 * @param orderGoods
	 */
	private void openCreateForGoods(OrderInfo orderInfo, List<OrderGoods> orderGoods) {
		for(OrderGoods goods : orderGoods){
			goods.setOrderId(orderInfo.getId());//订单ID
			goods.preInsert();

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
			orderDispatch.preInsert();

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
	private OrderInfo openCreateForOrder(OrderInfo info, OrderMasterInfo masterInfo, OrderAddress orderAddress) {
		String customerId = info.getCustomerId();
		String remark = info.getCustomerRemark();//订单备注(用户备注)
		Date servie_time = info.getServiceTime();//服务时间
		if(null == servie_time){
			throw new ServiceException("服务时间不能为空");
		}
		String stationId = info.getStationId();

		String orgId = orderAddress.getOrgId();
		String latitude = orderAddress.getAddrLatitude();//服务地址：纬度
		String longitude = orderAddress.getAddrLongitude();//服务地址：经度

		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价

		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		List<OrderGoods> orderGoodsList = info.getGoodsInfoList();
		if(null != orderGoodsList && orderGoodsList.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OrderGoods orderGoodsInfo : orderGoodsList){
				String cate_goods_id = orderGoodsInfo.getGoodsId();
				if("house".equals(orderGoodsInfo.getGoodsType())) {//按居室的商品
					cate_goods_id = orderGoodsInfo.getHouseId();
				}
				int buy_num = orderGoodsInfo.getGoodsNum();

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				if(null == commodity){
					throw new ServiceException("未找到自营服务服务商品ID对应的商品信息");
				}
				goods = new OrderGoods();
				goods.setSortId(commodity.getSortId());//服务分类ID
				goods.setItemId(commodity.getItemId());//服务项目ID
				goods.setItemName(commodity.getItemName());//项目名称
				goods.setGoodsId(commodity.getId());//商品ID
				goods.setGoodsName(commodity.getName());//商品名称
				goods.setGoodsNum(buy_num);//订购商品数
				goods.setGoodsType(commodity.getType());
				goods.setGoodsUnit(commodity.getUnit());
				goods.setPayPrice(commodity.getPrice().toString());//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());

				goods.setConvertHours(commodity.getConvertHours());		// 折算时长
				goods.setStartPerNum(commodity.getStartPerNum());   		//起步人数（第一个4小时时长派人数量）
				goods.setCappingPerNum(commodity.getCappingPerNum());		//封项人数

				orderGoods.add(goods);
				BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
				originPrice = originPrice.add(price);//商品总价
				sortItemNames = commodity.getSortName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + "+" + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

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
					//addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
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
			if(info.getTechList()!=null && info.getTechList().size()>0){
				techDispatchNum = info.getTechList().size();
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("商品信息不能为空");
		}

		//--------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(masterInfo.getId());//主订单ID
		orderInfo.setOrderType("common");//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(orgId);  //所属服务机构ID
		orderInfo.setStationId(stationId);        //服务站id
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(originPrice.toString());            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		orderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		orderInfo.setLatitude(latitude);                //服务地址  纬度
		orderInfo.setLongitude(longitude);         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(servie_time);     //上门时间（服务时间）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(servie_time,serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(servie_time,serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource("own");  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(customerId);    // 客户ID
		orderInfo.setCustomerRemark(remark);   // 客户备注
		orderInfo.setCustomerRemarkPic(null);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.setEshopCode(null);
		orderInfo.preInsert();
		orderInfoDao.insert(orderInfo);

		orderInfo.setGoodsInfoList(orderGoods);//商品信息
		orderInfo.setGoodsSortId(orderGoods.get(0).getSortId());

		try {
			List<OrderDispatch> techList = info.getTechList();
			if(techList == null || techList.size() == 0) {
				techList = openCreateForOrderFindDispatchList(orderInfo, techDispatchNum);//获取派单技师
			}else{
				List<String> techIdList = new ArrayList();
				for(OrderDispatch tech : techList){
					techIdList.add(tech.getTechId());
				}
				//新增、改派判断库存
				//boolean flag = checkTechTimeWithStationId(techIdList,stationId,servie_time,DateUtils.addSeconds(servie_time, serviceSecond.intValue()));
				boolean flag = false;

				OrderInfo serchOrderInfo = new OrderInfo();
				serchOrderInfo.setOrgId(orderInfo.getOrgId());
				serchOrderInfo.setStationId(orderInfo.getStationId());
				serchOrderInfo.setServiceTime(orderInfo.getServiceTime());
				serchOrderInfo.setFinishTime(orderInfo.getFinishTime());
				serchOrderInfo.setGoodsSortId(orderInfo.getGoodsSortId());
				serchOrderInfo.setSerchFullTech(true);
				List<OrderDispatch> checkTechList = orderToolsService.listTechByGoodsAndTime(serchOrderInfo);
				List<String> checkTechIdList = new ArrayList<>();
				if(checkTechList != null) {
					for (OrderDispatch checkTech : checkTechList) {
						checkTechIdList.add(checkTech.getTechId());
					}
				}
				for(String techId2 : techIdList){
					if(!checkTechIdList.contains(techId2)){
						flag = true;
					}
				}
				if(flag){
					throw new ServiceException("订单创建失败，库存不足，请重新选择技师或服务时间");
				}

			}

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
	private List<OrderDispatch> openCreateForOrderFindDispatchList(OrderInfo orderInfo,int techDispatchNum){
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
	 * 订单创建 - 订单地址表
	 * @param info
	 * @return
	 */
	private OrderAddress openCreateForAddress(OrderInfo info) {
		String customerAddressId = info.getCustomerAddressId();
		String customerId = info.getCustomerId();
		if(StringUtils.isBlank(customerId)){
			throw new ServiceException("未找到客户信息");
		}
		if(StringUtils.isBlank(customerAddressId)){
			throw new ServiceException("未找到客户信息");
		}

		OrderCustomInfo custom = orderCustomInfoDao.get(customerId);
		if(null == custom){
			throw new ServiceException("未找到客户信息");
		}
		OrderCustomAddress customInfo = orderCustomAddressDao.get(customerAddressId);
		if(null == customInfo){
			throw new ServiceException("未找到客户信息");
		}
		String name = customInfo.getAddressName();
		String phone = customInfo.getAddressPhone();//用户电话
		String province_code = customInfo.getProvinceCode();//省CODE
		String city_code = customInfo.getCityCode();//市CODE
		String area_code = customInfo.getAreaCode();//区CODE
		String placename = customInfo.getPlacename();//区CODE
		String detailAddress = customInfo.getDetailAddress();//服务地址：小区+详细地址

		//省名称
		String provinceName = "";
		//市名称
		String cityName = "";
		//区名称
		String areaName = "";

		if(null != province_code) {
			List<Area> provinceList = areaDao.getNameByCode(province_code);
			if (provinceList != null && provinceList.size() > 0) {
				provinceName = provinceList.get(0).getName();
			}
		}
		if(null != city_code) {
			List<Area> cityList = areaDao.getNameByCode(city_code);
			if (cityList != null && cityList.size() > 0) {
				cityName = cityList.get(0).getName();
			}
		}
		if(null != area_code) {
			List<Area> areaList = areaDao.getNameByCode(area_code);
			if (areaList != null && areaList.size() > 0) {
				areaName = areaList.get(0).getName();
			}
		}
		String address = "";
		if(StringUtils.isNotBlank(provinceName) && StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(areaName)){
			address = provinceName + cityName + areaName + placename + detailAddress;
		}

		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName(name);//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setPlacename(placename);
		orderAddress.setDetailAddress(detailAddress);//详细地址
		orderAddress.setAddress(address);//收货人完整地址
		orderAddress.preInsert();
		orderAddressDao.insert(orderAddress);

		orderAddress.setOrgId(custom.getOrgId());//客户所属机构
		//orderAddress.setStationId(customInfo.getStationId());//客户所属服务站
		if(StringUtils.isNotBlank(customInfo.getAddrLatitude())){
			orderAddress.setAddrLatitude(customInfo.getAddrLatitude());//纬度
		}else{
			orderAddress.setAddrLatitude(null);//纬度
		}
		if(StringUtils.isNotBlank(customInfo.getAddrLongitude())){
			orderAddress.setAddrLongitude(customInfo.getAddrLongitude());//经度
		}else{
			orderAddress.setAddrLongitude(null);//经度
		}
		return orderAddress;
	}

	/**
	 * 订单创建 - 订单主表
	 * @param info
	 * @return
	 */
	private OrderMasterInfo openCreateForMaster(OrderInfo info,String order_type) {
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		masterInfo.preInsert();
		orderMasterInfoDao.insert(masterInfo);
		return masterInfo;
	}

	/**
	 * 根据ID查找客户
	 * @param info
	 * @return
	 */
	public OrderCustomInfo findCustomerById(OrderCustomInfo info) {
		OrderCustomInfo customInfo = orderCustomInfoDao.findCustomerById(info);
		List<OrderCustomAddress> addressList = orderCustomInfoDao.listAddressByCustomAddress(customInfo);
		if(null != customInfo){
			info.setOrgId(UserUtils.getUser().getOrganization().getId());
			List<OrderDropdownInfo> stationList = orderCustomInfoDao.findStationList(info);
			customInfo.setStationList(stationList);

			if(addressList!=null && addressList.size()>0){
				customInfo.setAddress(addressList.get(0));
				for(OrderCustomAddress address : addressList){
					if("yes".equals(address.getDefaultType())){
						customInfo.setAddress(address);
					}
				}
			}

            if(customInfo.getAddress() == null){
                customInfo.setAddress(new OrderCustomAddress());
            }
		}
		return customInfo;
	}
	/**
	 * 根据手机号查找客户
	 * @param info
	 * @return
	 */
	public OrderCustomInfo findCustomerByPhone(OrderCustomInfo info) {
		info.setOrgId(UserUtils.getUser().getOrganization().getId());
		info.setSource("own");
		OrderCustomInfo customInfo = orderCustomInfoDao.findCustomerByPhone(info);
		List<OrderCustomAddress> addressList = orderCustomInfoDao.listAddressByCustomAddress(customInfo);
		if(null != customInfo){
			info.setOrgId(UserUtils.getUser().getOrganization().getId());
			List<OrderDropdownInfo> stationList = orderCustomInfoDao.findStationList(info);
			customInfo.setStationList(stationList);

			if(addressList!=null && addressList.size()>0){
				customInfo.setAddress(addressList.get(0));
				for(OrderCustomAddress address : addressList){
					if("yes".equals(address.getDefaultType())){
						customInfo.setAddress(address);
					}
				}
			}

            if(customInfo.getAddress() == null){
                customInfo.setAddress(new OrderCustomAddress());
            }
		}
		return customInfo;
	}
	/**
	 * 获取服务项目列表
	 * @param info
	 * @return
	 */
	public List<OrderDropdownInfo> findItemList(OrderInfo info) {
		info.setOrgId(UserUtils.getUser().getOrganization().getId());
		return orderGoodsDao.findItemList(info);
	}
	/**
	 * 获取服务项目下的商品列表
	 * @param orderInfo
	 * @return
	 */
	public List<OrderGoods> findGoodsListByItem(OrderGoods orderInfo) {
		List<OrderGoods>  list = new ArrayList<OrderGoods>();
		List<OrderGoods> goodsList = dao.getGoodsList(orderInfo);    //服务项目下所有商品信息
		OrderGoods houseInfo = null;

		//得到除了居室以外的商品及选择信息
		if(goodsList!=null && goodsList.size()!=0) {
			List<OrderGoodsTypeHouse> houses = new ArrayList<OrderGoodsTypeHouse>();
			for (OrderGoods goods : goodsList) {//循环所有商品
				if ("house".equals(goods.getGoodsType())) {//按居室的商品
					OrderGoodsTypeHouse house = new OrderGoodsTypeHouse();
					house.setId(goods.getGoodsId());
					house.setName(goods.getGoodsName());
					house.setPayPrice(goods.getPayPrice());
					house.setMinPurchase(goods.getMinPurchase());
					house.setGoodsUnit(goods.getGoodsUnit());
					house.setGoodsNum(goods.getMinPurchase());
					houses.add(house);

					if (null == houseInfo) {
						houseInfo = new OrderGoods();
						BeanUtils.copyProperties(goods, houseInfo);
					}
				} else {

					String dj = goods.getPayPrice();//商品单价
					if (StringUtils.isEmpty(dj)) {
						dj = "0";
					}
					int num = goods.getMinPurchase();//商品数量
					BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
					goods.setPayPriceSum(price.toString());//总价

					list.add(goods);
				}
			}

			if (null != houseInfo) {

				String dj = houseInfo.getPayPrice();//商品单价
				if (StringUtils.isEmpty(dj)) {
					dj = "0";
				}
				int num = houseInfo.getMinPurchase();//商品数量
				BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
				houseInfo.setPayPriceSum(price.toString());//总价

				if (houses != null && houses.size() > 0) {
					houseInfo.setHouseId(houses.get(0).getId());
					houseInfo.setHouses(houses);
				} else {
					houseInfo.setHouseId("");
					houseInfo.setHouses(null);
				}
				list.add(houseInfo);
			}
		}
		return list;
	}

	/**
	 * 获取商品的技师列表
	 * @param orderInfo(itemId)
	 * @return
	 */
	public List<OrderDispatch> findTechListByGoods(OrderInfo orderInfo) {
		String techName = orderInfo.getTechName();//查询条件
		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList();
		if(goodsInfoList == null || goodsInfoList.size() == 0 ){
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String orgId = UserUtils.getUser().getOrganization().getId();
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		//serchInfo.setOrderId(orderInfo.getId());
		serchInfo.setTechName(techName);//查询条件
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		return techList;
	}

	/**
	 * 获取技师的时间列表
	 * @param orderInfo(goodsList,tech(null))
	 * @return
	 */
	public List<OrderTimeList> findTimeListByTech(OrderInfo orderInfo) {

		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OrderTimeList> list = new ArrayList<>();
		int value = 1;
		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList();
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//

				String cate_goods_id = goods.getGoodsId();
				if("house".equals(goods.getGoodsType())) {//按居室的商品
					cate_goods_id = goods.getHouseId();
				}
				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				if(0 == goodsNum){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				Double convertHours = commodity.getConvertHours();		// 折算时长
				if(convertHours == null || 0 == convertHours){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				if(0 == startPerNum){
					startPerNum =1;
				}
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数
				if(0 == cappinPerNum){
					cappinPerNum = 30;
				}

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = 0.0;
				if(convertHours!=null) {
					totalTime = convertHours * goodsNum;//商品需要时间
				}
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					//addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
					addTechNum= (b1.subtract(b2).divide(b2, 0, BigDecimal.ROUND_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum) {//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}

			if(orderInfo.getTechList()!=null && orderInfo.getTechList().size()!=0) {
				techDispatchNum = orderInfo.getTechList().size();
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			logger.error("未找到当前订单服务商品信息");
			return null;
		}
		String stationId = orderInfo.getStationId();//服务站ID
		if(null == stationId){
			logger.error("未找到当前订单的服务站信息");
			return null;
		}
		Double serviceSecond = (serviceHour * 3600);

		List<OrderDispatch> techList = orderInfo.getTechList();
		if(techList==null || techList.size()==0) {
			//取得技师List
			OrderDispatch serchInfo = new OrderDispatch();
			//展示当前下单客户所在服务站的所有可服务的技师
			serchInfo.setStationId(stationId);
			//（1）会此技能的
			String orgId =  UserUtils.getUser().getOrganization().getId();
			SerSkillSort serchSkillSort = new SerSkillSort();
			serchSkillSort.setOrgId(orgId);
			serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
			String skillId = "";
			List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
			if (skillSortList != null && skillSortList.size() == 1) {
				skillId = skillSortList.get(0).getSkillId();
			} else {
				logger.error("未找到商品需求的技能信息");
				return null;
			}
			serchInfo.setSkillId(skillId);
			//（2）上线、在职
			serchInfo.setTechStatus("yes");
			serchInfo.setJobStatus("online");
			//自动派单 全职 ; 手动派单没有条件
			serchInfo.setJobNature("full_time");
			//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
			//serchInfo.setOrderId(orderInfo.getId());
			techList = dao.getTechListBySkillId(serchInfo);
            if(techList.size() < techDispatchNum){//技师数量不够
                logger.error("技师数量不够");
                return null;
            }
		}else{
            techDispatchNum = techList.size();
        }

		for(Date date : dateList){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				responseRe.setValue(String.valueOf(value));
				value++;
				responseRe.setLabel(DateUtils.formatDate(date, "yyyy-MM-dd"));
				//该日服务时间点列表
				List<OrderDispatch> hours = findTimeListHours(date,techList, techDispatchNum,serviceSecond);
				if(hours!= null && hours.size()>0){
					responseRe.setServiceTime(hours);
					list.add(responseRe);
				}
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"未找到服务时间点列表");
			}

		}
		return list;
	}

	private List<OrderDispatch> findTimeListHours(Date date, List<OrderDispatch> techList,int techDispatchNum,Double serviceSecond ) {
		List<OrderDispatch> techForList = new ArrayList<>();
		if(techList != null){//深浅拷贝问题
			for(OrderDispatch dispatch: techList){
				techForList.add(dispatch);
			}
		}
		int week = DateUtils.getWeekNum(date); //周几
		Date serviceDateMin = DateUtils.parseDate(DateUtils.formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		Date serviceDateMax = DateUtils.parseDate(DateUtils.formatDate(date, "yyyy-MM-dd") + " 23:59:59");

		Iterator<OrderDispatch> it = techForList.iterator();
		while(it.hasNext()) {//循环技师List 取得可用时间
			OrderDispatch tech = it.next();

			//-----------------取得技师 当天(15天中的某天)可用工作时间 并且转成时间点列表 开始----------------------------------------------------------
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = dao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){
				it.remove();

				if(techForList.size() < techDispatchNum){//技师数量不够
					return null;
				}
				continue;
			}else {
				if(DateUtils.isToday(date)){
					if(DateUtils.timeBeforeNow(workTimeList.get(0).getEndTime())){
						it.remove();

						if(techForList.size() < techDispatchNum){//技师数量不够
							return null;
						}
						continue;
					}
				}
			}
			ServiceTechnicianWorkTime workTime = workTimeList.get(0);
			Date startDateForWork = workTime.getStartTime();
			if(DateUtils.isToday(date)) {
				if (DateUtils.timeBeforeNow(workTime.getStartTime())) {
					startDateForWork = DateUtils.parseDate(
							DateUtils.formatDate(startDateForWork, "yyyy-MM-dd") + " " +
									DateUtils.formatDate(new Date(), "HH:mm:ss"));
				}
			}
			List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(startDateForWork,workTime.getEndTime());
			//-------------------取得技师 当天(15天中的某天)可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			if(workTimes != null) {
				//-------------------取得技师 当天(15天中的某天)休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
				serchTech.setStartTime(serviceDateMin);
				serchTech.setEndTime(serviceDateMax);
				//List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
				List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), date, "holiday");
				if (techHolidyList != null && techHolidyList.size() != 0) {
					for (TechScheduleInfo holiday : techHolidyList) {
						//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(holiday.getStartTime(), holiday.getEndTime());
						List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());
						Iterator<String> it1 = workTimes.iterator();
						while (it1.hasNext()) {
							String work = (String) it1.next();
							if (holidays.contains(work)) {//去除休假时间
								it1.remove();
								//continue;
							}
						}
					}
				}
				//-------------------取得技师 当天(15天中的某天)休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

				//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
				//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
				//--- 服务状态(wait_service:待服务 started:已上门, finish:已完成)',
				//List<OrderDispatch> orderList = dao.findTechOrderList(serchTech);
				List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), date, "order");
				if (techOrderList != null && techOrderList.size() != 0) {
					for (TechScheduleInfo order : techOrderList) {
						int intervalTimeS = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) &&
								Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time")) + serviceSecond.intValue();
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + serviceSecond.intValue();
						}

						int intervalTimeE = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) &&
								Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
						}

						List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
								DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS),
								DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE));
						if (orders != null && workTimes!= null) {
							Iterator<String> it2 = workTimes.iterator();
							while (it2.hasNext()) {
								String work = (String) it2.next();
								if (orders.contains(work)) {//去除订单时间
									it2.remove();
									//continue;
								}
							}
						}
					}
				}
				//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 结束------------------

			}
			tech.setWorkTimes(workTimes);//可用的时间列表
		}

		List<String> allTimeList = new ArrayList<String>();
		List<String> resTimeList = new ArrayList<String>();
		for(OrderDispatch tech : techForList){
			if(tech.getWorkTimes() != null) {
				allTimeList.addAll(tech.getWorkTimes());
			}
		}
		//统计每一个元素出现的频率
		//将List转换为Set
		Set uniqueSet = new HashSet(allTimeList);
		for (Object temp : uniqueSet) {
			if(Collections.frequency(allTimeList, temp) >= techDispatchNum){
				resTimeList.add(temp.toString());
			}
		}
		//时间排序
		String[] strings = new String[resTimeList.size()];
		resTimeList.toArray(strings);
		Arrays.sort(strings);//排序
		List<String> list = Arrays.asList(strings);

		List<OrderDispatch> listRe = new ArrayList<>();
		for(String time : list){
			OrderDispatch info = new OrderDispatch();
			info.setServiceTimeStr(time);
			listRe.add(info);
		}

		return listRe;
	}

	/**
	 * 获取商品所需人数和时间
	 * @param orderInfo(goodsList)
	 * @return
	 */
    public Map<String,String> findGoodsNeedTech(OrderInfo orderInfo) {
        List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList();
        int techDispatchNum = 0;//派人数量
        double orderTotalTime = 0.0;//订单所需时间
        double serviceHour = 0.0;//建议服务时长（小时）
        if(goodsInfoList != null && goodsInfoList.size() != 0 ){
            for(OrderGoods goods :goodsInfoList){//

                String cate_goods_id = goods.getGoodsId();
                if("house".equals(goods.getGoodsType())) {//按居室的商品
                    cate_goods_id = goods.getHouseId();
                }
                SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
                int goodsNum = goods.getGoodsNum();		// 订购商品数
                if(0 == goodsNum){
                    logger.error("未找到当前订单服务商品信息的订购商品数");
                }
                Double convertHours = commodity.getConvertHours();		// 折算时长
                if(convertHours == null || 0 == convertHours){
                    logger.error("未找到当前订单服务商品信息的订购商品数");
                }
                int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
                if(0 == startPerNum){
                    startPerNum =1;
                }
                int cappinPerNum = commodity.getCappingPerNum();		//封项人数
                if(0 == cappinPerNum){
                    cappinPerNum = 30;
                }

                int techNum = 0;//当前商品派人数量
                int addTechNum=0;
                Double totalTime = 0.0;
                if(convertHours!=null) {
                    totalTime = convertHours * goodsNum;//商品需要时间
                }
                Double goodsTime = convertHours * goodsNum;//gon
                orderTotalTime = orderTotalTime + goodsTime;

                if(totalTime > 4){//每4小时增加1人
                    BigDecimal b1 = new BigDecimal(totalTime);
                    BigDecimal b2 = new BigDecimal(new Double(4));
                    //addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
					addTechNum= (b1.subtract(b2).divide(b2, 0, BigDecimal.ROUND_UP).intValue());
                }
                techNum = startPerNum + addTechNum;
                if(techNum > cappinPerNum){//每个商品需求的人数
                    techNum = cappinPerNum;
                }
                if(techNum > techDispatchNum) {//订单需求的最少人数
                    techDispatchNum = techNum;
                }
            }

        } else {
            logger.error("未找到当前订单服务商品信息");
            return null;
        }

        Map<String,String> map = new HashMap<>();
        map.put("dispatchNum",Integer.toString(techDispatchNum));
        map.put("serviceHour",Double.toString(orderTotalTime));
        return map;
    }

}