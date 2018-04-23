/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
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
public class OrderInfoOperateService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	TechScheduleDao techScheduleDao;
	@Autowired
	OrderPayInfoDao orderPayInfoDao;
	@Autowired
	OrderRefundDao orderRefundDao;
	@Autowired
	OrderRefundGoodsDao orderRefundGoodsDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;

	@Autowired
	private OrderToolsService orderToolsService;
	@Autowired
	SerItemInfoDao serItemInfoDao;

	/**
	 * 增加、改派、更换时间 判断订单状态
	 * @param orderInfo
	 * @return
	 */
	public boolean checkOrderStatus(OrderInfo orderInfo) {
		orderInfo = get(orderInfo);
		if(orderInfo.getSuggestFinishTime()!=null && new Date().after(orderInfo.getSuggestFinishTime())){
			return false;
		}
		String orderStatus =  orderInfo.getOrderStatus();
		String serviceStatus =  orderInfo.getServiceStatus();

		//服务状态
		String serviceStatusWait = "wait_service";//待服务
		String serviceStatusStarted = "started";//已上门
		String serviceStatusFinish = "finish";//已完成
		String serviceStatusCancel = "cancel";//已取消
		//订单状态
		String orderStatusWaitdispatch = "waitdispatch";//待派单
		String orderStatusDispatched = "dispatched";//已派单
		String orderStatusCancel = "cancel";//已取消
		String orderStatusStarted = "started";//已上门
		String orderStatusFinish = "finish";//已完成
		String orderStatusSuccess = "success";//已成功
		String orderStatusStop = "stop";//已暂停
		String orderStatusClose = "close";//关闭

        /*
            订单状态为已取消
            订单状态为已完成/已完成
            服务状态为已完成
            （只要有一个满足就可以）
         */
		if(orderStatusCancel.equals(orderStatus) || orderStatusFinish.equals(orderStatus) ||
				orderStatusSuccess.equals(orderStatus) || orderStatusClose.equals(orderStatus) ||
				serviceStatusFinish.equals(serviceStatus) || serviceStatusCancel.equals(serviceStatus) ){
			return  false;
		}else{
			return true;
		}
	}

	/**
	 * 只有补单商品的订单不允许更换时间
	 * @param orderInfo
	 * @return
	 */
	public boolean checkOrderFullGoods(OrderInfo orderInfo) {
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				if(goods.getSortId().length() >= 3){
					return true;
				}
			}

		}
		return false;
	}
	/**
	 * 增加技师
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> addTech(OrderInfo orderInfo) {
		String serchTechName = orderInfo.getTechName();
		orderInfo = get(orderInfo.getId());//当前订单
		orderInfo.setTechName(serchTechName);
		List<OrderDispatch> techList = orderToolsService.listTechByGoodsAndTime(orderInfo);
		return techList;
	}

	/**
	 * app 技师列表
	 * @param orderInfo
	 * @return
	 */

	public List<OrderDispatch> appTech(OrderInfo orderInfo) {
		String serchTechName = orderInfo.getTechName();
		orderInfo = get(orderInfo.getId());//当前订单
		orderInfo.setTechName(serchTechName);
		orderInfo.setSerchFullTech(true);
		List<OrderDispatch> techList = orderToolsService.listTechByGoodsAndTime(orderInfo);
		return techList;
	}
	/**
	 * 增加技师保存
	 * @param orderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> addTechSave(OrderInfo orderInfo) {
		Double serviceHourRe = 0.0;
		List<String> techIdList = orderInfo.getTechIdList();//新增技师List
		if(techIdList==null || techIdList.size()==0){
			throw new ServiceException("请选择技师");
		}
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();
		//建议完成时间 增加人数后的时间计算 秒
		// Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() + techIdList.size());
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() + techIdList.size());

		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

		//新增、改派判断库存
		//boolean flag = checkTechTime(techIdList,serviceTime,DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));
		boolean flag = false;
		List<OrderDispatch> checkTechList = orderToolsService.listTechByGoodsAndTime(orderInfo);
		List<String> checkTechIdList = new ArrayList<>();
		if(checkTechList != null) {
			for (OrderDispatch checkTech : checkTechList) {
				checkTechIdList.add(checkTech.getTechId());
			}
		}
		for(String techId : techIdList){
			if(!checkTechIdList.contains(techId)){
				flag = true;
			}
		}
		if(flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}

		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceHourRe);//建议服务时长（小时）
		info.setFinishTime(DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);

		// 派单
		List<OrderDispatch> orderCreateMsgList = new ArrayList<>();
		for(String techId : techIdList){
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setTechId(techId);//技师ID
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);
			ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoDao.get(techId);
			if(technicianInfo != null) {
				orderDispatch.setTechPhone(technicianInfo.getPhone());
			}

			//排期
			TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
			techScheduleInfo.setTechId(techId);//技师ID
			techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(orderInfo.getServiceTime()));//日期
			int weekDay = DateUtils.getWeekNum(orderInfo.getServiceTime());//周几
			techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
			techScheduleInfo.setStartTime(orderInfo.getServiceTime());//起始时段
			techScheduleInfo.setEndTime(info.getFinishTime());//结束时段
			techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
			techScheduleInfo.setType("order");//'holiday：休假  order：订单'
			techScheduleInfo.preInsert();
			techScheduleDao.insertSchedule(techScheduleInfo);

			orderCreateMsgList.add(orderDispatch);
		}
		//排期
		for(OrderDispatch orderDispatch : techList){
			TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
			techScheduleInfo.setTechId(orderDispatch.getTechId());//技师ID
			techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(orderInfo.getServiceTime()));//日期
			int weekDay = DateUtils.getWeekNum(orderInfo.getServiceTime());//周几
			techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
			techScheduleInfo.setStartTime(orderInfo.getServiceTime());//起始时段
			techScheduleInfo.setEndTime(info.getFinishTime());//结束时段
			techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
			techScheduleInfo.setType("order");//'holiday：休假  order：订单'
			techScheduleInfo.preUpdate();
			techScheduleDao.updateScheduleByTypeIdTechId(techScheduleInfo);
		}

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		/*String jointGoodsCodes ="";//对接方商品CODE
		String jointEshopCode = "";//对接方E店CODE

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				jointGoodsCodes = jointGoodsCodes + goods.getJointGoodsCode();//对接方商品CODE
			}
		}else{
			throw new ServiceException("未找到当前订单服务商品信息");
		}

		BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
		if(organization != null){
			jointEshopCode = organization.getJointEshopCode();
		}*/

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour", serviceHourRe);
		map.put("list", techListRe);

		map.put("orderId", orderInfo.getId());
		//map.put("jointGoodsCodes",jointGoodsCodes);
		//map.put("jointEshopCode", jointEshopCode);
		map.put("orderSource",orderInfo.getOrderSource());

		map.put("orderCreateMsgList",orderCreateMsgList);
		map.put("orderNumber",orderInfo.getOrderNumber());
		return map;
	}

	/**
	 * 改派技师保存
	 * @param orderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> dispatchTechSave(OrderInfo orderInfo) {
		Double serviceHourRe = 0.0;
		String dispatchTechId = orderInfo.getDispatchTechId();//改派前技师ID
		List<String> techIdList = orderInfo.getTechIdList();//改派技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();

		//建议完成时间 增加人数后的时间计算 秒
		//Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() - 1 + techIdList.size());
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() - 1 + techIdList.size());

		//新增、改派判断库存
		//boolean flag = checkTechTime(techIdList,serviceTime,DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));
		boolean flag = false;
		List<OrderDispatch> checkTechList = orderToolsService.listTechByGoodsAndTime(orderInfo);
		List<String> checkTechIdList = new ArrayList<>();
		if(checkTechList != null) {
			for (OrderDispatch checkTech : checkTechList) {
				checkTechIdList.add(checkTech.getTechId());
			}
		}
		for(String techId : techIdList){
			if(!checkTechIdList.contains(techId)){
				flag = true;
			}
		}
		if(flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}

		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceHourRe);//建议服务时长（小时）
		info.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);
		for(OrderDispatch orderDispatch : techList){
			//如果改派前是自己的订单 把自己的派单转为无效
			if(dispatchTechId.equals(orderDispatch.getTechId())){
				OrderDispatch orderDispatchUpdate = new OrderDispatch();
				orderDispatchUpdate.setId(orderDispatch.getId());//ID
				orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
				orderDispatch.preUpdate();
				orderDispatchDao.update(orderDispatch);//数据库改派前技师设为不可用
			}else{
				//排期
				TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
				techScheduleInfo.setTechId(orderDispatch.getTechId());//技师ID
				techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(orderInfo.getServiceTime()));//日期
				int weekDay = DateUtils.getWeekNum(orderInfo.getServiceTime());//周几
				techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
				techScheduleInfo.setStartTime(orderInfo.getServiceTime());//起始时段
				techScheduleInfo.setEndTime(info.getFinishTime());//结束时段
				techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
				techScheduleInfo.setType("order");//'holiday：休假  order：订单'
				techScheduleInfo.preUpdate();
				techScheduleDao.updateScheduleByTypeIdTechId(techScheduleInfo);
			}
		}

		// 改派 原来有，现在没有
		List<OrderDispatch> orderDispatchMsgList = new ArrayList<>();
		OrderDispatch orderDispatchMsg = new OrderDispatch();
		orderDispatchMsg.setTechId(dispatchTechId);//技师ID
		ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoDao.get(dispatchTechId);
		if(technicianInfo != null) {
			orderDispatchMsg.setTechPhone(technicianInfo.getPhone());
		}
		orderDispatchMsgList.add(orderDispatchMsg);

		//排期
		TechScheduleInfo techScheduleInfoDel = new TechScheduleInfo();
		techScheduleInfoDel.setTechId(dispatchTechId);//技师ID
		techScheduleInfoDel.setTypeId(orderInfo.getId());//休假ID或订单ID
		techScheduleInfoDel.setType("order");//'holiday：休假  order：订单'
		techScheduleInfoDel.preUpdate();
		techScheduleDao.deleteScheduleByTypeIdTechId(techScheduleInfoDel);

		// 派单 原来没有，现在有
		List<OrderDispatch> orderCreateMsgList = new ArrayList<>();
		for(String techId : techIdList){//改派技师List  insert
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setTechId(techId);//技师ID
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);
			ServiceTechnicianInfo technicianInfoC = serviceTechnicianInfoDao.get(techId);
			if(technicianInfoC != null) {
				orderDispatch.setTechPhone(technicianInfoC.getPhone());
			}

			//排期
			TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
			techScheduleInfo.setTechId(techId);//技师ID
			techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(orderInfo.getServiceTime()));//日期
			int weekDay = DateUtils.getWeekNum(orderInfo.getServiceTime());//周几
			techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
			techScheduleInfo.setStartTime(orderInfo.getServiceTime());//起始时段
			techScheduleInfo.setEndTime(info.getFinishTime());//结束时段
			techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
			techScheduleInfo.setType("order");//'holiday：休假  order：订单'
			techScheduleInfo.preInsert();
			techScheduleDao.insertSchedule(techScheduleInfo);

			orderCreateMsgList.add(orderDispatch);
		}
		//return techList;

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

/*
		String jointGoodsCodes ="";//对接方商品CODE
		String jointEshopCode = "";//对接方E店CODE

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				jointGoodsCodes = jointGoodsCodes + goods.getJointGoodsCode();//对接方商品CODE
			}
		}else{
			throw new ServiceException("未找到当前订单服务商品信息");
		}

		BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
		if(organization != null){
			jointEshopCode = organization.getJointEshopCode();
		}else{
			throw new ServiceException("未找到当前订单对应的机构信息");
		}*/

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);

		map.put("orderId",orderInfo.getId());
		//map.put("jointGoodsCodes",jointGoodsCodes);
		//map.put("jointEshopCode", jointEshopCode);
		map.put("orderSource",orderInfo.getOrderSource());

		map.put("orderDispatchMsgList", orderDispatchMsgList);
		map.put("orderCreateMsgList", orderCreateMsgList);
		map.put("orderNumber", orderInfo.getOrderNumber());
		return map;
	}

	/**
	 * 更换时间
	 * @param orderInfo
	 * @return
	 */
	public List<OrderTimeList> timeDataList(OrderInfo orderInfo) {

		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OrderTimeList> list = new ArrayList<>();
		int value = 1;

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		int techDispatchNum = orderToolsService.getTechDispatchNumByGoodsList(goodsInfoList);//派人数量
		double serviceHour = orderToolsService.getServiceHourByGoodsList(goodsInfoList);//建议服务时长（小时）

		orderInfo = get(orderInfo);
		String stationId = orderInfo.getStationId();//服务站ID
		if(null == stationId){
			logger.error("未找到当前订单的服务站信息");
			return null;
		}

		Double serviceSecond = (serviceHour * 3600);

		String skillId = orderToolsService.getSkillIdByOrgSort(orderInfo.getOrgId(), goodsInfoList.get(0).getSortId());
		List<OrderDispatch> techList = orderToolsService.listTechByStationSkillOrder(stationId, skillId, null,true,null);

		if(techList.size() < techDispatchNum){//技师数量不够
			return null;
		}

		for(Date date : dateList){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				responseRe.setValue(String.valueOf(value));
				value++;
				responseRe.setLabel(DateUtils.formatDate(date, "yyyy-MM-dd"));
				//该日服务时间点列表
				List<OrderDispatch> hours = timeDataListHours(date, orderInfo,techList, techDispatchNum,serviceSecond);
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

	private List<OrderDispatch> timeDataListHours(Date date,OrderInfo orderInfo, List<OrderDispatch> techList,int techDispatchNum,Double serviceSecond ) {
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
						if(!orderInfo.getId().equals(order.getTypeId())) {//当前订单不考虑
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
								intervalTimeE =  Integer.parseInt(Global.getConfig("order_split_time"));
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
	 * 更换时间保存
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> saveTime(OrderInfo orderInfo) {
		//String jointGoodsCodes ="";
		if(null == orderInfo){
			throw new ServiceException("服务时间不可为空,请选择日期");
		}
		Double serviceHourRe = 0.0;
		Date newServiceDate = orderInfo.getServiceTime();//新选择时间
		if(null == newServiceDate){
			throw new ServiceException("服务时间不可为空,请选择日期");
		}
		orderInfo = get(orderInfo.getId());//当前订单
		if(null == orderInfo){
			throw new ServiceException("未找到当前订单信息");
		}
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		List<OrderDispatch> techBeforList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

        int techDispatchNum = orderToolsService.getTechDispatchNumByGoodsList(goodsInfoList);//派人数量
        double newServiceHour = orderToolsService.getServiceHourByGoodsList(goodsInfoList);//建议服务时长（小时）

        if(techDispatchNum == 0 ){
			throw new ServiceException("未找到当前订单服务商品信息");
		}

		Double serviceSecond = (newServiceHour * 3600);
		Date newFinishTime = DateUtils.addSeconds(newServiceDate,serviceSecond.intValue());//完成时间

		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setOrgId(orderInfo.getOrgId());
		serchOrderInfo.setStationId(orderInfo.getStationId());
		serchOrderInfo.setServiceTime(newServiceDate);
		serchOrderInfo.setFinishTime(newFinishTime);
		serchOrderInfo.setGoodsSortId(goodsInfoList.get(0).getSortId());
		serchOrderInfo.setSerchFullTech(true);
		serchOrderInfo.setSerchNowOrderId(orderInfo.getId());
		List<OrderDispatch> techListRe = orderToolsService.listTechByGoodsAndTime(serchOrderInfo);
		if(techListRe.size() != 0){
			if(techListRe.size() < techDispatchNum){//技师数量不够
				throw new ServiceException("当前时间未找到技师");
			}

			//一个自然月内，根据当前服务站内各个技师的订单数量，优先分配给订单数量少的技师
			List<String> serchTechIds = new ArrayList<>();//可分配技师
			for(OrderDispatch tech :techListRe){
				serchTechIds.add(tech.getTechId());
			}
			Date monthFristDay = DateUtils.getMonthFristDay(newServiceDate);
			Date monthLastDay = DateUtils.getMonthLastDay(newServiceDate);
			OrderDispatch serchTechInfo = new OrderDispatch();
			serchTechInfo.setStartTime(monthFristDay);
			serchTechInfo.setEndTime(monthLastDay);
			serchTechInfo.setTechIds(serchTechIds);
			List<OrderDispatch> techListOrderByNum = dao.getTechListOrderByNum(serchTechInfo);//订单数量少的技师
			List<String> dispatchTechIds = new ArrayList<>();
			for(int i=0;i<techDispatchNum;i++){//订单数量少的技师  订单需要技师数量
				dispatchTechIds.add(techListOrderByNum.get(i).getTechId());
			}

			serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

			OrderInfo info = new OrderInfo();
			info.setId(orderInfo.getId());
			info.setServiceTime(newServiceDate);//服务时间
			info.setServiceHour(serviceHourRe);//建议服务时长（小时）
			info.setFinishTime(newFinishTime);//实际完成时间（用来计算库存）
			info.setSuggestFinishTime(newFinishTime);//实际完成时间（用来计算库存）
			info.preUpdate();
			dao.update(info);

			List<String> newTechIds = new ArrayList<>();
			List<String> oldTechIds = new ArrayList<>();
			List<String> delTechIds = new ArrayList<>();

			List<String> techBeforIds = new ArrayList<>();
			for(OrderDispatch tech :techBeforList){
				if(dispatchTechIds.contains(tech.getTechId())){//又分配给当前技师
					oldTechIds.add(tech.getTechId());
				}else{
					OrderDispatch orderDispatch = new OrderDispatch();
					orderDispatch.setId(tech.getId());//技师ID
					orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
					orderDispatch.preUpdate();
					orderDispatchDao.update(orderDispatch);//数据库改派前技师设为不可用

					delTechIds.add(tech.getTechId());
				}
			}
			for(String techId : dispatchTechIds){//新增技师
				if(oldTechIds!=null && !oldTechIds.contains(techId)){
					OrderDispatch orderDispatch = new OrderDispatch();
					orderDispatch.setTechId(techId);//技师ID
					orderDispatch.setOrderId(orderInfo.getId());//订单ID
					orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
					orderDispatch.preInsert();
					orderDispatchDao.insert(orderDispatch);

					newTechIds.add(techId);
				}
			}

			List<OrderDispatch> techLastList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

			List<String> msgTechIds = new ArrayList<>();
			msgTechIds.addAll(newTechIds);
			msgTechIds.addAll(oldTechIds);
			msgTechIds.addAll(delTechIds);
			OrderInfo orderMsg = new OrderInfo();
			orderMsg.setTechIdList(msgTechIds);
			List<OrderDispatch> msgTechList = dao.getOrderDispatchMsgTechList(orderMsg); //订单当前已有技师List
			// 派单 原来没有，现在有
			List<OrderDispatch> orderCreateMsgList = new ArrayList<>();
			// 改派 原来有，现在没有
			List<OrderDispatch> orderDispatchMsgList = new ArrayList<>();
			// 时间变化 原来有，现在有
			List<OrderDispatch> orderServiceTimeMsgList = new ArrayList<>();
			for(OrderDispatch msgInfo : msgTechList){
				if(newTechIds.contains(msgInfo.getTechId())){
					orderCreateMsgList.add(msgInfo);// 派单 原来没有，现在有

					//排期
					TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
					techScheduleInfo.setTechId(msgInfo.getTechId());//技师ID
					techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(newServiceDate));//日期
					int weekDay = DateUtils.getWeekNum(newServiceDate);//周几
					techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
					techScheduleInfo.setStartTime(newServiceDate);//起始时段
					techScheduleInfo.setEndTime(newFinishTime);//结束时段
					techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
					techScheduleInfo.setType("order");//'holiday：休假  order：订单'
					techScheduleInfo.preInsert();
					techScheduleDao.insertSchedule(techScheduleInfo);
				}
				if(delTechIds.contains(msgInfo.getTechId())){
					orderDispatchMsgList.add(msgInfo);// 改派 原来有，现在没有

					//排期
					TechScheduleInfo techScheduleInfoDel = new TechScheduleInfo();
					techScheduleInfoDel.setTechId(msgInfo.getTechId());//技师ID
					techScheduleInfoDel.setTypeId(orderInfo.getId());//休假ID或订单ID
					techScheduleInfoDel.setType("order");//'holiday：休假  order：订单'
					techScheduleInfoDel.preUpdate();
					techScheduleDao.deleteScheduleByTypeIdTechId(techScheduleInfoDel);
				}
				if(oldTechIds.contains(msgInfo.getTechId())) {
					orderServiceTimeMsgList.add(msgInfo);// 时间变化 原来有，现在有

					//排期
					TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
					techScheduleInfo.setTechId(msgInfo.getTechId());//技师ID
					techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(newServiceDate));//日期
					int weekDay = DateUtils.getWeekNum(newServiceDate);//周几
					techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
					techScheduleInfo.setStartTime(newServiceDate);//起始时段
					techScheduleInfo.setEndTime(newFinishTime);//结束时段
					techScheduleInfo.setTypeId(orderInfo.getId());//休假ID或订单ID
					techScheduleInfo.setType("order");//'holiday：休假  order：订单'
					techScheduleInfo.preUpdate();
					techScheduleDao.updateScheduleByTypeIdTechId(techScheduleInfo);
				}
			}

			/*BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
			String jointEshopCode = "";
			if(organization != null){
				jointEshopCode = organization.getJointEshopCode();
			}else{
				throw new ServiceException("未找到当前订单对应的机构信息");
			}*/

			HashMap<String,Object> map = new HashMap<>();
			map.put("serviceHour",serviceHourRe);
			map.put("list",techLastList);
			map.put("orderId",orderInfo.getId());
			map.put("serviceDate",newServiceDate);
			//map.put("jointGoodsCodes",jointGoodsCodes);
			//map.put("jointEshopCode", jointEshopCode);
			map.put("orderSource",orderInfo.getOrderSource());

			map.put("orderServiceTimeMsgList", orderServiceTimeMsgList);
			map.put("orderDispatchMsgList", orderDispatchMsgList);
			map.put("orderCreateMsgList", orderCreateMsgList);
			map.put("orderNumber", orderInfo.getOrderNumber());

			return map;
		}else{
			throw new ServiceException("未找到可用技师");
		}
	}

	/**
	 * 判断订单状态 是否可取消
	 * @param info
	 * @return
	 */
	public boolean checkOrderCancelStatus(OrderInfo info) {
		info = get(info);
		String orderStatus =  info.getOrderStatus();
		String serviceStatus =  info.getServiceStatus();
		String payStatus = info.getPayStatus();
		String orderSource = info.getOrderSource();

		//服务状态
		String serviceStatusWait = "wait_service";//待服务
		String serviceStatusStarted = "started";//已上门
		String serviceStatusFinish = "finish";//已完成
		String serviceStatusCancel = "cancel";//已取消
		//订单状态
		String orderStatusWaitdispatch = "waitdispatch";//待派单
		String orderStatusDispatched = "dispatched";//已派单
		String orderStatusCancel = "cancel";//已取消
		String orderStatusStarted = "started";//已上门
		String orderStatusFinish = "finish";//已完成
		String orderStatusSuccess = "success";//已成功
		String orderStatusStop = "stop";//已暂停
		String orderStatusClose = "close";//关闭
		//支付状态
		String waitpay = "waitpay";//待支付
		String payed = "payed";//已支付
		//订单来源
		String own = "own";//本机构
		String gasq = "gasq";//国安社区

        /*
       		只有订单来源为本机构的订单
            若支付状态不是未支付、或服务状态是已取消或订单状态已取消,此时无需执行取消订单的流程
         */
		if(!own.equals(orderSource)){
			return true;
		}

		if(!waitpay.equals(payStatus) || serviceStatusCancel.equals(serviceStatus) ||
				orderStatusCancel.equals(orderStatus) || orderStatusClose.equals(orderStatus)){
			return true;
		}

		return false;
	}

	/**
	 * 取消订单
	 * @param orderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object>  orderCancel(OrderInfo orderInfo) {
		orderInfo.setServiceStatus("cancel");
		orderInfo.setOrderStatus("cancel");
		orderInfo.preUpdate();
		dao.orderCancel(orderInfo);

		// 删除排期
		TechScheduleInfo scheduleInfo = new TechScheduleInfo();
		scheduleInfo.setType("order");
		scheduleInfo.setTypeId(orderInfo.getId());
		scheduleInfo.preUpdate();
		techScheduleDao.deleteScheduleByTypeId(scheduleInfo);

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		OrderInfo info = get(orderInfo.getId());
		HashMap<String,Object> map = new HashMap<>();
		if(techListRe==null || techListRe.size()==0){
			map.put("list",null);
		}else{
			map.put("list",techListRe);
		}
		map.put("info",info);
		return map;
	}

	/**
	 * 退款验证
	 * @param info
	 * @return
	 */
	public boolean checkOrderRefundStatus(OrderInfo info) {
		info = get(info);
		String orderStatus =  info.getOrderStatus();
		String payStatus = info.getPayStatus();
		String orderSource = info.getOrderSource();

		//订单状态
		String orderStatusSuccess = "success";//已成功
		//支付状态
		String payed = "payed";//已支付
		//订单来源
		String own = "own";//本机构

        /*
       		只有订单来源为本机构的订单
            支付状态：已支付 并且 订单状态：已成功
            订单所有商品全部退款后，不再显示此按钮
         */
        boolean flag = false;
		if(!own.equals(orderSource)){
			flag = true;
		}

		if(!payed.equals(payStatus) || !orderStatusSuccess.equals(orderStatus)){
			flag = true;
		}
		if(flag){
			List<OrderGoods> list = dao.listNotRefundOrderGoodsByOrderId(info);
			if(list!=null && list.size()>0){
				flag = false;
			}else{
				flag = true;
			}
		}

		return flag;
	}


	public boolean checkOrderRefundGoods(OrderInfo info) {
		boolean flag = false;
		List<String> refundGoodsList = new ArrayList<>();
		List<OrderGoods> goodsInfoList = info.getGoodsInfoList();
		for(OrderGoods goods : goodsInfoList){
			refundGoodsList.add(goods.getGoodsId());
		}
		info.setRefundGoodsList(refundGoodsList);
		List<OrderGoods> list = dao.listRefundOrderGoodsByOrderIdGoods(info);
		if(list!=null && list.size()>0){
			flag = false;
		}else{
			flag = true;
		}

		return flag;
	}

    public OrderInfo orderRefundInit(OrderInfo info) {
		//支付信息
		OrderPayInfo payInfo = orderPayInfoDao.getPayInfoByOrderId(info);
		//服务信息
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);
		List<OrderGoods> goodsList = new ArrayList<>();
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			for(OrderGoods orderGoods : goodsInfoList){
				if(orderGoods.getGoodsNum() != orderGoods.getGoodsRefundNum()){
					goodsList.add(orderGoods);
				}
			}
		}
		// 退款信息
		List<OrderRefund> refundList = orderRefundDao.listRefundByOrderId(info);
		String refundDifferenceType = "";
		String refundDifference = "";
		if(refundList!=null && refundList.size()>0){
			BigDecimal num = new BigDecimal(0);

			for(OrderRefund refund : refundList){
				String type = refund.getRefundDifferenceType();
				if(type != null) {
					if(refund.getRefundDifference() != null) {
						BigDecimal price = new BigDecimal(refund.getRefundDifference());
						if ("many".equals(type)) {
							num = num.add(price);
						} else {
							num = num.subtract(price);
						}
					}
				}
			}
			if(num.compareTo(new BigDecimal(0)) > 0){
				 refundDifferenceType = "多退";
				 refundDifference = num.toString();
			}else if(num.compareTo(new BigDecimal(0)) < 0){
				 refundDifferenceType = "少退";
				 refundDifference = new BigDecimal(0).subtract(num).toString();
			}
		}

		OrderInfo resOrderInfo = new OrderInfo();
		resOrderInfo.setGoodsInfoList(goodsList);
		resOrderInfo.setPayPrice(payInfo.getPayAccount());
		resOrderInfo.setPayPlatform(payInfo.getPayPlatform());
		if(StringUtils.isNotBlank(refundDifferenceType) && StringUtils.isNotBlank(refundDifference)) {
			resOrderInfo.setOrderNowRefundStatus("* 该订单已" + refundDifferenceType + " ￥" + refundDifference);
		}

		return resOrderInfo;
    }


	/**
	 * APP根据订单id查询对应的退款信息
	 * 多条退款商品合并 根据服务项目集合
	 * @param info
	 * @return
	 */
	public OrderRefund getOrderRefundInit(OrderInfo info) {
		List<String> itemIds=new ArrayList<String>();
		// 退款商品集合
		List<OrderRefundGoods> refundGoodsByOrderId = orderRefundGoodsDao.getRefundGoodsByOrderId(info);
		if (refundGoodsByOrderId != null && refundGoodsByOrderId.size()>0){
			for (OrderRefundGoods orderRefundGoods:refundGoodsByOrderId){
				String itemId = orderRefundGoods.getItemId();
				itemIds.add(itemId);
			}
		}
		//将项目id集合去重
		Set set = new  HashSet();
		//最后的 项目id集合
		List<String> newList = new  ArrayList<String>();
		for (String cd:itemIds) {
			if(set.add(cd)){
				newList.add(cd);
			}
		}
		//项目集合
		List<OrderGoods> orderGoodsList=new ArrayList<OrderGoods>();
		//商品结合
		List<OrderGoods> goodsList=new ArrayList<OrderGoods>();
		for (String id:newList){
			OrderGoods byId = serItemInfoDao.getById(id);
			orderGoodsList.add(byId);
		}
		//循环商品
		for (OrderGoods item:orderGoodsList){
			for (OrderRefundGoods orderRefundGoods:refundGoodsByOrderId) {
				OrderGoods orderGoods=new OrderGoods();
				orderGoods.setGoodsId(orderRefundGoods.getGoodsId());
				orderGoods.setPayPrice(orderRefundGoods.getPayPrice());
				orderGoods.setGoodsName(orderRefundGoods.getGoodsName());
				orderGoods.setGoodsNum(Integer.parseInt(orderRefundGoods.getGoodsNum()));
				goodsList.add(orderGoods);
				item.setGoods(goodsList);
			}
		}
		OrderRefund orderRefund=new OrderRefund();
		List<OrderRefund> refundList = orderRefundDao.listRefundByOrderId(info);
		String refundDifferenceType = "";
		String refundDifference = "";
		BigDecimal refundAccountReality=new BigDecimal(0);
		if(refundList!=null && refundList.size()>0){
			BigDecimal num = new BigDecimal(0);

			for(OrderRefund refund : refundList){
				String type = refund.getRefundDifferenceType();
				BigDecimal price = new BigDecimal(refund.getRefundDifference());
				if("many".equals(type)){
					num = num.add(price);
				}else{
					num = num.subtract(price);
				}
				refundAccountReality.add(new BigDecimal(refund.getRefundAccountReality()));
			}
			if(num.compareTo(new BigDecimal(0)) > 0){
				refundDifferenceType = "多退";
				refundDifference = num.toString();
			}else if(num.compareTo(new BigDecimal(0)) < 0){
				refundDifferenceType = "少退";
				refundDifference = new BigDecimal(0).subtract(num).toString();
			}
		}
		orderRefund.setRefundDifference(refundDifference);
		orderRefund.setRefundDifferenceType(refundDifferenceType);
		orderRefund.setRefundAccount(refundAccountReality.toString());
		orderRefund.setGoodsInfoList(orderGoodsList);
		return orderRefund;
	}
    @Transactional(readOnly = false)
	public HashMap<String,Object> orderRefundSave(OrderInfo info) {
		List<OrderGoods> goodsInfoList = info.getGoodsInfoList();
		OrderRefund orderRefund = info.getOrderRefundInfo();

		/*if(Integer.valueOf(orderRefund.getRefundAccountReality()) < 0){
		    throw new ServiceException("实际退款金额小于0元");
        }*/
		// 插入退款表；
        orderRefund.setOrderId(info.getId());
        orderRefund.setApplyTime(new Date());
        orderRefund.setFinishTime(new Date());
        orderRefund.setRefundStatus("refunded");
		orderRefund.setRefundNumber(DateUtils.getDateAndRandomTenNum("03"));
		orderRefund.preInsert();
		if(StringUtils.isBlank(orderRefund.getRefundDifferenceType())){
			orderRefund.setRefundDifferenceType(null);
		}
		if(StringUtils.isBlank(orderRefund.getRefundDifference())){
			orderRefund.setRefundDifference(null);
		}
		orderRefundDao.insert(orderRefund);

		if(goodsInfoList != null){
			OrderRefundGoods orderRefundGoods = new OrderRefundGoods();
			for(OrderGoods orderGoods : goodsInfoList){
				// 退货表
				orderRefundGoods = new OrderRefundGoods();
				orderRefundGoods.setOrderId(info.getId());
				orderRefundGoods.setRefundId(orderRefund.getId());
				orderRefundGoods.setItemId(orderGoods.getItemId());
				orderRefundGoods.setItemName(orderGoods.getItemName());
				orderRefundGoods.setGoodsId(orderGoods.getGoodsId());
				orderRefundGoods.setGoodsName(orderGoods.getGoodsName());
				orderRefundGoods.setGoodsNum(String.valueOf(orderGoods.getGoodsNum()));
				orderRefundGoods.setPayPrice(orderGoods.getPayPrice());
				orderRefundGoods.setGoodsUnit(orderGoods.getGoodsUnit());
				orderRefundGoods.preInsert();
				orderRefundGoodsDao.insert(orderRefundGoods);

				// 商品表
				orderGoods.setOrderId(info.getId());
				orderGoodsDao.updateRefundNumByOrderIdItemId(orderGoods);
			}
		}
		List<OrderGoods> list = dao.listNotRefundOrderGoodsByOrderId(info);
		if(list==null || list.size()==0){
			// 如果所有商品（包括品类和数量），全部已退款，则修改订单状态为‘已关闭’；
			OrderInfo order = new OrderInfo();
			order.setId(info.getId());
			order.setOrderStatus("close");
			order.preUpdate();
			dao.update(order);
		}
		return null;
	}

}