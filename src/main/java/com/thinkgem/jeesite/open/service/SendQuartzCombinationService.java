/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;


import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.sys.dao.SysJointLogDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class SendQuartzCombinationService extends CrudService<OrderInfoDao, OrderInfo> {
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
    OrderCombinationGasqDao orderCombinationGasqDao;
	@Autowired
    SysJointLogDao sysJointLogDao;
    @Autowired
    OrderDispatchDao orderDispatchDao;
    @Autowired
    OrderGoodsDao orderGoodsDao;
    @Autowired
    TechScheduleDao techScheduleDao;
    @Autowired
    private ServiceTechnicianInfoDao technicianInfoDao;

    @Autowired
    private MessageInfoService messageInfoService;

    /**
	 * 待执行对接数据
	 */
	@Transactional(readOnly = false)
	public void doCreartCombinationOrder() {
		List<CombinationOrderInfo> list = combinationOrderDao.listCombinationWaitCreart();
		if(list==null || list.size()==0){
			System.out.println("未找到待生成组合订单数据");
		}else{
		    //今天是周天
			int todayWeek = DateUtils.getWeekNum(new Date());
			if(todayWeek != 7){
				return;
			}

			List<Date> fristWeekDayList = DateUtils.fristWeekDayList();//第一周日期
            Timestamp fristServiceTimeStart = new Timestamp(fristWeekDayList.get(0).getTime());
            Date endDate = DateUtils.parseDate(DateUtils.formatDate(fristWeekDayList.get(fristWeekDayList.size()-1),"yyyy-MM-dd") + " 23:59:59");
            Timestamp fristServiceTimeEnd = new Timestamp(endDate.getTime());

			List<Date> lastWeekDayList = DateUtils.lastWeekDayList();//第二周日期

            List<OrderInfo> orderInfoList = new ArrayList<>();
			for(CombinationOrderInfo info : list){
                //第二周是否有订单
                OrderInfo lastSerchInfo = new OrderInfo();
                lastSerchInfo.setMasterId(info.getMasterId());
                lastSerchInfo.setServiceTimeStart(fristServiceTimeStart);
                lastSerchInfo.setServiceTimeEnd(fristServiceTimeEnd);
                List<OrderInfo> lastOrderList = orderInfoDao.listOrderByMasterAndDate(lastSerchInfo);

			    //服务时间段
                List<OrderCombinationFrequencyInfo> frequencyList= combinationOrderDao.listFrequencyByMasterId(info);
                info.setFreList(frequencyList);

				if("week_one".equals(info.getServiceFrequency())){//服务频次 week_one:1周1次
					if(lastOrderList==null || lastOrderList.size()==0){//已有订单就不生成
                        List<Date> dateList = creartCombinationOrderDate(info, lastWeekDayList);
                        orderInfoList = creartCombinationOrder(info, dateList);
                    }
				}else if("week_some".equals(info.getServiceFrequency())){//服务频次 week_some:1周多次
                    List<Date> dateList = creartCombinationOrderDate(info, lastWeekDayList);
                    orderInfoList = creartCombinationOrder(info, dateList);
				}else if("two_week_one".equals(info.getServiceFrequency())){//服务频次 two_week_one:2周1次
                    Date serviceEnd = info.getServiceEnd();
                    if(!fristWeekDayList.contains(serviceEnd)){
                        List<Date> dateList = creartCombinationOrderDate(info, lastWeekDayList);
                        orderInfoList = creartCombinationOrder(info, dateList);
                    }
				}
			}
            try {
                if(orderInfoList != null){
                    for(OrderInfo orderInfo : orderInfoList){
                        User user = new User();
                        user.setId("gasq001");
                        orderInfo.setCreateBy(user);
                        messageInfoService.insert(orderInfo, "orderCreate");//新增
                    }
                }
            }catch (Exception e){
                logger.error("订单创建-推送消息失败-系统异常");
            }

            try {
                if(orderInfoList!=null && orderInfoList.size()>0) {
                    CombinationOrderInfo combinationInfo = new CombinationOrderInfo();
                    combinationInfo.setMasterId(orderInfoList.get(0).getMasterId());
                    combinationInfo.setJointGroupId(orderInfoList.get(0).getJointGroupId());
                    combinationInfo.setOrderInfoList(orderInfoList);
                    OpenSendUtil.updateGroupOrderInfo(combinationInfo);
                }
            }catch (Exception e){
                logger.error("订单创建-对接失败-系统异常");
            }
		}
		return;
	}

	private List<Date> creartCombinationOrderDate(CombinationOrderInfo info, List<Date> dateList) {
        if(info==null || dateList==null || dateList.size()==0){
            return null;
        }

        List<OrderCombinationFrequencyInfo> freList = info.getFreList();
        Date serviceStart = info.getServiceStart();
        int num = info.getBespeakTotal();

        List<Date> list = new ArrayList<>();
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
                    //判断今天是否有订单
                    OrderInfo serchInfo = new OrderInfo();
                    serchInfo.setMasterId(info.getMasterId());
                    serchInfo.setServiceTimeStart(new Timestamp(DateUtils.getDateFirstTime(dateMap).getTime()));
                    serchInfo.setServiceTimeEnd(new Timestamp(DateUtils.getDateLastTime(dateMap).getTime()));
                    List<OrderInfo> orderList = orderInfoDao.listOrderByMasterAndDate(serchInfo);
                    if(orderList==null || orderList.size()==0){
                        list.add(dateMap);
                    }
                }
            }
        }

        //排序
        Collections.sort(list);
        //最大数量
        int maxNum = list.size();
        if(num < maxNum){
            maxNum = num;
        }
        List<Date> listDate = new ArrayList<>();
        for(int i=0;i<maxNum;i++){
            listDate.add(list.get(i));
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
	private List<OrderInfo> creartCombinationOrder(CombinationOrderInfo info, List<Date> dateList) {
	    if(info==null || dateList==null || dateList.size()==0){
	        return null;
        }
        List<OrderCombinationFrequencyInfo> freList = info.getFreList();
        String masterId = info.getMasterId();
        int serviceNum = info.getServiceNum();
        Double serviceHour = info.getServiceHour();
        String techId = info.getTechId();
        String combinationGoodsId = info.getCombinationGoodsId();

        //返回时间数组的最后日期
        Date serviceEnd = getLastDate(dateList);
        // 更新 组合订单信息order_combination_info
        CombinationOrderInfo updateCombinationOrderInfo = new CombinationOrderInfo();
        updateCombinationOrderInfo.setMasterId(masterId);
        updateCombinationOrderInfo.setServiceEnd(serviceEnd);
        combinationOrderDao.updateServiceEndByMasterId(updateCombinationOrderInfo);

        List<OrderInfo> orderList = new ArrayList<>();
		for(Date creartDate : dateList) {//每天生成一组订单

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

			List<Date> listDate = DateUtils.listTimeByFrequency(creartDate, serviceStartBeginTime, serviceNum, serviceHour);
			//根据组合商品ID返回子商品信息
			OrderGoods goods = getOrderGoodsByCombination(combinationGoodsId);

			List<OrderInfo> orderInfoList = new ArrayList<>();
			// 新增 子商品信息
			for (Date startDate : listDate) {
				HashMap<String, Object> map = combinationCreateOrder(startDate, info, goods);
				OrderInfo orderInfo = (OrderInfo) map.get("orderInfoMsg");
                orderInfo.setJointGroupId(info.getJointGroupId());
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

            // tech_schedule  服务技师排期 --------------------------------------------------------------
            String startTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
                    +  DateUtils.formatDate(serviceStartBeginTime,"HH:mm" + ":00");
            Date startTime = DateUtils.parseDate(startTimeStr);

            String endTimeStr = DateUtils.formatDate(creartDate,"yyyy-MM-dd") + " "
                    +  DateUtils.formatDate(serviceStartEndTime,"HH:mm" + ":00");
            Date endTime = DateUtils.parseDate(startTimeStr);

            openCreateForTechSchedule(techId, startTime, endTime, groupId, masterId);

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