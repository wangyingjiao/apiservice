/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.map.GaoDeMapUtil;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
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
		if (null == orderInfo){
			throw new ServiceException("没有该订单");
		}
		orderInfo.setNowId(info.getNowId());
		OrderGoods goodsInfo = new OrderGoods();
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() > 0){
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());
			goodsInfo.setGoods(goodsInfoList);
		}else {
			throw new ServiceException("没有商品信息");
		}

		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		//商品图片
		String pics = dao.appGetPics(orderInfo.getId());
		if (pics !=null){
			List<String> picl = (List<String>) JsonMapper.fromJsonString(pics, ArrayList.class);
			if (picl !=null && picl.size()>0){
				goodsInfo.setPicture(ossHost+picl.get(0));
			}
		}
		//app的技师列表 appTechList
		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		if (techList==null || techList.size()==0){
			throw new ServiceException("没有技师");
		}
//		for(OrderGoods orderGoods : goodsInfoList){
//			String dj = orderGoods.getPayPrice();//商品单价
//			int num = orderGoods.getGoodsNum();//商品数量
//			BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
//			orderGoods.setPayPrice(price.toString());//总价
//		}
		orderInfo.setGoodsInfo(goodsInfo);
		orderInfo.setTechList(techList);
		List<String> idList=new ArrayList<String>();
		//app其他技师
		List<AppServiceTechnicianInfo> appTechList=new ArrayList<AppServiceTechnicianInfo>();
		for (OrderDispatch apt:techList){
			ServiceTechnicianInfo temInfo=new ServiceTechnicianInfo();
			temInfo.setId(apt.getTechId());
			//当前登陆用户信息
			AppServiceTechnicianInfo technicianById = serviceTechnicianInfoDao.getTechnicianById(temInfo);
			//当前登陆用户的id是否与当前订单的拥有人id不相同 取出不相同的技师放入其他技师list
			if (technicianById !=null){
				if (!technicianById.getId().equals(orderInfo.getNowId())){
					appTechList.add(technicianById);
				}
				//将订单下的技师id取出来
				idList.add(technicianById.getId());
			}
		}
		//如果包含这个id 可以操作 不包含 不能对订单进行操作
		if (idList != null && idList.size()>0) {
			if (idList.contains(orderInfo.getNowId())) {
				orderInfo.setIsTech("yes");
			}
		}
		orderInfo.setAppTechList(appTechList);
		//客户信息
		OrderCustomInfo customerInfo=new OrderCustomInfo();
		customerInfo.setCustomerRemark(orderInfo.getCustomerRemark());
		List<String> ll=new ArrayList<String>();
		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(null != customerRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			if (pictureDetails.size()>0){
				for (String s:pictureDetails){
					String url=ossHost+s;
					ll.add(url);
				}
			}
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		customerInfo.setCustomerRemarkPic(ll);
		orderInfo.setCustomerInfo(customerInfo);
		//业务人员信息
		BusinessInfo bus=new BusinessInfo();
		bus.setBusinessName(orderInfo.getBusinessName());
		bus.setBusinessPhone(orderInfo.getBusinessPhone());
		bus.setBusinessRemark(orderInfo.getBusinessRemark());
		List<String> bp=new ArrayList<String>();
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		if(null != businessRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic,ArrayList.class);
			orderInfo.setBusinessRemarkPics(pictureDetails);
			if (pictureDetails.size()>0){
				for (String pic:pictureDetails){
					String url=ossHost+pic;
					bp.add(url);
				}
			}
		}
		bus.setBusinessRemarkPic(bp);
		orderInfo.setBusinessInfo(bus);

		//门店信息
		ShopInfo shop=new ShopInfo();
		shop.setId(orderInfo.getStationId());
		shop.setShopName(orderInfo.getShopName());
		shop.setShopPhone(orderInfo.getShopPhone());
		shop.setShopAddress(orderInfo.getShopAddr());
		shop.setShopRemark(orderInfo.getShopRemark());
		List<String> ls=new ArrayList<String>();
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			if (pictureDetails.size()>0){
				for (String pic:pictureDetails){
					String url=ossHost+pic;
					ls.add(url);
				}
			}
			orderInfo.setShopRemarkPics(pictureDetails);
		}
		shop.setShopRemarkPic(ls);
		orderInfo.setShopInfo(shop);
		String orderSource = orderInfo.getOrderSource();
		if (orderSource !=null) {
			if (orderSource.equals("own")) {
				orderInfo.setOrderSource("本机构");
			} else if (orderSource.equals("gasq")) {
				orderInfo.setOrderSource("国安社区");
			}
		}
		String serviceStatus = orderInfo.getServiceStatus();
		if (serviceStatus != null) {
			if (serviceStatus.equals("wait_service")) {
				orderInfo.setServiceStatusName("待服务");
			} else if (serviceStatus.equals("started")) {
				orderInfo.setServiceStatusName("已上门");
			} else if (serviceStatus.equals("finish")) {
				orderInfo.setServiceStatusName("已完成");
			} else if (serviceStatus.equals("cancel")) {
				orderInfo.setServiceStatusName("已取消");
			}
		}
		String orderStatus = orderInfo.getOrderStatus();
		if (orderStatus != null) {
			if (orderStatus.equals("waitdispatch")) {
				orderInfo.setOrderStatusName("待派单");
			} else if (orderStatus.equals("dispatched")) {
				orderInfo.setOrderStatusName("已派单");
			} else if (orderStatus.equals("cancel")) {
				orderInfo.setOrderStatusName("已取消");
			} else if (orderStatus.equals("started")) {
				orderInfo.setOrderStatusName("已上门");
			} else if (orderStatus.equals("finish")) {
				orderInfo.setOrderStatusName("已完成");
			} else if (orderStatus.equals("success")) {
				orderInfo.setOrderStatusName("已成功");
			} else if (orderStatus.equals("stop")) {
				orderInfo.setOrderStatusName("已暂停");
			}
		}
		String payStatus = orderInfo.getPayStatus();
		if (payStatus != null) {
			if (payStatus.equals("waitpay")) {
				orderInfo.setPayStatusName("待支付");
			} else if (payStatus.equals("payed")) {
				orderInfo.setPayStatusName("已支付");
			}
		}
		//订单备注 数据库中的json 存的是list
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		List<String> orp=new ArrayList<String>();
		if (null != orderRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			if(pictureDetails.size()>0){
				for (String pic:pictureDetails){
					String url=ossHost+pic;
					orp.add(url);
				}
			}
		}
		orderInfo.setOrderRemarkPics(orp);

		return orderInfo;
	}

	public OrderInfo formData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		if(orderInfo == null){
			return null;
		}
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			OrderGoods goodsInfo = new OrderGoods();
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());

			for(OrderGoods orderGoods : goodsInfoList){
				String dj = orderGoods.getPayPrice();//商品单价
				if(StringUtils.isEmpty(dj)){
					dj = "0";
				}
				int num = orderGoods.getGoodsNum();//商品数量
				BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
				orderGoods.setPayPriceSum(price.toString());//总价
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
		String orgId = orderInfo.getOrgId();
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		//serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		serchInfo.setTechName(techName);//查询条件
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

		if (null != techListPart){
			for(OrderDispatch part : techListPart){
				beforTimeCheckTechList.add(part);
				beforTimeCheckTechIdList.add(part.getTechId());
			}
		}
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			//serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表

			List<String> timeCheckDelTechIdList = new ArrayList<String>();
			for(OrderDispatch orderTech : orderTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				orderTech.setServiceTime(DateUtils.addSeconds(orderTech.getServiceTime(),-(15*60 + 10*60)));
				int finishTimeHH = Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH"));
				if(11 <= finishTimeHH && finishTimeHH < 14){
					orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(),(15*60 + 40*60 + 10*60)));
				}else{
					orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(),(15*60 + 10*60)));
				}

				if(!DateUtils.checkDatesRepeat(serviceTime,finishTime,orderTech.getServiceTime(),orderTech.getFinishTime())){
					timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
				}
			}
			for(OrderDispatch tech : beforTimeCheckTechList){
				if(!timeCheckDelTechIdList.contains(tech.getTechId())){
					techListRe.add(tech);
				}
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

		List<OrderDispatch> techListRe = new ArrayList<>();

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String orgId = orderInfo.getOrgId();
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if((skillSortList!=null && skillSortList.size() >0) && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品所需求的技能信息");
		}

		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件  app只要全职的
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);
		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师id
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		//工作时间符合机构工作时间 的人员列表
		List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
		if (workTechIdList == null || workTechIdList.size()==0){
			throw new ServiceException("工作时间不符合");
		}
		//（4）考虑技师的休假时间  休假时间在这个服务时间内的员工id
		List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);
		//时间满足的人员
		List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
		List<String> beforTimeCheckTechIdList = new ArrayList<String>();
		if(techList != null && techList.size()>0){
			for(OrderDispatch tech : techList){
				String techId = tech.getTechId();
				boolean b =  workTechIdList.contains(techId);
				boolean b1 = holidayTechIdList == null || (holidayTechIdList != null && !holidayTechIdList.contains(tech.getTechId()));
				//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(tech.getTechId()))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(tech.getTechId()))) ){
					beforTimeCheckTechList.add(tech);
					beforTimeCheckTechIdList.add(tech.getTechId());
				}
			}
		}else {
			throw new ServiceException("没有会此技能且在岗在线的全职技师");
		}
		//时间满足条件的人员列表
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			//serchInfo.setServiceTime(DateUtils.addMinutes(serviceTime,90));//订单结束时间在当前订单上门时间前90分钟之后
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表

			List<String> timeCheckDelTechIdList = new ArrayList<String>();
			if(orderTechList !=null && orderTechList.size()>0) {
				for (OrderDispatch orderTech : orderTechList) {
					//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
					//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
					//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
					//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
					//（4）富余时间定为10分钟"
					orderTech.setServiceTime(DateUtils.addSeconds(orderTech.getServiceTime(), -(15 * 60 + 10 * 60)));
					int finishTimeHH = Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH"));
					if (11 <= finishTimeHH && finishTimeHH < 14) {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (15 * 60 + 40 * 60 + 10 * 60)));
					} else {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (15 * 60 + 10 * 60)));
					}

					if (!DateUtils.checkDatesRepeat(serviceTime, finishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
						timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
					}
				}
			}
			for(OrderDispatch tech : beforTimeCheckTechList){
				if(!timeCheckDelTechIdList.contains(tech.getTechId())){
					techListRe.add(tech);
				}
			}

		}
