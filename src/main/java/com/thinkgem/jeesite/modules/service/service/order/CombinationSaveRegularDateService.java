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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 组合订单设置固定时间Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSaveRegularDateService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {

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

	public static List<OrderTimeList> saveRegularDateWeekList() {
		List<Date> dateList = DateUtils.getWeekLaterTwoWeekDays();
		List<OrderTimeList> list = new ArrayList<>();
		List<String> weekList = new ArrayList<>();
		HashMap<String, List<KeyValueEntity>> map = new HashMap<>();
		for(Date date : dateList){
			String week = String.valueOf(DateUtils.getWeekNum(date));
			if(weekList.contains(week)){
				List<KeyValueEntity> keyValueEntityList = map.get(week);
				KeyValueEntity keyValueEntity = new KeyValueEntity();
				keyValueEntity.setKey(DateUtils.formatDate(date,"yyyy-MM-dd"));
				keyValueEntity.setValue(DateUtils.formatDate(date,"yyyy-MM-dd") + "(" + DateUtils.getWeekL(date) + ")");
				keyValueEntityList.add(keyValueEntity);
				map.put(week,keyValueEntityList);
			}else{
				List<KeyValueEntity> keyValueEntityList = new ArrayList<>();
				KeyValueEntity keyValueEntity = new KeyValueEntity();
				keyValueEntity.setKey(DateUtils.formatDate(date,"yyyy-MM-dd"));
				keyValueEntity.setValue(DateUtils.formatDate(date,"yyyy-MM-dd") + "(" + DateUtils.getWeekL(date) + ")");
				keyValueEntityList.add(keyValueEntity);
				map.put(week,keyValueEntityList);
				weekList.add(week);
			}

		}

		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			OrderTimeList orderTime = new OrderTimeList();
			orderTime.setLabel(entry.getKey().toString());
			orderTime.setHoursList((List<KeyValueEntity>)entry.getValue());
			list.add(orderTime);
		}

		return  list;
	}

	/**
	 * 设置固定时间- 查询服务日期
	 * @param combinationOrderInfo(masterId,serviceNum)
	 * @return
	 */
	public List<OrderTimeList> saveRegularDateDateList(CombinationOrderInfo combinationOrderInfo) {
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String masterId = combinationOrderInfo.getMasterId();
		Map<String,Date> map = DateUtils.getTwoWeekLaterWeekDays();//获取第三周数据，查询排期用

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

		for(int i=1; i<=7; i++){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				responseRe.setValue(String.valueOf(i));
				responseRe.setLabel(String.valueOf(i));
				//该日服务时间点列表
				List<OrderDispatch> hours = listHourByWeek(i, map,techList, techDispatchNum,serviceSecond,masterId);
				if(hours!= null && hours.size()>0){
					List<KeyValueEntity> hoursList = new ArrayList<>();
					for(int j=0;j<hours.size();j++){
						KeyValueEntity keyValueEntity = new KeyValueEntity();
						keyValueEntity.setKey(String.valueOf(j));
						keyValueEntity.setValue(hours.get(j).getServiceTimeStr());
						hoursList.add(keyValueEntity);
					}
					responseRe.setHoursList(hoursList);
					list.add(responseRe);
				}
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"未找到服务时间点列表");
			}

		}
		return list;
	}

	private List<OrderDispatch> listHourByWeek(int week, Map<String,Date> map, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond,String masterId) {
		List<OrderDispatch> techForList = new ArrayList<>();
		if(techList != null){//深浅拷贝问题
			for(OrderDispatch dispatch: techList){
				techForList.add(dispatch);
			}
		}
		Date date = map.get(String.valueOf(week));
		Iterator<OrderDispatch> it = techForList.iterator();
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
				if(techForList.size() < techDispatchNum){//技师数量不够
					return null;
				}
				continue;//下一位技师
			}

			ServiceTechnicianWorkTime workTime = workTimeList.get(0);
			List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(workTime.getStartTime(),workTime.getEndTime());
			//-------------------取得技师 周几可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			if(workTimes != null) {
				//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
				List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(tech.getTechId(), week, date, "holiday");
				if (techHolidyList != null && techHolidyList.size() != 0) {
					for (TechScheduleInfo holiday : techHolidyList) {
						List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());
						Iterator<String> it1 = workTimes.iterator();
						while (it1.hasNext()) {
							String work =  it1.next();
							if (holidays.contains(work)) {//去除休假时间
								it1.remove();
							}
						}
					}
				}
				//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

				//----------取得技师 组合订单频率  如果和工作时间重复 删除该时间点 开始-------------------
				//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
				CombinationOrderInfo serchCombinationInfo = new CombinationOrderInfo();
				serchCombinationInfo.setTechId(tech.getTechId());
				serchCombinationInfo.setSerchWeek(week);
				List<OrderCombinationFrequencyInfo> frequencyList = combinationOrderDao.listFrequencyByTechWeek(serchCombinationInfo);
				if (frequencyList != null && frequencyList.size() != 0) {
					for (OrderCombinationFrequencyInfo frequency : frequencyList) {
						if(!masterId.equals(frequency.getMasterId())){
							int intervalTimeS = 0;//必须间隔时间 秒
							if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) &&
									Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) < 14) {
								//可以接单的时间则为：40分钟+路上时间+富余时间
								intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time")) + serviceSecond.intValue();
							} else {
								//可以接单的时间则为：路上时间+富余时间
								intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + serviceSecond.intValue();
							}

							int intervalTimeE = 0;//必须间隔时间 秒
							if (11 <= Integer.parseInt(DateUtils.formatDate(frequency.getEndTime(), "HH")) &&
									Integer.parseInt(DateUtils.formatDate(frequency.getEndTime(), "HH")) < 14) {
								//可以接单的时间则为：40分钟+路上时间+富余时间
								intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
							} else {
								//可以接单的时间则为：路上时间+富余时间
								intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
							}

							List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
									DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
									DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
							if (orders != null && workTimes!= null) {
								Iterator<String> it2 = workTimes.iterator();
								while (it2.hasNext()) {
									String work = it2.next();
									if (orders.contains(work)) {//去除订单时间
										it2.remove();
									}
								}
							}
						}
					}
				}
				//----------取得技师 组合订单频率   如果和工作时间重复 删除该时间点 结束------------------

				//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------

				List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), date, "order");
				if (techOrderList != null && techOrderList.size() != 0) {
					for (TechScheduleInfo order : techOrderList) {
						if (!masterId.equals(order.getMasterId())) {
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
							if (orders != null && workTimes != null) {
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
			Date startDate = null;
			try {
				startDate = DateUtils.parseDate(time,"HH:mm");
			} catch (ParseException e) {
				return null;
			}
			Date endDate = DateUtils.addSeconds(startDate, serviceSecond.intValue());
			String timeStr = time + "-" + DateUtils.formatDate(endDate,"HH:mm");
			info.setServiceTimeStr(timeStr);
			listRe.add(info);
		}

		return listRe;
	}

	/**
	 * 设置固定时间- 查询服务技师
	 * @param combinationOrderInfo(serviceNum，masterId，freList,serviceStart)
	 * @return
	 */
	public List<OrderDispatch> saveRegularDateTechList(CombinationOrderInfo combinationOrderInfo) {
		String techName = combinationOrderInfo.getTechName();//查询条件
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String masterId = combinationOrderInfo.getMasterId();
		String serviceFrequency = combinationOrderInfo.getServiceFrequency();//服务频次
		List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();
		Date serviceStart = combinationOrderInfo.getServiceStart();// 第一次选择日期
		if(serviceStart == null){
			return null;
		}

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

		List<Date> creartDateList = listCreartDateByFrequency(serviceFrequency, freList,serviceNum, serviceStart,combinationInfo.getBespeakTotal());

		for(Date creartDate : creartDateList) {
		//for(OrderCombinationFrequencyInfo frequency : freList){
			OrderTimeList responseRe = new OrderTimeList();
			try {


					Date creartDateTime = null;
					int serviceStartWeek = DateUtils.getWeekNum(creartDate);
					for (OrderCombinationFrequencyInfo frequency : freList) {
						if (serviceStartWeek == frequency.getWeek()) {
							creartDateTime = DateUtils.parseDate(frequency.getTimeArea().split("-")[0],"HH:mm");
						}
					}

					String creartDateTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
							+  DateUtils.formatDate(creartDateTime,"HH:mm" + ":00");
					Date creartDateTimeStrTime = DateUtils.parseDate(creartDateTimeStr);

					removeBusyTechByWeekTime(creartDateTimeStrTime, techList, techDispatchNum, serviceSecond);
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
			}
		}
		return techList;
	}

	private void removeBusyTechByWeekTime(Date creartDate, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond) {
		int week = DateUtils.getWeekNum(creartDate);
//		Date selectTime = serviceStart;
//		String selectTimeStr = frequencyInfo.getTimeArea().split("-")[0];
//		Date selectTimeH = null;
//		try {
//			selectTimeH = DateUtils.parseDate(selectTimeStr,"HH:mm");
//		} catch (ParseException e) {
//			return;
//		}

//		Date serviceStartBeginTime = null;// 第一次选择日期开始时间
//		Date serviceStartEndTime = null;// 第一次选择日期结束时间
//		String startTimeStrFre = frequencyInfo.getTimeArea().split("-")[0];
//		String endTimeStrFre = frequencyInfo.getTimeArea().split("-")[1];
//		//Date startTime = null;
//		//Date endTime = null;
//		try {
//			serviceStartBeginTime = DateUtils.parseDate(startTimeStrFre,"HH:mm");
//			serviceStartEndTime = DateUtils.parseDate(endTimeStrFre,"HH:mm");
//		} catch (ParseException e) {
//		}
//		String startTimeStr = DateUtils.formatDate(serviceStart,"yyyy-MM-dd") + " "
//				+  DateUtils.formatDate(serviceStartBeginTime,"HH:mm" + ":00");
//		Date checkStartTime = DateUtils.parseDate(startTimeStr);
//
//		String endTimeStr = DateUtils.formatDate(serviceStart,"yyyy-MM-dd") + " "
//				+  DateUtils.formatDate(serviceStartEndTime,"HH:mm" + ":00");
//		Date checkEndTime = DateUtils.parseDate(endTimeStr);

		Date checkStartTime = creartDate;
		Date checkEndTime = DateUtils.addSecondsNotDayE(creartDate,serviceSecond.intValue());

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
//			if(!(
//					(selectTimeH.after(workTime.getStartTime()) || selectTimeH.compareTo(workTime.getStartTime())==0) &&
//							selectTimeH.before(workTime.getEndTime())
//			)){
//				it.remove();
//				if(techList.size() < techDispatchNum){//技师数量不够
//					return;
//				}
//				continue;//下一位技师
//			}

			Date techWorkStartTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
					+ " " + DateUtils.formatDate(workTime.getStartTime(), "HH:mm:ss"));//工作开始时间
			Date techWorkEndTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
					+ " " + DateUtils.formatDate(workTime.getEndTime(), "HH:mm:ss"));//工作结束时间
			if (techWorkStartTime.before(techWorkEndTime) && checkStartTime.before(checkEndTime)) {
				// 订单时间在工作时间内,可以下单
				if ((techWorkStartTime.before(checkStartTime) || techWorkStartTime.compareTo(checkStartTime) == 0)
						&& (techWorkEndTime.after(checkStartTime) || techWorkEndTime.compareTo(checkStartTime) == 0)) {

				}else{
					it.remove();
					if(techList.size() < techDispatchNum){//技师数量不够
						return;
					}
					continue;//下一位技师
				}
			}else{
				it.remove();
				if(techList.size() < techDispatchNum){//技师数量不够
					return;
				}
				continue;//下一位技师
			}

			//-------------------取得技师 周几可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
			List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(tech.getTechId(), week, creartDate, "holiday");
			if (techHolidyList != null && techHolidyList.size() != 0) {
				for (TechScheduleInfo holiday : techHolidyList) {
					//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());

//					if(
//						(selectTime.after(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))
//								|| selectTime.compareTo(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))==0) &&
//						selectTime.before(holiday.getEndTime())
//					){
//						it.remove();
//						if(techList.size() < techDispatchNum){//技师数量不够
//							return;
//						}
//						continue;//下一位技师
//					}
					Date holidayStartTime = DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue());
					Date holidayEndTime = holiday.getEndTime();
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, holidayStartTime, holidayEndTime)) {
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						continue;//下一位技师
					}
				}
			}
			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

			//----------取得技师 组合订单频率    如果和工作时间重复 删除该时间点 开始-------------------
			//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
			CombinationOrderInfo serchCombinationInfo = new CombinationOrderInfo();
			serchCombinationInfo.setTechId(tech.getTechId());
			serchCombinationInfo.setSerchWeek(week);
			List<OrderCombinationFrequencyInfo> frequencyList = combinationOrderDao.listFrequencyByTechWeek(serchCombinationInfo);
			if (frequencyList != null && frequencyList.size() != 0) {
				for (OrderCombinationFrequencyInfo frequency : frequencyList) {

					Date frequencyStartTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(frequency.getStartTime(), "HH:mm:ss"));//工作开始时间
					Date frequencyEndTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(frequency.getEndTime(), "HH:mm:ss"));//工作结束时间

					int intervalTimeS = 0;//必须间隔时间 秒
					if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequencyStartTime, -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) &&
							Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequencyStartTime, -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) < 14) {
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time")) + serviceSecond.intValue();
					} else {
						//可以接单的时间则为：路上时间+富余时间
						intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + serviceSecond.intValue();
					}

					int intervalTimeE = 0;//必须间隔时间 秒
					if (11 <= Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) &&
							Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) < 14) {
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
					} else {
						//可以接单的时间则为：路上时间+富余时间
						intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
					}

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
//					if(
//						(selectTime.after(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))
//							|| selectTime.compareTo(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))==0) &&
//						selectTime.before(DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE))
//					){
//						it.remove();
//						if(techList.size() < techDispatchNum){//技师数量不够
//							return;
//						}
//						continue;//下一位技师
//					}
					Date orderStartTime = DateUtils.addSecondsNotDayB(frequencyStartTime, -intervalTimeS);
					Date orderEndTime = DateUtils.addSecondsNotDayE(frequencyEndTime, intervalTimeE);
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						continue;//下一位技师
					}
				}
			}
			//----------取得技师 组合订单频率   如果和工作时间重复 删除该时间点 结束------------------

			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 开始-------------------
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), creartDate, "order");
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
						intervalTimeE =  Integer.parseInt(Global.getConfig("order_split_time"));
					}

					//List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
					//DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
					//DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
