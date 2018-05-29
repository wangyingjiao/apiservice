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

		for(OrderCombinationFrequencyInfo frequency : freList){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				removeBusyTechByWeekTime(frequency, techList, techDispatchNum, serviceSecond);
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
			}

		}
		return techList;
	}

	private void removeBusyTechByWeekTime(OrderCombinationFrequencyInfo frequencyInfo, List<OrderDispatch> techList, int techDispatchNum, Double serviceSecond) {
		int week = frequencyInfo.getWeek();
		Date checkStartTime = frequencyInfo.getStartTime();
		Date checkEndTime = frequencyInfo.getEndTime();

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
//					(selectTime.after(workTime.getStartTime()) || selectTime.compareTo(workTime.getStartTime())==0) &&
//							selectTime.before(workTime.getEndTime())
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
			/*List<TechScheduleInfo> techHolidyList = orderToolsService.listTechScheduleByTechWeekTime(tech.getTechId(), week, new Date(), "holiday");
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
			}*/
			//-------------------取得技师 周几休假时间 转成时间点列表 如果和工作时间重复 删除该时间点  结束-------------

			//----------取得技师 当天(15天中的某天)订单   如果和工作时间重复 删除该时间点 开始-------------------
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
//							(selectTime.after(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))
//									|| selectTime.compareTo(DateUtils.addSecondsNotDayB(frequency.getStartTime(), -intervalTimeS))==0) &&
//									selectTime.before(DateUtils.addSecondsNotDayE(frequency.getEndTime(), intervalTimeE))
//							){
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
		}
	}


	/**
	 * 更换固定技师 - 保存前验证
	 * @param combinationOrderInfo(itemId)
	 * @return
	 */
	public boolean checkRegularDateTech(CombinationOrderInfo combinationOrderInfo) {
		String techId = combinationOrderInfo.getTechId();
		String masterId = combinationOrderInfo.getMasterId();

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByMasterId(masterId);
		int serviceNum = combinationInfo.getServiceNum();//预约个数
		List<OrderCombinationFrequencyInfo> freList = frequencyDao.getFrequencyList(combinationOrderInfo);

		int techDispatchNum = 1;//派人数量
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);

		List<OrderDispatch> techList = new ArrayList<>();
		OrderDispatch orderDispatch = new OrderDispatch();
		orderDispatch.setTechId(techId);
		techList.add(orderDispatch);
		for(OrderCombinationFrequencyInfo frequency : freList){
			OrderTimeList responseRe = new OrderTimeList();
			try {
				removeBusyTechByWeekTime(frequency, techList, techDispatchNum, serviceSecond);
			}catch (Exception e){
				logger.error(responseRe.getLabel()+"去除忙碌技师失败");
			}

		}
		if(techList.size() == 0){
			return true;
		}
		return false;
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