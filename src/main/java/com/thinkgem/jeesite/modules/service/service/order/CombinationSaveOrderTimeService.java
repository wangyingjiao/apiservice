/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 组合订单设置子订单时间Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSaveOrderTimeService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {
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
	private OrderToolsService orderToolsService;

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;

	/**
	 * 更换时间
	 * @param orderInfo
	 * @return
	 */
	public List<OrderTimeList> updateOrderTimeDateList(OrderInfo orderInfo) {

		List<Date> dateList = DateUtils.getAfterSevenDays();
		List<OrderTimeList> list = new ArrayList<>();
		int value = 1;

		List<OrderGoods> goodsInfoList = orderInfoDao.getOrderGoodsList(orderInfo); //取得订单服务信息
		int techDispatchNum = orderToolsService.getTechDispatchNumByGoodsList(goodsInfoList);//派人数量
		double serviceHour = orderToolsService.getServiceHourByGoodsList(goodsInfoList);//建议服务时长（小时）

		orderInfo = orderInfoDao.get(orderInfo);
		String stationId = orderInfo.getStationId();//服务站ID
		if(null == stationId){
			logger.error("未找到当前订单的服务站信息");
			return null;
		}

		Double serviceSecond = (serviceHour * 3600);

		String skillId = orderToolsService.getSkillIdByOrgSort(orderInfo.getOrgId(), orderToolsService.getNotFullGoodsSortId(goodsInfoList));
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
						if(!orderInfo.getId().equals(order.getTypeId())) {//当前订单不考虑
							int intervalTimeS = 0;//必须间隔时间 秒
							int intervalTimeE = 0;//必须间隔时间 秒
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
	//更换子订单时间 - 技师列表
	public Map<String,Object> updateOrderTimeTechList(OrderInfo orderInfo) {
		//技师的技能  排期表时间  服务信息-分类-技能
		OrderInfo info = orderInfoDao.get(orderInfo.getId());
		//服务时间
		Date serviceTime = orderInfo.getServiceTime();
		//建议服务时长（小时）
		Double serviceHour = info.getServiceHour();
		//建议完成时间
		Date finishTime =null;
		//根据masterId去查组合订单主表
		CombinationOrderInfo combinationOrderInfo=new CombinationOrderInfo();
		combinationOrderInfo.setMasterId(info.getMasterId());
		CombinationOrderInfo combinationById = combinationOrderDao.getCombinationById(combinationOrderInfo);
		//判断是不是拆单
		if ("group_split_yes".equals(combinationById.getOrderType())){
			//拆单 根据order_group_id order_number查询所有集合
			int size = 0;
			OrderCombinationGasqInfo listByOrderNumber = orderCombinationGasqDao.getListByOrderNumber(info);
			List<OrderCombinationGasqInfo> listByOrderGroupId = orderCombinationGasqDao.getListByOrderGroupId(listByOrderNumber);
			if (listByOrderGroupId != null && listByOrderGroupId.size() > 0){
				size = listByOrderGroupId.size();
			}
			//计算订单的建议完成时间
			Double serviceSecond = (serviceHour * size * 3600);
			finishTime = DateUtils.addSeconds(serviceTime, serviceSecond.intValue());
		}
		if ("group_split_no".equals(combinationById.getOrderType())){
			//计算订单的建议完成时间
			Double serviceSecond = (serviceHour * 3600);
			finishTime = DateUtils.addSeconds(serviceTime, serviceSecond.intValue());
		}
		info.setServiceTime(orderInfo.getServiceTime());
		info.setFinishTime(finishTime);
		info.setSerchFullTech(true);
		List<OrderDispatch> techList = orderToolsService.listTechByGoodsAndTimeAndOldTech(info);
		//当前技师
		List<OrderDispatch> byOrderId = orderDispatchDao.getByOrderId(orderInfo);
		OrderDispatch orderDispatch = byOrderId.get(0);
		ServiceTechnicianInfo technicianInfo=new ServiceTechnicianInfo();
		technicianInfo.setId(orderDispatch.getTechId());
		ServiceTechnicianInfo byId = serviceTechnicianInfoDao.getById(technicianInfo);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("list",techList);
		map.put("tech",byId);
		return  map;
	}
	@Transactional(readOnly = false)
	//更换子订单时间 - 保存(拆单的)  参数 orderId serviceTime techId 拆单就一个技师
	public HashMap<String,Object> updateOrderTimeSave(OrderInfo orderInfo){
        int i =0;
        OrderInfo info = orderInfoDao.get(orderInfo.getId());
        //根据orderNumber masterId获取组合订单的orderGroupId
        OrderCombinationGasqInfo listByOrderNumber = orderCombinationGasqDao.getListByOrderNumber(info);
		CombinationOrderInfo combinationByMasterId = combinationOrderDao.getCombinationByMasterId(info.getMasterId());
		if (!"group_split_yes".equals(combinationByMasterId.getOrderType())){
			throw new ServiceException("该订单订单类型有误");
		}
        //计算建议完成时间
        int size = 0;
		Date serviceTime = orderInfo.getServiceTime();
		//建议服务时长（小时）（排期表）
		Double serviceHour = info.getServiceHour();
		//建议完成时间（排期表）
		Date finishTime =null;
		//根据groupId获取组合订单的订单集合
        List<OrderCombinationGasqInfo> listByOrderGroupId = orderCombinationGasqDao.getListByOrderGroupId(listByOrderNumber);
        if (listByOrderGroupId != null && listByOrderGroupId.size() > 0){
            size = listByOrderGroupId.size();
            for (OrderCombinationGasqInfo orderCombinationGasqInfo:listByOrderGroupId){
                //获取同一groupId 的订单
                OrderInfo orderTem = new OrderInfo();
                orderTem.setOrderNumber(orderCombinationGasqInfo.getOrderNumber());
                OrderInfo bySn = orderInfoDao.getBySn(orderTem);
                bySn.setServiceTime(serviceTime);
                Double serviceSecondTem = (bySn.getServiceHour() * 3600);
                Date date = DateUtils.addSeconds(serviceTime, serviceSecondTem.intValue());
                bySn.setSuggestFinishTime(date);
                bySn.preUpdate();
                //先将订单表修改 *******
                orderInfoDao.saveOrderTime(orderInfo);
				serviceTime=date;
            }
        }
		//计算订单的建议完成时间（排期表）
		Double serviceSecond = (serviceHour * size * 3600);
		finishTime = DateUtils.addSeconds(orderInfo.getServiceTime(), serviceSecond.intValue());
		int weekNum = DateUtils.getWeekNum(orderInfo.getServiceTime());
		//修改派单表
        // 派单 原来没有，现在有
        List<OrderDispatch> orderCreateMsgList = new ArrayList<>();
        // 改派 原来有，现在没有
        List<OrderDispatch> orderDispatchMsgList = new ArrayList<>();
        // 时间变化 原来有，现在有
        List<OrderDispatch> orderServiceTimeMsgList = new ArrayList<>();

		//1.将之前的派单的可用状态置为no
        //根据订单id获取之前的技师集合
		List<OrderDispatch> byOrderId = orderDispatchDao.getByOrderId(orderInfo);
		if (byOrderId == null || byOrderId.size() < 1){
            throw new ServiceException("该订单尚未派单");
		}
        OrderDispatch orderDispatch = byOrderId.get(0);
        //如果只改变时间 没改技师 派单表不变
        if (!orderInfo.getTechId().equals(orderDispatch.getTechId())){
            orderDispatchMsgList.add(orderDispatch);
            //换技师 换时间
            orderDispatch.setStatus("no");
            orderDispatch.preUpdate();
            int update = orderDispatchDao.update(orderDispatch);
            if (update < 1){
                throw new ServiceException("删除改派前的派单表失败");
            }
            //2.将新技师加入派单表中
            OrderDispatch dispatch=new OrderDispatch();
            dispatch.setStatus("yes");
            dispatch.setTechId(orderInfo.getTechId());
            dispatch.setOrderId(orderInfo.getId());
            dispatch.preInsert();
            int insert = orderDispatchDao.insert(dispatch);
            if (insert < 1){
                throw new ServiceException("新增派单表失败");
            }
            orderCreateMsgList.add(dispatch);
        }else {
            orderServiceTimeMsgList.add(orderDispatch);
        }
		//修改排期表
		//根据orderGroupId查询排期表 将之前的删除 新增改派后的
		TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
		techScheduleInfo.setType("master");
		techScheduleInfo.setTypeId(listByOrderNumber.getOrderGroupId());
		techScheduleInfo.setMasterId(info.getMasterId());
		List<TechScheduleInfo> orderSchedule = techScheduleDao.getOrderScheduleByTypeId(techScheduleInfo);
		if (orderSchedule != null && orderSchedule.size() > 0){
			//组合订单 同一个orderGroupId只有一个排期表
			TechScheduleInfo techScheduleInfo1 = orderSchedule.get(0);
			techScheduleInfo1.preUpdate();
			int delete = techScheduleDao.deleteSchedule(techScheduleInfo1);
			if (delete < 1){
				throw new ServiceException("删除改派前的排期表失败");
			}
		}
		//新增改派后的排期表
		TechScheduleInfo newTechScheduleInfo=new TechScheduleInfo();
		newTechScheduleInfo.setTechId(orderInfo.getTechId());
		newTechScheduleInfo.setTypeId(listByOrderNumber.getOrderGroupId());
		newTechScheduleInfo.setType("master");
		newTechScheduleInfo.setMasterId(info.getMasterId());
        newTechScheduleInfo.setScheduleWeek(weekNum);
        newTechScheduleInfo.setStartTime(orderInfo.getServiceTime());
        newTechScheduleInfo.setEndTime(finishTime);
        newTechScheduleInfo.setScheduleDate(orderInfo.getServiceTime());
		newTechScheduleInfo.preInsert();
        i = techScheduleDao.insertSchedule(newTechScheduleInfo);
		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHour);
		map.put("orderId",orderInfo.getId());
		map.put("serviceDate",orderInfo.getServiceTime());
		map.put("orderSource",info.getOrderSource());

        map.put("orderServiceTimeMsgList", orderServiceTimeMsgList);
        map.put("orderDispatchMsgList", orderDispatchMsgList);
        map.put("orderCreateMsgList", orderCreateMsgList);

		map.put("orderNumber", info.getOrderNumber());
        return map;
	}

    //根据订单id serviceTime （techId） 验证保存的时间是否已有排期冲突
	public boolean checkTech(OrderInfo orderInfo){
        //获取子订单的服务时长 得到结束时间
        OrderInfo info = orderInfoDao.get(orderInfo.getId());
        //建议服务时长
        Double serviceHour = info.getServiceHour();
        //建议完成时间
        Date finishTime =null;
        Date serviceTime = orderInfo.getServiceTime();
        if ("group_split_yes".equals(info.getOrderType())){
            //拆单 根据order_group_id order_number查询所有集合
            int size = 0;
            OrderCombinationGasqInfo listByOrderNumber = orderCombinationGasqDao.getListByOrderNumber(info);
            List<OrderCombinationGasqInfo> listByOrderGroupId = orderCombinationGasqDao.getListByOrderGroupId(listByOrderNumber);
            if (listByOrderGroupId != null && listByOrderGroupId.size() > 0){
                size = listByOrderGroupId.size();
            }
            //计算订单的建议完成时间
            Double serviceSecond = (serviceHour * size * 3600);
            finishTime = DateUtils.addSeconds(serviceTime, serviceSecond.intValue());
            TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
            techScheduleInfo.setTechId(orderInfo.getTechId());
            techScheduleInfo.setStartTime(serviceTime);
            techScheduleInfo.setEndTime(finishTime);
            techScheduleInfo.setTypeId(listByOrderNumber.getOrderGroupId());
            List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechIdAndMasterId(techScheduleInfo);
            if (scheduleByTechId != null && scheduleByTechId.size() > 0){
                return false;
            }
        }else if ("group_split_no".equals(info.getOrderType())){
            //根据订单服务时间 得到结束时间
            Double serviceSecond = (serviceHour * 3600);
            finishTime = DateUtils.addSeconds(serviceTime, serviceSecond.intValue());
            //排期表中技师 组合不拆单
            List<OrderDispatch> byOrderId = orderDispatchDao.getByOrderId(orderInfo);
            if (byOrderId != null && byOrderId.size() > 0){
                //根据每个技师id  开始 结束时间查询排期表 是否有相同时间的排期
                for (OrderDispatch orderDispatch:byOrderId){
                    TechScheduleInfo techScheduleInfo=new TechScheduleInfo();
                    techScheduleInfo.setTechId(orderDispatch.getTechId());
                    techScheduleInfo.setStartTime(serviceTime);
                    techScheduleInfo.setEndTime(finishTime);
                    techScheduleInfo.setMasterId(info.getMasterId());
                    List<TechScheduleInfo> scheduleByTechId = techScheduleDao.getScheduleByTechIdAndMasterId(techScheduleInfo);
                    if (scheduleByTechId != null && scheduleByTechId.size() > 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}