//					if(
//							(selectTime.after(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))
//									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))==0) &&
//									selectTime.before(DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE))
//							){
//						it.remove();
//						if(techList.size() < techDispatchNum){//技师数量不够
//							return;
//						}
//						continue;//下一位技师
//					}
					Date orderStartTime = DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS);
					Date orderEndTime = DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE);
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						continue;//下一位技师
					}
				}
			}
			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 结束------------------

		}
	}

    public boolean checkCombinationStatus(CombinationOrderInfo combinationOrderInfo) {
        CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(combinationOrderInfo.getMasterId());
        if(!"dispatched".equals(combinationInfo.getOrderStatus())){
            return false;
        }
        if(combinationInfo.getBespeakTotal() <= combinationInfo.getBespeakNum()){
            return false;
        }
        return true;
    }

	/**
	 * 设置固定时间- 保存前验证
	 * @param combinationOrderInfo(itemId)
	 * @return
	 */
	public boolean checkRegularDateTech(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String serviceFrequency = combinationOrderInfo.getServiceFrequency();//服务频次
		List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();//服务时间
		Date serviceStart = combinationOrderInfo.getServiceStart();// 第一次选择日期
		String techId = combinationOrderInfo.getTechId();
		ServiceTechnicianInfo techInfo = technicianInfoDao.get(techId);
		if(techInfo == null){
            return true;
        }
		if(!"yes".equals(techInfo.getStatus())){
		    return true;
        }
        if(!"online".equals(techInfo.getJobStatus())){
            return true;
        }
        if(!"full_time".equals(techInfo.getJobNature())){
            return true;
        }

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		/*if(serviceNum > combinationInfo.getBespeakTotal()){
			return true;
		}*/

		List<Date> creartDateList = listCreartDateByFrequency(serviceFrequency, freList,serviceNum, serviceStart,combinationInfo.getBespeakTotal());

		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);

		for(Date creartDate : creartDateList) {
			Date serviceStartBeginTime = null;// 第一次选择日期开始时间
			Date serviceStartEndTime = null;// 第一次选择日期结束时间
			int serviceStartWeek = DateUtils.getWeekNum(creartDate);
			// 新增 组合订单服务时间order_combination_frequency
			for (OrderCombinationFrequencyInfo frequency : freList) {
				if (serviceStartWeek == frequency.getWeek()) {//第一次服务日期的开始时间(时分)

					String startTimeStr = frequency.getTimeArea().split("-")[0];
					String endTimeStr = frequency.getTimeArea().split("-")[1];
					Date startTime = null;
					Date endTime = null;
					try {
						serviceStartBeginTime = DateUtils.parseDate(startTimeStr,"HH:mm");
						serviceStartEndTime = DateUtils.parseDate(endTimeStr,"HH:mm");
					} catch (ParseException e) {

					}
				}
			}

			String startTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
					+  DateUtils.formatDate(serviceStartBeginTime,"HH:mm" + ":00");
			Date checkStartTime = DateUtils.parseDate(startTimeStr);

			String endTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
					+  DateUtils.formatDate(serviceStartEndTime,"HH:mm" + ":00");
			Date checkEndTime = DateUtils.parseDate(endTimeStr);

			int week = DateUtils.getWeekNum(creartDate);
//
//			String selectTimeStr = frequencyInfo.getTimeArea().split("-")[0];
//			Date selectTime = null;
//			try {
//				selectTime = DateUtils.parseDate(selectTimeStr,"HH:mm");
//			} catch (ParseException e) {
//				return true;
//			}

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
//			if (!(
//					(selectTime.after(workTime.getStartTime()) || selectTime.compareTo(workTime.getStartTime()) == 0) &&
//							selectTime.before(workTime.getEndTime())
//			)) {
//				return true;
//			}

			Date techWorkStartTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
					+ " " + DateUtils.formatDate(workTime.getStartTime(), "HH:mm:ss"));//工作开始时间
			Date techWorkEndTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
					+ " " + DateUtils.formatDate(workTime.getEndTime(), "HH:mm:ss"));//工作结束时间
			if (techWorkStartTime.before(techWorkEndTime) && checkStartTime.before(checkEndTime)) {
				// 订单时间在工作时间内,可以下单
				if ((techWorkStartTime.before(checkStartTime) || techWorkStartTime.compareTo(checkStartTime) == 0)
						&& (techWorkEndTime.after(checkStartTime) || techWorkEndTime.compareTo(checkStartTime) == 0)) {

				}else{
					return true;
				}
			}else{
				return true;
			}

			//-------------------取得技师 周几可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
			List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(techId, week, creartDate, "holiday");
			if (techHolidyList != null && techHolidyList.size() != 0) {
				for (TechScheduleInfo holiday : techHolidyList) {
					//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());
					Date holidayStartTime = DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue());
					Date holidayEndTime = holiday.getEndTime();
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, holidayStartTime, holidayEndTime)) {
						return true;
					}
