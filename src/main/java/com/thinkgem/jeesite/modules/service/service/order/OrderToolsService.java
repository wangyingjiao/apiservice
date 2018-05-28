/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.BeanUtils;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenCreateResponse;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderToolsService extends CrudService<OrderInfoDao, OrderInfo> {

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
		Date finishTime = orderInfo.getSuggestFinishTime();//完成时间
		String sortId = orderInfo.getGoodsSortId();
		boolean serchFullTech = orderInfo.getSerchFullTech();
		String serchNowOrderId = orderInfo.getSerchNowOrderId();

		// 根据订单ID返回分类ID
		if(StringUtils.isBlank(sortId)) {
			sortId = dao.getSortIdByOrderId(orderInfo);
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
	 * 根据商品信息和服务时间返回技师列表(web组合订单更换时间中 更换技师时 展示原来的技师)
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> listTechByGoodsAndTimeAndOldTech(OrderInfo orderInfo) {
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
			sortId = dao.getSortIdByOrderId(orderInfo);
		}
		// 根据机构ID、分类ID返回技能ID
		String skillId = getSkillIdByOrgSort(orgId, sortId);

		// 取得符合条件的技师
		List<OrderDispatch> techList = listTechByStationSkillOrderAndOldTech(stationId, skillId, orderId, serchFullTech, serchTechName);
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
	 * 根据商品信息和服务时间返回技师列表(web组合订单更换时间中 更换技师时 展示原来的技师)
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> listTechByGoodsAndTimeAndOldTechCom(OrderInfo orderInfo) {
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
			sortId = dao.getSortIdByOrderId(orderInfo);
		}
		// 根据机构ID、分类ID返回技能ID
		String skillId = getSkillIdByOrgSort(orgId, sortId);

		// 取得符合条件的技师
		List<OrderDispatch> techList = listTechByStationSkillOrderAndOldTech(stationId, skillId, orderId, serchFullTech, serchTechName);
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
			List<TechScheduleInfo> techOrderList = listTechScheduleByTechsTime(techIdList, serviceTime, "master");
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
	public List<TechScheduleInfo> listTechScheduleByTechsTime(List<String> techIdListFull, Date serviceTime, String type) {
		TechScheduleInfo serchInfo = new TechScheduleInfo();
		serchInfo.setType(type);
		serchInfo.setScheduleDate(DateUtils.getDateFirstTime(serviceTime));
		serchInfo.setTechIdList(techIdListFull);
		List<TechScheduleInfo> list = dao.listTechScheduleByTechsTime(serchInfo);
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
	public List<ServiceTechnicianWorkTime> listTechWorkByTechsTime(List<String> techIdListFull, Date serviceTime) {
		int weekNum = DateUtils.getWeekNum(serviceTime);
		ServiceTechnicianWorkTime serchInfo = new ServiceTechnicianWorkTime();
		serchInfo.setWeek(Integer.toString(weekNum));
		serchInfo.setTechIdList(techIdListFull);
		List<ServiceTechnicianWorkTime> list = dao.listTechWorkByTechsTime(serchInfo);
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
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);
		return  techList;
	}

	/**
	 * 取得符合条件的技师
	 * @param stationId 服务站
	 * @param skillId 技能
	 * @param orderId 订单ID    派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
	 * @param serchTechName  列表查询条件
	 * @return
	 */
	public List<OrderDispatch> listTechByStationSkillOrderAndOldTech(String stationId, String skillId, String orderId, boolean serchFullTech, String serchTechName){
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
		List<OrderDispatch> techList = dao.getTechListBySkillIdAndOldTech(serchInfo);
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
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过机构ID分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		return  skillId;
	}

	/**
	 * 根据商品信息返回派人数量
	 * @param goodsInfoList
	 * @return
	 */
	public int getTechDispatchNumByGoodsList(List<OrderGoods> goodsInfoList){
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				if(goods.getSortId().length()<3){
					continue;
				}
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				if(0 == goodsNum){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				Double convertHours = goods.getConvertHours();		// 折算时长
				if(convertHours == null || 0 == convertHours){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				if(0 == startPerNum){
					startPerNum =1;
				}
				int cappinPerNum = goods.getCappingPerNum();		//封项人数
				if(0 == cappinPerNum){
					cappinPerNum = 30;
				}

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = 0.0;
				if(convertHours!=null) {
					totalTime = convertHours * goodsNum;//商品需要时间
				}
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.subtract(b2).divide(b2, 0, BigDecimal.ROUND_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum) {//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}
		} else {
			logger.error("未找到当前订单服务商品信息");
		}
		return  techDispatchNum;
	}

	/**
	 * 根据商品信息返回建议服务时长
	 * @param goodsInfoList
	 * @return
	 */
	public double getServiceHourByGoodsList(List<OrderGoods> goodsInfoList){
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				if(goods.getSortId().length()<3){
					continue;
				}
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				if(0 == goodsNum){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				Double convertHours = goods.getConvertHours();		// 折算时长
				if(convertHours == null || 0 == convertHours){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				if(0 == startPerNum){
					startPerNum =1;
				}
				int cappinPerNum = goods.getCappingPerNum();		//封项人数
				if(0 == cappinPerNum){
					cappinPerNum = 30;
				}

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = 0.0;
				if(convertHours!=null) {
					totalTime = convertHours * goodsNum;//商品需要时间
				}
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					//addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
					addTechNum= (b1.subtract(b2).divide(b2, 0, BigDecimal.ROUND_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum) {//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			logger.error("未找到当前订单服务商品信息");
		}
		return  serviceHour;
	}

	/**
	 * 返回订单除补单商品外的分类ID
	 * @param goodsInfoList
	 * @return
	 */
	public String getNotFullGoodsSortId(List<OrderGoods> goodsInfoList) {
		String sortId = "";
		if(goodsInfoList != null && goodsInfoList.size() != 0 ) {
			for (OrderGoods goods : goodsInfoList) {
				if(goods.getSortId().length() >= 3){
					return  goods.getSortId();
				}
			}
		}
		return sortId;
	}

	public List<TechScheduleInfo> listTechScheduleByTechWeekTime(String techId, int week, Date date, String holiday) {
		TechScheduleInfo serchInfo = new TechScheduleInfo();
		serchInfo.setTechId(techId);
		serchInfo.setScheduleWeek(week);
		serchInfo.setScheduleDate(DateUtils.getDateFirstTime(date));
		serchInfo.setType(holiday);
		List<TechScheduleInfo> list = dao.listTechScheduleByTechWeekTime(serchInfo);
		return list;
	}
}