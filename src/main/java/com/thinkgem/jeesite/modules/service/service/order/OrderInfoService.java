/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.BeanUtils;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.common.utils.map.GaoDeMapUtil;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderInfoService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderCustomInfoDao orderCustomInfoDao;
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





	public OrderInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderInfo> findList(OrderInfo orderInfo) {
		return super.findList(orderInfo);
	}

	//app查询详情
	public OrderInfo appFormData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		//图片路径
		PropertiesLoader loader = new PropertiesLoader("oss.properties");
		String ossHost = loader.getProperty("OSS_THUMB_HOST");
		if (null == orderInfo){
			throw new ServiceException("没有该订单");
		}
		orderInfo.setNowId(info.getNowId());
		OrderGoods goodsInfo = new OrderGoods();
		List<String> ids = new ArrayList<>();
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		List<OrderGoods> tem=new ArrayList<OrderGoods>();
		if(goodsInfoList != null && goodsInfoList.size() > 0){
			//获取服务项目id集合
			for (int i=0;i<goodsInfoList.size();i++) {
				if(!ids.contains(goodsInfoList.get(i).getItemId())) {
					ids.add(goodsInfoList.get(i).getItemId());
				}
			}
			//循环 id
			for(String id:ids){
				OrderGoods orderGoods = serItemInfoDao.getById(id);
				List<OrderGoods> orderGoodsList1 = new ArrayList<>();
				for (int i=0;i<goodsInfoList.size();i++) {
					//如果服务项目的id与商品的服务项目id相同
					if(id.equals(goodsInfoList.get(i).getItemId())){
						//加入到集合中
						orderGoodsList1.add(goodsInfoList.get(i));
						orderGoods.setItemName(goodsInfoList.get(i).getItemName());
					}
				}
				orderGoods.setGoods(orderGoodsList1);
				String picture = orderGoods.getPicture();
				if (StringUtils.isNotBlank(picture)) {
					List<String> picl = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
					if (picl !=null && picl.size()>0){
						orderGoods.setPicture(ossHost+picl.get(0));
					}
				}
				tem.add(orderGoods);
			}
		}else {
			throw new ServiceException("没有商品信息");
		}
		orderInfo.setGoodsInfoList(tem);
		//商品图片