//					if (
//							(selectTime.after(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()))
//									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue())) == 0) &&
//									selectTime.before(holiday.getEndTime())
//							) {
//						return true;
//					}
				}
			}
			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

			//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
			//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
			CombinationOrderInfo serchCombinationInfo = new CombinationOrderInfo();
			serchCombinationInfo.setTechId(techId);
			serchCombinationInfo.setSerchWeek(week);
			List<OrderCombinationFrequencyInfo> frequencyList = combinationOrderDao.listFrequencyByTechWeek(serchCombinationInfo);
			if (frequencyList != null && frequencyList.size() != 0) {
				for (OrderCombinationFrequencyInfo frequency : frequencyList) {

					Date frequencyStartTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(frequency.getStartTime(), "HH:mm:ss"));//工作开始时间
					Date frequencyEndTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(frequency.getEndTime(), "HH:mm:ss"));//工作结束时间

					int intervalTimeS = 0;//必须间隔时间 秒
					if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequencyStartTime, -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) &&
							Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(frequencyStartTime, -(Integer.parseInt(Global.getConfig("order_split_time")))), "HH")) < 14) {
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time")) + serviceSecond.intValue();
					} else {
						//可以接单的时间则为：路上时间+富余时间
						intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time")) + serviceSecond.intValue();
					}

					int intervalTimeE = 0;//必须间隔时间 秒
					if (11 <= Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) &&
							Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) < 14) {
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
					} else {
						//可以接单的时间则为：路上时间+富余时间
						intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
					}

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
					Date orderStartTime = DateUtils.addSecondsNotDayB(frequencyStartTime, -intervalTimeS);
					Date orderEndTime = DateUtils.addSecondsNotDayE(frequencyEndTime, intervalTimeE);
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
						return true;
					}
