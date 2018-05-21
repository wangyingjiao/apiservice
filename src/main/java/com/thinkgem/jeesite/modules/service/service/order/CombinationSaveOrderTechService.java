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
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 组合订单设置子订单技师Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSaveOrderTechService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {
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

    public List<OrderDispatch> updateOrderTechInit(CombinationOrderInfo combinationOrderInfo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(combinationOrderInfo.getOrderId());
        List<OrderDispatch> list = orderInfoDao.getOrderDispatchList(orderInfo);
        return list;
    }


	/**
	 * 增加 改派 技师列表
	 * @param combinationOrderInfo(orderId,techName)
	 * @return
	 */
	public List<OrderDispatch> updateOrderTechTechList(CombinationOrderInfo combinationOrderInfo) {
		List<OrderDispatch> techList = orderToolsService.listTechByGoodsAndTimeForCombination(combinationOrderInfo);
		return techList;
	}

	/**
	 * 子订单  更换技师 增加保存按钮
	 * @param combinationOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> updateOrderTechAddSave(CombinationOrderInfo combinationOrderInfo) {
		Double serviceHourRe = 0.0;
		List<String> techIdList = combinationOrderInfo.getTechIdList();//新增技师List
		if(techIdList==null || techIdList.size()==0){
			throw new ServiceException("请选择技师");
		}
		OrderInfo orderInfo = orderInfoDao.get(combinationOrderInfo.getOrderId());//当前订单
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() + techIdList.size());

		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

		//新增、改派判断库存
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
		info.setFinishTime(DateUtils.addSecondsNotDayE(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSecondsNotDayE(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		orderInfoDao.update(info);

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
			techScheduleInfo.setMasterId(orderInfo.getMasterId());
			techScheduleInfo.setType("master");//'holiday：休假  order：订单'
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
			techScheduleInfo.setMasterId(orderInfo.getMasterId());
			techScheduleInfo.setType("master");//'holiday：休假  order：订单'
			techScheduleInfo.preUpdate();
			techScheduleDao.updateScheduleByTypeIdTechId(techScheduleInfo);
		}

		List<OrderDispatch> techListRe = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour", serviceHourRe);
		map.put("list", techListRe);

		map.put("orderId", orderInfo.getId());
		map.put("orderSource",orderInfo.getOrderSource());
		map.put("orderCreateMsgList",orderCreateMsgList);
		map.put("orderNumber",orderInfo.getOrderNumber());
		return map;
	}

	/**
	 * 子订单  更换技师  改派保存按钮
	 * @param combinationOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> updateOrderTechDispatchSave(CombinationOrderInfo combinationOrderInfo) {
		Double serviceHourRe = 0.0;
		String dispatchTechId = combinationOrderInfo.getTechId();//改派前技师ID
		List<String> techIdList = combinationOrderInfo.getTechIdList();//改派技师List
		OrderInfo orderInfo = orderInfoDao.get(combinationOrderInfo.getOrderId());//当前订单
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();

		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() - 1 + techIdList.size());

		//新增、改派判断库存
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
		info.setFinishTime(DateUtils.addSecondsNotDayE(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSecondsNotDayE(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		orderInfoDao.update(info);
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

		List<OrderDispatch> techListRe = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);

		map.put("orderId",orderInfo.getId());
		map.put("orderSource",orderInfo.getOrderSource());

		map.put("orderDispatchMsgList", orderDispatchMsgList);
		map.put("orderCreateMsgList", orderCreateMsgList);
		map.put("orderNumber", orderInfo.getOrderNumber());
		return map;
	}

}