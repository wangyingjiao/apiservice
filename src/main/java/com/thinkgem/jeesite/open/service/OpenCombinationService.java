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
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderToolsService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
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
public class OpenCombinationService extends CrudService<OrderInfoDao, OrderInfo> {

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
	private OrderToolsService orderToolsService;


	/**
     * 对接接口 选择服务时间
	 * @param info
     * @return
     */
    public Map<String,Object> serviceTimesForGroup(OpenServiceTimesForGroupRequest info) {
		List<Date> dateList = DateUtils.getAfterSevenDays();
		List<OpenServiceTimesResponse> list = new ArrayList<>();

		String store_id = info.getStore_id();//门店ID
		if(null == store_id){
			throw new ServiceException("门店ID不能为空");
		}
		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}

		List<OpenServiceInfo> serviceInfos = info.getService_info();//下单服务项目信息
		String platform = info.getPlatform();//对接平台代号   默认值 : gasq
		String groupId = info.getGroup_id();//国安社区组ID
		String serviceNum = info.getGasq_order_num();//预约个数

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByGroupId(groupId);
		if(combinationInfo == null){
			throw new ServiceException("未找到组合订单信息");
		}
		SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(combinationInfo.getCombinationGoodsId());
		int techDispatchNum = 1;//派人数量
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		String stationId = combinationInfo.getStationId();//服务站ID
		String orgId = combinationInfo.getOrgId();//机构
		Double serviceSecond = (serviceHour * Integer.getInteger(serviceNum) * 3600);

		BasicOrganization organization = basicOrganizationDao.get(orgId);
		if(organization==null){
			throw new ServiceException("未找到机构信息");
		}
		Date orgWorkStartTime = organization.getWorkStartTime();;//工作开始时间
		Date orgWorkEndTime = organization.getWorkEndTime();//工作结束时间
		if(orgWorkStartTime==null || orgWorkEndTime==null){
			throw new ServiceException("未找到机构工作时间信息");
		}
		/*//通过对接方E店CODE获取机构
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(eshop_code);
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
		}*/

		//技能
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(commodity.getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if (skillSortList != null && skillSortList.size() == 1) {
			skillId = skillSortList.get(0).getSkillId();
		} else {
			throw new ServiceException("未找到商品需求的技能信息");
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
		//serchInfo.setJobNature("full_time");
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);
		if(techList.size() < techDispatchNum){//技师数量不够
			logger.error("技师数量不够");
			Map<String,Object> value = new HashMap();
			value.put("availabletimes",list);
			return value;
		}
//
//		for(Date date : dateList){
//			OrderTimeList responseRe = new OrderTimeList();
//			try {
//				responseRe.setValue(String.valueOf(value));
//				value++;
//				responseRe.setLabel(DateUtils.formatDate(date, "yyyy-MM-dd"));
//				//该日服务时间点列表
//				List<OrderDispatch> hours = findTimeListHours(date,techList, techDispatchNum,serviceSecond);
//				if(hours!= null && hours.size()>0){
//					responseRe.setServiceTime(hours);
//					list.add(responseRe);
//				}
//			}catch (Exception e){
//				logger.error(responseRe.getLabel()+"未找到服务时间点列表");
//			}
//
//		}
//		//return list;



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

				//去除订单前后时间段
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
		List<String> list = Arrays.asList(strings);

		return list;
	}

}