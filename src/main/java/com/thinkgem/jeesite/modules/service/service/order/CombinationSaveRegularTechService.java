/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
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

import java.text.ParseException;
import java.util.*;

/**
 * 组合订单设置固定技师Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSaveRegularTechService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {

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
	 * 更换固定技师 - 技师列表
	 * @param combinationOrderInfo(techName，masterId)
	 * @return
	 */
	public List<OrderDispatch> updateRegularTechTechList(CombinationOrderInfo combinationOrderInfo) {
		String techName = combinationOrderInfo.getTechName();//查询条件
		String masterId = combinationOrderInfo.getMasterId();

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		int serviceNum = combinationInfo.getServiceNum();//预约个数
		List<OrderCombinationFrequencyInfo> freList = frequencyDao.getFrequencyList(combinationOrderInfo);

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
		serchInfo.setTechName(techName);
		serchInfo.setTechId(combinationInfo.getTechId());
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillIdRemoveRegularTech(serchInfo);
		if(techList.size() < techDispatchNum){//技师数量不够
			logger.error("技师数量不够");
			return null;
		}


		List<Date> creartDateList = listUpdateDateByFrequency(freList,serviceNum,combinationInfo.getBespeakTotal());

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

				removeBusyTechByWeekTime(creartDateTimeStrTime, techList, techDispatchNum, serviceSecond,masterId);
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
			}
		}
//
//		for(OrderCombinationFrequencyInfo frequency : freList){
//			OrderTimeList responseRe = new OrderTimeList();
//			try {
//				removeBusyTechByWeekTime(frequency, techList, techDispatchNum, serviceSecond);
//			}catch (Exception e){
//				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
//			}
//
//		}
		return techList;
	}

	/**
	 * 根据频次开始时间返回所有需要生成的日期
	 * @param freList   服务时间   周
	 * @param num 最大次数
	 * @return
	 */
	private static List<Date> listUpdateDateByFrequency(List<OrderCombinationFrequencyInfo> freList,int serviceNum,int num) {
		List<Date> list = new ArrayList<>();

		List<Date> dateList = DateUtils.getThisWeekEndLastWeekDayList();
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
				list.add(dateMap);
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
	private void removeBusyTechByWeekTime(Date creartDate, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond,String masterId) {
		int week = DateUtils.getWeekNum(creartDate);

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
				Boolean forFlag = false;
				for (TechScheduleInfo holiday : techHolidyList) {
					Date holidayStartTime = DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue());
					Date holidayEndTime = holiday.getEndTime();
					if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, holidayStartTime, holidayEndTime)) {
						it.remove();
						if(techList.size() < techDispatchNum){//技师数量不够
							return;
						}
						forFlag = true;
						break;//下一位技师
					}
				}
				if(forFlag){
					continue;//下一位技师
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
				Boolean forFlag = false;
				for (OrderCombinationFrequencyInfo frequency : frequencyList) {
					if (!masterId.equals(frequency.getMasterId())) {
						Date frequencyStartTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
								+ " " + DateUtils.formatDate(frequency.getStartTime(), "HH:mm:ss"));//工作开始时间
						Date frequencyEndTime = DateUtils.parseDate(DateUtils.formatDate(checkStartTime, "yyyy-MM-dd")
								+ " " + DateUtils.formatDate(frequency.getEndTime(), "HH:mm:ss"));//工作结束时间

						int intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
						int intervalTimeE = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) &&
								Integer.parseInt(DateUtils.formatDate(frequencyEndTime, "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
						}

						Date orderStartTime = DateUtils.addSecondsNotDayB(frequencyStartTime, -intervalTimeS);
						Date orderEndTime = DateUtils.addSecondsNotDayE(frequencyEndTime, intervalTimeE);
						if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
							it.remove();
							if (techList.size() < techDispatchNum) {//技师数量不够
								return;
							}
							forFlag = true;
							break;//下一位技师
						}
					}
				}
				if(forFlag){
					continue;//下一位技师
				}
			}
			//----------取得技师 组合订单频率   如果和工作时间重复 删除该时间点 结束------------------

			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 开始-------------------
			List<TechScheduleInfo> techOrderList = orderToolsService.listTechScheduleByTechTime(tech.getTechId(), creartDate, "order");
			if (techOrderList != null && techOrderList.size() != 0) {
				Boolean forFlag = false;
				for (TechScheduleInfo order : techOrderList) {
					if (!masterId.equals(order.getMasterId())) {

						int intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
						int intervalTimeE = 0;//必须间隔时间 秒
						if (11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) &&
								Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) < 14) {
							//可以接单的时间则为：40分钟+路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
						} else {
							//可以接单的时间则为：路上时间+富余时间
							intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
						}

						Date orderStartTime = DateUtils.addSecondsNotDayB(order.getStartTime(), -intervalTimeS);
						Date orderEndTime = DateUtils.addSecondsNotDayE(order.getEndTime(), intervalTimeE);
						if (!DateUtils.checkDatesRepeat(checkStartTime, checkEndTime, orderStartTime, orderEndTime)) {
							it.remove();
							if (techList.size() < techDispatchNum) {//技师数量不够
								return;
							}
							forFlag = true;
							break;//下一位技师
						}
					}
				}
				if(forFlag){
					continue;//下一位技师
				}
			}
			//----------取得技师 开始时间订单   如果和工作时间重复 删除该时间点 结束------------------

		}
	}


	/**
	 * 更换固定技师 - 保存前验证
	 * @param combinationOrderInfo(itemId)
	 * @return
	 */
	public boolean checkRegularDateTech(CombinationOrderInfo combinationOrderInfo) {
		List<OrderDispatch> list = updateRegularTechTechList( combinationOrderInfo);
		if(list != null) {
			for (OrderDispatch dispatch : list) {
				if(dispatch.getTechId().equals(combinationOrderInfo.getTechId())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 更换固定技师 - 保存
	 * @param combinationOrderInfo(masterId，techId)
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<OrderInfo> updateRegularTechSave(CombinationOrderInfo combinationOrderInfo) {
		String masterId = combinationOrderInfo.getMasterId();
		String techId = combinationOrderInfo.getTechId();

		// 更新 组合订单信息order_combination_info
		CombinationOrderInfo updateCombinationOrderInfo = new CombinationOrderInfo();
		updateCombinationOrderInfo.setMasterId(masterId);
		updateCombinationOrderInfo.setTechId(techId);
		combinationOrderDao.updateManyTechByMasterId(updateCombinationOrderInfo);

		return null;
	}
}