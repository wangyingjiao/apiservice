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
		if(!"group_split_no".equals(orderInfo.getOrderType())){
			throw new ServiceException("订单类型不允许此操作");
		}
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() + techIdList.size());

		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

		//新增、改派判断库存
		boolean flag = false;
		List<OrderDispatch> checkTechList = orderToolsService.listTechByGoodsAndTimeForCombination(combinationOrderInfo);
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
	 * 子订单  更换技师  改派保存按钮 拆单  多次服务订单
	 * @param combinationOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> updateOrderTechDispatchSaveForMany(CombinationOrderInfo combinationOrderInfo) {
		String dispatchTechIdOld = combinationOrderInfo.getTechId();//改派前技师ID
		ServiceTechnicianInfo dispatchTechOld = serviceTechnicianInfoDao.get(dispatchTechIdOld);//改派前技师

		List<String> techIdList = combinationOrderInfo.getTechIdList();//改派后技师List
		if(techIdList==null || techIdList.size()!=1){
			throw new ServiceException("技师数量有误，请重新选择！");
		}
		String dispatchTechIdNew = techIdList.get(0);//改派后技师ID
		ServiceTechnicianInfo dispatchTechNew = serviceTechnicianInfoDao.get(dispatchTechIdNew);//改派后技师

		List<OrderCombinationGasqInfo> gasqInfos = orderCombinationGasqDao.listGroupOrderByOrderId(combinationOrderInfo);//订单组List  orderId
		if(gasqInfos==null || gasqInfos.size()==0){
			throw new ServiceException("未找到订单组信息，请检查数据是否有误！");
		}
		String masterId = gasqInfos.get(0).getMasterId();//订单组ID
		String groupId = gasqInfos.get(0).getOrderGroupId();//订单组ID
		String orderSource = gasqInfos.get(0).getOrderSource();//订单来源

		OrderInfo serchScheduleInfo = new OrderInfo();
		serchScheduleInfo.setId(combinationOrderInfo.getOrderId());
		TechScheduleInfo scheduleInfo = orderInfoDao.getTechScheduleByOrderIdForCombination(serchScheduleInfo);
		Date groupServiceTime = scheduleInfo.getStartTime();//服务时间
		Date groupFinishTime = scheduleInfo.getEndTime();//完成时间


		List<OrderInfo> orderDispatchMsgList = new ArrayList<>();//改派发消息技师列表
		List<OrderInfo> orderCreateMsgList = new ArrayList<>();//新增发消息技师列表
		List<OrderInfo> orderSendList = new ArrayList<>();//改派技师列表 对接国安

		//关联组订单 工单修改
		for(OrderCombinationGasqInfo gasqInfo : gasqInfos){
			OrderInfo serchOrderInfo = new OrderInfo();//当前订单
			serchOrderInfo.setId(gasqInfo.getOrderId());
			List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(serchOrderInfo); //订单当前已有技师List
			for(OrderDispatch orderDispatch : techList){
				//改派前技师工单 不可用
				OrderDispatch orderDispatchUpdate = new OrderDispatch();
				orderDispatchUpdate.setId(orderDispatch.getId());//ID
				orderDispatchUpdate.setStatus("no");//状态(yes：可用 no：不可用)
				orderDispatchUpdate.preUpdate();
				orderDispatchDao.update(orderDispatchUpdate);//数据库改派前技师设为不可用
			}

			//改派后技师工单新增保存
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setTechId(dispatchTechIdNew);//技师ID
			orderDispatch.setOrderId(gasqInfo.getOrderId());//订单ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);

			//改派发消息技师列表------------------------------
			OrderInfo orderDispatchMsg = new OrderInfo();
			orderDispatchMsg.setId(gasqInfo.getOrderId());
			orderDispatchMsg.setOrderNumber(gasqInfo.getOrderNumber());

			List<OrderDispatch> orderDispatchMsgTechList = new ArrayList<>();
			OrderDispatch orderDispatchMsgTech = new OrderDispatch();
			orderDispatchMsgTech.setTechId(dispatchTechOld.getId());
			orderDispatchMsgTech.setTechPhone(dispatchTechOld.getPhone());
			orderDispatchMsgTechList.add(orderDispatchMsgTech);
			orderDispatchMsg.setTechList(orderDispatchMsgTechList);

			orderDispatchMsgList.add(orderDispatchMsg);

			//新增发消息技师列表-------------------------------
			OrderInfo orderCreateMsg = new OrderInfo();
			orderCreateMsg.setId(gasqInfo.getOrderId());
			orderCreateMsg.setOrderNumber(gasqInfo.getOrderNumber());

			List<OrderDispatch> orderCreateMsgTechList = new ArrayList<>();
			OrderDispatch orderCreateMsgTech = new OrderDispatch();
			orderCreateMsgTech.setTechId(dispatchTechNew.getId());
			orderCreateMsgTech.setTechPhone(dispatchTechNew.getPhone());
			orderCreateMsgTechList.add(orderCreateMsgTech);
			orderCreateMsg.setTechList(orderCreateMsgTechList);

			orderCreateMsgList.add(orderCreateMsg);

			//对接国安技师列表 ----------------------------
			OrderInfo orderSend = new OrderInfo();
			orderSend.setId(gasqInfo.getOrderId());
			orderSend.setOrderNumber(gasqInfo.getOrderNumber());

			List<OrderDispatch> orderSendTechList = new ArrayList<>();
			OrderDispatch orderSendTech = new OrderDispatch();
			orderSendTech.setTechId(dispatchTechNew.getId());
			orderSendTech.setTechPhone(dispatchTechNew.getPhone());
			orderSendTechList.add(orderSendTech);
			orderSend.setTechList(orderSendTechList);

			orderSendList.add(orderSend);
		}

		//排期修改
		TechScheduleInfo techScheduleInfoDel = new TechScheduleInfo();
		techScheduleInfoDel.setTechId(dispatchTechIdOld);//技师ID
		techScheduleInfoDel.setTypeId(groupId);
		techScheduleInfoDel.setType("master");
		techScheduleInfoDel.preUpdate();
		techScheduleDao.deleteScheduleByTypeIdTechId(techScheduleInfoDel);

		//排期
		TechScheduleInfo techScheduleInfo = new TechScheduleInfo();
		techScheduleInfo.setTechId(dispatchTechIdNew);//技师ID
		techScheduleInfo.setScheduleDate(DateUtils.getDateFirstTime(groupServiceTime));//日期
		int weekDay = DateUtils.getWeekNum(groupServiceTime);//周几
		techScheduleInfo.setScheduleWeek(weekDay);//日期（周一，周二。。。1,2,3,4,5,6,7）
		techScheduleInfo.setStartTime(groupServiceTime);//起始时段
		techScheduleInfo.setEndTime(groupFinishTime);//结束时段
		techScheduleInfo.setTypeId(groupId);//休假ID或订单ID或groupID
		techScheduleInfo.setType("master");//'holiday：休假  order：订单 master:组合订单',
		techScheduleInfo.setMasterId(masterId);
		techScheduleInfo.preInsert();
		techScheduleDao.insertSchedule(techScheduleInfo);

		HashMap<String,Object> map = new HashMap<>();
		map.put("orderSource",orderSource);
		map.put("orderDispatchMsgList", orderDispatchMsgList);
		map.put("orderCreateMsgList", orderCreateMsgList);
		map.put("orderSendList", orderSendList);
		return map;
	}

	/**
	 * 子订单  更换技师  改派保存按钮  不拆单  单次服务订单
	 * @param combinationOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> updateOrderTechDispatchSaveForOnce(CombinationOrderInfo combinationOrderInfo) {
		Double serviceHourRe = 0.0;
		String dispatchTechId = combinationOrderInfo.getTechId();//改派前技师ID
		List<String> techIdList = combinationOrderInfo.getTechIdList();//改派技师List
		OrderInfo orderInfo = orderInfoDao.get(combinationOrderInfo.getOrderId());//当前订单
		List<OrderDispatch> techList = orderInfoDao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		Date serviceTime = null;//服务时间
		Date finishTime = null;
		serviceTime = orderInfo.getServiceTime();//服务时间
		finishTime = orderInfo.getFinishTime();//完成时间

		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((DateUtils.getDistanceSecondOfTwoDate(serviceTime, finishTime)) * techList.size())/( techList.size() - 1 + techIdList.size());

		//新增、改派判断库存
		boolean flag = false;
		List<OrderDispatch> checkTechList = orderToolsService.listTechByGoodsAndTimeForCombination(combinationOrderInfo);
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

	public OrderInfo getOrderTypeByOrderId(CombinationOrderInfo combinationOrderInfo) {
		return orderInfoDao.get(combinationOrderInfo.getOrderId());
	}

}