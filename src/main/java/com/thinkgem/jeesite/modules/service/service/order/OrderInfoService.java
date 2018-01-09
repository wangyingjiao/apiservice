/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.math.BigDecimal;
import java.util.*;

import com.google.common.collect.Sets;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.map.GaoDeMapUtil;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoodsTypeHouse;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
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

	public OrderInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderInfo> findList(OrderInfo orderInfo) {
		return super.findList(orderInfo);
	}

	public OrderInfo formData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		OrderGoods goodsInfo = new OrderGoods();
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());
			goodsInfo.setGoods(goodsInfoList);
		}

		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		orderInfo.setGoodsInfo(goodsInfo);
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
		orderInfo.getSqlMap().put("dsf", dataOrderRoleFilter(UserUtils.getUser(), "a"));
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
				int cappinPerNum = goods.getCappinPerNum();		//封项人数

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
	public List<OrderDispatch> addTechSave(OrderInfo orderInfo) {
		List<String> techIdList = orderInfo.getTechIdList();//新增技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() + techIdList.size());

		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceSecond/3600);//建议服务时长（小时）
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
			techList.add(orderDispatch);
		}
		return techList;
	}

	/**
	 * 改派技师保存
	 * @param orderInfo
	 * @return
	 */
	public List<OrderDispatch> dispatchTechSave(OrderInfo orderInfo) {
		String dispatchTechId = orderInfo.getDispatchTechId();//改派前技师ID
		List<String> techIdList = orderInfo.getTechIdList();//改派技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() - 1 + techIdList.size());

		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceSecond/3600);//建议服务时长（小时）
		info.setFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.setSuggestFinishTime(DateUtils.addSeconds(serviceTime,serviceSecond.intValue()));//实际完成时间（用来计算库存）
		info.preUpdate();
		dao.update(info);
		for(OrderDispatch orderDispatch : techList){
			if(dispatchTechId.equals(orderDispatch.getTechId())){
				techList.remove(orderDispatch);//返回的技师列表删除改派前技师

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
			techList.add(orderDispatch);
		}
		return techList;

	}

	/**
	 * timeData更换时间
	 * @param orderInfo
	 * @return
	 */
	public List<String> timeData(OrderInfo orderInfo) {
		//根据传入的年月日取得当天有时间且没休假的技师
		List<OrderGoods> goodsInfoList = null;
		orderInfo = get(orderInfo.getId());//当前订单
		goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		int techDispatchNum = 0;//派人数量
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappinPerNum();		//封项人数

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
		Date serviceDate = orderInfo.getServiceTime();//服务时间年月日
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
		for(OrderDispatch tech : techList){
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = dao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){
				techList.remove(tech);

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
					for(String work : workTimes){
						if(holidays.contains(work)){//去除休假时间
							workTimes.remove(work);
							continue;
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
					for(String work : workTimes){
						if(orders.contains(work)){//去除订单时间
							workTimes.remove(work);
							continue;
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
		return resTimeList;
	}

	/**
	 * 更换时间保存
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public List<OrderDispatch> saveTime(OrderInfo orderInfo) {
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		List<OrderDispatch> techBeforList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		int techDispatchNum = 0;//派人数量
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappinPerNum();		//封项人数

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
		Date serviceDate = orderInfo.getServiceTime();//服务时间
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）
		Double serviceSecond = (serviceHour * 3600);

		int week = DateUtils.getWeekNum(serviceDate); //周几
		Date serviceDateMin = DateUtils.parseDate(DateUtils.formatDate(serviceDate, "yyyy") + "-" +
				DateUtils.formatDate(serviceDate, "MM") + "-" +
				DateUtils.formatDate(serviceDate, "dd") + " 00:00:00");
		Date serviceDateMax = DateUtils.parseDate(DateUtils.formatDate(serviceDate, "yyyy") + "-" +
				DateUtils.formatDate(serviceDate, "MM") + "-" +
				DateUtils.formatDate(serviceDate, "dd") + " 23:59:59");


		//根据传入的年月日取得当天有时间且没休假的技师
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
		for(OrderDispatch tech : techList){
			OrderDispatch serchTech = new OrderDispatch();
			//取得符合条件的技师的 工作时间 服务时间List
			serchTech.setTechId(tech.getTechId());
			serchTech.setWeek(week);
			List<ServiceTechnicianWorkTime> workTimeList = dao.findTechWorkTimeList(serchTech);
			if(workTimeList == null || workTimeList.size() == 0){
				techList.remove(tech);

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
					for(String work : workTimes){
						if(holidays.contains(work)){//去除休假时间
							workTimes.remove(work);
							continue;
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
					for(String work : workTimes){
						if(orders.contains(work)){//去除订单时间
							workTimes.remove(work);
							continue;
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
		return null;
		// order_dispatch tech status ==> no

		// insert order_dispatch new tech
		/*//更新服务时间
		dao.saveTime(orderInfo);

		List<String> techIdList = orderInfo.getTechIdList();//新增技师List
		orderInfo = get(orderInfo.getId());//当前订单
		List<OrderDispatch> techList = dao.getOrderDispatchList(orderInfo); //订单当前已有技师List
		Double serviceHour = orderInfo.getServiceHour();//建议服务时长（小时）

		Date serviceTime = orderInfo.getServiceTime();//服务时间
		//建议完成时间 增加人数后的时间计算 秒
		Double serviceSecond = ((serviceHour * 3600) * techList.size())/( techList.size() + techIdList.size());

		OrderInfo info = new OrderInfo();
		info.setId(orderInfo.getId());
		info.setServiceHour(serviceSecond/3600);//建议服务时长（小时）
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
			techList.add(orderDispatch);
		}
		return techList;*/
	}

	public List<OrderDispatch> timeData1(OrderInfo orderInfo) {

		//根据传入的年月日取得当天有时间且没休假的技师
		List<OrderGoods> goodsInfoList = null;
		if(StringUtils.isNotEmpty(orderInfo.getId())){
			orderInfo = get(orderInfo.getId());//当前订单
			goodsInfoList = dao.getOrderGoodsList(orderInfo); //取得订单服务信息
		}else{
			goodsInfoList = orderInfo.getGoodsInfoList(); //取得订单服务信息
		}
		if(goodsInfoList != null && goodsInfoList.size() != 0 ){
			int techDispatchNum = 0;//派人数量
			for(OrderGoods goods :goodsInfoList){//
				int goodsNum = goods.getGoodsNum();		// 订购商品数
				Double convertHours = goods.getConvertHours();		// 折算时长
				int startPerNum = goods.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = goods.getCappinPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double totalTime = convertHours * goodsNum;//gon

				if(totalTime > 4){//每4小时增加1人
					BigDecimal b1 = new BigDecimal(totalTime);
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
		}else{
			throw new ServiceException("订单没有服务信息！");
		}
		String stationId = orderInfo.getStationId();//服务站ID
		Date serviceDate = orderInfo.getServiceTime();//服务时间年月日
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


		//取得符合条件的技师的 工作时间 服务时间List
		//去除休假时间
		//去除订单前后时间段
		//返回合并的时间段


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

				int intervalTime = 0;//必须间隔时间 秒
				/*//无意义注释  北京市界经纬度:北纬39°26'至41°03',东经115°25'至117°30'
				String latitudeOrder = orderTech.getLatitude();//纬度
				String longitudeOrder = orderTech.getLongitude();//经度
				String origin = latitudeOrder + "," + longitudeOrder;
				String destination = latitude + "," + longitude;
				int bicyclingTime = Integer.parseInt(GaoDeMapUtil.directionBicycling(origin,destination));//骑行时间*/
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
		Page<OrderInfo> pageResult = dao.appFindList(page, orderInfo);
		return pageResult;
	}

	//订单编辑
	public int appSaveRemark(OrderInfo orderInfo){
		return dao.appUpdate(orderInfo);
	}
}