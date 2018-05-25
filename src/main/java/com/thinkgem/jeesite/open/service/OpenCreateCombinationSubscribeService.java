/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

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
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderToolsService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.open.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OpenCreateCombinationSubscribeService extends CrudService<OrderInfoDao, OrderInfo> {

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
	OrderCombinationGasqDao orderCombinationGasqDao;

	@Autowired
	private OrderToolsService orderToolsService;

	/**
	 * 对接接口 订单创建
	 * @param info
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> openCreate(OpenCreateForSubscribeRequest info) {
		OpenCreateForSubscribeResponse response = new OpenCreateForSubscribeResponse();
		if(null == info){
			throw new ServiceException("未接收到订单信息!");
		}

		String platform = info.getPlatform();// 对接平台代号 默认值 gasq
		List<String>  remark_pic = info.getRemark_pic();//订单备注(用户备注)
		String remark = info.getRemark();//订单备注(用户备注)
		String service_time = info.getService_time();//服务时间
		if(StringUtils.isBlank(service_time)){
			throw new ServiceException("服务时间不能为空");
		}
		List<String> gasq_order_sn = info.getGasq_order_sn();//国安社区订单SN
		if(gasq_order_sn==null || gasq_order_sn.size()==0){
			throw new ServiceException("国安社区订单SN不能为空");
		}
		int serviceNum =gasq_order_sn.size();
		String group_id = info.getGroup_id();//订单组ID
		if(StringUtils.isBlank(group_id)){
			throw new ServiceException("国安社区订单组ID不能为空");
		}

		CombinationOrderInfo combinationInfo = combinationOrderDao.getCombinationByGroupId(group_id);
		if(combinationInfo == null){
			throw new ServiceException("未找到组合订单信息");
		}
		//根据组合商品ID返回子商品信息
		OrderGoods goods = getOrderGoodsByCombination(combinationInfo.getCombinationGoodsId());

		Date groupServiceTime = DateUtils.parseDate(service_time);
		//获取组合信息
		double serviceHour =combinationInfo.getServiceHour();//单次建议服务时长
		Double serviceSecond = (serviceHour * serviceNum * 3600);
		Date groupFinishTime = DateUtils.addSecondsNotDayE(groupServiceTime, serviceSecond.intValue());

		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setOrgId(combinationInfo.getOrgId());
		serchOrderInfo.setStationId(combinationInfo.getStationId());
		serchOrderInfo.setServiceTime(groupServiceTime);
		serchOrderInfo.setFinishTime(groupFinishTime);
		serchOrderInfo.setGoodsSortId(goods.getSortId());
		List<OrderDispatch> techList = openCreateForOrderFindDispatchList(serchOrderInfo, 1);
		if(techList==null || techList.size()==0){
			throw new ServiceException("未找到服务人员信息");
		}
		OrderDispatch techInfo = techList.get(0);
		combinationInfo.setTechId(techInfo.getTechId());
		combinationInfo.setTechPhone(techInfo.getTechPhone());

		List<Date> listDate = DateUtils.listTimeByFrequency(groupServiceTime,serviceNum,serviceHour);

		List<OrderInfo> orderInfoList = new ArrayList<>();
		// 新增 子商品信息
		for(int i=0;i<listDate.size();i++){
			String gasqOrderSn = gasq_order_sn.get(i);
			Date startDate =listDate.get(i);
			HashMap<String,Object> map = combinationCreateOrder(startDate, combinationInfo, goods, gasqOrderSn);
			OrderInfo orderInfo = (OrderInfo)map.get("orderInfoMsg");
			orderInfoList.add(orderInfo);
		}

		List<OpenCreateForSubscribeServiceInfo> serviceList = new ArrayList<>();
		String groupId = IdGen.uuid();
		for(int i=0; i<orderInfoList.size(); i++){
			String gasqOrderSn = gasq_order_sn.get(i);
			OrderCombinationGasqInfo combinationGasqInfo = new OrderCombinationGasqInfo();
			combinationGasqInfo.setMasterId(combinationInfo.getMasterId());
			combinationGasqInfo.setJointOrderSn(gasqOrderSn);
			combinationGasqInfo.setOrderGroupId(groupId);
			combinationGasqInfo.setOrderNumber(orderInfoList.get(i).getOrderNumber());
			User user = new User();
			user.setId("gasq001");
			combinationGasqInfo.setUpdateBy(user);
			combinationGasqInfo.setUpdateDate(new Date());
			orderCombinationGasqDao.updateOrderGroupByMasterJointSn(combinationGasqInfo);

			orderInfoList.get(i).setJointOrderId(gasqOrderSn);

			OpenCreateForSubscribeServiceInfo serviceInfo = new OpenCreateForSubscribeServiceInfo();
			serviceInfo.setGasq_order_sn(gasqOrderSn);
			serviceInfo.setService_order_sn(orderInfoList.get(i).getOrderNumber());
			serviceList.add(serviceInfo);
		}

		// tech_schedule  服务技师排期 ------------------------------------------------------------------------------
		openCreateForTechSchedule( techInfo.getTechId(), groupServiceTime, groupFinishTime , groupId, combinationInfo.getMasterId());

		//更新已预约次数
		CombinationOrderInfo updateCombinationInfo = new CombinationOrderInfo();
		updateCombinationInfo.setMasterId(combinationInfo.getMasterId());
		updateCombinationInfo.setServiceNum(serviceNum);
		combinationOrderDao.updateBespeakByMasterId(updateCombinationInfo);

		HashMap<String,Object> map = new HashMap<>();
		map.put("orderInfoMsg",orderInfoList);

		//------------------------------------------------------------------------------------------------
		response = new OpenCreateForSubscribeResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_info(serviceList);
		map.put("response",response);
		return map;
	}

	/**
	 * 订单创建 - 子订单信息 - 获取派单技师
	 * @param orderInfo
	 * @return
	 */
	private List<OrderDispatch> openCreateForOrderFindDispatchList(OrderInfo orderInfo,int techDispatchNum){
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		OrderInfo serchOrderInfo = new OrderInfo();
		serchOrderInfo.setOrgId(orderInfo.getOrgId());
		serchOrderInfo.setStationId(orderInfo.getStationId());
		serchOrderInfo.setServiceTime(orderInfo.getServiceTime());
		serchOrderInfo.setFinishTime(orderInfo.getFinishTime());
		serchOrderInfo.setGoodsSortId(orderInfo.getGoodsSortId());
		serchOrderInfo.setSerchFullTech(true);
		List<OrderDispatch> techListRe = orderToolsService.listTechByGoodsAndTime(serchOrderInfo);
		if(techListRe != null && techListRe.size() != 0){
			if(techListRe.size() < techDispatchNum){//技师数量不够
				throw new ServiceException("技师数量不满足当前商品的需求人数");
			}

			//一个自然月内，根据当前服务站内各个技师的订单数量，优先分配给订单数量少的技师
			List<String> serchTechIds = new ArrayList<>();
			for(OrderDispatch tech :techListRe){
				serchTechIds.add(tech.getTechId());
			}
			Date monthFristDay = DateUtils.getMonthFristDay(serviceTime);
			Date monthLastDay = DateUtils.getMonthLastDay(serviceTime);
			OrderDispatch serchTechInfo = new OrderDispatch();
			serchTechInfo.setStartTime(monthFristDay);
			serchTechInfo.setEndTime(monthLastDay);
			serchTechInfo.setTechIds(serchTechIds);
			List<OrderDispatch> techListOrderByNum = orderInfoDao.getTechListOrderByNum(serchTechInfo);//订单数量少的技师
			List<OrderDispatch> dispatchTechs = new ArrayList<>();
			for(int i=0;i<techDispatchNum;i++){//订单数量少的技师  订单需要技师数量
				dispatchTechs.add(techListOrderByNum.get(i));
			}
			return dispatchTechs;
		}else {
			throw new ServiceException("技师数量不满足当前商品的需求人数");
		}
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

	private HashMap<String,Object> combinationCreateOrder(Date startDate, CombinationOrderInfo combinationInfo, OrderGoods goods,String gasqOrderSn) {

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		try{
			orderInfo = openCreateForOrder(startDate, combinationInfo, goods, gasqOrderSn);
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
		User user = new User();
		user.setId("gasq001");
		techScheduleInfo.setId(IdGen.uuid());
		techScheduleInfo.setCreateBy(user);
		techScheduleInfo.setCreateDate(new Date());
		techScheduleInfo.setUpdateBy(user);
		techScheduleInfo.setUpdateDate(techScheduleInfo.getCreateDate());
		techScheduleDao.insertSchedule(techScheduleInfo);
	}

	/**
	 * 订单创建 - 订单服务关联
	 * @param goods
	 */
	private void openCreateForGoods(OrderInfo orderInfo, OrderGoods goods) {
		goods.setOrderId(orderInfo.getId());//订单ID
		User user = new User();
		user.setId("gasq001");
		goods.setId(IdGen.uuid());
		goods.setCreateBy(user);
		goods.setCreateDate(new Date());
		goods.setUpdateBy(user);
		goods.setUpdateDate(goods.getCreateDate());
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
		User user = new User();
		user.setId("gasq001");
		orderDispatch.setId(IdGen.uuid());
		orderDispatch.setCreateBy(user);
		orderDispatch.setCreateDate(new Date());
		orderDispatch.setUpdateBy(user);
		orderDispatch.setUpdateDate(orderDispatch.getCreateDate());
		orderDispatchDao.insert(orderDispatch);
	}

	/**
	 * 订单创建 - 子订单信息
	 * @param startDate
	 * @param combinationInfo
	 * @param goods
	 * @return
	 */
	private OrderInfo openCreateForOrder(Date startDate, CombinationOrderInfo combinationInfo, OrderGoods goods, String gasqOrderSn) {
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
		orderInfo.setJointOrderId(gasqOrderSn);
		User user = new User();
		user.setId("gasq001");
		orderInfo.setId(IdGen.uuid());
		orderInfo.setCreateBy(user);
		orderInfo.setCreateDate(new Date());
		orderInfo.setUpdateBy(user);
		orderInfo.setUpdateDate(orderInfo.getCreateDate());
		orderInfoDao.insert(orderInfo);

		return orderInfo;
	}
}