//		List<String> picc = dao.appGetPics(orderInfo.getId());
//		String pics=null;
//		if (picc !=null && picc.size()>0){
//			pics=picc.get(0);
//		}
//		if (StringUtils.isNotBlank(pics)){
//			List<String> picl = (List<String>) JsonMapper.fromJsonString(pics, ArrayList.class);
//			if (picl !=null && picl.size()>0){
//				goodsInfo.setPicture(ossHost+picl.get(0));
//			}
//		}
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
		if(StringUtils.isNotBlank(customerRemarkPic)){
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
		if(StringUtils.isNotBlank(businessRemarkPic)){
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
		/*
		门店备注图片暂时不展示
		List<String> ls=new ArrayList<String>();
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(StringUtils.isNotBlank(shopRemarkPic)){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			if (pictureDetails.size()>0){
				for (String pic:pictureDetails){
					String url=ossHost+pic;
					ls.add(url);
				}
			}
			orderInfo.setShopRemarkPics(pictureDetails);
		}
		shop.setShopRemarkPic(ls);*/
		orderInfo.setShopInfo(shop);
		String orderSource = orderInfo.getOrderSource();
		if (StringUtils.isNotBlank(orderSource)) {
			if (orderSource.equals("own")) {
				orderInfo.setOrderSource("本机构");
			} else if (orderSource.equals("gasq")) {
				orderInfo.setOrderSource("国安社区");
			}
		}
		String serviceStatus = orderInfo.getServiceStatus();
		if (StringUtils.isNotBlank(serviceStatus)) {
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
		if (StringUtils.isNotBlank(orderStatus)) {
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
		if (StringUtils.isNotBlank(payStatus)) {
			if (payStatus.equals("waitpay")) {
				orderInfo.setPayStatusName("待支付");
			} else if (payStatus.equals("payed")) {
				orderInfo.setPayStatusName("已支付");
			}
		}
		//订单备注 数据库中的json 存的是list
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		List<String> orp=new ArrayList<String>();
		if (StringUtils.isNotBlank(orderRemarkPic)){
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
			//goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			//goodsInfo.setItemName(goodsInfoList.get(0).getItemName());

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
		/*
		门店备注图片暂时不展示
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			orderInfo.setShopRemarkPics(pictureDetails);
		}*/
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

			int intervalTimeS =  Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

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

			for(OrderDispatch orderTech : orderTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"
				if (11 <= Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH")) &&
						Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH")) < 14) {
					orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(),Integer.parseInt(Global.getConfig("order_eat_time"))));
				}
				if(!DateUtils.checkDatesRepeat(checkServiceTime,checkFinishTime,orderTech.getServiceTime(),orderTech.getFinishTime())){
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
		if(beforTimeCheckTechIdList !=null && beforTimeCheckTechIdList.size() != 0){
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
					orderTech.setServiceTime(DateUtils.addSeconds(orderTech.getServiceTime(), -(Integer.parseInt(Global.getConfig("order_split_time")))));
					int finishTimeHH = Integer.parseInt(DateUtils.formatDate(orderTech.getFinishTime(), "HH"));
					if (11 <= finishTimeHH && finishTimeHH < 14) {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (Integer.parseInt(Global.getConfig("order_split_time")) + Integer.parseInt(Global.getConfig("order_eat_time")))));
					} else {
						orderTech.setFinishTime(DateUtils.addSeconds(orderTech.getFinishTime(), (Integer.parseInt(Global.getConfig("order_split_time")))));
					}

					if (11 <= Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) &&
							Integer.parseInt(DateUtils.formatDate(finishTime, "HH")) < 14) {
						finishTime = DateUtils.addSeconds(finishTime,Integer.parseInt(Global.getConfig("order_eat_time")));
					}
					if (!DateUtils.checkDatesRepeat(serviceTime, finishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
						timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
					}
				}
			}
			if (beforTimeCheckTechList != null && beforTimeCheckTechList.size()>0) {
				for (OrderDispatch tech : beforTimeCheckTechList) {
					if (!timeCheckDelTechIdList.contains(tech.getTechId())) {
						techListRe.add(tech);
					}
				}
			}
		}
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

		//新增、改派判断库存
		boolean flag = checkTechTime(techIdList,serviceTime,DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));
		if(!flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}

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
		map.put("serviceHour", serviceHourRe);
		map.put("list", techListRe);

		map.put("orderId", orderInfo.getId());
		map.put("jointGoodsCodes",jointGoodsCodes);
		map.put("jointEshopCode", jointEshopCode);

		map.put("orderCreateMsgList",orderCreateMsgList);
		map.put("orderNumber",orderInfo.getOrderNumber());
		return map;
	}

	/**
	 * 新增、改派判断库存
	 * @param techIdList
	 * @param serviceTime
	 * @param finishTime
	 * @return
	 */
	private boolean checkTechTime(List<String> techIdList, Date serviceTime, Date finishTime) {
		boolean flag = true;
		String stationId = "";
		List<String> techIdListFull = new ArrayList<>();
		List<String> techIdListPart = new ArrayList<>();
		if(techIdList!=null && techIdList.size()>0) {
			for (String techId : techIdList) {
				ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoDao.get(techId);
				if (technicianInfo != null) {
					if("full_time".equals(technicianInfo.getJobNature())) {//岗位性质（full_time:全职，part_time:兼职）
						techIdListFull.add(techId);
					}else{
						techIdListPart.add(techId);
					}
					stationId= technicianInfo.getStationId();
				}
			}
		}else{
			return false;
		}
		int techNum = techIdList.size();
		List<String> techListRe = new ArrayList<>();
		if(techIdListPart!=null && techIdListPart.size()>0) {
			for(String techId : techIdListPart){
				techListRe.add(techId);
			}
		}
		if(techIdListFull!=null && techIdListFull.size()>0) {
			OrderDispatch serchInfo = new OrderDispatch();
			serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
			serchInfo.setStartTime(serviceTime);
			serchInfo.setEndTime(finishTime);
			serchInfo.setStationId(stationId);
			List<String> workTechIdList = dao.getTechByWorkTime(serchInfo);
			//（4）考虑技师的休假时间
			List<String> holidayTechIdList = dao.getTechByHoliday(serchInfo);

			List<OrderDispatch> beforTimeCheckTechList = new ArrayList<OrderDispatch>();
			List<String> beforTimeCheckTechIdList = new ArrayList<String>();

			for(String techId : techIdListFull){//有工作时间并且没有休假的技师 有时间接单 还未考虑是否有订单
				if((workTechIdList!=null && workTechIdList.contains(techId))
						&& (holidayTechIdList == null || (holidayTechIdList!=null && !holidayTechIdList.contains(techId))) ){
					beforTimeCheckTechIdList.add(techId);
				}
			}

			if(beforTimeCheckTechIdList.size() != 0){
				serchInfo.setTechIds(beforTimeCheckTechIdList);
				//今天的订单
				serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
				serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
				//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
				List<OrderDispatch> orderTechList = dao.getTechByOrder(serchInfo);//技师列表

				List<String> timeCheckDelTechIdList = new ArrayList<String>();

				int intervalTimeS =  Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
				int intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

				Date checkServiceTime = DateUtils.addSecondsNotDayB(serviceTime, -intervalTimeS);
				Date checkFinishTime = DateUtils.addSecondsNotDayE(serviceTime, intervalTimeE);

				for(OrderDispatch orderTech : orderTechList){
					//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
					//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
					//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
					//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
					//（4）富余时间定为10分钟"
					if (!DateUtils.checkDatesRepeat(checkServiceTime, checkFinishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
						timeCheckDelTechIdList.add(orderTech.getTechId());//有订单的技师 删除
					}
				}
				for(String techId : beforTimeCheckTechIdList){
					if(!timeCheckDelTechIdList.contains(techId)){
						techListRe.add(techId);
					}
				}
			}
		}
		if(techListRe.size() != techNum){
			return false;
		}

		return flag;
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

		//新增、改派判断库存
		boolean flag = checkTechTime(techIdList,serviceTime,DateUtils.addSeconds(serviceTime, serviceSecond.intValue()));
		if(!flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}

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
		int insert =0;
		//改派前技师ID
		String dispatchTechId = orderInfo.getDispatchTechId();
		//改派后技师id
		String techId = orderInfo.getTechId();
		String orderId = orderInfo.getId();
		ServiceTechnicianInfo tec=new ServiceTechnicianInfo();
		Date serviceTime = orderInfo.getServiceTime();
		Date finishTime = orderInfo.getFinishTime();
		List techIdList=new ArrayList();
		techIdList.add(techId);
		//新增、改派判断库存
		boolean flag = checkTechTime(techIdList,serviceTime,finishTime);
		if(!flag){
			throw new ServiceException("技师已经派单，请重新选择！");
		}

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
		//如果派单为空 说明订单不属于该技师
		if(null == orderDispatch){
			throw new ServiceException("订单不属于该技师！");
		}
		if (StringUtils.isNotBlank(orderInfo.getServiceStatus()) && StringUtils.isNotBlank(orderInfo.getOrderStatus())) {
			//订单服务状态为 wait_service:待服务 started:已上门 订单状态 waitdispatch:待派单;dispatched:已派单 可以改派
			if ((orderInfo.getServiceStatus().equals("wait_service") || orderInfo.getServiceStatus().equals("started"))
					&& (orderInfo.getOrderStatus().equals("waitdispatch") || orderInfo.getOrderStatus().equals("dispatched")) ) {

				OrderDispatch orderDispatchUpdate = new OrderDispatch();
				orderDispatchUpdate.setId(dispatchTechId);//ID
				orderDispatch.setStatus("no");//状态(yes：可用 no：不可用)
				orderDispatch.appPreUpdate();
				//数据库将改派前技师派单 设为不可用
				int update1 = orderDispatchDao.update(orderDispatch);
				//将老派单改成无效  再新增

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
			}else {
				//
				throw new ServiceException("订单状态与服务状态不符合改派要求！");
			}
		}else {
			throw new ServiceException("订单状态与服务状态不可为空！");
		}
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
			List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(startDateForWork,workTime.getEndTime());
			//-------------------取得技师 当天(15天中的某天)可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			if(workTimes != null) {
				//-------------------取得技师 当天(15天中的某天)休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
				serchTech.setStartTime(serviceDateMin);
				serchTech.setEndTime(serviceDateMax);
				List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
				if (holidayList != null && holidayList.size() != 0) {
					for (ServiceTechnicianHoliday holiday : holidayList) {
						//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(holiday.getStartTime(), holiday.getEndTime());
						List<String> holidays = DateUtils.getHeafHourTimeList(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());
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
								intervalTimeE =  Integer.parseInt(Global.getConfig("order_split_time"));
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

			int intervalTimeS =  Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

			int intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

			Date checkServiceTime = DateUtils.addSecondsNotDayB(newServiceDate, -intervalTimeS);
			Date checkFinishTime = DateUtils.addSecondsNotDayE(newFinishTime, intervalTimeE);

			for(OrderDispatch orderTech : orderTechList){
				if(!orderInfo.getId().equals(orderTech.getOrderId())) {
					//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
					//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
					//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
					//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
					//（4）富余时间定为10分钟"
					if (!DateUtils.checkDatesRepeat(checkServiceTime, checkFinishTime, orderTech.getServiceTime(), orderTech.getFinishTime())) {
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
		if (StringUtils.isNotBlank(orderRemarkPic)){
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
		orderInfo.appPreUpdate();
		int i =0;
		try{
			i = dao.appUpdateRemark(orderInfo);
		}catch (Exception e){
			//更改订单备注时 长度过长捕获异常信息
			throw new ServiceException("修改数据库失败可能是长度过长");
		}
		return i;
	}

	//订单的编辑（订单备注以及订单的服务状态修改）
	@Transactional(readOnly = false)
	public int appSaveOrder(OrderInfo orderInfo){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//判断订单是否属于该技师
		OrderDispatch dis=new OrderDispatch();
		dis.setTechId(orderInfo.getNowId());
		dis.setOrderId(orderInfo.getId());
		OrderDispatch byOrderTechId = orderDispatchDao.getByOrderTechId(dis);
		//派单表为空 不属于
		if (null == byOrderTechId){
			throw new ServiceException("订单不属于该技师");
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
				//比数据库中结束时间早1个小时的时间
				String format = df.format(new Date(finishTime.getTime() - (long) 1 * 60 * 60 * 1000));
				Date date1 = DateUtils.parseDate(format);
				//如果提前一个多小时 不能点击 抛异常
				if (date.before(date1)){
					throw new ServiceException("最多提前一个小时点击完成");
				}
			}
			//如果是上门服务 把点击时间加入数据库
			if (orderInfo.getServiceStatus().equals("started")){
				//数据库查询出来的状态不是上门 将第一次上门时间添加到数据库 不是第一次不添加数据库
				if (!info.getServiceStatus().equals("started")){
					orderInfo.setRealServiceTime(new Date());
				}
				Date serviceTime = info.getServiceTime();
				Date date=new Date();
				String beforeService = df.format(new Date(serviceTime.getTime() - (long) 30 * 60 * 1000));
				Date before = DateUtils.parseDate(beforeService);
				if (date.before(before)){
					throw new ServiceException("最多提前半个小时点击上门服务");
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
            订单状态为已完成/已完成
            服务状态为已完成
            （只要有一个满足就可以）
         */
	    if(orderStatusCancel.equals(orderStatus) || orderStatusFinish.equals(orderStatus) || orderStatusSuccess.equals(orderStatus) ||
                serviceStatusFinish.equals(serviceStatus) || serviceStatusCancel.equals(serviceStatus) ){
	        return  false;
        }else{
	        return true;
        }
    }

	/**
	 * 通过ID返回订单编号
	 * @param orderId
	 * @return
	 */
	public String getOrderSnById(String orderId) {
    	OrderInfo orderInfo = get(orderId);
		return orderInfo.getOrderNumber();
	}

	@Transactional(readOnly = false)
	public HashMap<String,Object> createOrder(OrderInfo info) {
		// 一期暂定-普通订单
		String order_type = "common";//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单

		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		try {
			masterInfo = openCreateForMaster(info,order_type);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单主表信息失败");
		}

		// order_address  订单地址表---------------------------------------------------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		try{
			orderAddress = openCreateForAddress(info);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单地址表信息失败");
		}

		// order_info  子订单信息 -------------------------------------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		try{
			orderInfo = openCreateForOrder(info, masterInfo, orderAddress);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存子订单信息表信息失败!");
		}
		if(null == orderInfo){
			throw new ServiceException("保存子订单信息表信息失败!");
		}

		// order_dispatch -派单表----------------------------------------------------------------------------------
		List<OrderDispatch> orderDispatches = orderInfo.getTechList();//派单信息
		if(null != orderDispatches && orderDispatches.size() > 0) {
			try{
				openCreateForDispatch(orderInfo, orderDispatches);
			}catch (ServiceException ex){
				throw new ServiceException(ex.getMessage());
			}catch (Exception e){
				throw new ServiceException("保存派单信息表失败!");
			}
		}else {
			throw new ServiceException("未找到派单信息");
		}

		// order_goods-订单服务关联--------------------------------------------------------------------
		List<OrderGoods> orderGoods = orderInfo.getGoodsInfoList();//商品信息
		if(null != orderGoods && orderGoods.size() > 0){
			try{
				openCreateForGoods(orderInfo, orderGoods);
			}catch (ServiceException ex){
				throw new ServiceException(ex.getMessage());
			}catch (Exception e){
				throw new ServiceException("保存订单商品信息表失败!");
			}
		}else {
			throw new ServiceException("未找到商品信息");
		}

		// order_pay_info  支付信息 ------------------------------------------------------------------------------
		try{
			String originPrice = orderInfo.getOriginPrice();//对接总价
			openCreateForPay( masterInfo, originPrice);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单支付信息表失败!");
		}

		//------------------------------------------------------------------------------------------------
		OpenCreateResponse response = new OpenCreateResponse();

		OrderInfo orderInfoMsg = new OrderInfo();
		orderInfoMsg.setId(orderInfo.getId());
		orderInfoMsg.setOrderNumber(orderInfo.getOrderNumber());
		orderInfoMsg.setTechList(orderDispatches);

		HashMap<String,Object> map = new HashMap<>();
		map.put("response",response);
		map.put("orderInfoMsg",orderInfoMsg);
		return map;
	}

	/**
	 * 订单创建 - 支付信息
	 * @param masterInfo
	 * @param originPrice
	 */
	private void openCreateForPay(OrderMasterInfo masterInfo, String originPrice) {
		OrderPayInfo payInfo = new OrderPayInfo();
		payInfo.setPayNumber(DateUtils.getDateAndRandomTenNum("02"));//支付编号
		payInfo.setMasterId(masterInfo.getId());//主订单ID',
		payInfo.setPayPlatform(null);//支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
		payInfo.setPayMethod(null);//支付方式(online:在线 offline:货到付款)',
		payInfo.setPayTime(null);//支付时间',
		payInfo.setPayAccount(originPrice);//支付总额',
		payInfo.setPayStatus("waitpay");//支付状态(waitpay:待支付 payed:已支付)',
		payInfo.preInsert();

		orderPayInfoDao.insert(payInfo);
	}

	/**
	 * 订单创建 - 订单服务关联
	 * @param orderGoods
	 */
	private void openCreateForGoods(OrderInfo orderInfo, List<OrderGoods> orderGoods) {
		for(OrderGoods goods : orderGoods){
			goods.setOrderId(orderInfo.getId());//订单ID
			goods.preInsert();

			orderGoodsDao.insert(goods);
		}
	}

	/**
	 * 订单创建 - 派单表
	 * @param orderInfo
	 * @param orderDispatches
	 */
	private void openCreateForDispatch(OrderInfo orderInfo, List<OrderDispatch> orderDispatches) {
		for(OrderDispatch dispatch : orderDispatches){
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setTechId(dispatch.getTechId());//技师ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();

			orderDispatchDao.insert(orderDispatch);
		}
	}

	/**
	 * 订单创建 - 子订单信息
	 * @param info
	 * @param masterInfo
	 * @param orderAddress
	 * @return
	 */
	private OrderInfo openCreateForOrder(OrderInfo info, OrderMasterInfo masterInfo, OrderAddress orderAddress) {
		String customerId = info.getCustomerId();
		String remark = info.getCustomerRemark();//订单备注(用户备注)
		Date servie_time = info.getServiceTime();//服务时间
		if(null == servie_time){
			throw new ServiceException("服务时间不能为空");
		}
		String stationId = info.getStationId();

		String orgId = orderAddress.getOrgId();
		String latitude = orderAddress.getAddrLatitude();//服务地址：纬度
		String longitude = orderAddress.getAddrLongitude();//服务地址：经度

		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价

		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		List<OrderGoods> orderGoodsList = info.getGoodsInfoList();
		if(null != orderGoodsList && orderGoodsList.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OrderGoods orderGoodsInfo : orderGoodsList){
				String cate_goods_id = orderGoodsInfo.getGoodsId();
				if("house".equals(orderGoodsInfo.getGoodsType())) {//按居室的商品
					cate_goods_id = orderGoodsInfo.getHouseId();
				}
				int buy_num = orderGoodsInfo.getGoodsNum();

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				if(null == commodity){
					throw new ServiceException("未找到自营服务服务商品ID对应的商品信息");
				}
				goods = new OrderGoods();
				goods.setSortId(commodity.getSortId());//服务分类ID
				goods.setItemId(commodity.getItemId());//服务项目ID
				goods.setItemName(commodity.getItemName());//项目名称
				goods.setGoodsId(commodity.getId());//商品ID
				goods.setGoodsName(commodity.getName());//商品名称
				goods.setGoodsNum(buy_num);//订购商品数
				goods.setGoodsType(commodity.getType());
				goods.setGoodsUnit(commodity.getUnit());
				goods.setPayPrice(commodity.getPrice().toString());//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());

				goods.setConvertHours(commodity.getConvertHours());		// 折算时长
				goods.setStartPerNum(commodity.getStartPerNum());   		//起步人数（第一个4小时时长派人数量）
				goods.setCappingPerNum(commodity.getCappingPerNum());		//封项人数

				orderGoods.add(goods);
				BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
				originPrice = originPrice.add(price);//商品总价
				sortItemNames = commodity.getSortName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + "+" + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

				int goodsNum = buy_num;		// 订购商品数
				Double convertHours = commodity.getConvertHours();		// 折算时长
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double goodsTime = convertHours * goodsNum;//折算时长 * 订购商品数
				orderTotalTime = orderTotalTime + goodsTime;//订单商品总时长

				if(goodsTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(goodsTime);
					BigDecimal b2 = new BigDecimal(new Double(4));
					addTechNum= (b1.divide(b2, 0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				techNum = startPerNum + addTechNum;
				if(techNum > cappinPerNum){//每个商品的人数
					techNum = cappinPerNum;
				}
				if(techNum > techDispatchNum){//订单的最大人数
					techDispatchNum = techNum;
				}
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime/techDispatchNum);//建议服务时长（小时） = 订单商品总时长/ 派人数量
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("商品信息不能为空");
		}

		//通过对接方E店CODE获取机构
		BasicOrganization organization = basicOrganizationDao.get(orgId);
		String eshop_code = "";
		if(null != organization){
			eshop_code = organization.getJointEshopCode();
		}

		//--------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(masterInfo.getId());//主订单ID
		orderInfo.setOrderType("common");//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(orgId);  //所属服务机构ID
		orderInfo.setStationId(stationId);        //服务站id
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(originPrice.toString());            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		orderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		orderInfo.setLatitude(latitude);                //服务地址  纬度
		orderInfo.setLongitude(longitude);         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(servie_time);     //上门时间（服务时间）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(servie_time,serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(servie_time,serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource("own");  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(customerId);    // 客户ID
		orderInfo.setCustomerRemark(remark);   // 客户备注
		orderInfo.setCustomerRemarkPic(null);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.setEshopCode(eshop_code);
		orderInfo.preInsert();
		orderInfoDao.insert(orderInfo);

		orderInfo.setGoodsInfoList(orderGoods);//商品信息

		try {
			List<OrderDispatch> techList = info.getTechList();
			if(techList == null || techList.size() == 0) {
				techList = openCreateForOrderFindDispatchList(orderInfo, techDispatchNum, serviceSecond);//获取派单技师
			}
			orderInfo.setTechList(techList);//派单技师List
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("派单失败");
		}

		return orderInfo;
	}

	/**
	 * 订单创建 - 子订单信息 - 获取派单技师
	 * @param orderInfo
	 * @return
	 */
	private List<OrderDispatch> openCreateForOrderFindDispatchList(OrderInfo orderInfo,int techDispatchNum,Double serviceSecond){

		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList(); //取得订单服务信息
		String orgId = orderInfo.getOrgId();
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceTime = orderInfo.getServiceTime();//服务时间
		Date finishTime = orderInfo.getFinishTime();//完成时间

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		SerSkillSort serchSkillSort = new SerSkillSort();
		serchSkillSort.setOrgId(orgId);
		serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
		String skillId = "";
		List<SerSkillSort> skillSortList = orderInfoDao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
		if(skillSortList!=null && skillSortList.size()==1){
			skillId = skillSortList.get(0).getSkillId();
		}else{
			throw new ServiceException("未找到商品需求的技能信息");
		}
		if(null == skillId){
			throw new ServiceException("未找到当前商品对应的技能信息");
		}
		serchInfo.setSkillId(skillId);
		//（2）上线、在职
		serchInfo.setTechStatus("yes");
		serchInfo.setJobStatus("online");
		//自动派单 全职 ; 手动派单没有条件
		serchInfo.setJobNature("full_time");
		//派单、新增订单 没有订单ID ; 改派、增加技师 有订单ID
		//serchInfo.setOrderId(orderInfo.getId());
		//serchInfo.setTechName(techName);
		//serchInfo.setOrderId(orderInfo.getId());
		List<OrderDispatch> techList = orderInfoDao.getTechListBySkillId(serchInfo);

		if(techList== null || techList.size() < techDispatchNum){//技师数量不够
			throw new ServiceException("技师数量不满足当前商品的需求人数");
		}

		//（3）考虑技师的工作时间
		//取得当前机构下工作时间包括服务时间的技师
		serchInfo.setWeek(DateUtils.getWeekNum(serviceTime));
		serchInfo.setStartTime(serviceTime);
		serchInfo.setEndTime(finishTime);
		List<String> workTechIdList = orderInfoDao.getTechByWorkTime(serchInfo);
		//（4）考虑技师的休假时间
		List<String> holidayTechIdList = orderInfoDao.getTechByHoliday(serchInfo);

		List<OrderDispatch> techListRe = new ArrayList<>();
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
			//今天的订单
			serchInfo.setStartTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 00:00:00"));
			serchInfo.setEndTime(DateUtils.parseDate(DateUtils.formatDate(serviceTime, "yyyy-MM-dd") + " 23:59:59"));
			//（5）考虑技师是否已有订单" //去除有订单并且时间冲突的技师
			List<OrderDispatch> orderTechList = orderInfoDao.getTechByOrder(serchInfo);//订单结束时间在当前订单上门时间前90分钟之后的技师列表

			List<String> timeCheckDelTechIdList = new ArrayList<String>();

			int intervalTimeS = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒
			int intervalTimeE = Integer.parseInt(Global.getConfig("order_split_time"));//必须间隔时间 秒

			Date checkServiceTime = DateUtils.addSecondsNotDayB(serviceTime, -intervalTimeS);
			Date checkFinishTime = DateUtils.addSecondsNotDayE(finishTime, intervalTimeE);

			for(OrderDispatch orderTech : orderTechList){
				//（1）路上时间：计算得出，--按照骑行的时间计算 --> 15MIN
				//		上一单的用户地址与下一单用户地址之间的距离，则可以接单的时间为：路上时间+富余时间
				//（2）若上一单的完成时间在11点到14点之间，则要预留出40分钟的吃饭时间，可以接单的时间则为：40分钟+路上时间+富余时间
				//				(3)若当前时间已经超过上一单完成时间90分钟，无需按照上面的方式计算，直接视为从当前时间起就可以接单
				//（4）富余时间定为10分钟"

				if(!DateUtils.checkDatesRepeat(checkServiceTime,checkFinishTime,orderTech.getServiceTime(),orderTech.getFinishTime())){
					timeCheckDelTechIdList.add(orderTech.getTechId());
				}
			}
			for(OrderDispatch tech : beforTimeCheckTechList){
				if(!timeCheckDelTechIdList.contains(tech.getTechId())){
					techListRe.add(tech);
				}
			}

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
	 * 订单创建 - 订单地址表
	 * @param info
	 * @return
	 */
	private OrderAddress openCreateForAddress(OrderInfo info) {
		String customerId = info.getCustomerId();
		if(StringUtils.isBlank(customerId)){
			throw new ServiceException("未找到客户信息");
		}
		OrderCustomInfo customInfo = orderCustomInfoDao.get(customerId);
		if(null == customInfo){
			throw new ServiceException("未找到客户信息");
		}
		String name = customInfo.getName();
		String phone = customInfo.getPhone();//用户电话
		String province_code = customInfo.getProvinceCode();//省CODE
		String city_code = customInfo.getCityCode();//市CODE
		String area_code = customInfo.getAreaCode();//区CODE
		String detailAddress = customInfo.getAddress();//服务地址：小区+详细地址

		//省名称
		String provinceName = "";
		//市名称
		String cityName = "";
		//区名称
		String areaName = "";

		if(null != province_code) {
			List<Area> provinceList = areaDao.getNameByCode(province_code);
			if (provinceList != null && provinceList.size() > 0) {
				provinceName = provinceList.get(0).getName();
			}
		}
		if(null == city_code) {
			List<Area> cityList = areaDao.getNameByCode(city_code);
			if (cityList != null && cityList.size() > 0) {
				cityName = cityList.get(0).getName();
			}
		}
		if(null == area_code) {
			List<Area> areaList = areaDao.getNameByCode(area_code);
			if (areaList != null && areaList.size() > 0) {
				areaName = areaList.get(0).getName();
			}
		}
		String address = "";
		if(StringUtils.isNotBlank(provinceName) && StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(areaName)){
			address = provinceName + cityName + areaName + detailAddress;
		}

		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName(name);//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setDetailAddress(detailAddress);//详细地址
		orderAddress.setAddress(address);//收货人完整地址
		orderAddress.preInsert();
		orderAddressDao.insert(orderAddress);

		orderAddress.setOrgId(customInfo.getOrgId());//客户所属机构
		//orderAddress.setStationId(customInfo.getStationId());//客户所属服务站
		orderAddress.setAddrLatitude(customInfo.getAddrLatitude());//纬度
		orderAddress.setAddrLongitude(customInfo.getAddrLongitude());//经度
		return orderAddress;
	}

	/**
	 * 订单创建 - 订单主表
	 * @param info
	 * @return
	 */
	private OrderMasterInfo openCreateForMaster(OrderInfo info,String order_type) {
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		masterInfo.preInsert();
		orderMasterInfoDao.insert(masterInfo);
		return masterInfo;
	}

	/**
	 * 根据ID查找客户
	 * @param info
	 * @return
	 */
	public OrderCustomInfo findCustomerById(OrderCustomInfo info) {
		OrderCustomInfo customInfo = orderCustomInfoDao.get(info);
		if(null != customInfo){
			info.setOrgId(UserUtils.getUser().getOrganization().getId());
			List<OrderDropdownInfo> stationList = orderCustomInfoDao.findStationList(info);
			customInfo.setStationList(stationList);
		}
		return customInfo;
	}
	/**
	 * 根据手机号查找客户
	 * @param info
	 * @return
	 */
	public OrderCustomInfo findCustomerByPhone(OrderCustomInfo info) {
		info.setOrgId(UserUtils.getUser().getOrganization().getId());
		info.setSource("own");
		OrderCustomInfo customInfo = orderCustomInfoDao.findCustomerByPhone(info);
		if(null != customInfo){
			info.setOrgId(UserUtils.getUser().getOrganization().getId());
			List<OrderDropdownInfo> stationList = orderCustomInfoDao.findStationList(info);
			customInfo.setStationList(stationList);
		}
		return customInfo;
	}
	/**
	 * 获取服务项目列表
	 * @param info
	 * @return
	 */
	public List<OrderDropdownInfo> findItemList(OrderInfo info) {
		info.setOrgId(UserUtils.getUser().getOrganization().getId());
		return orderGoodsDao.findItemList(info);
	}
	/**
	 * 获取服务项目下的商品列表
	 * @param orderInfo
	 * @return
	 */
	public List<OrderGoods> findGoodsListByItem(OrderGoods orderInfo) {
		List<OrderGoods>  list = new ArrayList<OrderGoods>();
		List<OrderGoods> goodsList = dao.getGoodsList(orderInfo);    //服务项目下所有商品信息
		OrderGoods houseInfo = null;

		//得到除了居室以外的商品及选择信息
		if(goodsList!=null && goodsList.size()!=0) {
			List<OrderGoodsTypeHouse> houses = new ArrayList<OrderGoodsTypeHouse>();
			for (OrderGoods goods : goodsList) {//循环所有商品
				if ("house".equals(goods.getGoodsType())) {//按居室的商品
					OrderGoodsTypeHouse house = new OrderGoodsTypeHouse();
					house.setId(goods.getGoodsId());
					house.setName(goods.getGoodsName());
					house.setPayPrice(goods.getPayPrice());
					houses.add(house);

					if (null == houseInfo) {
						houseInfo = new OrderGoods();
						BeanUtils.copyProperties(goods, houseInfo);
					}
				} else {

					String dj = goods.getPayPrice();//商品单价
					if (StringUtils.isEmpty(dj)) {
						dj = "0";
					}
					int num = goods.getMinPurchase();//商品数量
					BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
					goods.setPayPriceSum(price.toString());//总价

					list.add(goods);
				}
			}

			if (null != houseInfo) {

				String dj = houseInfo.getPayPrice();//商品单价
				if (StringUtils.isEmpty(dj)) {
					dj = "0";
				}
				int num = houseInfo.getMinPurchase();//商品数量
				BigDecimal price = new BigDecimal(dj).multiply(new BigDecimal(num));
				houseInfo.setPayPriceSum(price.toString());//总价

				houseInfo.setGoodsChecked(true);

				if (houses != null && houses.size() > 0) {
					houseInfo.setHouseId(houses.get(0).getId());
					houseInfo.setHouses(houses);
				} else {
					houseInfo.setHouseId("");
					houseInfo.setHouses(null);
				}
				list.add(houseInfo);
			}
		}
		return list;
	}

	/**
	 * 获取商品的技师列表
	 * @param orderInfo(itemId)
	 * @return
	 */
	public List<OrderDispatch> findTechListByGoods(OrderInfo orderInfo) {
		String techName = orderInfo.getTechName();//查询条件
		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList();
		if(goodsInfoList == null || goodsInfoList.size() == 0 ){
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID

		//取得技师List
		OrderDispatch serchInfo = new OrderDispatch();
		//展示当前下单客户所在服务站的所有可服务的技师
		serchInfo.setStationId(stationId);
		//（1）会此技能的
		String orgId = UserUtils.getUser().getOrganization().getId();
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
		serchInfo.setTechName(techName);//查询条件
		List<OrderDispatch> techList = dao.getTechListBySkillId(serchInfo);

		return techList;
	}

	/**
	 * 获取技师的时间列表
	 * @param orderInfo(goodsList,tech(null))
	 * @return
	 */
	public List<OrderTimeList> findTimeListByTech(OrderInfo orderInfo) {

		List<Date> dateList = DateUtils.getAfterFifteenDays();
		List<OrderTimeList> list = new ArrayList<>();
		int value = 1;
		List<OrderGoods> goodsInfoList = orderInfo.getGoodsInfoList();
		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//

				String cate_goods_id = goods.getGoodsId();
				if("house".equals(goods.getGoodsType())) {//按居室的商品
					cate_goods_id = goods.getHouseId();
				}
				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				if(0 == goodsNum){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				Double convertHours = commodity.getConvertHours();		// 折算时长
				if(convertHours == null || 0 == convertHours){
					logger.error("未找到当前订单服务商品信息的订购商品数");
				}
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				if(0 == startPerNum){
					startPerNum =1;
				}
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数
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
		String stationId = orderInfo.getStationId();//服务站ID
		if(null == stationId){
			logger.error("未找到当前订单的服务站信息");
			return null;
		}
		Double serviceSecond = (serviceHour * 3600);

		List<OrderDispatch> techList = orderInfo.getTechList();
		if(techList==null || techList.size()==0) {
			//取得技师List
			OrderDispatch serchInfo = new OrderDispatch();
			//展示当前下单客户所在服务站的所有可服务的技师
			serchInfo.setStationId(stationId);
			//（1）会此技能的
			String orgId =  UserUtils.getUser().getOrganization().getId();
			SerSkillSort serchSkillSort = new SerSkillSort();
			serchSkillSort.setOrgId(orgId);
			serchSkillSort.setSortId(goodsInfoList.get(0).getSortId());
			String skillId = "";
			List<SerSkillSort> skillSortList = dao.getSkillIdBySortId(serchSkillSort);//通过服务分类ID取得技能ID
			if (skillSortList != null && skillSortList.size() == 1) {
				skillId = skillSortList.get(0).getSkillId();
			} else {
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
			techList = dao.getTechListBySkillId(serchInfo);
		}
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
				List<OrderDispatch> hours = findTimeListHours(date,techList, techDispatchNum,serviceSecond);
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

	private List<OrderDispatch> findTimeListHours(Date date, List<OrderDispatch> techList,int techDispatchNum,Double serviceSecond ) {
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
			List<String> workTimes = DateUtils.getHeafHourTimeListLeftBorder(startDateForWork,workTime.getEndTime());
			//-------------------取得技师 当天(15天中的某天)可用工作时间  并且转成时间点列表 结束-----------------------------------------------------------

			if(workTimes != null) {
				//-------------------取得技师 当天(15天中的某天)休假时间 转成时间点列表 如果和工作时间重复 删除该时间点 开始---------------------------------------------
				serchTech.setStartTime(serviceDateMin);
				serchTech.setEndTime(serviceDateMax);
				List<ServiceTechnicianHoliday> holidayList = dao.findTechHolidayList(serchTech);//取得今天的休假时间
				if (holidayList != null && holidayList.size() != 0) {
					for (ServiceTechnicianHoliday holiday : holidayList) {
						//List<String> holidays = DateUtils.getHeafHourTimeListLeftBorder(holiday.getStartTime(), holiday.getEndTime());
						List<String> holidays = DateUtils.getHeafHourTimeList(DateUtils.addSecondsNotDayB(holiday.getStartTime(), -serviceSecond.intValue()), holiday.getEndTime());
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
}