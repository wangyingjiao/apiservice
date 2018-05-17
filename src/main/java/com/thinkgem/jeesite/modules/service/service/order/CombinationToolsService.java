/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationToolsService extends  CrudService<CombinationOrderDao, CombinationOrderInfo> {

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderCustomInfoDao orderCustomInfoDao;
	@Autowired
	OrderCustomAddressDao orderCustomAddressDao;
	@Autowired
	OrderAddressDao orderAddressDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	SerItemInfoDao serItemInfoDao;
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

	/**
	 * 根据商品信息和服务时间返回技师列表(增加技师)
	 *
	 * 传入参数
	 * @param orderInfo
	 * 列表查询条件 techName
	 * 订单ID      id
	 *
	 * 返回值
	 * @return List<OrderDispatch>
	 *
	 */
	public List<OrderDispatch> listTechByGoodsAndTime(OrderInfo orderInfo) {
		String serchTechName = orderInfo.getTechName();//技师列表查询条件
		//根据订单ID取得当前订单信息
		String orderId = orderInfo.getId();
		String orgId = orderInfo.getOrgId();//机构ID
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间
		String sortId = orderInfo.getGoodsSortId();
		boolean serchFullTech = orderInfo.getSerchFullTech();
		String serchNowOrderId = orderInfo.getSerchNowOrderId();

		// 根据订单ID返回分类ID
		if(StringUtils.isBlank(sortId)) {
			sortId = orderInfoDao.getSortIdByOrderId(orderInfo);
		}
		// 根据机构ID、分类ID返回技能ID
		String skillId = getSkillIdByOrgSort(orgId, sortId);

		// 取得符合条件的技师
		List<OrderDispatch> techList = listTechByStationSkillOrder(stationId, skillId, orderId, serchFullTech, serchTechName);
		List<String> techIdList = new ArrayList<>();
		List<OrderDispatch> techListPart = new ArrayList<>();//兼职
		List<String> techIdListPart = new ArrayList<>();
		List<OrderDispatch> techListFull = new ArrayList<>();//全职
		List<String> techIdListFull = new ArrayList<>();
		if(null != techList){
			for(OrderDispatch orderDispatch : techList){
				if("part_time".equals(orderDispatch.getJobNature())){
					techListPart.add(orderDispatch);
					techIdListPart.add(orderDispatch.getTechId());
				}else{
					techListFull.add(orderDispatch);
					techIdListFull.add(orderDispatch.getTechId());
				}
				techIdList.add(orderDispatch.getTechId());
			}
		}
		List<String> checkWorkTechIdList = new ArrayList<String>();
		List<String> checkHolidyTechIdList = new ArrayList<String>();
		List<String> checkOrderTechIdList = new ArrayList<String>();

		if(techIdListFull != null && techIdListFull.size() > 0) {
			//（3）考虑技师的工作时间
			//取得当前机构下工作时间包括服务时间的技师
			List<ServiceTechnicianWorkTime> techWorkList = listTechWorkByTechsTime(techIdListFull, serviceTime);
			if (techWorkList != null) {
				for (ServiceTechnicianWorkTime techWork : techWorkList) {
					//Date techWorkStartTime = techWork.getStartTime();//工作开始时间
					//Date techWorkEndTime = techWork.getEndTime();//工作结束时间
					Date techWorkStartTime = DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(techWork.getStartTime(), "HH:mm:ss"));//工作开始时间
					Date techWorkEndTime = DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd")
							+ " " + DateUtils.formatDate(techWork.getEndTime(), "HH:mm:ss"));//工作结束时间
					if (techWorkStartTime.before(techWorkEndTime) && serviceTime.before(finishTime)) {
						// 订单时间在工作时间内,可以下单
						if ((techWorkStartTime.before(serviceTime) || techWorkStartTime.compareTo(serviceTime) == 0)
								&& (techWorkEndTime.after(serviceTime) || techWorkEndTime.compareTo(serviceTime) == 0)) {
							checkWorkTechIdList.add(techWork.getTechId());
						}
					}
				}
			}

			//（4）考虑技师的休假时间
			List<TechScheduleInfo> techHolidyList = listTechScheduleByTechsTime(techIdListFull, serviceTime, "holiday");
			if (techHolidyList != null) {
				for (TechScheduleInfo techHolidy : techHolidyList) {
					Date techHolidyStartTime = techHolidy.getStartTime();//休假开始时间
					Date techHolidyEndTime = techHolidy.getEndTime();//休假结束时间
					if (techHolidyStartTime.before(techHolidyEndTime) && serviceTime.before(finishTime)) {
						// 订单时间和休假时间有重合,不可下单
						if (!DateUtils.checkDatesRepeat(techHolidyStartTime, techHolidyEndTime, serviceTime, finishTime)) {
							checkHolidyTechIdList.add(techHolidy.getTechId());
						}
					}
				}
			}
		}

		if(techIdList != null && techIdList.size() > 0) {
			// 考虑技师的订单
			List<TechScheduleInfo> techOrderList = listTechScheduleByTechsTime(techIdList, serviceTime, "order");
			if (techOrderList != null) {
				int intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
				int intervalTimeE = 0;//必须间隔时间 秒
				if (11 <= Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) &&
						Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) < 14) {
					//可以接单的时间则为：40分钟+路上时间+富余时间
					intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time"));
				} else {
					//可以接单的时间则为：路上时间+富余时间
					intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));
				}
				Date checkServiceTime = DateUtils.addSecondsNotDayB(serviceTime, -intervalTimeS);
				Date checkFinishTime = DateUtils.addSecondsNotDayE(finishTime, intervalTimeE);

				for (TechScheduleInfo techOrder : techOrderList) {
					if(!techOrder.getTypeId().equals(serchNowOrderId)) {//当前订单不考虑
						Date techOrderStartTime = techOrder.getStartTime();//订单开始时间
						Date techOrderEndTime = techOrder.getEndTime();//订单结束时间
						if (techOrderStartTime.before(techOrderEndTime) && serviceTime.before(finishTime)) {
							if (11 <= Integer.parseInt(DateUtils.formatDate(techOrderEndTime, "HH")) &&
									Integer.parseInt(DateUtils.formatDate(techOrderEndTime, "HH")) < 14) {
								techOrderEndTime = DateUtils.addSeconds(techOrderEndTime, Integer.parseInt(Global.getConfig("order_eat_time")));
							}
							// 订单时间和已有订单时间有重合,不可下单
							if (!DateUtils.checkDatesRepeat(checkServiceTime, checkFinishTime, techOrderStartTime, techOrderEndTime)) {
								checkOrderTechIdList.add(techOrder.getTechId());
							}
						}
					}
				}
			}
		}

		// 返回的技师列表
		List<OrderDispatch> techListRe = new ArrayList<>();
		if(techIdList != null){
			for(OrderDispatch tech : techList){
				// 全职技师
				if(techIdListFull.contains(tech.getTechId())){
					if(checkWorkTechIdList.contains(tech.getTechId())// 有工作时间
						&& !checkHolidyTechIdList.contains(tech.getTechId())// 没有休假
							&& !checkOrderTechIdList.contains(tech.getTechId())){// 没有订单
						techListRe.add(tech);
					}
				}else{
					// 兼职技师
					if(!checkOrderTechIdList.contains(tech.getTechId())){// 没有订单
						techListRe.add(tech);
					}
				}
			}
		}
		return techListRe;
	}

	/**
	 * 根据技师List、时间 取得工作时间
	 * @param techIdListFull
	 * @param serviceTime
	 * @return
	 */
	private List<TechScheduleInfo> listTechScheduleByTechsTime(List<String> techIdListFull, Date serviceTime, String type) {
		TechScheduleInfo serchInfo = new TechScheduleInfo();
		serchInfo.setType(type);
		serchInfo.setScheduleDate(DateUtils.getDateFirstTime(serviceTime));
		serchInfo.setTechIdList(techIdListFull);
		List<TechScheduleInfo> list = orderInfoDao.listTechScheduleByTechsTime(serchInfo);
		return list;
	}
	/**
	 * 根据技师、时间 取得工作时间
	 * @param techId
	 * @param serviceTime
	 * @return
	 */
	public List<TechScheduleInfo> listTechScheduleByTechTime(String techId, Date serviceTime, String type) {
		List<String> techIdList = new ArrayList<>();
		techIdList.add(techId);
		return listTechScheduleByTechsTime(techIdList, serviceTime, type);
	}

	/**
	 * 根据技师List、时间 取得工作时间
	 * @param techIdListFull
	 * @param serviceTime
	 * @return
	 */
	private List<ServiceTechnicianWorkTime> listTechWorkByTechsTime(List<String> techIdListFull, Date serviceTime) {
		int weekNum = DateUtils.getWeekNum(serviceTime);
		ServiceTechnicianWorkTime serchInfo = new ServiceTechnicianWorkTime();
		serchInfo.setWeek(Integer.toString(weekNum));
		serchInfo.setTechIdList(techIdListFull);
		List<ServiceTechnicianWorkTime> list = orderInfoDao.listTechWorkByTechsTime(serchInfo);
		return list;
	}

	/**
	 * 取得符合条件的技师
	 * @param stationId 服务站
	 * @param skillId 技能
	 * @param orderId 订单ID    派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
	 * @param serchTechName  列表查询条件
	 * @return
	 */
	public List<OrderDispatch> listTechByStationSkillOrder(String stationId, String skillId, String orderId, boolean serchFullTech, String serchTechName){
		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		if(StringUtils.isNotBlank(orderId)) {
			//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
			serchInfo.setOrderId(orderId);
		}
		if(serchFullTech){
			//自动派单 全职 ; 手动派单没有条件
			serchInfo.setJobNature("full_time");
		}
		serchInfo.setTechName(serchTechName);//查询条件
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);
		return  techList;
	}

	/**
	 * 根据机构ID、分类ID返回技能ID
	 * @param orgId 机构ID
	 * @param sortId 分类ID
	 * @return
	 */
	public String getSkillIdByOrgSort(String orgId,String sortId){
		String skillId = "";
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(sortId);
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过机构ID分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		return  skillId;
	}

}