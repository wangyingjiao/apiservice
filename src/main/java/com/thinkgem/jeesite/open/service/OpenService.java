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
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
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
	@Autowired
	BasicStoreDao basicStoreDao;


	/**
     * 对接接口 选择服务时间
	 * @param info
     * @return
     */
    public Map<String,Object> openServiceTimes(OpenServiceTimesRequest info) {
		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OpenServiceTimesResponse> list = new ArrayList<>();

		String store_id = info.getStore_id();//门店ID
		if(null == store_id){
			throw new ServiceException("门店ID不能为空");
		}
		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}

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

				String key = openServiceInfo.getCate_goods_id();
				String cate_goods_id = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
				if(null == cate_goods_id){
					throw new ServiceException("自营服务服务商品ID不能为空");
				}
				int buy_num = openServiceInfo.getBuy_num();
				if(0 == buy_num){
					throw new ServiceException("购买数量不能为空");
				}
				/*String pay_price = openServiceInfo.getPay_price();
				if(null == pay_price){
					throw new ServiceException("服务项目单价不能为空");
				}*/

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
				//goods.setPayPrice(pay_price);//对接后单价
				//goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());
				orderGoods.add(goods);

				//originPrice = originPrice.add(commodity.getPrice());//商品总价
				//openPrice = openPrice.add(new BigDecimal(pay_price));
				//sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
				//goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

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
			throw new ServiceException("下单服务项目商品信息不能为空");
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
			throw new ServiceException("未找到E店CODE对应的机构信息");
		}
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
		Double serviceSecond = (serviceHour * 3600);
		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(orderGoods.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
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
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);


		for(Date date : dateList){
			OpenServiceTimesResponse responseRe = new OpenServiceTimesResponse();
			responseRe.setFormat(DateUtils.formatDate(date, "yyyy-MM-dd"));

			responseRe.setDayOfWeek(DateUtils.getWeekL(date));

			List<String> resTimeList;
			try {
				//该日服务时间点列表
				resTimeList = openServiceTimesHours(date,techList,techDispatchNum ,serviceSecond );

			}catch (Exception e){
				logger.error(responseRe.getFormat()+"未找到服务时间点列表");
				resTimeList = new ArrayList<>();
			}

			List<OpenHours> hours = new ArrayList<>();
			Date reStartTime = orgWorkStartTime;
			Date reEndTime = orgWorkEndTime;
			if(resTimeList!=null && resTimeList.size()>0){
				String resTimeListStart = resTimeList.get(0);
				String resTimeListEnd = resTimeList.get(resTimeList.size()-1);
				if(reStartTime.after(DateUtils.parseDate(DateUtils.formatDate(reStartTime,"yyyy-MM-dd"+" "+resTimeListStart)))){
					reStartTime = DateUtils.parseDate(DateUtils.formatDate(reStartTime,"yyyy-MM-dd"+" "+resTimeListStart));
				}
				if(reEndTime.before(DateUtils.parseDate(DateUtils.formatDate(reEndTime,"yyyy-MM-dd"+" "+resTimeListEnd)))){
					reEndTime = DateUtils.parseDate(DateUtils.formatDate(reEndTime,"yyyy-MM-dd"+" "+resTimeListEnd));
				}
			}

			List<String> heafHourTimeList = DateUtils.getHeafHourTimeListBorder(reStartTime,reEndTime);//机构的上下班时间
			for(String heafHourTime : heafHourTimeList){
				OpenHours openHours  = new OpenHours();
				openHours.setHour(heafHourTime);
				if(resTimeList != null) {
                    if (resTimeList.contains(heafHourTime)) {//是否可用
                        openHours.setDisenable("enable");
                    } else {
                        openHours.setDisenable("disable");
                    }
                }else{
                    openHours.setDisenable("disable");
                }
				hours.add(openHours);
			}
			responseRe.setHours(hours);
			list.add(responseRe);
		}

		Map<String,Object> value = new HashMap();
		value.put("availabletimes",list);

		return value;
	}

	/**
	 * 对接接口 选择服务时间 - 该日服务时间点列表
	 * @param date
	 * @param
	 * @return
	 */
	private List<String> openServiceTimesHours(Date date, List<OrderDispatch> techList,int techDispatchNum,Double serviceSecond ) {
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
		while(it.hasNext()) {
			OrderDispatch tech = it.next();
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
					Date dateAddTwoHour = DateUtils.addSecondsNotDayE(new Date(),2*60*60);//当天时间向后退2H

					startDateForWork = DateUtils.parseDate(
							DateUtils.formatDate(startDateForWork, "yyyy-MM-dd") + " " +
									DateUtils.formatDate(dateAddTwoHour, "HH:mm:ss"));
				}
			}
			List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(startDateForWork,workTime.getEndTime());

			if(workTimes != null) {
				//去除休假时间
				serchTech.setStartTime(serviceDateMin);
				serchTech.setEndTime(serviceDateMax);
				List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
				if (holidayList != null && holidayList.size() != 0) {
					for (ServiceTechnicianHoliday holiday : holidayList) {
						List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(holiday.getStartTime(), holiday.getEndTime());
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

				//去除订单前后时间段
				List<OrderDispatch> orderList = dao.findTechOrderList(serchTech);
				if (orderList != null && orderList.size() != 0) {
					for (OrderDispatch order : orderList) {
						int intervalTimeS = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -(15 * 60 + 10 * 60)), "HH")) &&
								Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -(15 * 60 + 10 * 60)), "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeS = 40 * 60 + 15 * 60 + 10 * 60 + serviceSecond.intValue();
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeS = 15 * 60 + 10 * 60 + serviceSecond.intValue();
						}

						int intervalTimeE = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) &&
								Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeE = 40 * 60 + 15 * 60 + 10 * 60;
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeE = 15 * 60 + 10 * 60;
						}

						List<String> orders = DateUtils.getHeafHourTimeListBorder(
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
			}
			tech.setWorkTimes(workTimes);
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
		List<String> list = java.util.Arrays.asList(strings);

		return list;
	}

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
			BigDecimal openPrice = orderInfo.getOpenPrice();//对接总价
			openCreateForPay( masterInfo, openPrice);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单支付信息表失败!");
		}

		//------------------------------------------------------------------------------------------------
		response = new OpenCreateResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_order_id(orderInfo.getOrderNumber());// 自营服务订单ID

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
	private OrderInfo openCreateForOrder(OpenCreateRequest info, OrderMasterInfo masterInfo, OrderAddress orderAddress) {
		String store_id = info.getStore_id();//门店ID
		if(null == store_id){
			throw new ServiceException("门店ID不能为空");
		}
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
		String gasq_order_id = info.getGasq_order_id();
		if(null == gasq_order_id){
			throw new ServiceException("国安社区订单ID不能为空");
		}
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度
		String sum_price = info.getSum_price();//订单总支付价格
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
		if(null == order_type){
			throw new ServiceException("订单类型不能为空");
		}

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
				goods.setPayPrice(pay_price);//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());

                goods.setConvertHours(commodity.getConvertHours());		// 折算时长
                goods.setStartPerNum(commodity.getStartPerNum());   		//起步人数（第一个4小时时长派人数量）
                goods.setCappingPerNum(commodity.getCappingPerNum());		//封项人数

				orderGoods.add(goods);
				BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
				originPrice = originPrice.add(price);//商品总价
				openPrice = openPrice.add(new BigDecimal(pay_price));
				sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

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
			throw new ServiceException("商品信息不能为空");
		}

		//通过对接方E店CODE获取机构
		BasicOrganization organizationSerch = new BasicOrganization();
		organizationSerch.setJointEshopCode(eshop_code);
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(organizationSerch);
		String orgId = "";
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
		}else{
			throw new ServiceException("未找到E店CODE对应的机构信息");
		}
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
		String shop_name = "";//门店名称
		String shop_phone = "";//门店电话
		String shop_addr = "";//门店地址

		BasicStore basicStore = basicStoreDao.get(store_id);
		if(basicStore != null){
			shop_name = basicStore.getStoreName();
			shop_phone = basicStore.getStorePhone();
			shop_addr = basicStore.getAddress();
		}else{
			throw new ServiceException("未找到门店ID对应的门店信息");
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
		orderInfo.setCustomerRemarkPic(remark_pic_String);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.setJointOrderId(gasq_order_id);//国安社区订单编号
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

		orderInfo.setOpenPrice(openPrice);
		orderInfo.setGoodsInfoList(orderGoods);//商品信息

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

		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList(); //取得订单服务信息
		String orgId = orderInfo.getOrgId();
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		if(null == skillId){
			throw new ServiceException("未找到当前商品对应的技能信息");
		}
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		//serchInfo.setOrderId(orderInfo.getId());
		//serchInfo.setTechName(techName);
		//serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);

		if(techList== null || techList.size() < techDispatchNum){//技师数量不够
			throw new ServiceException("技师数量不满足当前商品的需求人数");
		}

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = orderInfoDao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = orderInfoDao.getTechByHoliday(serchInfo);

		List<OrderDispatch> techListRe = new ArrayList<>();
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
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = orderInfoDao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表

			List<String> timeCheckDelTechIdList = new ArrayList<String>();

			int intervalTimeS =  15 * 60 + 10 * 60;//必须间隔时间 秒
			/*if (11 <= Integer.parseInt(DateUtils.formatDate(serviceTime, "HH")) &&
					Integer.parseInt(DateUtils.formatDate(serviceTime, "HH")) < 14) {
				//可以接单的时间则为：40分钟+路上时间+富余时间
				intervalTimeS = 40 * 60 + 15 * 60 + 10 * 60 ;
			} else {
				//可以接单的时间则为：路上时间+富余时间
				intervalTimeS = 15 * 60 + 10 * 60;
			}*/

			int intervalTimeE =  15 * 60 + 10 * 60;//必须间隔时间 秒
			/*if (11 <= Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) &&
					Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) < 14) {
				//可以接单的时间则为：40分钟+路上时间+富余时间
				intervalTimeE = 40 * 60 + 15 * 60 + 10 * 60;
			} else {
				//可以接单的时间则为：路上时间+富余时间
				intervalTimeE = 15 * 60 + 10 * 60;
			}*/

			Date checkServiceTime = DateUtils.addSecondsNotDayB(serviceTime, -intervalTimeS);
			Date checkFinishTime = DateUtils.addSecondsNotDayE(finishTime, intervalTimeE);

			for(OrderDispatch orderTech : orderTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"

				if(!DateUtils.checkDatesRepeat(checkServiceTime,checkFinishTime,orderTech.getServiceTime(),orderTech.getFinishTime())){
					timeCheckDelTechIdList.add(orderTech.getTechId());
				}
			}
			for(OrderDispatch tech : beforTimeCheckTechList){
				if(!timeCheckDelTechIdList.contains(tech.getTechId())){
					techListRe.add(tech);
				}
			}

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
	private OrderAddress openCreateForAddress(OpenCreateRequest info) {
		String phone = info.getPhone();//用户电话
		String province_code = info.getProvince_code();//省CODE
		if(null == province_code){
			throw new ServiceException("省CODE不能为空");
		}
		String city_code = info.getCity_code();//市CODE
		if(null == province_code){
			throw new ServiceException("市CODE不能为空");
		}
		String area_code = info.getArea_code();//区CODE
		if(null == province_code){
			throw new ServiceException("区CODE不能为空");
		}
		String address = info.getAddress();//服务地址：小区+详细地址

		//省名称
		List<Area>  provinceList = areaDao.getNameByCode(province_code);
		String provinceName = "";
		if(provinceList != null && provinceList.size() > 0){
			provinceName = provinceList.get(0).getName();
		}else {
			throw new ServiceException("未找到省CODE对应的省份名称");
		}
		//市名称
		List<Area>  cityList = areaDao.getNameByCode(province_code);
		String cityName = "";
		if(cityList != null && cityList.size() > 0){
			cityName = cityList.get(0).getName();
		}else {
			throw new ServiceException("未找到市CODE对应的市名称");
		}
		//区名称
		List<Area>  areaList = areaDao.getNameByCode(province_code);
		String areaName = "";
		if(areaList != null && areaList.size() > 0){
			areaName = areaList.get(0).getName();
		}else {
			throw new ServiceException("未找到区CODE对应的区名称");
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

	/**
	 * 对接接口 订单状态更新
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> openUpdateStauts(OpenUpdateStautsRequest info) {
		OpenUpdateStautsResponse response = new OpenUpdateStautsResponse();
		if(null == info){
			throw new ServiceException("未接收到订单信息");
		}
		String platform = info.getPlatform();//对接平台代号
		String orderSn = info.getService_order_id();// 自营服务订单ID->编号
		if(StringUtils.isBlank(orderSn)){
			throw new ServiceException("自营服务订单sn不能为空");
		}
		String gasq_order_sn = info.getGasq_order_id();//国安社区订单编号
		if(StringUtils.isBlank(gasq_order_sn)){
			throw new ServiceException("国安社区订单sn不能为空");
		}
		OrderInfo checkInfo = new OrderInfo();
		checkInfo.setOrderNumber(orderSn);
		checkInfo.setJointOrderId(gasq_order_sn);
		OrderInfo checkInfoRe = orderInfoDao.checkGasqSnOrderSn(checkInfo);
		if(null == checkInfoRe){
			throw new ServiceException("未找到订单,请确认订单sn是否正确");
		}
		String cancelReason = info.getComment();//取消原因
		String status = info.getStatus();//cancel 取消；finish 已签收；success 完成

		try{
            OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(checkInfoRe.getId());
            if("cancel".equals(status)){
                //orderInfo.setServiceStatus("cancel");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
                orderInfo.setOrderStatus("cancel");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
                orderInfo.setCancelReason(cancelReason);//取消原因
            }else if("signed".equals(status)){
                //orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
                orderInfo.setOrderStatus("finish");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
            }else if("success".equals(status)){
                //orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
                orderInfo.setOrderStatus("success");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
            }

            orderInfo.setUpdateDate(new Date());
            int num = orderInfoDao.openUpdateOrder(orderInfo);
            if(num == 0){
				throw new ServiceException("订单状态更新失败");
            }else{
				response = new OpenUpdateStautsResponse();
				response.setSuccess(true);// 状态：true 成功；false 失败
				response.setService_order_id(orderSn);// 自营服务订单ID

				HashMap<String,Object> map = new HashMap<>();
				map.put("response",response);

				if("cancel".equals(status)){
					//推送消息  取消订单时
					//标题：订单已取消
					//内容：编号为XXXXXXXXX的订单已取消，请点击查看
					OrderInfo orderInfoMsg = new OrderInfo();
					orderInfoMsg.setId(checkInfoRe.getId());
					orderInfoMsg.setOrderNumber(orderSn);
					List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List
					orderInfoMsg.setTechList(techList);
					map.put("orderInfoMsg",orderInfoMsg);
				}

				return map;
            }
		}catch (Exception e){
			throw new ServiceException("订单状态更新失败E!");
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
			throw new ServiceException("未接收到订单信息!");
		}
		String orderSn = info.getService_order_id();// 自营服务订单ID-->编号
        if(null == orderSn){
			throw new ServiceException("接收到自营服务订单编号为空");
        }
		OrderInfo orderInfo1 = new OrderInfo();
		orderInfo1.setOrderNumber(orderSn);
		OrderInfo newOrderInfo = orderInfoDao.getBySn(orderInfo1);//当前订单
		if(null == newOrderInfo){
			throw new ServiceException("未找到到订单信息");
		}
		String orderId = newOrderInfo.getId();

		int num = 0;//更新件数
		List<OpenServiceInfo> serviceInfos = info.getService_info();//更新服务项目信息及数量
		if(null != serviceInfos && serviceInfos.size() > 0){
			try{
			    String sum_price = info.getSum_price();
			    num = num + openUpdateInfoServiceInfos(orderId, serviceInfos,sum_price);
            }catch (ServiceException ex){
				throw new ServiceException(ex.getMessage());
			}
            catch (Exception e){
				throw new ServiceException("更新服务项目信息及数量失败E!");
            }
		}

		OpenGuoanxiaInfo guoanxia_info = info.getGuoanxia_info();//国安侠信息
		if(null != guoanxia_info){
            try{
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(orderId);// 自营服务订单ID
                orderInfo.setBusinessName(guoanxia_info.getName());//业务人员姓名
                orderInfo.setBusinessPhone(guoanxia_info.getPhone());//业务人员电话
                orderInfo.setBusinessRemark(guoanxia_info.getRemark());//业务人员备注
                List<String> remark_pic = guoanxia_info.getRemark_pic();
                if(null != remark_pic){
                    String remarkPic = JsonMapper.toJsonString(remark_pic);
                    orderInfo.setBusinessRemarkPic(remarkPic);// 业务人员备注图片
                }

                User user = new User();
                user.setId("gasq001");
                orderInfo.setUpdateBy(user);
                orderInfo.setUpdateDate(new Date());

                num = num + orderInfoDao.openUpdateOrder(orderInfo);
            }catch (Exception e){
				throw new ServiceException("更新国安侠信息失败E!");
            }
		}
/*
		OpenCostomerInfo costomer_info = info.getCostomer_info();//用户信息
		if(null != costomer_info){
            try{
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(info.getService_order_id());// 自营服务订单ID
                orderInfo.setCustomerRemark(costomer_info.getRemark());//用户备注
                List<String> remark_pic = costomer_info.getRemark_pic();
                if(null != remark_pic){
                    String remarkPic = JsonMapper.toJsonString(remark_pic);
                    orderInfo.setCustomerRemarkPic(remarkPic);// 用户备注图片
                }

                User user = new User();
                user.setId("gasq001");
                orderInfo.setUpdateBy(user);
                orderInfo.setUpdateDate(new Date());

                num = num + orderInfoDao.openUpdateOrder(orderInfo);
            }catch (Exception e){
				throw new ServiceException("更新用户信息失败E!");
            }
		}*/

		OpenStoreInfo store_info = info.getStore_info();//门店信息
		if(null != store_info){
            try{
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(orderId);// 自营服务订单ID
                orderInfo.setShopName(store_info.getName());//门店名
                orderInfo.setShopPhone(store_info.getTelephone());//门店电话
                orderInfo.setShopRemark(store_info.getRemark());//门店备注
                List<String> remark_pic = store_info.getRemark_pic();
                if(null != remark_pic){
                    String remarkPic = JsonMapper.toJsonString(remark_pic);
                    orderInfo.setShopRemarkPic(remarkPic);// 门店备注图片
                }

                User user = new User();
                user.setId("gasq001");
                orderInfo.setUpdateBy(user);
                orderInfo.setUpdateDate(new Date());

                num = num + orderInfoDao.openUpdateOrder(orderInfo);
            }catch (Exception e){
				throw new ServiceException("更新门店信息失败E!");
            }
		}

		if(num == 0){//更新件数
			throw new ServiceException("操作失败");
		}else{
			response = new OpenUpdateInfoResponse();
			response.setSuccess(true);// 状态：true 成功；false 失败
			response.setService_order_id(orderSn);// 自营服务订单ID
			return response;
		}
	}

	/**
	 * 更新服务项目信息及数量
	 * @param orderId
	 * @param serviceInfos
	 */
	private int openUpdateInfoServiceInfos(String orderId, List<OpenServiceInfo> serviceInfos,String sum_price) {
		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价

		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		OrderGoods goods = new OrderGoods();
		for(OpenServiceInfo openServiceInfo : serviceInfos){

			String key = openServiceInfo.getCate_goods_id();
			String cate_goods_id = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
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

			goods.setGoodsType(commodity.getType());
			goods.setGoodsUnit(commodity.getUnit());

			goods.setMajorSort(commodity.getMajorSort());

            User user = new User();
            user.setId("gasq001");
            goods.setId(IdGen.uuid());
            goods.setCreateBy(user);
            goods.setCreateDate(new Date());
            goods.setUpdateBy(user);
            goods.setUpdateDate(goods.getCreateDate());

            orderGoods.add(goods);
            BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
            originPrice = originPrice.add(price);//商品总价

			sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
			goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

			int goodsNum = buy_num;		// 订购商品数
			Double convertHours = commodity.getConvertHours();		// 折算时长

			Double goodsTime = convertHours * goodsNum;//gon
			orderTotalTime = orderTotalTime + goodsTime;
		}

		OrderInfo orderInfoBefor = orderInfoDao.get(orderId);//当前订单
		if(orderInfoBefor == null){
			throw new ServiceException("未找到当前订单信息");
		}
		Date serviceTime = orderInfoBefor.getServiceTime();//服务时间

		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setId(orderId);
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(serchOrderInfo); //订单当前已有技师List
		if(techList != null && techList.size() != 0){
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techList.size());//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("未找到当前订单的技师信息");
		}

		//删除 订单 原商品信息
		OrderGoods serchOrderGoods = new OrderGoods();
		serchOrderGoods.setOrderId(orderId);
		orderGoodsDao.deleteGoodsByOrderId(serchOrderGoods);

		//更新订单信息
		OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(sum_price);            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',

        User user = new User();
        user.setId("gasq001");
        orderInfo.setUpdateBy(user);
        orderInfo.setUpdateDate(new Date());

        int num = orderInfoDao.update(orderInfo);

		//新增商品信息
		for(OrderGoods goodsInsert : orderGoods){
			orderGoodsDao.insert(goodsInsert);
		}

		return num;
	}

}