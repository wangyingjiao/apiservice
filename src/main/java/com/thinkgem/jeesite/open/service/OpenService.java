/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.open.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OpenService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
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
	BasicOrganizationDao basicOrganizationDao;
	@Autowired
	BasicServiceStationDao basicServiceStationDao;


	/**
     * 对接接口 选择服务时间
	 * @param info
     * @return
     */
    public List<OpenServiceTimesResponse> openServiceTimes(OpenServiceTimesRequest info) {
		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OpenServiceTimesResponse> list = new ArrayList<>();
		for(Date date : dateList){
			OpenServiceTimesResponse response = new OpenServiceTimesResponse();
			response.setFormat_date(DateUtils.formatDate(date, "yyyy-MM-dd"));
			response.setWeekday(DateUtils.formatDate(date, "E"));
			//该日服务时间点列表
			List<OpenHours> hours = openServiceTimesHours(date, info);
			response.setHours(hours);
		}
    	return null;
    }

	/**
	 * 对接接口 选择服务时间 - 该日服务时间点列表
	 * @param date
	 * @param info
	 * @return
	 */
	private List<OpenHours> openServiceTimesHours(Date date, OpenServiceTimesRequest info) {
		String store_id = info.getStore_id();//门店ID
		String eshop_code = info.getEshop_code();//E店编码
		String latitude = info.getLatitude();//地址纬度
		String longitude = info.getLongitude();//地址经度
		List<OpenServiceInfo> serviceInfos = info.getService_info();//下单服务项目信息
		String platform = info.getPlatform();//对接平台代号   默认值 : gasq


		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价
		BigDecimal openPrice = new BigDecimal(0);//对接总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		if(null != serviceInfos && serviceInfos.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OpenServiceInfo openServiceInfo : serviceInfos){
				String cate_goods_id = openServiceInfo.getCate_goods_id();
				int buy_num = openServiceInfo.getBuy_num();
				String pay_price = openServiceInfo.getPay_price();

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				goods = new OrderGoods();
				goods.setSortId(commodity.getSortId());//服务分类ID
				goods.setItemId(commodity.getItemId());//服务项目ID
				goods.setItemName(commodity.getItemName());//项目名称
				goods.setGoodsId(commodity.getId());//商品ID
				goods.setGoodsName(commodity.getName());//商品名称
				goods.setGoodsNum(buy_num);//订购商品数
				goods.setPayPrice(pay_price);//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());
				orderGoods.add(goods);

				originPrice = originPrice.add(commodity.getPrice());//商品总价
				openPrice = openPrice.add(new BigDecimal(pay_price));
				sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

				int goodsNum = buy_num;		// 订购商品数
				Double convertHours = commodity.getConvertHours();		// 折算时长
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

				if(goodsTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(goodsTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单的最大人数
					techDispatchNum = techNum;
				}
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			return null;
		}

		//通过对接方E店CODE获取机构
		BasicOrganization organizationSerch = new BasicOrganization();
		organizationSerch.setJointEshopCode(eshop_code);
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(organizationSerch);
		String orgId = "";
		Date orgWorkStartTime;//工作开始时间
		Date orgWorkEndTime;//工作结束时间
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
			orgWorkStartTime = organization.get(0).getWorkStartTime();
			orgWorkEndTime = organization.get(0).getWorkEndTime();
		}else{
			return null;
		}
		//通过门店ID获取服务站
		BasicServiceStation stationSerch = new BasicServiceStation();
		stationSerch.setStoreId(store_id);
		List<BasicServiceStation> stations = basicServiceStationDao.getStationListByStoreId(stationSerch);
		String stationId = "";
		if(null != stations && stations.size() > 0){
			stationId = stations.get(0).getId();
		}else{
			return null;
		}

		if(orderGoods != null && orderGoods.size() != 0 ){
			for(OrderGoods goods :orderGoods){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//商品需要时间

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}
		}else{
			throw new ServiceException("订单没有服务信息！");
		}

		int week = DateUtils.getWeekNum(date); //周几
		Date serviceDateMin = DateUtils.parseDate(DateUtils.formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		Date serviceDateMax = DateUtils.parseDate(DateUtils.formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		Double serviceSecond = (serviceHour * 3600);

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = dao.getSkillIdBySortId(orderGoods.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		//serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		if(techList.size() < techDispatchNum){//技师数量不够
			return null;
		}
		Iterator<OrderDispatch> it = techList.iterator();
		while(it.hasNext()) {
			OrderDispatch tech = it.next();
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = dao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){
				it.remove();

				if(techList.size() < techDispatchNum){//技师数量不够
					return null;
				}
				continue;
			}
			ServiceTechnicianWorkTime workTime = workTimeList.get(0);
			List<String> workTimes = DateUtils.getHeafHourTimeList(workTime.getStartTime(),workTime.getEndTime());

			//去除休假时间
			serchTech.setStartTime(serviceDateMin);
			serchTech.setEndTime(serviceDateMax);
			List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
			if(holidayList != null && holidayList.size() !=0){
				for(ServiceTechnicianHoliday holiday : holidayList){
					List<String> holidays = DateUtils.getHeafHourTimeList(holiday.getStartTime(),holiday.getEndTime());
					Iterator<String> it1 = workTimes.iterator();
					while(it.hasNext()) {
						String work = (String)it1.next();
						if(holidays.contains(work)){//去除休假时间
							it1.remove();
							//continue;
						}
					}
				}
			}

			//去除订单前后时间段
			List<OrderDispatch> orderList = dao.findTechOrderList(serchTech);
			if(orderList != null && orderList.size() !=0){
				for(OrderDispatch order : orderList){
					int intervalTime = 0;//必须间隔时间 秒
					if(11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(),"HH")) &&
							Integer.parseInt(DateUtils.formatDate(order.getEndTime(),"HH"))  < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + 15*60 + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = 15*60 + 10*60;
					}

					List<String> orders = DateUtils.getHeafHourTimeList(
							DateUtils.addSeconds(order.getStartTime(),-serviceSecond.intValue()),
							DateUtils.addSeconds(order.getEndTime(),intervalTime));
					Iterator<String> it2 = workTimes.iterator();
					while(it.hasNext()) {
						String work = (String)it2.next();
						if(orders.contains(work)){//去除订单时间
							it2.remove();
							//continue;
						}
					}
				}
			}
			tech.setWorkTimes(workTimes);
		}

		List<String> allTimeList = new ArrayList<String>();
		List<String> resTimeList = new ArrayList<String>();
		for(OrderDispatch tech : techList){
			allTimeList.addAll(tech.getWorkTimes());
		}
		//统计每一个元素出现的频率
		//将List转换为Set
		Set uniqueSet = new HashSet(allTimeList);
		for (Object temp : uniqueSet) {
			if(Collections.frequency(allTimeList, temp) >= techDispatchNum){
				resTimeList.add(temp.toString());
			}
		}

		List<OpenHours> hours = new ArrayList<>();
		List<String> heafHourTimeList = DateUtils.getHeafHourTimeListBorder(orgWorkStartTime,orgWorkEndTime);//机构的上下班时间
		for(String heafHourTime : heafHourTimeList){
			OpenHours openHours  = new OpenHours();
			openHours.setTime(heafHourTime);
			if(resTimeList.contains(heafHourTime)){//是否可用
				openHours.setDisenable("enable");
			}else{
				openHours.setDisenable("disable");
			}
			hours.add(openHours);
		}

		return hours;
	}

	/**
	 * 对接接口 订单创建
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public OpenCreateResponse openCreate(OpenCreateRequest info) {
		OpenCreateResponse response = new OpenCreateResponse();
		if(null == info){
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}

		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = openCreateForMaster(info);


		// order_address  订单地址表---------------------------------------------------------------------------------
		OrderAddress orderAddress = openCreateForAddress(info);

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = openCreateForOrder(info, masterInfo, orderAddress);
		if(null == orderInfo){
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return null;
		}

		// order_dispatch -派单表----------------------------------------------------------------------------------
		List<OrderDispatch> orderDispatches = orderInfo.getTechList();//派单信息
		if(null != orderDispatches && orderDispatches.size() > 0) {
			openCreateForDispatch(orderInfo, orderDispatches);
		}

		// order_goods-订单服务关联--------------------------------------------------------------------
		List<OrderGoods> orderGoods = orderInfo.getGoodsInfoList();//商品信息
		if(null != orderGoods && orderGoods.size() > 0){
			openCreateForGoods(orderInfo, orderGoods);
		}

		// order_pay_info  支付信息 ------------------------------------------------------------------------------
		BigDecimal openPrice = orderInfo.getOpenPrice();//对接总价
		openCreateForPay( masterInfo, openPrice);

		//------------------------------------------------------------------------------------------------
		response = new OpenCreateResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_order_id(orderInfo.getId());// 自营服务订单ID
		return response;
	}

	/**
	 * 订单创建 - 支付信息
	 * @param masterInfo
	 * @param openPrice
	 */
	private void openCreateForPay(OrderMasterInfo masterInfo, BigDecimal openPrice) {
		OrderPayInfo payInfo = new OrderPayInfo();
		payInfo.setPayNumber(DateUtils.getDateAndRandomTenNum("02"));//支付编号
		payInfo.setMasterId(masterInfo.getId());//主订单ID',
		payInfo.setPayPlatform(null);//支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
		payInfo.setPayMethod(null);//支付方式(online:在线 offline:货到付款)',
		payInfo.setPayTime(null);//支付时间',
		payInfo.setPayAccount(openPrice.toString());//支付总额',
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
	private OrderInfo openCreateForOrder(OpenCreateRequest info, OrderMasterInfo masterInfo, OrderAddress orderAddress) {
		String store_id = info.getStore_id();//门店ID
		String eshop_code = info.getEshop_code();//E店编码
		String remark = info.getRemark();//订单备注(用户备注)
		List<OpenServiceInfo> serviceInfos = info.getServiceInfos();
		String servie_time = info.getServie_time();//服务时间
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度
		String sum_price = info.getSum_price();//订单总支付价格
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单

		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价
		BigDecimal openPrice = new BigDecimal(0);//对接总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		if(null != serviceInfos && serviceInfos.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OpenServiceInfo openServiceInfo : serviceInfos){
				String cate_goods_id = openServiceInfo.getCate_goods_id();
				int buy_num = openServiceInfo.getBuy_num();
				String pay_price = openServiceInfo.getPay_price();

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				goods = new OrderGoods();
				goods.setSortId(commodity.getSortId());//服务分类ID
				goods.setItemId(commodity.getItemId());//服务项目ID
				goods.setItemName(commodity.getItemName());//项目名称
				goods.setGoodsId(commodity.getId());//商品ID
				goods.setGoodsName(commodity.getName());//商品名称
				goods.setGoodsNum(buy_num);//订购商品数
				goods.setPayPrice(pay_price);//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());
				orderGoods.add(goods);

				originPrice = originPrice.add(commodity.getPrice());//商品总价
				openPrice = openPrice.add(new BigDecimal(pay_price));
				sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

				int goodsNum = buy_num;		// 订购商品数
				Double convertHours = commodity.getConvertHours();		// 折算时长
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

				if(goodsTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(goodsTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单的最大人数
					techDispatchNum = techNum;
				}
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			return null;
		}

		//通过对接方E店CODE获取机构
		BasicOrganization organizationSerch = new BasicOrganization();
		organizationSerch.setJointEshopCode(eshop_code);
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(organizationSerch);
		String orgId = "";
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
		}else{
			return null;
		}
		//通过门店ID获取服务站
		BasicServiceStation stationSerch = new BasicServiceStation();
		stationSerch.setStoreId(store_id);
		List<BasicServiceStation> stations = basicServiceStationDao.getStationListByStoreId(stationSerch);
		String stationId = "";
		if(null != stations && stations.size() > 0){
			stationId = stations.get(0).getId();
		}else{
			return null;
		}

		//--------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(masterInfo.getId());//主订单ID
		orderInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(orgId);  //所属服务机构ID
		orderInfo.setStationId(stationId);        //服务站id
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(sum_price);            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		orderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		orderInfo.setLatitude(latitude);                //服务地址  纬度
		orderInfo.setLongitude(longitude);         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(DateUtils.parseDate(servie_time));     //上门时间（服务时间）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(DateUtils.parseDate(servie_time),serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(DateUtils.parseDate(servie_time),serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource("gasq");  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(null);    // 客户ID
		orderInfo.setCustomerRemark(remark);   // 客户备注
		orderInfo.setCustomerRemarkPic(null);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.preInsert();
		orderInfoDao.insert(orderInfo);

		orderInfo.setOpenPrice(openPrice);
		orderInfo.setGoodsInfoList(orderGoods);//商品信息

		List<OrderDispatch> techList = openCreateForOrderFindDispatchList(orderInfo);//获取派单技师
		orderInfo.setTechList(techList);//派单技师List

		return orderInfo;
	}

	/**
	 * 订单创建 - 子订单信息 - 获取派单技师
	 * @param orderInfo
	 * @return
	 */
	private List<OrderDispatch> openCreateForOrderFindDispatchList(OrderInfo orderInfo){

		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList(); //取得订单服务信息

		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间

		int techDispatchNum = 0;//派人数量
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//商品需要时间

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}
		}else{
			throw new ServiceException("订单没有服务信息！");
		}

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = orderInfoDao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		//serchInfo.setTechName(techName);
		//serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = orderInfoDao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = orderInfoDao.getTechByHoliday(serchInfo);

		List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
		List<String> beforTimeCheckTechIdList = new ArrayList<String>();
		if(techList != null){
			for(OrderDispatch tech : techList){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
					beforTimeCheckTechList.add(tech);
					beforTimeCheckTechIdList.add(tech.getTechId());
				}
			}
		}
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = orderInfoDao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表
			List<OrderDispatch> maybeTechList = new ArrayList<OrderDispatch>();//订单上门时间在当前订单结束时间后90分钟之前的技师列表

			for(OrderDispatch orderTech : orderTechList){
				//处理当前订单实际结束时间90分之后开始的订单
				if(orderTech.getServiceTime().before(DateUtils.addMinutes(finishTime,90))){
					maybeTechList.add(orderTech);
				}
			}

			List<OrderDispatch> clashTechList = new ArrayList<OrderDispatch>();//得到不可以接单的技师
			//当前订单  上门时间前90分钟之后，结束时间后90分钟之前   有订单的技师判断是否有时间进行下一单
			for(OrderDispatch orderTech : maybeTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				Date serviceTimeOrder = orderTech.getServiceTime();//服务时间
				Date finishTimeOrder = orderTech.getFinishTime();//完成时间

				int intervalTime = 0;//必须间隔时间 秒
				int bicyclingTime = 15*60;//骑行时间
				//若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间
				int finishTimeHHOrder = Integer.parseInt(DateUtils.formatDate(finishTimeOrder, "HH"));
				int finishTimeHH = Integer.parseInt(DateUtils.formatDate(finishTime, "HH"));

				//可以接单
				if(finishTimeOrder.before(serviceTime)) {//订单结束时间在当前订单开始时间之前

					if(11 <= finishTimeHHOrder && finishTimeHHOrder < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTimeOrder,intervalTime).before(serviceTime)){
						//时间可以
						continue;
					}

				}else if(finishTime.before(serviceTimeOrder)) {//订单开始时间在当前订单结束时间之后
					if(11 <= finishTimeHH && finishTimeHH < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTime,intervalTime).before(serviceTimeOrder)){
						//时间可以
						continue;
					}
				}

				//其它情况 不可以接单
				clashTechList.add(orderTech);
			}

			List<OrderDispatch> techListRe = new ArrayList<>();
			//有时间接单 还未考虑是否有订单 的技师列表  去除 有订单并且时间冲突的技师
			for(OrderDispatch beforTimeCheckTech: beforTimeCheckTechList){
				boolean flag = true;
				for(OrderDispatch clashTech : clashTechList){
					if(beforTimeCheckTech.getTechId().equals(clashTech.getTechId())){
						flag = false;//时间冲突的技师
						break;
					}
				}
				if(flag) {
					techListRe.add(beforTimeCheckTech);
				}
			}
			if(techListRe.size() < techDispatchNum){//技师数量不够
				return null;
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
		}
		return null;
	}

	/**
	 * 订单创建 - 订单地址表
	 * @param info
	 * @return
	 */
	private OrderAddress openCreateForAddress(OpenCreateRequest info) {
		String phone = info.getPhone();//用户电话
		String area_code = info.getArea_code();//区CODE
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		String address = info.getAddress();//服务地址：小区+详细地址

		//省名称
		List<Area>  provinceList = areaDao.getNameByCode(province_code);
		String provinceName = "";
		if(provinceList != null && provinceList.size() > 0){
			provinceName = provinceList.get(0).getName();
		}
		//市名称
		List<Area>  cityList = areaDao.getNameByCode(province_code);
		String cityName = "";
		if(cityList != null && cityList.size() > 0){
			cityName = cityList.get(0).getName();
		}
		//区名称
		List<Area>  areaList = areaDao.getNameByCode(province_code);
		String areaName = "";
		if(areaList != null && areaList.size() > 0){
			areaName = areaList.get(0).getName();
		}
		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName("");//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setDetailAddress(address);//详细地址
		orderAddress.setAddress(provinceName + cityName + areaName + address);//收货人完整地址
		orderAddress.preInsert();
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

		OrderMasterInfo masterInfo = new OrderMasterInfo();
		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		masterInfo.preInsert();
		orderMasterInfoDao.insert(masterInfo);
		return masterInfo;
	}

	/**
	 * 对接接口 订单状态更新
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public OpenUpdateStautsResponse openUpdateStauts(OpenUpdateStautsRequest info) {
		OpenUpdateStautsResponse response = new OpenUpdateStautsResponse();
		if(null == info){
			response = new OpenUpdateStautsResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}
		String platform = info.getPlatform();//对接平台代号
		String orderId = info.getService_order_id();// 自营服务订单ID
		String cancelReason = info.getComment();//取消原因
		String status = info.getStatus();//cancel 取消；finish 已签收；success 完成

		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(orderId);// 自营服务订单ID
		if("cancel".equals(status)){
			orderInfo.setServiceStatus("cancel");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("cancel");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
			orderInfo.setCancelReason(cancelReason);//取消原因
		}else if("finish".equals(status)){
			orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("finish");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
		}else if("success".equals(status)){
			orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("success");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
		}

		int num = orderInfoDao.openUpdateGuoanxia(orderInfo);
		if(num == 0){
			response = new OpenUpdateStautsResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id(orderId);// 自营服务订单ID
			return response;
		}else{
			response = new OpenUpdateStautsResponse();
			response.setSuccess(true);// 状态：true 成功；false 失败
			response.setService_order_id(orderId);// 自营服务订单ID
			return response;
		}
	}

	/**
	 * 对接接口 更新订单信息
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public OpenUpdateInfoResponse openUpdateInfo(OpenUpdateInfoRequest info) {
		OpenUpdateInfoResponse response = new OpenUpdateInfoResponse();
		if(null == info){
			response = new OpenUpdateInfoResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id("");// 自营服务订单ID
			return response;
		}
		String orderId = info.getService_order_id();// 自营服务订单ID


		int num = 0;//更新件数
		List<OpenServiceInfo> serviceInfos = info.getService_info();//更新服务项目信息及数量
		if(null != serviceInfos && serviceInfos.size() > 0){
			num = num + openUpdateInfoServiceInfos(orderId, serviceInfos);
		}

		OpenGuoanxiaInfo guoanxia_info = info.getGuoanxia_info();//国安侠信息
		if(null != guoanxia_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setBusinessName(guoanxia_info.getName());//业务人员姓名
			orderInfo.setBusinessPhone(guoanxia_info.getPhone());//业务人员电话
			orderInfo.setBusinessRemark(guoanxia_info.getRemark());//业务人员备注
			List<String> remark_pic = guoanxia_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setBusinessRemarkPic(remarkPic);// 业务人员备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		OpenCostomerInfo costomer_info = info.getCostomer_info();//用户信息
		if(null != costomer_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setCustomerRemark(costomer_info.getRemark());//用户备注
			List<String> remark_pic = costomer_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setCustomerRemarkPic(remarkPic);// 用户备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		OpenStoreInfo store_info = info.getStore_info();//门店信息
		if(null != store_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setShopName(store_info.getName());//门店名
			orderInfo.setShopPhone(store_info.getTelephone());//门店电话
			orderInfo.setShopRemark(store_info.getRemark());//门店备注
			List<String> remark_pic = store_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setShopRemarkPic(remarkPic);// 门店备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		if(num == 0){//更新件数
			response = new OpenUpdateInfoResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id(info.getService_order_id());// 自营服务订单ID
			return response;
		}else{
			response = new OpenUpdateInfoResponse();
			response.setSuccess(true);// 状态：true 成功；false 失败
			response.setService_order_id(info.getService_order_id());// 自营服务订单ID
			return response;
		}
	}

	/**
	 * 更新服务项目信息及数量
	 * @param orderId
	 * @param serviceInfos
	 */
	private int openUpdateInfoServiceInfos(String orderId, List<OpenServiceInfo> serviceInfos) {
		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价
		BigDecimal openPrice = new BigDecimal(0);//对接总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		OrderGoods goods = new OrderGoods();
		for(OpenServiceInfo openServiceInfo : serviceInfos){
			String cate_goods_id = openServiceInfo.getCate_goods_id();
			int buy_num = openServiceInfo.getBuy_num();
			String pay_price = openServiceInfo.getPay_price();

			SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
			goods = new OrderGoods();
			goods.setOrderId(orderId);
			goods.setSortId(commodity.getSortId());//服务分类ID
			goods.setItemId(commodity.getItemId());//服务项目ID
			goods.setItemName(commodity.getItemName());//项目名称
			goods.setGoodsId(commodity.getId());//商品ID
			goods.setGoodsName(commodity.getName());//商品名称
			goods.setGoodsNum(buy_num);//订购商品数
			goods.setPayPrice(pay_price);//对接后单价
			goods.setOriginPrice(commodity.getPrice().toString());//原价
			goods.setMajorSort(commodity.getMajorSort());
			goods.preInsert();
			orderGoods.add(goods);

			originPrice = originPrice.add(commodity.getPrice());//商品总价
			openPrice = openPrice.add(new BigDecimal(pay_price));
			sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
			goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

			int goodsNum = buy_num;		// 订购商品数
			Double convertHours = commodity.getConvertHours();		// 折算时长

			Double goodsTime = convertHours * goodsNum;//gon
			orderTotalTime = orderTotalTime + goodsTime;
		}

		OrderInfo orderInfoBefor = orderInfoDao.get(orderId);//当前订单
		Date serviceTime = orderInfoBefor.getServiceTime();//服务时间

		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setId(orderId);
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(serchOrderInfo); //订单当前已有技师List
		if(techList != null && techList.size() != 0){
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techList.size());//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			return 0;
		}

		//删除 订单 原商品信息
		OrderGoods serchOrderGoods = new OrderGoods();
		serchOrderGoods.setOrderId(orderId);
		orderGoodsDao.deleteGoodsByOrderId(serchOrderGoods);

		//更新订单信息
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(openPrice.toString());            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.preUpdate();
		int num = orderInfoDao.update(orderInfo);

		//新增商品信息
		for(OrderGoods goodsInsert : orderGoods){
			orderGoodsDao.insert(goodsInsert);
		}

		return num;
	}

	/**
	 *  国安社区开放接口 - 服务项目保存
	 * @param info
	 * @return
	 */
	public OpenUpdateStautsResponse openSendSaveItem(OpenUpdateStautsRequest info) {

		return null;

	}

	/**
	 *  国安社区开放接口 - 更新订单信息
	 *  更新服务时间、改派服务人员、服务人员端备注
	 * @param info
	 * @return
	 */
	public OpenUpdateStautsResponse openSendSaveOrder(OpenUpdateStautsRequest info) {
		return null;
	}
}