//		if (null != techListPart){
//			for(OrderDispatch part : techListPart){
//				techListRe.add(part);
//			}
//		}
		return techListRe;
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
		if(techIdList==null || techIdList.size()==0){
			throw new ServiceException("请选择技师");
		}
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
		info.setFinishTime(DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);

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
			orderCreateMsgList.add(orderDispatch);
		}

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

		String jointGoodsCodes ="";//对接方商品CODE
		String jointEshopCode = "";//对接方E店CODE

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				jointGoodsCodes = jointGoodsCodes + goods.getJointGoodsCode();//对接方商品CODE
			}
		}else{
			throw new ServiceException("未找到当前订单服务商品信息");
		}

		BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
		if(organization != null){
			jointEshopCode = organization.getJointEshopCode();
		}



		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);

		map.put("orderId",orderInfo.getId());
		map.put("jointGoodsCodes",jointGoodsCodes);
		map.put("jointEshopCode", jointEshopCode);

		map.put("orderCreateMsgList",orderCreateMsgList);
		map.put("orderNumber",orderInfo.getOrderNumber());
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
			//如果改派前是自己的订单 把自己的派单转为无效
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

		// 改派 原来有，现在没有
		List<OrderDispatch> orderDispatchMsgList = new ArrayList<>();
		OrderDispatch orderDispatchMsg = new OrderDispatch();
		orderDispatchMsg.setTechId(dispatchTechId);//技师ID
		ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoDao.get(dispatchTechId);
		if(technicianInfo != null) {
			orderDispatchMsg.setTechPhone(technicianInfo.getPhone());
		}
		orderDispatchMsgList.add(orderDispatchMsg);
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
			orderCreateMsgList.add(orderDispatch);
		}
		//return techList;

		List<OrderDispatch> techListRe = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List


		String jointGoodsCodes ="";//对接方商品CODE
		String jointEshopCode = "";//对接方E店CODE

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				jointGoodsCodes = jointGoodsCodes + goods.getJointGoodsCode();//对接方商品CODE
			}
		}else{
			throw new ServiceException("未找到当前订单服务商品信息");
		}

		BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
		if(organization != null){
			jointEshopCode = organization.getJointEshopCode();
		}else{
			throw new ServiceException("未找到当前订单对应的机构信息");
		}

		HashMap<String,Object> map = new HashMap<>();
		map.put("serviceHour",serviceHourRe);
		map.put("list",techListRe);

		map.put("orderId",orderInfo.getId());
		map.put("jointGoodsCodes",jointGoodsCodes);
		map.put("jointEshopCode", jointEshopCode);

		map.put("orderDispatchMsgList", orderDispatchMsgList);
		map.put("orderCreateMsgList", orderCreateMsgList);
		map.put("orderNumber", orderInfo.getOrderNumber());
		return map;
	}

	//app 通过id获取订单详情
	public OrderInfo appGet(OrderInfo info){
		return dao.appGet(info);
	}
	//app改派保存调用 获取技师列表
	public List<OrderDispatch> getOrderDispatchList(OrderInfo info){
		List<OrderDispatch> orderDispatchList = dao.getOrderDispatchList(info);
		return orderDispatchList;
	}
	//app改派保存
	@Transactional(readOnly = false)
	public int appDispatchTechSave(OrderInfo orderInfo) {
		//改派前技师ID
		String dispatchTechId = orderInfo.getDispatchTechId();
		//改派后技师id
		String techId = orderInfo.getTechId();
		String orderId = orderInfo.getId();
		ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
		//改派前技师信息
//		tec.setId(dispatchTechId);
//		ServiceTechnicianInfo oldTech = serviceTechnicianInfoDao.getById(tec);
		//改派技师信息
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
		//根据订单技师  获取老派单 订单id和改派前技师id去查
		OrderDispatch orderDispatch = dao.appGetOrderDispatch(orderInfo);

		OrderDispatch orderDispatchUpdate = new OrderDispatch();
		orderDispatchUpdate.setId(dispatchTechId);//ID
		orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
		orderDispatch.appPreUpdate();
		//数据库将改派前技师派单 设为不可用
		int update1 = orderDispatchDao.update(orderDispatch);
		//将老派单改成无效  再新增
		int insert =0;
		if (update1 > 0){
			OrderDispatch newDis = new OrderDispatch();
			newDis.setTechId(techId);//技师ID
			newDis.setOrderId(orderInfo.getId());//订单ID
			newDis.setStatus("yes");//状态(yes：可用 no：不可用)
			newDis.appreInsert();
			insert = orderDispatchDao.insert(newDis);
		}else {
			throw new ServiceException("删除改派前技师派单失败");
		}
		return insert;
	}

	public List<OrderTimeList> timeDataList(OrderInfo orderInfo) {

		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OrderTimeList> list = new ArrayList<>();
		int value = 1;

		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
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
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		} else {
			logger.error("未找到当前订单服务商品信息");
			return null;
		}
		orderInfo = get(orderInfo);
		String stationId = orderInfo.getStationId();//服务站ID
		if(null == stationId){
			logger.error("未找到当前订单的服务站信息");
			return null;
		}
		/*Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）
		if(serviceHour == null || 0 == serviceHour){
			logger.error("未找到当前订单的建议服务时长");
			return null;
		}*/
		Double serviceSecond = (serviceHour * 3600);

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String orgId = orderInfo.getOrgId();
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			logger.error("未找到商品需求的技能信息");
			return null;
		}
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		//serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

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
					startDateForWork = DateUtils.parseDate(
							DateUtils.formatDate(startDateForWork, "yyyy-MM-dd") + " " +
									DateUtils.formatDate(new Date(), "HH:mm:ss"));
				}
			}
			List<String> workTimes = DateUtils.getHeafHourTimeListBorder(startDateForWork,workTime.getEndTime());
			//-------------------取得技师 当天(15天中的某天)可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			if(workTimes != null) {
				//-------------------取得技师 当天(15天中的某天)休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
				serchTech.setStartTime(serviceDateMin);
				serchTech.setEndTime(serviceDateMax);
				List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
				if (holidayList != null && holidayList.size() != 0) {
					for (ServiceTechnicianHoliday holiday : holidayList) {
						List<String> holidays = DateUtils.getHeafHourTimeListBorderHoliday(holiday.getStartTime(), holiday.getEndTime());
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
				List<OrderDispatch> orderList = dao.findTechOrderList(serchTech);
				if (orderList != null && orderList.size() != 0) {
					for (OrderDispatch order : orderList) {
						if(!orderInfo.getId().equals(order.getOrderId())) {//当前订单不考虑
							int intervalTimeS = 0;//必须间隔时间 秒
							if (11 <= Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -serviceSecond.intValue()), "HH")) &&
									Integer.parseInt(DateUtils.formatDate(DateUtils.addSecondsNotDayB(order.getStartTime(), -serviceSecond.intValue()), "HH")) < 14) {
								//可以接单的时间则为：40分钟+路上时间+富余时间
								intervalTimeS = 40 * 60 + 15 * 60 + 10 * 60 + serviceSecond.intValue();
							} else {
								//可以接单的时间则为：路上时间+富余时间
								intervalTimeS = 15 * 60 + 10 * 60 + serviceSecond.intValue();
							}

							int intervalTimeE = 0;//必须间隔时间 秒
							if (11 <= Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) &&
									Integer.parseInt(DateUtils.formatDate(order.getEndTime(), "HH")) < 14) {
								//可以接单的时间则为：40分钟+路上时间+富余时间
								intervalTimeE = 40 * 60 + 15 * 60 + 10 * 60;
							} else {
								//可以接单的时间则为：路上时间+富余时间
								intervalTimeE = 15 * 60 + 10 * 60;
							}

							List<String> orders = DateUtils.getHeafHourTimeListBorder(
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
		String jointGoodsCodes ="";
		if(null == orderInfo){
			throw new ServiceException("服务时间不可为空,请选择日期");
		}
		Double serviceHourRe = 0.0;
        Date newServiceDate = orderInfo.getServiceTime();//新选择时间
		if(null == newServiceDate){
			throw new ServiceException("服务时间不可为空,请选择日期");
		}
		orderInfo = get(orderInfo.getId());//当前订单
		if(null == orderInfo){
			throw new ServiceException("未找到当前订单信息");
		}
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		List<OrderDispatch> techBeforList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double newServiceHour = 0.0;//建议服务时长（小时）
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				jointGoodsCodes = jointGoodsCodes + goods.getJointGoodsCode();//对接方商品CODE

				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				if(0 == startPerNum){
					startPerNum = 1;
				}
				int cappinPerNum = goods.getCappingPerNum();		//封项人数
				if(0 == cappinPerNum){
					cappinPerNum = 30;
				}

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = 0.0;
				if(convertHours != null) {
					totalTime = convertHours * goodsNum;//商品需要时间
					orderTotalTime = orderTotalTime + totalTime;
				}

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
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			newServiceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("未找到当前订单服务商品信息");
		}
        String stationId = orderInfo.getStationId();//服务站ID

        Double serviceSecond = (newServiceHour * 3600);
        Date newFinishTime = DateUtils.addSeconds(newServiceDate,serviceSecond.intValue());//完成时间

        //取得技师List
        OrderDispatch serchInfo = new OrderDispatch();
        //展示当前下单客户所在服务站的所有可服务的技师
        serchInfo.setStationId(stationId);
        //（1）会此技能的
		String orgId = orderInfo.getOrgId();
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
        serchInfo.setSkillId(skillId);
        //（2）上线、在职
        serchInfo.setTechStatus("yes");
        serchInfo.setJobStatus("online");
        //自动派单 全职 ; 手动派单没有条件
        serchInfo.setJobNature("full_time");
        //派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
        //serchInfo.setOrderId(orderInfo.getId());
        List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);
        if(techList.size() < techDispatchNum){//技师数量不够
			throw new ServiceException("当前时间未找到技师");
        }
        //（3）考虑技师的工作时间
        //取得当前机构下工作时间包括服务时间的技师
        serchInfo.setWeek(DateUtils.getWeekNum(newServiceDate));
        serchInfo.setStartTime(newServiceDate);
        serchInfo.setEndTime(newFinishTime);
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

		List<OrderDispatch> techListRe = new ArrayList<>();
		if(beforTimeCheckTechIdList.size() != 0){
			serchInfo.setTechIds(beforTimeCheckTechIdList);
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(newServiceDate, "yyyy-MM-dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(newServiceDate, "yyyy-MM-dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//技师列表

			List<String> timeCheckDelTechIdList = new ArrayList<String>();
			for(OrderDispatch orderTech : orderTechList){
				if(!orderInfo.getId().equals(orderTech.getOrderId())) {
					//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
					//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
					//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
					//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
					//（4）富余时间定为10分钟"
					orderTech.setServiceTime(DateUtils.addSeconds(orderTech.getServiceTime(), -(15 * 60 + 10 * 60)));
					int finishTimeHH = Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH"));
					if (11 <= finishTimeHH && finishTimeHH < 14) {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (15 * 60 + 40 * 60 + 10 * 60)));
					} else {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (15 * 60 + 10 * 60)));
					}

					if (!DateUtils.checkDatesRepeat(newServiceDate, newFinishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
						timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
					}
				}
			}
			for(OrderDispatch tech : beforTimeCheckTechList){
				if(!timeCheckDelTechIdList.contains(tech.getTechId())){
					techListRe.add(tech);
				}
			}

            if(techListRe.size() < techDispatchNum){//技师数量不够
				throw new ServiceException("当前时间未找到技师");
            }

            //一个自然月内，根据当前服务站内各个技师的订单数量，优先分配给订单数量少的技师
            List<String> serchTechIds = new ArrayList<>();//可分配技师
            for(OrderDispatch tech :techListRe){
                serchTechIds.add(tech.getTechId());
            }
            Date monthFristDay = DateUtils.getMonthFristDay(newServiceDate);
            Date monthLastDay = DateUtils.getMonthLastDay(newServiceDate);
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
			info.setServiceTime(newServiceDate);//服务时间
			info.setServiceHour(serviceHourRe);//建议服务时长（小时）
			info.setFinishTime(newFinishTime);//实际完成时间（用来计算库存）
			info.setSuggestFinishTime(newFinishTime);//实际完成时间（用来计算库存）
			info.preUpdate();
			dao.update(info);

			List<String> newTechIds = new ArrayList<>();
			List<String> oldTechIds = new ArrayList<>();
			List<String> delTechIds = new ArrayList<>();

			List<String> techBeforIds = new ArrayList<>();
			for(OrderDispatch tech :techBeforList){
				if(dispatchTechIds.contains(tech.getTechId())){//又分配给当前技师
					oldTechIds.add(tech.getTechId());
				}else{
					OrderDispatch orderDispatch = new OrderDispatch();
					orderDispatch.setId(tech.getId());//技师ID
					orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
					orderDispatch.preUpdate();
					orderDispatchDao.update(orderDispatch);//数据库改派前技师设为不可用

					delTechIds.add(tech.getTechId());
				}
			}
			for(String techId : dispatchTechIds){//新增技师
				if(oldTechIds!=null && !oldTechIds.contains(techId)){
					OrderDispatch orderDispatch = new OrderDispatch();
					orderDispatch.setTechId(techId);//技师ID
					orderDispatch.setOrderId(orderInfo.getId());//订单ID
					orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
					orderDispatch.preInsert();
					orderDispatchDao.insert(orderDispatch);

					newTechIds.add(techId);
				}
			}

			List<OrderDispatch> techLastList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List

			List<String> msgTechIds = new ArrayList<>();
			msgTechIds.addAll(newTechIds);
			msgTechIds.addAll(oldTechIds);
			msgTechIds.addAll(delTechIds);
			OrderInfo orderMsg = new OrderInfo();
			orderMsg.setTechIdList(msgTechIds);
			List<OrderDispatch> msgTechList = dao.getOrderDispatchMsgTechList(orderMsg); //订单当前已有技师List
			// 派单 原来没有，现在有
			List<OrderDispatch> orderCreateMsgList = new ArrayList<>();
			// 改派 原来有，现在没有
			List<OrderDispatch> orderDispatchMsgList = new ArrayList<>();
			// 时间变化 原来有，现在有
			List<OrderDispatch> orderServiceTimeMsgList = new ArrayList<>();
			for(OrderDispatch msgInfo : msgTechList){
				if(newTechIds.contains(msgInfo.getTechId())){
					orderCreateMsgList.add(msgInfo);// 派单 原来没有，现在有
				}
				if(delTechIds.contains(msgInfo.getTechId())){
					orderDispatchMsgList.add(msgInfo);// 改派 原来有，现在没有
				}
				if(oldTechIds.contains(msgInfo.getTechId())) {
					orderServiceTimeMsgList.add(msgInfo);// 时间变化 原来有，现在有
				}
			}

			BasicOrganization organization = dao.getBasicOrganizationByOrgId(orderInfo);
			String jointEshopCode = "";
			if(organization != null){
				jointEshopCode = organization.getJointEshopCode();
			}else{
				throw new ServiceException("未找到当前订单对应的机构信息");
			}

			HashMap<String,Object> map = new HashMap<>();
			map.put("serviceHour",serviceHourRe);
			map.put("list",techLastList);
			map.put("orderId",orderInfo.getId());
			map.put("serviceDate",newServiceDate);
			map.put("jointGoodsCodes",jointGoodsCodes);
			map.put("jointEshopCode", jointEshopCode);

			map.put("orderServiceTimeMsgList", orderServiceTimeMsgList);
			map.put("orderDispatchMsgList", orderDispatchMsgList);
			map.put("orderCreateMsgList", orderCreateMsgList);
			map.put("orderNumber", orderInfo.getOrderNumber());

			return map;
        }else{
			throw new ServiceException("未找到可用技师");
		}
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
		//查询已完成订单列表
		if (orderInfo.getServiceTime()!=null && orderInfo.getFinishTime()!=null){
			Date serviceTime = orderInfo.getServiceTime();
			Date finishTime = orderInfo.getFinishTime();
			Date end = DateUtils.parseDate(DateUtils.formatDate(finishTime, "yyyy-MM-dd") + " 23:59:59");
			orderInfo.setOrderTimeStart(new Timestamp(serviceTime.getTime()));
			orderInfo.setOrderTimeEnd(new Timestamp(end.getTime()));
		}
		List<OrderInfo> orderInfos = dao.appFindList(orderInfo);
		if (orderInfos!= null && orderInfos.size()>0) {
			for (OrderInfo info : orderInfos) {
				String majorSort = info.getMajorSort();
				if (majorSort.equals("clean")) {
					info.setMajorSortName("保洁");
				} else if (majorSort.equals("repair")) {
					info.setMajorSortName("家修");
				}
				String serviceStatus = info.getServiceStatus();
				if (serviceStatus.equals("wait_service")) {
					info.setServiceStatusName("待服务");
				} else if (serviceStatus.equals("started")) {
					info.setServiceStatusName("已上门");
				} else if (serviceStatus.equals("finish")) {
					info.setServiceStatusName("已完成");
				}
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
	//订单的编辑（订单备注以及订单的服务状态修改）
	@Transactional(readOnly = false)
	public int appSaveRemark(OrderInfo orderInfo){
		//判断订单是否属于该技师
		OrderDispatch dis=new OrderDispatch();
		dis.setTechId(orderInfo.getNowId());
		dis.setOrderId(orderInfo.getId());
		OrderDispatch byOrderTechId = orderDispatchDao.getByOrderTechId(dis);
		//派单表为空 不属于
		if (null == byOrderTechId){
			throw new ServiceException("订单不属于该技师");
		}
		//订单备注图片  JSON串存的数组
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if (null != orderRemarkPic){
			List<String> pics = (List<String>) JsonMapper.fromJsonString(orderRemarkPic, ArrayList.class);
			List<String> tem=new ArrayList<String>();
			for (String pic:pics){
				if (pic.contains("https://")){
					//获取第三个/下标
					int index = index(pic);
					//截取新数组
					pic = pic.substring(index + 1);
				}
				tem.add(pic);
			}
			String s = JsonMapper.toJsonString(tem);
			orderInfo.setOrderRemarkPic(s);
		}
		//根据服务状态 更改订单的服务状态
		if (StringUtils.isNotBlank(orderInfo.getServiceStatus())){
			//查询数据库 获取 完成时间和服务状态
			OrderInfo info = dao.appGet(orderInfo);
			//如果查询出来的订单的服务状态是取消的 返回订单已取消
			if (info.getServiceStatus().equals("cancel")){
				throw new ServiceException("订单已取消");
			}
			if (orderInfo.getServiceStatus().equals("finish")){
				Date finishTime = info.getFinishTime();
				Date date=new Date();
				//如果提前完成  更新完成时间
				if (date.before(finishTime)){
					orderInfo.setFinishTime(date);
				}
			}
			//如果是上门服务 把点击时间加入数据库
			if (orderInfo.getServiceStatus().equals("started")){
				//数据库查询出来的状态不是上门 将第一次上门时间添加到数据库 不是第一次不添加数据库
				if (!info.getServiceStatus().equals("started")){
					orderInfo.setRealServiceTime(new Date());
				}
			}
		}
		orderInfo.appPreUpdate();
		int i =0;
		try{
			i = dao.appUpdate(orderInfo);
		}catch (Exception e){
			//更改订单备注时 长度过长捕获异常信息
			throw new ServiceException("修改数据库失败可能是长度过长");
		}
		return i;
	}
	//获取订单对应机构的对接code
	public BasicOrganization getBasicOrganizationByOrgId(OrderInfo orderInfo){
		return dao.getBasicOrganizationByOrgId(orderInfo);
	}
	//app根据商品id获取订单对应商品的对接code
	public List<String> getGoodsCode(OrderInfo orderInfo){
		List<String> goods = dao.getGoods(orderInfo);
		List<String> code = new ArrayList<String>();
		if (goods.size() > 0) {
			for (String goodId : goods) {
				String goodsCode = dao.getGoodsCode(goodId);
				code.add(goodsCode);
			}
		}
		return code;
	}


	//获取字符串中第三个/的下标
	public static int index(String s) {
		Pattern p = Pattern.compile("/");
		Matcher m = p.matcher(s);
		int c=0,index=-1;
		while(m.find()){
			c++;
			index=m.start();
			if(c==3){
				return index;
			}
		}
		return index;
	}

    /**
     * 判断订单状态
     * @param orderInfo
     * @return
     */
    public boolean checkOrderStatus(OrderInfo orderInfo) {
	    orderInfo = get(orderInfo);
        String orderStatus =  orderInfo.getOrderStatus();
        String serviceStatus =  orderInfo.getServiceStatus();

	    //服务状态
	    String serviceStatusWait = "wait_service";//待服务
        String serviceStatusStarted = "started";//已上门
        String serviceStatusFinish = "finish";//已完成
        String serviceStatusCancel = "cancel";//已取消
        //订单状态
        String orderStatusWaitdispatch = "waitdispatch";//待派单
        String orderStatusDispatched = "dispatched";//已派单
        String orderStatusCancel = "cancel";//已取消
        String orderStatusStarted = "started";//已上门
        String orderStatusFinish = "finish";//已完成
        String orderStatusSuccess = "success";//已成功
        String orderStatusStop = "stop";//已暂停

        /*
            订单状态为已取消
            订单状态为已完成
            服务状态为已完成
            （只要有一个满足就可以）
         */
	    if(orderStatusCancel.equals(orderStatus) || orderStatusFinish.equals(orderStatus) ||
                serviceStatusFinish.equals(serviceStatus) || serviceStatusCancel.equals(serviceStatus) ){
	        return  false;
        }else{
	        return true;
        }
    }
}