//					if (
//							(selectTime.after(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))
//									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS)) == 0) &&
//									selectTime.before(DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE))
//							) {
//
//					}
				}
			}



			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 开始-------------------
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(techId, creartDate, "order");
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

					//List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
					//DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
					//DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
					Date orderStartTime = DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS);
					Date orderEndTime = DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE);
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
						return true;
					}
//					if (
//							(selectTime.after(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))
//									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS)) == 0) &&
//									selectTime.before(DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE))
//							) {
//						return true;
//					}
				}
			}
		}
		return false;
	}

	/**
	 * 根据频次开始时间返回所有需要生成的日期
	 * @param serviceFrequency  服务频次   week_one:1周1次 week_some:1周多次 two_week_one:2周1次
	 * @param freList   服务时间   周
	 * @param serviceStart  第一次选择日期
	 * @param num 最大次数
	 * @return
	 */
	private static List<Date> listCreartDateByFrequency(String serviceFrequency, List<OrderCombinationFrequencyInfo> freList,int serviceNum, Date serviceStart,int num) {
		List<Date> list = new ArrayList<>();
		if("two_week_one".equals(serviceFrequency)){//2周1次
			list.add(serviceStart);
			return list;
		}
		List<Date> dateList = DateUtils.getWeekLaterTwoWeekDays();
		List<String> weekList = new ArrayList<>();
		HashMap<String, List<Date>> map = new HashMap<>();
		for(Date date : dateList){
			String week = String.valueOf(DateUtils.getWeekNum(date));
			if(weekList.contains(week)){
				List<Date> keyValueEntityList = map.get(week);
				keyValueEntityList.add(date);
				map.put(week,keyValueEntityList);
			}else{
				List<Date> keyValueEntityList = new ArrayList<>();
				keyValueEntityList.add(date);
				map.put(week,keyValueEntityList);
				weekList.add(week);
			}
		}

		for(OrderCombinationFrequencyInfo frequencyInfo : freList){
			List<Date> dateListMap = map.get(String.valueOf(frequencyInfo.getWeek()));
			for(Date dateMap : dateListMap){
				if(!serviceStart.after(dateMap)){
					list.add(dateMap);
				}
			}
		}

		//排序
		Collections.sort(list);
		//最大数量
		int maxNum = list.size() * serviceNum;
		if(num < maxNum){
			maxNum = num;
		}
		List<Date> listDate = new ArrayList<>();
		int j=0;
		for(int i=0;i<maxNum;i=i+serviceNum){
			listDate.add(list.get(j));
            j++;
		}
		return listDate;
	}

	/**
	 * 返回时间数组的最后日期
	 * @param creartDateList
	 * @return
	 */
	private Date getLastDate(List<Date> creartDateList) {
		Date last = creartDateList.get(0);
		for(Date date : creartDateList){
			if(date.after(last)){
				last = date;
			}
		}
		return last;
	}

	/**
	 * 设置固定时间- 保存
	 * @param combinationOrderInfo(serviceNum，masterId，freList，serviceFrequency，serviceStart，techId)
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<OrderInfo> saveRegularDate(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String serviceFrequency = combinationOrderInfo.getServiceFrequency();//服务频次
		List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();//服务时间
		Date serviceStart = combinationOrderInfo.getServiceStart();// 第一次选择日期

		String techId = combinationOrderInfo.getTechId();
		ServiceTechnicianInfo techInfo = technicianInfoDao.get(techId);
		//获取组合信息
		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		combinationInfo.setMasterId(masterId);
		combinationInfo.setServiceNum(serviceNum);
		combinationInfo.setServiceFrequency(serviceFrequency);
		combinationInfo.setServiceStart(serviceStart);
		combinationInfo.setTechId(techId);
		combinationInfo.setTechPhone(techInfo.getPhone());
		int remainderOfBespeak = combinationInfo.getBespeakTotal() - combinationInfo.getBespeakNum();

		//根据频次开始时间返回所有需要生成的日期
		List<Date> creartDateList = listCreartDateByFrequency(serviceFrequency, freList,serviceNum, serviceStart,combinationInfo.getBespeakTotal());
		//返回时间数组的最后日期
		Date serviceEnd = getLastDate(creartDateList);

		if(combinationInfo.getBespeakTotal() < creartDateList.size()){
			throw new ServiceException("没有可用订单");
		}

		// 更新 组合订单信息order_combination_info
		CombinationOrderInfo updateCombinationOrderInfo = new CombinationOrderInfo();
		updateCombinationOrderInfo.setMasterId(masterId);
		updateCombinationOrderInfo.setServiceNum(serviceNum);
		updateCombinationOrderInfo.setServiceFrequency(serviceFrequency);
		updateCombinationOrderInfo.setServiceStart(serviceStart);
		updateCombinationOrderInfo.setServiceEnd(serviceEnd);
		updateCombinationOrderInfo.setTechId(techId);
		combinationOrderDao.updateManyByMasterId(updateCombinationOrderInfo);

		// 新增 组合订单服务时间order_combination_frequency
		for(OrderCombinationFrequencyInfo frequency : freList){
			frequency.setMasterId(masterId);
			String startTimeStr = frequency.getTimeArea().split("-")[0];
			String endTimeStr = frequency.getTimeArea().split("-")[1];
			Date startTime = null;
			Date endTime = null;
			try {
				startTime = DateUtils.parseDate(startTimeStr,"HH:mm");
				endTime = DateUtils.parseDate(endTimeStr,"HH:mm");
			} catch (ParseException e) {

			}
			frequency.setStartTime(startTime);
			frequency.setEndTime(endTime);
			frequency.preInsert();
			frequencyDao.insert(frequency);
		}
		List<OrderInfo> orderList = new ArrayList<>();
		for(Date creartDate : creartDateList) {//每天生成一组订单

			Date serviceStartBeginTime = null;// 第一次选择日期开始时间
			Date serviceStartEndTime = null;// 第一次选择日期结束时间
			int serviceStartWeek = DateUtils.getWeekNum(creartDate);
			// 新增 组合订单服务时间order_combination_frequency
			for (OrderCombinationFrequencyInfo frequency : freList) {
				if (serviceStartWeek == frequency.getWeek()) {//第一次服务日期的开始时间(时分)
					serviceStartBeginTime = frequency.getStartTime();
					serviceStartEndTime = frequency.getEndTime();
				}
			}

			String startTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
					+  DateUtils.formatDate(serviceStartBeginTime,"HH:mm" + ":00");
			Date startTime = DateUtils.parseDate(startTimeStr);

			String endTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
					+  DateUtils.formatDate(serviceStartEndTime,"HH:mm" + ":00");
			Date endTime = DateUtils.parseDate(endTimeStr);

			serviceNum = (remainderOfBespeak-orderList.size()) < serviceNum ? (remainderOfBespeak-orderList.size()) : serviceNum;
			List<Date> listDate = DateUtils.listTimeByFrequency(creartDate, serviceStartBeginTime, serviceNum, combinationInfo.getServiceHour());
			//根据组合商品ID返回子商品信息
			OrderGoods goods = getOrderGoodsByCombination(combinationInfo.getCombinationGoodsId());

			List<OrderInfo> orderInfoList = new ArrayList<>();
			// 新增 子商品信息
			for (Date startDate : listDate) {
				HashMap<String, Object> map = combinationCreateOrder(startDate, combinationInfo, goods);
				OrderInfo orderInfo = (OrderInfo) map.get("orderInfoMsg");
				orderInfo.setJointGroupId(combinationInfo.getJointGroupId());
				orderInfoList.add(orderInfo);
			}

			//更新 组合订单对接编号信息order_combination_gasq
			List<OrderCombinationGasqInfo> combinationGasqInfoList = orderCombinationGasqDao.listFreeGasqSn(masterId);
			String groupId = IdGen.uuid();
			for (int i = 0; i < orderInfoList.size(); i++) {
				OrderCombinationGasqInfo combinationGasqInfo = combinationGasqInfoList.get(i);
				combinationGasqInfo.setOrderGroupId(groupId);
				combinationGasqInfo.setOrderNumber(orderInfoList.get(i).getOrderNumber());
				combinationGasqInfo.preUpdate();
				orderCombinationGasqDao.updateOrderGroup(combinationGasqInfo);

				orderInfoList.get(i).setJointOrderId(combinationGasqInfo.getJointOrderSn());
				orderInfoList.get(i).setServiceTimeForGroup(startTime);

				// order_info  对接订单ID 更新
				OrderInfo updateJointInfo = new OrderInfo();
				updateJointInfo.setId(orderInfoList.get(i).getId());
				updateJointInfo.setJointOrderId(combinationGasqInfo.getJointOrderSn());
				updateJointInfo.preUpdate();
				orderInfoDao.update(updateJointInfo);
			}

			// tech_schedule  服务技师排期 ------------------------------------------------------------------------------

			openCreateForTechSchedule(combinationInfo.getTechId(), startTime, endTime, groupId, masterId);

			//更新已预约次数
			CombinationOrderInfo updateCombinationInfo = new CombinationOrderInfo();
			updateCombinationInfo.setMasterId(masterId);
			updateCombinationInfo.setServiceNum(serviceNum);
			combinationOrderDao.updateBespeakByMasterId(updateCombinationInfo);

			orderList.addAll(orderInfoList);
		}
		return orderList;
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
		orderInfoMsg.setMasterId(orderInfo.getMasterId());
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



	/**
	 * 更换固定时间 - 保存
	 * @param combinationOrderInfo(serviceNum，masterId，freList，serviceFrequency，serviceStart，techId)
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<OrderInfo> updateRegularDate(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		int serviceNum = combinationOrderInfo.getServiceNum();//预约个数
		String serviceFrequency = combinationOrderInfo.getServiceFrequency();//服务频次
		List<OrderCombinationFrequencyInfo> freList = combinationOrderInfo.getFreList();//服务时间
		Date serviceStart = combinationOrderInfo.getServiceStart();// 第一次选择日期
		String techId = combinationOrderInfo.getTechId();

		// 更新 组合订单信息order_combination_info
		CombinationOrderInfo updateCombinationOrderInfo = new CombinationOrderInfo();
		updateCombinationOrderInfo.setMasterId(masterId);
		updateCombinationOrderInfo.setServiceNum(serviceNum);
		updateCombinationOrderInfo.setServiceFrequency(serviceFrequency);
		updateCombinationOrderInfo.setServiceStart(serviceStart);
		updateCombinationOrderInfo.setTechId(techId);
		combinationOrderDao.updateManyByMasterId(updateCombinationOrderInfo);

		//删除 旧的组合订单服务时间order_combination_frequency
		frequencyDao.deleteOldFrequencyByMasterId(masterId);
		// 新增 组合订单服务时间order_combination_frequency
		for(OrderCombinationFrequencyInfo frequency : freList){
			frequency.setMasterId(masterId);
			String startTimeStr = frequency.getTimeArea().split("-")[0];
			String endTimeStr = frequency.getTimeArea().split("-")[1];
			Date startTime = null;
			Date endTime = null;
			try {
				startTime = DateUtils.parseDate(startTimeStr,"HH:mm");
				endTime = DateUtils.parseDate(endTimeStr,"HH:mm");
			} catch (ParseException e) {

			}
			frequency.setStartTime(startTime);
			frequency.setEndTime(endTime);
			frequency.preInsert();
			frequencyDao.insert(frequency);

		}
		return null;
	}
}