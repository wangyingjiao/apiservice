/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.sys.entity.KeyValueEntity;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 组合订单预约Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSubscribeService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {

	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
    OrderCombinationGasqDao orderCombinationGasqDao;
	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
	FrequencyDao frequencyDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	TechScheduleDao techScheduleDao;
	@Autowired
	private ServiceTechnicianInfoDao technicianInfoDao;
	@Autowired
	private OrderToolsService orderToolsService;

	/**
	 * 后台预约- 查询服务日期
	 * @param combinationOrderInfo(masterId,serviceNum)
	 * @return
	 */
	public List<OrderTimeList> subscribeDateList(CombinationOrderInfo combinationOrderInfo) {
		int value = 1;
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String masterId = combinationOrderInfo.getMasterId();
		List<Date> dateList = DateUtils.getAfterSevenDays();
		List<OrderTimeList> list = new ArrayList<>();

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(combinationInfo.getCombinationGoodsId());
		int techDispatchNum = 1;//派人数量
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		String stationId = combinationInfo.getStationId();//服务站ID
		String orgId = combinationInfo.getOrgId();//机构
		Double serviceSecond = (serviceHour * serviceNum * 3600);

		//技能
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(commodity.getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if (skillSortList != null && skillSortList.size() == 1) {
			skillId = skillSortList.get(0).getSkillId();
		} else {
			logger.error("未找到商品需求的技能信息");
			return null;
		}

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);
		if(techList.size() < techDispatchNum){//技师数量不够
			logger.error("技师数量不够");
			return null;
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
			List<ServiceTechnicianWorkTime> workTimeList = orderInfoDao.findTechWorkTimeList(serchTech);
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
	 * 后台预约 - 查询服务技师
	 * @param combinationOrderInfo(serviceNum，masterId，serviceTime)
	 * @return
	 */
	public List<OrderDispatch> subscribeTechList(CombinationOrderInfo combinationOrderInfo) {
		String techName = combinationOrderInfo.getTechName();//查询条件
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String masterId = combinationOrderInfo.getMasterId();
		Date serviceTime = combinationOrderInfo.getServiceTime();
		//List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(combinationInfo.getCombinationGoodsId());
		int techDispatchNum = 1;//派人数量
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		String stationId = combinationInfo.getStationId();//服务站ID
		String orgId = combinationInfo.getOrgId();//机构
		Double serviceSecond = (serviceHour * serviceNum * 3600);

		//技能
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(commodity.getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if (skillSortList != null && skillSortList.size() == 1) {
			skillId = skillSortList.get(0).getSkillId();
		} else {
			logger.error("未找到商品需求的技能信息");
			return null;
		}

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);
		if(techList.size() < techDispatchNum){//技师数量不够
			logger.error("技师数量不够");
			return null;
		}

		try {
			removeBusyTechByWeekTime(serviceTime, techList, techDispatchNum, serviceSecond);
		}catch (Exception e){
			logger.error("去除忙碌技师失败");
		}

		return techList;
	}

	private void removeBusyTechByWeekTime(Date serviceTime, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond) {
		int week = DateUtils.getWeekNum(serviceTime);
		String selectTimeStr = DateUtils.formatDate(serviceTime,"HH:mm");
		Date selectTime = null;
		try {
			selectTime = DateUtils.parseDate(selectTimeStr,"HH:mm");
		} catch (ParseException e) {
			return;
		}
		Iterator<OrderDispatch> it = techList.iterator();
		while(it.hasNext()) {//循环技师List 取得可用时间
			OrderDispatch tech = it.next();

			//-----------------取得技师 周几可用工作时间 并且转成时间点列表 开始----------------------------------------------------------
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = orderInfoDao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){//技师没有工作时间，删除该技师
				it.remove();
				if(techList.size() < techDispatchNum){//技师数量不够
					return;
				}
				continue;//下一位技师
			}

			ServiceTechnicianWorkTime workTime = workTimeList.get(0);
			//List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(workTime.getStartTime(),workTime.getEndTime());
			if(!(
					(selectTime.after(workTime.getStartTime()) || selectTime.compareTo(workTime.getStartTime())==0) &&
							selectTime.before(workTime.getEndTime())
			)){
				it.remove();
				if(techList.size() < techDispatchNum){//技师数量不够
					return;
				}
				continue;//下一位技师
			}
			//-------------------取得技师 周几可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
			List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), serviceTime, "holiday");
			if (techHolidyList != null && techHolidyList.size() != 0) {
				for (TechScheduleInfo holiday : techHolidyList) {
					//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());

					if(
						(selectTime.after(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))
								|| selectTime.compareTo(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))==0) &&
						selectTime.before(holiday.getEndTime())
					){
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						continue;//下一位技师
					}
				}
			}
			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

			//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
			//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), serviceTime, "order");
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

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
					if(
						(selectTime.after(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))
							|| selectTime.compareTo(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))==0) &&
						selectTime.before(DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE))
					){
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						continue;//下一位技师
					}
				}
			}
		}
	}

	/**
	 * 后台预约- 保存前验证
	 * @param combinationOrderInfo(itemId)
	 * @return
	 */
	public boolean checkSubscribeTech(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		//List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();//服务时间
		String techId = combinationOrderInfo.getTechId();
		Date serviceTime = combinationOrderInfo.getServiceTime();

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		if(serviceNum > combinationInfo.getBespeakTotal()){
			return true;
		}
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);
		int week = DateUtils.getWeekNum(serviceTime);
		String selectTimeStr = DateUtils.formatDate(serviceTime,"HH:mm");
		Date selectTime = null;
		try {
			selectTime = DateUtils.parseDate(selectTimeStr,"HH:mm");
		} catch (ParseException e) {
			return true;
		}


		//-----------------取得技师 周几可用工作时间 并且转成时间点列表 开始----------------------------------------------------------
		OrderDispatch serchTech = new OrderDispatch();
		//取得符合条件的技师的 工作时间 服务时间List
		serchTech.setTechId(techId);
		serchTech.setWeek(week);
		List<ServiceTechnicianWorkTime> workTimeList = orderInfoDao.findTechWorkTimeList(serchTech);
		if (workTimeList == null || workTimeList.size() == 0) {//技师没有工作时间，删除该技师
			return true;
		}

		ServiceTechnicianWorkTime workTime = workTimeList.get(0);
		//List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(workTime.getStartTime(),workTime.getEndTime());
		if (!(
				(selectTime.after(workTime.getStartTime()) || selectTime.compareTo(workTime.getStartTime()) == 0) &&
						selectTime.before(workTime.getEndTime())
		)) {
			return true;
		}
		//-------------------取得技师 周几可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

		//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
		List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechTime(techId, serviceTime, "holiday");
		if (techHolidyList != null && techHolidyList.size() != 0) {
			for (TechScheduleInfo holiday : techHolidyList) {
				//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());

				if (
						(selectTime.after(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))
								|| selectTime.compareTo(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue())) == 0) &&
								selectTime.before(holiday.getEndTime())
						) {
					return true;
				}
			}
		}
		//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

		//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
		//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
		List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(techId, serviceTime, "order");
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

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
				if (
						(selectTime.after(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))
								|| selectTime.compareTo(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS)) == 0) &&
								selectTime.before(DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE))
						) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 后台预约- 保存
	 * @param combinationOrderInfo(serviceNum，masterId，serviceTime，techId)
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<OrderInfo> subscribeSave(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String techId = combinationOrderInfo.getTechId();
		Date groupServiceTime = combinationOrderInfo.getServiceTime();

		ServiceTechnicianInfo techInfo = technicianInfoDao.get(techId);
		//获取组合信息
		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);
		Date groupFinishTime = DateUtils.addSecondsNotDayE(groupServiceTime, serviceSecond.intValue());

		combinationInfo.setTechId(techInfo.getId());
		combinationInfo.setTechPhone(techInfo.getPhone());

		List<Date> listDate = DateUtils.listTimeByFrequency(groupServiceTime,serviceNum,serviceHour);
		//根据组合商品ID返回子商品信息
		OrderGoods goods = getOrderGoodsByCombination(combinationInfo.getCombinationGoodsId());

		List<OrderInfo> orderInfoList = new ArrayList<>();
		// 新增 子商品信息
		for(Date startDate :listDate){
			HashMap<String,Object> map = combinationCreateOrder(startDate, combinationInfo, goods);
			OrderInfo orderInfo = (OrderInfo)map.get("orderInfoMsg");
			orderInfo.setJointGroupId(combinationInfo.getJointGroupId());
			orderInfoList.add(orderInfo);
		}

		//更新 组合订单对接编号信息order_combination_gasq
		List<OrderCombinationGasqInfo> combinationGasqInfoList = orderCombinationGasqDao.listFreeGasqSn(masterId);
		String groupId = IdGen.uuid();
		if(combinationGasqInfoList.size()<orderInfoList.size()){
			throw new ServiceException("没有可用订单");
		}
		for(int i=0; i<orderInfoList.size(); i++){
			OrderCombinationGasqInfo combinationGasqInfo = combinationGasqInfoList.get(i);
			combinationGasqInfo.setOrderGroupId(groupId);
			combinationGasqInfo.setOrderNumber(orderInfoList.get(i).getOrderNumber());
			combinationGasqInfo.preUpdate();
			orderCombinationGasqDao.updateOrderGroup(combinationGasqInfo);

			orderInfoList.get(i).setJointOrderId(combinationGasqInfo.getJointOrderSn());

			// order_info  对接订单ID 更新
			OrderInfo updateJointInfo = new OrderInfo();
			updateJointInfo.setId(orderInfoList.get(i).getId());
			updateJointInfo.setJointOrderId(combinationGasqInfo.getJointOrderSn());
			updateJointInfo.preUpdate();
			orderInfoDao.update(updateJointInfo);
		}

		// tech_schedule  服务技师排期 ------------------------------------------------------------------------------
		openCreateForTechSchedule( techId, groupServiceTime, groupFinishTime , groupId, masterId);

		//更新已预约次数
		CombinationOrderInfo updateCombinationInfo = new CombinationOrderInfo();
		updateCombinationInfo.setMasterId(masterId);
		updateCombinationInfo.setServiceNum(serviceNum);
		combinationOrderDao.updateBespeakByMasterId(updateCombinationInfo);

		return orderInfoList;
	}


	/**
	 * 根据组合商品ID返回第一个子商品信息
	 * @param combinationGoodsId
	 * @return
	 */
	private OrderGoods getOrderGoodsByCombination(String combinationGoodsId) {
		List<SerItemCommodity> sonGoodsList = orderGoodsDao.listGoodsByCombination(combinationGoodsId);
		SerItemCommodity commodity = sonGoodsList.get(0);
		OrderGoods goods = new OrderGoods();
		goods.setSortId(commodity.getSortId());//服务分类ID
		goods.setSortName(commodity.getSortName());
		goods.setItemId(commodity.getItemId());//服务项目ID
		goods.setItemName(commodity.getItemName());//项目名称
		goods.setGoodsId(commodity.getId());//商品ID
		goods.setGoodsName(commodity.getName());//商品名称
		goods.setGoodsNum(1);//订购商品数
		goods.setGoodsType(commodity.getType());
		goods.setGoodsUnit(commodity.getUnit());
		goods.setPayPrice(commodity.getGoodsPrice().toString());//对接后单价
		goods.setOriginPrice(commodity.getPrice().toString());//原价
		goods.setMajorSort(commodity.getMajorSort());

		goods.setConvertHours(commodity.getConvertHours());		// 折算时长
		goods.setStartPerNum(commodity.getStartPerNum());   		//起步人数（第一个4小时时长派人数量）
		goods.setCappingPerNum(commodity.getCappingPerNum());		//封项人数
		return goods;
	}

	private HashMap<String,Object> combinationCreateOrder(Date startDate, CombinationOrderInfo combinationInfo, OrderGoods goods) {

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		try{
			orderInfo = openCreateForOrder(startDate, combinationInfo, goods);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存子订单信息表信息失败!");
		}
		if(null == orderInfo){
			throw new ServiceException("保存子订单信息表信息失败!");
		}

		// order_dispatch -派单表----------------------------------------------------------------------------------
		try{
			openCreateForDispatch(orderInfo, combinationInfo.getTechId());
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存派单信息表失败!");
		}

		// order_goods-订单服务关联--------------------------------------------------------------------
		try{
			openCreateForGoods(orderInfo, goods);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单商品信息表失败!");
		}

		OrderInfo orderInfoMsg = new OrderInfo();
		orderInfoMsg.setId(orderInfo.getId());
		orderInfoMsg.setOrderNumber(orderInfo.getOrderNumber());
		List<OrderDispatch> techList = new ArrayList<>();
		OrderDispatch dispatch = new OrderDispatch();
		dispatch.setTechId(combinationInfo.getTechId());
		dispatch.setTechPhone(combinationInfo.getTechPhone());
		techList.add(dispatch);
		orderInfoMsg.setTechList(techList);
		orderInfoMsg.setServiceTime(startDate);

		HashMap<String,Object> map = new HashMap<>();
		map.put("orderInfoMsg",orderInfoMsg);
		return map;
	}


	/**
	 * 订单创建 - 排期表
	 * @param techId
	 * @param serviceStartBeginTime
	 * @param serviceStartEndTime
	 * @param groupId
	 */
	private void openCreateForTechSchedule(String techId, Date serviceStartBeginTime, Date serviceStartEndTime, String groupId, String masterId) {
		TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
		techScheduleInfo.setTechId(techId);//技师ID
		techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(serviceStartBeginTime));//日期
		int weekDay = DateUtils.getWeekNum(serviceStartBeginTime);//周几
		techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
		techScheduleInfo.setStartTime(serviceStartBeginTime);//起始时段
		techScheduleInfo.setEndTime(serviceStartEndTime);//结束时段
		techScheduleInfo.setTypeId(groupId);//休假ID或订单ID
		techScheduleInfo.setType("master");//'holiday：休假  order：订单'
		techScheduleInfo.setMasterId(masterId);
		techScheduleInfo.preInsert();
		techScheduleDao.insertSchedule(techScheduleInfo);
	}

	/**
	 * 订单创建 - 订单服务关联
	 * @param goods
	 */
	private void openCreateForGoods(OrderInfo orderInfo, OrderGoods goods) {
		goods.setOrderId(orderInfo.getId());//订单ID
		goods.preInsert();
		orderGoodsDao.insert(goods);
	}

	/**
	 * 订单创建 - 派单表
	 * @param orderInfo
	 * @param techId
	 */
	private void openCreateForDispatch(OrderInfo orderInfo, String techId) {
		OrderDispatch orderDispatch = new OrderDispatch();
		orderDispatch.setOrderId(orderInfo.getId());//订单ID
		orderDispatch.setTechId(techId);//技师ID
		orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
		orderDispatch.preInsert();
		orderDispatchDao.insert(orderDispatch);
	}

	/**
	 * 订单创建 - 子订单信息
	 * @param startDate
	 * @param combinationInfo
	 * @param goods
	 * @return
	 */
	private OrderInfo openCreateForOrder(Date startDate, CombinationOrderInfo combinationInfo, OrderGoods goods) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(combinationInfo.getMasterId());//主订单ID
		orderInfo.setOrderType(combinationInfo.getOrderType());//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(combinationInfo.getOrgId());  //所属服务机构ID
		orderInfo.setStationId(combinationInfo.getStationId());        //服务站id
		orderInfo.setMajorSort(combinationInfo.getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(goods.getPayPrice());            //实际付款价格
		orderInfo.setOriginPrice(goods.getOriginPrice());              //总价（原价）
		orderInfo.setOrderAddressId(combinationInfo.getOrderAddressId());  // 订单地址ID
		orderInfo.setLatitude(combinationInfo.getLatitude());                //服务地址  纬度
		orderInfo.setLongitude(combinationInfo.getLongitude());         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(startDate);     //上门时间（服务时间）
		Double serviceSecond = combinationInfo.getServiceHour() * 3600;
		orderInfo.setFinishTime(DateUtils.addSecondsNotDayE(startDate,serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSecondsNotDayE(startDate,serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(combinationInfo.getServiceHour());                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource(combinationInfo.getOrderSource());  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(combinationInfo.getCustomerId());    // 客户ID

		orderInfo.setOrderContent(goods.getSortName() + "+" + goods.getGoodsName());               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.setEshopCode(combinationInfo.getEshopCode());
		orderInfo.preInsert();
		orderInfoDao.insert(orderInfo);

		return orderInfo;
	}



}