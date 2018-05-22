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
				List<OrderDispatch> hours = listHourByWeek(i, map,techList, techDispatchNum,serviceSecond);
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

	private List<OrderDispatch> listHourByWeek(int week, Map<String,Date> map, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond) {
		List<OrderDispatch> techForList = new ArrayList<>();
		if(techList != null){//深浅拷贝问题
			for(OrderDispatch dispatch: techList){
				techForList.add(dispatch);
			}
		}
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
				List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(tech.getTechId(), week, new Date(), "holiday");
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
				//----------取得技师 组合订单频率   如果和工作时间重复 删除该时间点 结束------------------

				//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
				Date date = map.get(String.valueOf(week));
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

		for(OrderCombinationFrequencyInfo frequency : freList){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				removeBusyTechByWeekTime(frequency, techList, techDispatchNum, serviceSecond, serviceStart);
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
			}
		}
		return techList;
	}

	private void removeBusyTechByWeekTime(OrderCombinationFrequencyInfo frequencyInfo, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond, Date serviceStart) {
		int week = frequencyInfo.getWeek();
		String selectTimeStr = frequencyInfo.getTimeArea().split("-")[0];
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
			List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(tech.getTechId(), week, new Date(), "holiday");
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

			//----------取得技师 组合订单频率    如果和工作时间重复 删除该时间点 开始-------------------
			//--- 订单时间段--订单开始时间 减去商品需求时间 减去准备时间；---订单结束时间 加上准备时间 ----------------------
			CombinationOrderInfo serchCombinationInfo = new CombinationOrderInfo();
			serchCombinationInfo.setTechId(tech.getTechId());
			serchCombinationInfo.setSerchWeek(week);
			List<OrderCombinationFrequencyInfo> frequencyList = combinationOrderDao.listFrequencyByTechWeek(serchCombinationInfo);
			if (frequencyList != null && frequencyList.size() != 0) {
				for (OrderCombinationFrequencyInfo frequency : frequencyList) {
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

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
					if(
						(selectTime.after(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))
							|| selectTime.compareTo(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))==0) &&
						selectTime.before(DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE))
					){
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
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), serviceStart, "order");
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
			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 结束------------------

		}
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

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		if(serviceNum > combinationInfo.getBespeakTotal()){
			return true;
		}
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);

		for(OrderCombinationFrequencyInfo frequencyInfo : freList) {
			int week = frequencyInfo.getWeek();
			String selectTimeStr = frequencyInfo.getTimeArea().split("-")[0];
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
			List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(techId, week, new Date(), "holiday");
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
			CombinationOrderInfo serchCombinationInfo = new CombinationOrderInfo();
			serchCombinationInfo.setTechId(techId);
			serchCombinationInfo.setSerchWeek(week);
			List<OrderCombinationFrequencyInfo> frequencyList = combinationOrderDao.listFrequencyByTechWeek(serchCombinationInfo);
			if (frequencyList != null && frequencyList.size() != 0) {
				for (OrderCombinationFrequencyInfo frequency : frequencyList) {
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

//					List<String> orders = DateUtils.getHeafHourTimeListLeftBorder(
//							DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS),
//							DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE));
					if (
							(selectTime.after(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))
									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS)) == 0) &&
									selectTime.before(DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE))
							) {
						return true;
					}
				}
			}



			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 开始-------------------
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(techId, serviceStart, "order");
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
					if (
							(selectTime.after(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS))
									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS)) == 0) &&
									selectTime.before(DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE))
							) {
						return true;
					}
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
	 * @return
	 */
	private static List<Date> listCreartDateByFrequency(String serviceFrequency, List<OrderCombinationFrequencyInfo> freList, Date serviceStart) {
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

		return list;
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

		//根据频次开始时间返回所有需要生成的日期
		List<Date> creartDateList = listCreartDateByFrequency(serviceFrequency, freList, serviceStart);
		//返回时间数组的最后日期
		Date serviceEnd = getLastDate(creartDateList);

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
			int serviceStartWeek = DateUtils.getWeekNum(serviceStart);
			// 新增 组合订单服务时间order_combination_frequency
			for (OrderCombinationFrequencyInfo frequency : freList) {
				if (serviceStartWeek == frequency.getWeek()) {//第一次服务日期的开始时间(时分)
					serviceStartBeginTime = frequency.getStartTime();
					serviceStartEndTime = frequency.getEndTime();
				}
			}

			List<Date> listDate = DateUtils.listTimeByFrequency(serviceStart, serviceStartBeginTime, serviceNum, combinationInfo.getServiceHour());
			//根据组合商品ID返回子商品信息
			OrderGoods goods = getOrderGoodsByCombination(combinationInfo.getCombinationGoodsId());

			List<OrderInfo> orderInfoList = new ArrayList<>();
			// 新增 子商品信息
			for (Date startDate : listDate) {
				HashMap<String, Object> map = combinationCreateOrder(startDate, combinationInfo, goods);
				OrderInfo orderInfo = (OrderInfo) map.get("orderInfoMsg");
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


				// order_info  对接订单ID 更新
				OrderInfo updateJointInfo = new OrderInfo();
				updateJointInfo.setId(orderInfoList.get(i).getId());
				updateJointInfo.setJointOrderId(combinationGasqInfo.getJointOrderSn());
				updateJointInfo.preUpdate();
				orderInfoDao.update(updateJointInfo);
			}

			// tech_schedule  服务技师排期 ------------------------------------------------------------------------------
			openCreateForTechSchedule(combinationInfo.getTechId(), serviceStartBeginTime, serviceStartEndTime, groupId, masterId);

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
		techScheduleInfo.setScheduleDate(serviceStartBeginTime);//日期
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