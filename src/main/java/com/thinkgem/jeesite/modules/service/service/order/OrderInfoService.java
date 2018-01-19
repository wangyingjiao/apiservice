/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.math.BigDecimal;
import java.util.*;

import com.google.common.collect.Sets;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.AppFailResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.map.GaoDeMapUtil;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoodsTypeHouse;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderInfoDao;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderInfoService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
    BasicServiceStationDao basicServiceStationDao;
	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;

	public OrderInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderInfo> findList(OrderInfo orderInfo) {
		return super.findList(orderInfo);
	}

	//app查询详情
	public OrderInfo appFormData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		orderInfo.setNowId(info.getNowId());
		OrderGoods goodsInfo = new OrderGoods();
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());
			goodsInfo.setGoods(goodsInfoList);
		}
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_HOST");
		//商品图片
		String pics = dao.appGetPics(orderInfo.getId());
		List<String> picl = (List<String>) JsonMapper.fromJsonString(pics, ArrayList.class);
		goodsInfo.setPicture(ossHost+picl.get(0));
		//app的技师列表 appTechList
		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		for(OrderGoods orderGoods : goodsInfoList){
			String dj = orderGoods.getPayPrice();//商品单价
			int num = orderGoods.getGoodsNum();//商品数量
			BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
			orderGoods.setPayPrice(price.toString());//总价
		}
		orderInfo.setGoodsInfo(goodsInfo);
		orderInfo.setTechList(techList);
		//app其他技师
		List<AppServiceTechnicianInfo> appTechList=new ArrayList<AppServiceTechnicianInfo>();
		for (OrderDispatch apt:techList){
			ServiceTechnicianInfo temInfo=new ServiceTechnicianInfo();
			temInfo.setId(apt.getTechId());
			AppServiceTechnicianInfo technicianById = serviceTechnicianInfoDao.getTechnicianById(temInfo);
			technicianById.setId(apt.getTechId());
			if (!technicianById.getId().equals(orderInfo.getNowId())){
				appTechList.add(technicianById);
			}
		}
		orderInfo.setAppTechList(appTechList);
		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(null != customerRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		if(null != businessRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic,ArrayList.class);
			orderInfo.setBusinessRemarkPics(pictureDetails);
		}
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			orderInfo.setShopRemarkPics(pictureDetails);
		}
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if(null != orderRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			orderInfo.setOrderRemarkPics(pictureDetails);
		}
		return orderInfo;
	}

	public OrderInfo formData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			OrderGoods goodsInfo = new OrderGoods();
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());

			for(OrderGoods orderGoods : goodsInfoList){
				String dj = orderGoods.getPayPrice();//商品单价
				int num = orderGoods.getGoodsNum();//商品数量
				BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
				orderGoods.setPayPrice(price.toString());//总价
			}

			goodsInfo.setGoods(goodsInfoList);
			orderInfo.setGoodsInfo(goodsInfo);
		}

		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		orderInfo.setTechList(techList);

		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(null != customerRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		if(null != businessRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic,ArrayList.class);
			orderInfo.setBusinessRemarkPics(pictureDetails);
		}
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			orderInfo.setShopRemarkPics(pictureDetails);
		}
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if(null != orderRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			orderInfo.setOrderRemarkPics(pictureDetails);
		}
		return orderInfo;
	}

	public Page<OrderInfo> findPage(Page<OrderInfo> page, OrderInfo orderInfo) {
		orderInfo.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
		Page<OrderInfo> pageResult = super.findPage(page, orderInfo);
		return pageResult;
	}
	
	@Transactional(readOnly = false)
	public void save(OrderInfo orderInfo) {
		if(StringUtils.isBlank(orderInfo.getId())){
			//新增订单

		}


		super.save(orderInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderInfo orderInfo) {
		super.delete(orderInfo);
	}

	/**
	 * 请求订单列表时返回查询条件服务机构下拉列表
	 * @return
	 */
	public List<BasicOrganization> findOrganizationList() {
		BasicOrganization organization = new BasicOrganization();
		organization.getSqlMap().put("dsf", dataOrganFilter(UserUtils.getUser(), "a"));
		return dao.findOrganizationList(organization);
	}

	/**
	 * 取消订单
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public void cancelData(OrderInfo orderInfo) {
		dao.cancelData(orderInfo);
	}

	/**
	 * 增加技师
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> addTech(OrderInfo orderInfo) {
		String techName = orderInfo.getTechName();
		List<OrderGoods> goodsInfoList = null;
		orderInfo = get(orderInfo.getId());//当前订单
		goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息

		if(goodsInfoList == null || goodsInfoList.size() == 0 ){
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间

		List<OrderDispatch> techListRe = new ArrayList<>();

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = dao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		//serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		serchInfo.setTechName(techName);
		serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);
		List<OrderDispatch> techListPart = new ArrayList<>();//兼职
		List<OrderDispatch> techListFull = new ArrayList<>();//全职
		if(null != techList){
			for(OrderDispatch orderDispatch : techList){
				if("part_time".equals(orderDispatch.getJobNature())){
					techListPart.add(orderDispatch);
				}else{
					techListFull.add(orderDispatch);
				}
			}
		}

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

		List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
		List<String> beforTimeCheckTechIdList = new ArrayList<String>();
		if(techListFull != null){
			for(OrderDispatch tech : techListFull){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
					beforTimeCheckTechList.add(tech);
					beforTimeCheckTechIdList.add(tech.getTechId());
				}
			}
		}

		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表
			List<OrderDispatch> maybeTechList = new ArrayList<OrderDispatch>();//订单上门时间在当前订单结束时间后90分钟之前的技师列表

			for(OrderDispatch orderTech : orderTechList){
				//处理当前订单实际结束时间90分之后开始的订单
				if(orderTech.getServiceTime().before(DateUtils.addMinutes(finishTime,90))){
					maybeTechList.add(orderTech);
				}
			}

			List<OrderDispatch> clashTechList = new ArrayList<OrderDispatch>();//得到不可以接单的技师
			//当前订单  上门时间前90分钟之后，结束时间后90分钟之前   有订单的技师判断是否有时间进行下一单
			for(OrderDispatch orderTech : maybeTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				Date serviceTimeOrder = orderTech.getServiceTime();//服务时间
				Date finishTimeOrder = orderTech.getFinishTime();//完成时间

				int intervalTime = 0;//必须间隔时间 秒
				int bicyclingTime = 15*60;//骑行时间
				//若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间
				int finishTimeHHOrder = Integer.parseInt(DateUtils.formatDate(finishTimeOrder, "HH"));
				int finishTimeHH = Integer.parseInt(DateUtils.formatDate(finishTime, "HH"));

				//可以接单
				if(finishTimeOrder.before(serviceTime)) {//订单结束时间在当前订单开始时间之前

					if(11 <= finishTimeHHOrder && finishTimeHHOrder < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTimeOrder,intervalTime).before(serviceTime)){
						//时间可以
						continue;
					}

				}else if(finishTime.before(serviceTimeOrder)) {//订单开始时间在当前订单结束时间之后
					if(11 <= finishTimeHH && finishTimeHH < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTime,intervalTime).before(serviceTimeOrder)){
						//时间可以
						continue;
					}
				}

				//其它情况 不可以接单
				clashTechList.add(orderTech);
			}


			//有时间接单 还未考虑是否有订单 的技师列表  去除 有订单并且时间冲突的技师
			for(OrderDispatch beforTimeCheckTech: beforTimeCheckTechList){
				boolean flag = true;
				for(OrderDispatch clashTech : clashTechList){
					if(beforTimeCheckTech.getTechId().equals(clashTech.getTechId())){
						flag = false;//时间冲突的技师
						break;
					}
				}
				if(flag) {
					techListRe.add(beforTimeCheckTech);
				}
			}

		}
		if (null != techListPart){
			for(OrderDispatch part : techListPart){
				techListRe.add(part);
			}
		}
		return techListRe;
	}
	//app 技师列表
	public List<OrderDispatch> appTech(OrderInfo orderInfo) {
		List<OrderGoods> goodsInfoList = null;
		orderInfo = get(orderInfo.getId());//当前订单
		goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息

		if(goodsInfoList == null || goodsInfoList.size() == 0 ){
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = dao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		//serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师id
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

		List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
		List<String> beforTimeCheckTechIdList = new ArrayList<String>();
		if(techList != null){
			for(OrderDispatch tech : techList){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				String techId = tech.getTechId();
				boolean b =  workTechIdList.contains(techId);
				boolean b1 = holidayTechIdList == null || (holidayTechIdList != null && !holidayTechIdList.contains(tech.getTechId()));
				if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
					beforTimeCheckTechList.add(tech);
					beforTimeCheckTechIdList.add(tech.getTechId());
				}
			}
		}else {
			throw new ServiceException("没有可选择的技师");
		}
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表
			List<OrderDispatch> maybeTechList = new ArrayList<OrderDispatch>();//订单上门时间在当前订单结束时间后90分钟之前的技师列表

			for(OrderDispatch orderTech : orderTechList){
				//处理当前订单实际结束时间90分之后开始的订单
				if(orderTech.getServiceTime().before(DateUtils.addMinutes(finishTime,90))){
					maybeTechList.add(orderTech);
				}
			}

			List<OrderDispatch> clashTechList = new ArrayList<OrderDispatch>();//得到不可以接单的技师
			//当前订单  上门时间前90分钟之后，结束时间后90分钟之前   有订单的技师判断是否有时间进行下一单
			for(OrderDispatch orderTech : maybeTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				Date serviceTimeOrder = orderTech.getServiceTime();//服务时间
				Date finishTimeOrder = orderTech.getFinishTime();//完成时间

				int intervalTime = 0;//必须间隔时间 秒
				int bicyclingTime = 15*60;//骑行时间
				//若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间
				int finishTimeHHOrder = Integer.parseInt(DateUtils.formatDate(finishTimeOrder, "HH"));
				int finishTimeHH = Integer.parseInt(DateUtils.formatDate(finishTime, "HH"));

				//可以接单
				if(finishTimeOrder.before(serviceTime)) {//订单结束时间在当前订单开始时间之前

					if(11 <= finishTimeHHOrder && finishTimeHHOrder < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTimeOrder,intervalTime).before(serviceTime)){
						//时间可以
						continue;
					}

				}else if(finishTime.before(serviceTimeOrder)) {//订单开始时间在当前订单结束时间之后
					if(11 <= finishTimeHH && finishTimeHH < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTime,intervalTime).before(serviceTimeOrder)){
						//时间可以
						continue;
					}
				}

				//其它情况 不可以接单
				clashTechList.add(orderTech);
			}

			List<OrderDispatch> techListRe = new ArrayList<>();
			//有时间接单 还未考虑是否有订单 的技师列表  去除 有订单并且时间冲突的技师
			for(OrderDispatch beforTimeCheckTech: beforTimeCheckTechList){
				boolean flag = true;
				for(OrderDispatch clashTech : clashTechList){
					if(beforTimeCheckTech.getTechId().equals(clashTech.getTechId())){
						flag = false;//时间冲突的技师
						break;
					}
				}
				if(flag) {
					techListRe.add(beforTimeCheckTech);
				}
			}
			return techListRe;
		}
		return null;
	}

	public List<OrderDispatch> addTech1(OrderInfo orderInfo) {
		List<OrderGoods> goodsInfoList = null;
		if(StringUtils.isNotEmpty(orderInfo.getId())){
			orderInfo = get(orderInfo.getId());//当前订单
			goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		}else{
			goodsInfoList = orderInfo.getGoodsInfoList(); //取得订单服务信息
		}
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			int techMaxNum = 0;//派人数量
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//gon

				if(totalTime > 4){
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){
					techNum = cappinPerNum;
				}
				if(techNum > techMaxNum){
					techMaxNum = techNum;
				}

			}
		}else{
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间
		/*String latitude = orderInfo.getLatitude();//纬度
		String longitude = orderInfo.getLongitude();//经度*/

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = dao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		//serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

		List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
		List<String> beforTimeCheckTechIdList = new ArrayList<String>();
		if(techList != null){
			for(OrderDispatch tech : techList){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
					beforTimeCheckTechList.add(tech);
					beforTimeCheckTechIdList.add(tech.getTechId());
				}
			}
		}
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
					DateUtils.formatDate(serviceTime, "MM") + "-" +
					DateUtils.formatDate(serviceTime, "dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表
			List<OrderDispatch> maybeTechList = new ArrayList<OrderDispatch>();//订单上门时间在当前订单结束时间后90分钟之前的技师列表

			for(OrderDispatch orderTech : orderTechList){
				//处理当前订单实际结束时间90分之后开始的订单
				if(orderTech.getServiceTime().before(DateUtils.addMinutes(finishTime,90))){
					maybeTechList.add(orderTech);
				}
			}

			List<OrderDispatch> clashTechList = new ArrayList<OrderDispatch>();//得到不可以接单的技师
			//当前订单  上门时间前90分钟之后，结束时间后90分钟之前   有订单的技师判断是否有时间进行下一单
			for(OrderDispatch orderTech : maybeTechList){
				//（1）路上时间：计算得出，按照骑行的时间计算
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				Date serviceTimeOrder = orderTech.getServiceTime();//服务时间
				Date finishTimeOrder = orderTech.getFinishTime();//完成时间
				/*String latitudeOrder = orderTech.getLatitude();//纬度
				String longitudeOrder = orderTech.getLongitude();//经度
				//无意义注释  北京市界经纬度:北纬39°26'至41°03',东经115°25'至117°30'
				String origin = latitudeOrder + "," + longitudeOrder;
				String destination = latitude + "," + longitude;
				//int bicyclingTime = Integer.parseInt(GaoDeMapUtil.directionBicycling(origin,destination));//骑行时间*/

				int intervalTime = 0;//必须间隔时间 秒
				int bicyclingTime = 15*60;//骑行时间
				//若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间
				int finishTimeHHOrder = Integer.parseInt(DateUtils.formatDate(finishTimeOrder, "HH"));
				int finishTimeHH = Integer.parseInt(DateUtils.formatDate(finishTime, "HH"));

				//可以接单
				if(finishTimeOrder.before(serviceTime)) {//订单结束时间在当前订单开始时间之前

					if(11 <= finishTimeHHOrder && finishTimeHHOrder < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTimeOrder,intervalTime).before(serviceTime)){
						//时间可以
						continue;
					}

				}else if(finishTime.before(serviceTimeOrder)) {//订单开始时间在当前订单结束时间之后
					if(11 <= finishTimeHH && finishTimeHH < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + bicyclingTime + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = bicyclingTime + 10*60;
					}

					if(DateUtils.addSeconds(finishTime,intervalTime).before(serviceTimeOrder)){
						//时间可以
						continue;
					}
				}

				//其它情况 不可以接单
				clashTechList.add(orderTech);
			}

			List<OrderDispatch> techListRe = new ArrayList<>();
			//有时间接单 还未考虑是否有订单 的技师列表  去除 有订单并且时间冲突的技师
			for(OrderDispatch beforTimeCheckTech: beforTimeCheckTechList){
				boolean flag = true;
				for(OrderDispatch clashTech : clashTechList){
					if(beforTimeCheckTech.getTechId().equals(clashTech.getTechId())){
						flag = false;//时间冲突的技师
						break;
					}
				}
				if(flag) {
					techListRe.add(beforTimeCheckTech);
				}
			}

			return techListRe;
		}


		return null;
	}

	/**
	 * 增加技师保存
	 * @param orderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> addTechSave(OrderInfo orderInfo) {
		Double serviceHourRe = 0.0;
		List<String> techIdList = orderInfo.getTechIdList();//新增技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() + techIdList.size());
		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceHourRe);//建议服务时长（小时）
		info.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);

		for(String techId : techIdList){
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setTechId(techId);//技师ID
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);
			//techList.add(orderDispatch);
		}

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);
		return map;
	}

	/**
	 * 改派技师保存
	 * @param orderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> dispatchTechSave(OrderInfo orderInfo) {
		Double serviceHourRe = 0.0;
		String dispatchTechId = orderInfo.getDispatchTechId();//改派前技师ID
		List<String> techIdList = orderInfo.getTechIdList();//改派技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() - 1 + techIdList.size());
		serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceHourRe);//建议服务时长（小时）
		info.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);
		for(OrderDispatch orderDispatch : techList){
			if(dispatchTechId.equals(orderDispatch.getTechId())){
				//techList.remove(orderDispatch);//返回的技师列表删除改派前技师

				OrderDispatch orderDispatchUpdate = new OrderDispatch();
				orderDispatchUpdate.setId(orderDispatch.getId());//ID
				orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
				orderDispatch.preUpdate();
				orderDispatchDao.update(orderDispatch);//数据库改派前技师设为不可用

				break;
			}
		}

		for(String techId : techIdList){//改派技师List  insert
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setTechId(techId);//技师ID
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);
			//techList.add(orderDispatch);
		}
		//return techList;

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);
		return map;
	}

	//app
	public OrderInfo appGet(OrderInfo info){
		return dao.appGet(info);
	}
	//app改派保存
	@Transactional(readOnly = false)
	public int appDispatchTechSave(OrderInfo orderInfo) {
		String dispatchTechId = orderInfo.getDispatchTechId();//改派前技师ID
		String techId = orderInfo.getTechId();//改派技师id
		ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
		tec.setId(dispatchTechId);
		//订单当前技师
		ServiceTechnicianInfo oldTech = serviceTechnicianInfoDao.getById(tec);
		tec.setId(techId);
		ServiceTechnicianInfo newTech = serviceTechnicianInfoDao.getById(tec);
		//订单的服务站id 与技师的服务站id匹配
		if (newTech!=null){
			if (!newTech.getStationId().equals(orderInfo.getStationId())){
				throw new ServiceException("选择技师不属于该服务站！");
			}
		}else{
			throw new ServiceException("选择技师不可为空！");
		}
		//根据订单技师  获取老派单
		OrderDispatch orderDispatch = dao.appGetOrderDispatch(orderInfo);

		OrderDispatch orderDispatchUpdate = new OrderDispatch();
		orderDispatchUpdate.setId(oldTech.getId());//ID
		orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
		orderDispatch.appPreUpdate();
		//数据库改派前技师派单 设为不可用
		int update1 = orderDispatchDao.update(orderDispatch);

		OrderDispatch newDis = new OrderDispatch();
		newDis.setTechId(techId);//技师ID
		newDis.setOrderId(orderInfo.getId());//订单ID
		newDis.setStatus("yes");//状态(yes：可用 no：不可用)
		newDis.appreInsert();
		int insert = orderDispatchDao.insert(newDis);
		return insert;
	}

	/**
	 * timeData更换时间
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> timeData(OrderInfo orderInfo) {
		//根据传入的年月日取得当天有时间且没休假的技师
		List<OrderGoods> goodsInfoList = null;
        Date serviceDate = orderInfo.getServiceTime();//服务时间年月日
		orderInfo = get(orderInfo.getId());//当前订单
		goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		int techDispatchNum = 0;//派人数量
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//商品需要时间

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
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
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID
		int week = DateUtils.getWeekNum(serviceDate); //周几
		Date serviceDateMin = DateUtils.parseDate(DateUtils.formatDate(serviceDate, "yyyy") + "-" +
				DateUtils.formatDate(serviceDate, "MM") + "-" +
				DateUtils.formatDate(serviceDate, "dd") + " 00:00:00");
		Date serviceDateMax = DateUtils.parseDate(DateUtils.formatDate(serviceDate, "yyyy") + "-" +
				DateUtils.formatDate(serviceDate, "MM") + "-" +
				DateUtils.formatDate(serviceDate, "dd") + " 23:59:59");
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）
		Double serviceSecond = (serviceHour * 3600);

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String skillId = dao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		//serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		if(techList.size() < techDispatchNum){//技师数量不够
			return null;
		}
		Iterator<OrderDispatch> it = techList.iterator();
		while(it.hasNext()) {
			OrderDispatch tech = it.next();
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = dao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){
				it.remove();

				if(techList.size() < techDispatchNum){//技师数量不够
					return null;
				}
				continue;
			}
			ServiceTechnicianWorkTime workTime = workTimeList.get(0);
			List<String> workTimes = DateUtils.getHeafHourTimeList(workTime.getStartTime(),workTime.getEndTime());

			//去除休假时间
			serchTech.setStartTime(serviceDateMin);
			serchTech.setEndTime(serviceDateMax);
			List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
			if(holidayList != null && holidayList.size() !=0){
				for(ServiceTechnicianHoliday holiday : holidayList){
					List<String> holidays = DateUtils.getHeafHourTimeList(holiday.getStartTime(),holiday.getEndTime());
					Iterator<String> it1 = workTimes.iterator();
					while(it1.hasNext()) {
						String work = (String)it1.next();
						if(holidays.contains(work)){//去除休假时间
							it1.remove();
							//continue;
						}
					}
				}
			}

			//去除订单前后时间段
			List<OrderDispatch> orderList = dao.findTechOrderList(serchTech);
			if(orderList != null && orderList.size() !=0){
				for(OrderDispatch order : orderList){
					int intervalTime = 0;//必须间隔时间 秒
					if(11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(),"HH")) &&
							Integer.parseInt(DateUtils.formatDate(order.getEndTime(),"HH"))  < 14){
						//可以接单的时间则为：40分钟+路上时间+富余时间
						intervalTime = 40*60 + 15*60 + 10*60;
					}else{
						//可以接单的时间则为：路上时间+富余时间
						intervalTime = 15*60 + 10*60;
					}

					List<String> orders = DateUtils.getHeafHourTimeList(
							DateUtils.addSeconds(order.getStartTime(),-serviceSecond.intValue()),
							DateUtils.addSeconds(order.getEndTime(),intervalTime));
					Iterator<String> it2 = workTimes.iterator();
					while(it2.hasNext()) {
						String work = (String)it2.next();
						if(orders.contains(work)){//去除订单时间
							it2.remove();
							//continue;
						}
					}
				}
			}
			tech.setWorkTimes(workTimes);
		}

		List<String> allTimeList = new ArrayList<String>();
		List<String> resTimeList = new ArrayList<String>();
		for(OrderDispatch tech : techList){
			allTimeList.addAll(tech.getWorkTimes());
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
		List<String> list = java.util.Arrays.asList(strings);

		List<OrderDispatch> listRe = new ArrayList<>();
		for(String time : list){
			OrderDispatch info = new OrderDispatch();
			info.setServiceTimeStr(time);
			listRe.add(info);
		}

		return listRe;
	}

	/**
	 * 更换时间保存
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public HashMap<String,Object> saveTime(OrderInfo orderInfo) {
		Double serviceHourRe = 0.0;
        Date serviceDate = orderInfo.getServiceTime();//服务时间年月日
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		List<OrderDispatch> techBeforList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		int techDispatchNum = 0;//派人数量
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//商品需要时间

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品需求的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单需求的最少人数
					techDispatchNum = techNum;
				}
			}
		}else{
			throw new ServiceException("订单没有服务信息！");
		}
        String stationId = orderInfo.getStationId();//服务站ID
        Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）
        Double serviceSecond = (serviceHour * 3600);
        Date serviceTime = orderInfo.getServiceTime();//服务时间
        Date finishTime = DateUtils.addSeconds(serviceDate,serviceSecond.intValue());//完成时间

        //取得技师List
        OrderDispatch serchInfo = new OrderDispatch();
        //展示当前下单客户所在服务站的所有可服务的技师
        serchInfo.setStationId(stationId);
        //（1）会此技能的
        String skillId = dao.getSkillIdBySortId(goodsInfoList.get(0).getSortId());//通过服务分类ID取得技能ID
        serchInfo.setSkillId(skillId);
        //（2）上线、在职
        serchInfo.setTechStatus("yes");
        serchInfo.setJobStatus("online");
        //自动派单 全职 ; 手动派单没有条件
        //serchInfo.setJobNature("full_time");
        //派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
        serchInfo.setOrderId(orderInfo.getId());
        List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);
        if(techList.size() < techDispatchNum){//技师数量不够
            return null;
        }
        //（3）考虑技师的工作时间
        //取得当前机构下工作时间包括服务时间的技师
        serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
        serchInfo.setStartTime(serviceTime);
        serchInfo.setEndTime(finishTime);
        List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
        //（4）考虑技师的休假时间
        List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

        List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
        List<String> beforTimeCheckTechIdList = new ArrayList<String>();
        if(techList != null){
            for(OrderDispatch tech : techList){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
                if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
                        && (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
                    beforTimeCheckTechList.add(tech);
                    beforTimeCheckTechIdList.add(tech.getTechId());
                }
            }
        }
        if(beforTimeCheckTechIdList.size() != 0){
            serchInfo.setTechIds(beforTimeCheckTechIdList);
            serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
            //今天的订单
            serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
                    DateUtils.formatDate(serviceTime, "MM") + "-" +
                    DateUtils.formatDate(serviceTime, "dd") + " 00:00:00"));
            serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy") + "-" +
                    DateUtils.formatDate(serviceTime, "MM") + "-" +
                    DateUtils.formatDate(serviceTime, "dd") + " 23:59:59"));
            //（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
            List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表
            List<OrderDispatch> maybeTechList = new ArrayList<OrderDispatch>();//订单上门时间在当前订单结束时间后90分钟之前的技师列表

            for(OrderDispatch orderTech : orderTechList){
                //处理当前订单实际结束时间90分之后开始的订单
                if(orderTech.getServiceTime().before(DateUtils.addMinutes(finishTime,90))){
                    maybeTechList.add(orderTech);
                }
            }

            List<OrderDispatch> clashTechList = new ArrayList<OrderDispatch>();//得到不可以接单的技师
            //当前订单  上门时间前90分钟之后，结束时间后90分钟之前   有订单的技师判断是否有时间进行下一单
            for(OrderDispatch orderTech : maybeTechList){
                //（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
                //		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
                //（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
                //				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
                //（4）富余时间定为10分钟"
                Date serviceTimeOrder = orderTech.getServiceTime();//服务时间
                Date finishTimeOrder = orderTech.getFinishTime();//完成时间

                int intervalTime = 0;//必须间隔时间 秒
                int bicyclingTime = 15*60;//骑行时间
                //若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间
                int finishTimeHHOrder = Integer.parseInt(DateUtils.formatDate(finishTimeOrder, "HH"));
                int finishTimeHH = Integer.parseInt(DateUtils.formatDate(finishTime, "HH"));

                //可以接单
                if(finishTimeOrder.before(serviceTime)) {//订单结束时间在当前订单开始时间之前

                    if(11 <= finishTimeHHOrder && finishTimeHHOrder < 14){
                        //可以接单的时间则为：40分钟+路上时间+富余时间
                        intervalTime = 40*60 + bicyclingTime + 10*60;
                    }else{
                        //可以接单的时间则为：路上时间+富余时间
                        intervalTime = bicyclingTime + 10*60;
                    }

                    if(DateUtils.addSeconds(finishTimeOrder,intervalTime).before(serviceTime)){
                        //时间可以
                        continue;
                    }

                }else if(finishTime.before(serviceTimeOrder)) {//订单开始时间在当前订单结束时间之后
                    if(11 <= finishTimeHH && finishTimeHH < 14){
                        //可以接单的时间则为：40分钟+路上时间+富余时间
                        intervalTime = 40*60 + bicyclingTime + 10*60;
                    }else{
                        //可以接单的时间则为：路上时间+富余时间
                        intervalTime = bicyclingTime + 10*60;
                    }

                    if(DateUtils.addSeconds(finishTime,intervalTime).before(serviceTimeOrder)){
                        //时间可以
                        continue;
                    }
                }

                //其它情况 不可以接单
                clashTechList.add(orderTech);
            }

            List<OrderDispatch> techListRe = new ArrayList<>();
            //有时间接单 还未考虑是否有订单 的技师列表  去除 有订单并且时间冲突的技师
            for(OrderDispatch beforTimeCheckTech: beforTimeCheckTechList){
                boolean flag = true;
                for(OrderDispatch clashTech : clashTechList){
                    if(beforTimeCheckTech.getTechId().equals(clashTech.getTechId())){
                        flag = false;//时间冲突的技师
                        break;
                    }
                }
                if(flag) {
                    techListRe.add(beforTimeCheckTech);
                }
            }
            if(techListRe.size() < techDispatchNum){//技师数量不够
                return null;
            }

            //一个自然月内，根据当前服务站内各个技师的订单数量，优先分配给订单数量少的技师
            List<String> serchTechIds = new ArrayList<>();
            for(OrderDispatch tech :techListRe){
                serchTechIds.add(tech.getTechId());
            }
            Date monthFristDay = DateUtils.getMonthFristDay(serviceDate);
            Date monthLastDay = DateUtils.getMonthLastDay(serviceDate);
            OrderDispatch serchTechInfo = new OrderDispatch();
            serchTechInfo.setStartTime(monthFristDay);
            serchTechInfo.setEndTime(monthLastDay);
            serchTechInfo.setTechIds(serchTechIds);
            List<OrderDispatch> techListOrderByNum = dao.getTechListOrderByNum(serchTechInfo);//订单数量少的技师
            List<String> dispatchTechIds = new ArrayList<>();
            for(int i=0;i<techDispatchNum;i++){//订单数量少的技师  订单需要技师数量
                dispatchTechIds.add(techListOrderByNum.get(i).getTechId());
            }

			serviceHourRe = new BigDecimal(serviceSecond/3600).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

			OrderInfo info = new OrderInfo();
			info.setId(orderInfo.getId());
			info.setServiceTime(serviceDate);//服务时间
			info.setServiceHour(serviceHourRe);//建议服务时长（小时）
			info.setFinishTime(finishTime);//实际完成时间（用来计算库存）
			info.setSuggestFinishTime(finishTime);//实际完成时间（用来计算库存）
			info.preUpdate();
			dao.update(info);

			for(OrderDispatch tech :techBeforList){
				OrderDispatch orderDispatch = new OrderDispatch();
				orderDispatch.setId(tech.getId());//技师ID
				orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
				orderDispatch.preUpdate();
				orderDispatchDao.update(orderDispatch);//数据库改派前技师设为不可用
			}
			for(String techId : dispatchTechIds){
				OrderDispatch orderDispatch = new OrderDispatch();
				orderDispatch.setTechId(techId);//技师ID
				orderDispatch.setOrderId(orderInfo.getId());//订单ID
				orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
				orderDispatch.preInsert();
				orderDispatchDao.insert(orderDispatch);
				techList.add(orderDispatch);
			}

			List<OrderDispatch> techLastList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
            //return techLastList;

			HashMap<String,Object> map = new HashMap<>();
			map.put("serviceHour",serviceHourRe);
			map.put("list",techLastList);
			return map;
        }
        return null;
	}

	public List<OrderGoods> editGoodsInit(OrderInfo orderInfo) {
		List<OrderGoods>  list = new ArrayList<OrderGoods>();
		List<OrderGoods> orderGoodsList = dao.getOrderGoodsList(orderInfo);    //订单已选商品信息
		List<OrderGoods> goodsList = dao.getGoodsList(orderInfo);    //服务项目下所有商品信息

		OrderGoods houseInfo = new OrderGoods();

		//得到除了居室以外的商品及选择信息
		if(goodsList!=null && goodsList.size()!=0){
			List<OrderGoodsTypeHouse> houses = new ArrayList<OrderGoodsTypeHouse>();
			for (OrderGoods goods : goodsList){//循环所有商品
				if("house".equals(goods.getGoodsType())){//按居室的商品
					OrderGoodsTypeHouse house = new OrderGoodsTypeHouse();
					house.setId(goods.getGoodsId());
					house.setName(goods.getGoodsName());
					houses.add(house);
				}else{
					if(orderGoodsList!=null && orderGoodsList.size()!=0){
						for (OrderGoods orderGoods : orderGoodsList){//循环订单选择商品
							if(goods.getGoodsId().equals(orderGoods.getGoodsId())){//同一个商品
								goods.setGoodsNum(orderGoods.getGoodsNum());
								goods.setGoodsChecked(true);
							}
						}
					}
					list.add(goods);
				}
			}
			//得到居室商品及选择信息
			for (OrderGoods orderGoods : orderGoodsList){//循环订单选择商品
				if("house".equals(orderGoods.getGoodsType())){//按居室的商品
					houseInfo.setItemId(orderGoods.getItemId());
					houseInfo.setItemName(orderGoods.getItemName());
					houseInfo.setGoodsId(orderGoods.getGoodsId());
					houseInfo.setGoodsName(orderGoods.getGoodsName());
					houseInfo.setPayPrice(orderGoods.getPayPrice());
					houseInfo.setMinPurchase(orderGoods.getMinPurchase());
					houseInfo.setGoodsType(orderGoods.getGoodsType());
					houseInfo.setGoodsNum(orderGoods.getGoodsNum());
					houseInfo.setGoodsChecked(true);
					houseInfo.setHouses(houses);
					list.add(houseInfo);
				}
			}
		}

		return list;
	}
	//app订单列表
	public Page<OrderInfo> appFindPage(Page<OrderInfo> page, OrderInfo orderInfo) {
		orderInfo.setPage(page);
		List<OrderInfo> orderInfos = dao.appFindList(orderInfo);
		for (OrderInfo info:orderInfos){
			String majorSort = info.getMajorSort();
			if (majorSort.equals("clean")){
				info.setMajorSortName("保洁");
			}else if (majorSort.equals("repair")){
				info.setMajorSortName("家修");
			}
			String serviceStatus = info.getServiceStatus();
			if (serviceStatus.equals("wait_service")){
				info.setServiceStatusName("待服务");
			}else if (serviceStatus.equals("started")){
				info.setServiceStatusName("已上门");
			}else if (serviceStatus.equals("finish")){
				info.setServiceStatusName("已完成");
			}
		}
		page.setList(orderInfos);
		return page;
	}

    //根据权限获取对应的服务站
    public List<BasicServiceStation> getServiceStationList(BasicServiceStation station) {
        station.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
        return basicServiceStationDao.getServiceStationList(station);
    }
	//订单编辑
	@Transactional(readOnly = false)
	public int appSaveRemark(OrderInfo orderInfo){
		orderInfo.appPreUpdate();
		return dao.appUpdate(orderInfo);
	}
}