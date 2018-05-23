/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
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
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderToolsService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.open.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OpenCreateCombinationManyService extends CrudService<OrderInfoDao, OrderInfo> {

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
	public HashMap<String,Object> openCreate(OpenCreateRequest info) {
		OpenCreateResponse response = new OpenCreateResponse();
		if(null == info){
			throw new ServiceException("未接收到订单信息!");
		}

		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		try {
			masterInfo = openCreateForMaster(info);
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

		// customer_info  客户表---------------------------------------------------------------------------------
		OrderCustomInfo orderCustomInfo = new OrderCustomInfo();
		try{
			orderCustomInfo = openCreateForCustom(info);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存客户表信息失败");
		}

		// order_combination_info  组合订单信息 -------------------------------------------------------------------------------
		CombinationOrderInfo combinationInfo = new CombinationOrderInfo();
		try{
			combinationInfo = openCreateForCombination(info, masterInfo, orderAddress, orderCustomInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存组合订单信息表信息失败!");
		}
		if(null == combinationInfo){
			throw new ServiceException("保存组合订单信息表信息失败!");
		}

		// order_pay_info  支付信息 ------------------------------------------------------------------------------
		try{
			openCreateForPay(combinationInfo);
		}catch (ServiceException ex){
			throw new ServiceException(ex.getMessage());
		}catch (Exception e){
			throw new ServiceException("保存订单支付信息表失败!");
		}

		//------------------------------------------------------------------------------------------------
		response = new OpenCreateResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_group_id(combinationInfo.getMasterId());//

		HashMap<String,Object> map = new HashMap<>();
		map.put("response",response);
		return map;
	}


	/**
	 * 订单创建 - 支付信息
	 * @param combinationInfo
	 */
	private void openCreateForPay(CombinationOrderInfo combinationInfo) {
		OrderPayInfo payInfo = new OrderPayInfo();
		payInfo.setPayNumber(DateUtils.getDateAndRandomTenNum("02"));//支付编号
		payInfo.setMasterId(combinationInfo.getMasterId());//主订单ID',
		//payInfo.setOrderId(orderInfo.getId());
		payInfo.setPayPlatform(null);//支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
		payInfo.setPayMethod(null);//支付方式(online:在线 offline:货到付款)',
		payInfo.setPayTime(null);//支付时间',
		payInfo.setPayAccount(combinationInfo.getOriginPrice());//支付总额',
		payInfo.setPayStatus("waitpay");//支付状态(waitpay:待支付 payed:已支付)',

		User user = new User();
		user.setId("gasq001");
		payInfo.setId(IdGen.uuid());
		payInfo.setCreateBy(user);
		payInfo.setCreateDate(new Date());
		payInfo.setUpdateBy(user);
		payInfo.setUpdateDate(payInfo.getCreateDate());

		orderPayInfoDao.insert(payInfo);
	}

	/**
	 * 订单创建 - 组合订单信息
	 * @param info
	 * @param masterInfo
	 * @param orderAddress
	 * @return
	 */
	private CombinationOrderInfo openCreateForCombination(OpenCreateRequest info, OrderMasterInfo masterInfo, OrderAddress orderAddress, OrderCustomInfo orderCustomInfo) {
		String group_id = info.getGroup_id();
		if(null == group_id){
			throw new ServiceException("组合订单ID不能为空");
		}
		String store_id = info.getStore_id();//门店ID
		if(null == store_id){
			throw new ServiceException("门店ID不能为空");
		}
		String shop_name = info.getStore_name();//门店名称
		String shop_phone = info.getStore_phone();//门店电话
		String shop_addr = info.getStore_addr();//门店地址

		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}
		String remark = info.getRemark();//订单备注(用户备注)
		List<String> remark_pic = info.getRemark_pic();//订单备注(用户备注)
		String remark_pic_String = "";
		if(null != remark_pic){
			remark_pic_String = JsonMapper.toJsonString(remark_pic);
		}
		List<OpenServiceInfo> serviceInfos = info.getService_info();

		List<String> gasqOrderSnList = info.getGasq_order_sn();
		if(gasqOrderSnList==null || gasqOrderSnList.size() == 0){
			throw new ServiceException("国安社区订单SN不能为空");
		}
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度
		String sum_price = info.getSum_price();//订单总支付价格
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
		if(null == order_type){
			throw new ServiceException("订单类型不能为空");
		}

		//通过对接方E店CODE获取机构
		String orgId = orderCustomInfo.getOrgId();

		//通过门店ID获取服务站
		BasicServiceStation stationSerch = new BasicServiceStation();
		stationSerch.setStoreId(store_id);
		stationSerch.setOrgId(orgId);
		List<BasicServiceStation> stations = basicServiceStationDao.getStationListByStoreIdUseable(stationSerch);
		String stationId = "";
		if(null != stations && stations.size() > 0){
			stationId = stations.get(0).getId();
		}else{
			throw new ServiceException("未找到门店ID对应的服务站信息");
		}

		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		double serviceHour = 0.0;//建议服务时长（小时）
		double orderTotalTime = 0.0;//订单所需时间

		if(null != serviceInfos && serviceInfos.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OpenServiceInfo openServiceInfo : serviceInfos){

				String key = openServiceInfo.getCate_goods_id();
				String cate_goods_id = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
				if(null == cate_goods_id){
					throw new ServiceException("自营服务服务商品ID不能为空");
				}
				int buy_num = openServiceInfo.getBuy_num();
				if(0 == buy_num){
					throw new ServiceException("购买数量不能为空");
				}
				String pay_price = openServiceInfo.getPay_price();
				if(null == pay_price){
					throw new ServiceException("服务项目单价不能为空");
				}

				//验证对接状态
				SerItemCommodityEshop serchGoodsEshop = new SerItemCommodityEshop();
				serchGoodsEshop.setEshopCode(eshop_code);
				serchGoodsEshop.setGoodsId(cate_goods_id);
				serchGoodsEshop.setOrgId(orgId);
				serchGoodsEshop.setJointStatus("butt_success");
				serchGoodsEshop.setEnabledStatus("yes");
				SerItemCommodityEshop goodsEshop = orderGoodsDao.checkJointStatus(serchGoodsEshop);
				if(null == goodsEshop){
					throw new ServiceException("未找到自营服务服务商品ID对接的商品信息");
				}

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
				goods.setPayPrice(pay_price);//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());

				goods.setConvertHours(commodity.getConvertHours());		// 折算时长

				orderGoods.add(goods);

				Double goodsTime = commodity.getConvertHours() * buy_num;//折算时长 * 订购商品数
				orderTotalTime = orderTotalTime + goodsTime;//订单商品总时长

				BigDecimal price = commodity.getPrice().multiply(new BigDecimal(buy_num));
				originPrice = originPrice.add(price);//商品总价
				sortItemNames = commodity.getSortName();//下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
				goodsNames = goodsNames + "+" + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
			}
			BigDecimal serviceHourBigD = new BigDecimal(orderTotalTime);//建议服务时长（小时） = 订单商品总时长/ 1
			serviceHour = serviceHourBigD.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		}else{
			throw new ServiceException("商品信息不能为空");
		}

		//--------------------------------------------------
		CombinationOrderInfo combinationOrderInfo = new CombinationOrderInfo();
		combinationOrderInfo.setMasterId(masterInfo.getId());//主订单ID
		combinationOrderInfo.setJointGroupId(group_id);
		combinationOrderInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		combinationOrderInfo.setOrgId(orgId);  //所属服务机构ID
		combinationOrderInfo.setStationId(stationId);        //服务站id
		combinationOrderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)  `
		combinationOrderInfo.setCombinationGoodsId(orderGoods.get(0).getGoodsId());//下单组合商品ID
		combinationOrderInfo.setCombinationGoodsName(orderGoods.get(0).getGoodsName());//下单组合商品
		combinationOrderInfo.setCombinationGoodsNum(orderGoods.get(0).getGoodsNum());//下单数量
		combinationOrderInfo.setPayPrice(sum_price);            //实际付款价格
		combinationOrderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		combinationOrderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		combinationOrderInfo.setLatitude(latitude);                //服务地址  纬度
		combinationOrderInfo.setLongitude(longitude);         //服务地址  经度
		combinationOrderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		combinationOrderInfo.setServiceNum(1);
		combinationOrderInfo.setServiceHour(serviceHour);//建议服务时长（小时）
		combinationOrderInfo.setBespeakTotal(gasqOrderSnList.size());//可预约次数
		combinationOrderInfo.setBespeakNum(0);//预约次数
		combinationOrderInfo.setOrderStatus("dispatched");   // 订单状态(dispatched:已下单;cancel:已取消;success:已成功;close:已关闭)
		combinationOrderInfo.setOrderSource("gasq");  // 订单来源(own:本机构 gasq:国安社区)
		combinationOrderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		combinationOrderInfo.setCustomerId(orderCustomInfo.getId());    // 客户ID
		combinationOrderInfo.setOrderContent(sortItemNames + goodsNames);  //下单服务内容(服务分类+服务项目+商品名称)',//订单内容改为   服务分类+商品名称1+商品名称2
		combinationOrderInfo.setShopId(store_id);
		combinationOrderInfo.setShopName(shop_name);
		combinationOrderInfo.setShopPhone(shop_phone);
		combinationOrderInfo.setShopAddr(shop_addr);
		combinationOrderInfo.setEshopCode(eshop_code);

		User user = new User();
		user.setId("gasq001");
		combinationOrderInfo.setId(IdGen.uuid());
		combinationOrderInfo.setCreateBy(user);
		combinationOrderInfo.setCreateDate(new Date());
		combinationOrderInfo.setUpdateBy(user);
		combinationOrderInfo.setUpdateDate(combinationOrderInfo.getCreateDate());
		combinationOrderDao.insert(combinationOrderInfo);

		for(String gasqOrderSn : gasqOrderSnList){
			OrderCombinationGasqInfo gasqInfo = new OrderCombinationGasqInfo();
			gasqInfo.setMasterId(masterInfo.getId());// 主订单ID
			gasqInfo.setJointOrderSn(gasqOrderSn);//国安社区订单编号

			gasqInfo.setId(IdGen.uuid());
			gasqInfo.setCreateBy(user);
			gasqInfo.setCreateDate(new Date());
			gasqInfo.setUpdateBy(user);
			gasqInfo.setUpdateDate(gasqInfo.getCreateDate());
			orderCombinationGasqDao.insert(gasqInfo);
		}
		return combinationOrderInfo;
	}

	/**
	 * 客户表
	 * @param info
	 * @return
	 */
	private OrderCustomInfo openCreateForCustom(OpenCreateRequest info) {
		String loginMobile = info.getLogin_mobile();
		String loginName = info.getLogin_name();

		String name = info.getName();//用户
		String phone = info.getPhone();//用户电话
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		String area_code = info.getArea_code();//区CODE
		String placename = info.getPlacename();////服务地址：小区
		String detailAddress = info.getDetail_address();//服务地址：门牌号
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度

		String eshop_code = info.getEshop_code();//E店编码
		if(null == eshop_code){
			throw new ServiceException("E店编码不能为空");
		}
		//通过对接方E店CODE获取机构
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(eshop_code);
		String orgId = "";
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
		}else{
			throw new ServiceException("未找到E店CODE对应的机构信息");
		}
		User user = new User();
		user.setId("gasq001");

		// 判断是否在客户表存在
		OrderCustomInfo serchCustomInfo = new OrderCustomInfo();
		serchCustomInfo.setPhone(loginMobile);
		serchCustomInfo.setOrgId(orgId);//机构ID
		serchCustomInfo.setSource("gasq");// 来源   本机构:own    国安社区:gasq
		List<OrderCustomInfo> cusInfoList = orderCustomInfoDao.findCusList(serchCustomInfo);
		OrderCustomInfo orderCustomInfo = new OrderCustomInfo();
		if (null == cusInfoList || cusInfoList.size()==0) {
			orderCustomInfo.setName(loginName);//姓名
			orderCustomInfo.setPhone(loginMobile);//手机号
			orderCustomInfo.setEmail("");//邮编
			orderCustomInfo.setSource("gasq");//来源   本机构:own    国安社区:gasq',
			orderCustomInfo.setOrgId(orgId);

			orderCustomInfo.setId(IdGen.uuid());
			orderCustomInfo.setCreateBy(user);
			orderCustomInfo.setCreateDate(new Date());
			orderCustomInfo.setUpdateBy(user);
			orderCustomInfo.setUpdateDate(orderCustomInfo.getCreateDate());

			orderCustomInfoDao.insert(orderCustomInfo);
		}else {
			// 用户手机号不能重复
			orderCustomInfo = cusInfoList.get(0);
		}

		//判断客户地址是否存在
		OrderCustomAddress serchCustomAddress = new OrderCustomAddress();
		serchCustomAddress.setCustomerId(orderCustomInfo.getId());
		serchCustomAddress.setAddressName(name);
		serchCustomAddress.setAddressPhone(phone);
		serchCustomAddress.setAddrLatitude(latitude);//服务地址：纬度
		serchCustomAddress.setAddrLongitude(longitude);//服务地址：经度
		List<OrderCustomAddress> cusAddrList = orderCustomAddressDao.findCusAddrList(serchCustomAddress);
		OrderCustomAddress orderCustomAddress = new OrderCustomAddress();
		if (null == cusAddrList || cusAddrList.size()==0) {
			// 客户地址表
			orderCustomAddress.setCustomerId(orderCustomInfo.getId());
			orderCustomAddress.setAddressName(name);
			orderCustomAddress.setAddressPhone(phone);
			orderCustomAddress.setProvinceCode(province_code);//省_区号
			orderCustomAddress.setCityCode(city_code);//市_区号
			orderCustomAddress.setAreaCode(area_code);//区_区号
			orderCustomAddress.setPlacename(placename);//小区
			orderCustomAddress.setDetailAddress(detailAddress);//详细地址
			orderCustomAddress.setAddrLatitude(latitude);//服务地址：纬度
			orderCustomAddress.setAddrLongitude(longitude);//服务地址：经度
			orderCustomAddress.setDefaultType("yes");

			orderCustomAddress.setId(IdGen.uuid());
			orderCustomAddress.setCreateBy(user);
			orderCustomAddress.setCreateDate(new Date());
			orderCustomAddress.setUpdateBy(user);
			orderCustomAddress.setUpdateDate(orderCustomAddress.getCreateDate());
			orderCustomAddressDao.insert(orderCustomAddress);
		}else{
			orderCustomAddress = cusAddrList.get(0);
		}
		List<OrderCustomAddress> orderCustomAddressList = new ArrayList<>();
		orderCustomAddressList.add(orderCustomAddress);
		orderCustomInfo.setAddressList(orderCustomAddressList);
		return orderCustomInfo;
	}

	/**
	 * 订单创建 - 订单地址表
	 * @param info
	 * @return
	 */
	private OrderAddress openCreateForAddress(OpenCreateRequest info) {
		String name = info.getName();//用户
		String phone = info.getPhone();//用户电话
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		String area_code = info.getArea_code();//区CODE
		String placename = info.getPlacename();////服务地址：小区
		String detailAddress = info.getDetail_address();//服务地址：门牌号

		//省名称
		String provinceName = info.getProvince_name();
		//市名称
		String cityName = info.getCity_name();
		//区名称
		String areaName = info.getArea_name();

		String address = provinceName + cityName + areaName + placename + detailAddress;

		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName(name);//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setPlacename(placename);//小区
		orderAddress.setDetailAddress(detailAddress);//详细地址
		orderAddress.setAddress(address);//收货人完整地址

		User user = new User();
		user.setId("gasq001");
		orderAddress.setId(IdGen.uuid());
		orderAddress.setCreateBy(user);
		orderAddress.setCreateDate(new Date());
		orderAddress.setUpdateBy(user);
		orderAddress.setUpdateDate(orderAddress.getCreateDate());

		orderAddressDao.insert(orderAddress);

		return orderAddress;
	}

	/**
	 * 订单创建 - 订单主表
	 * @param info
	 * @return
	 */
	private OrderMasterInfo openCreateForMaster(OpenCreateRequest info) {
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单
		if(null == order_type){
			throw new ServiceException("订单类型不能为空");
		}
		OrderMasterInfo masterInfo = new OrderMasterInfo();

		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		masterInfo.setId(IdGen.uuid());
		User user = new User();
		user.setId("gasq001");
		masterInfo.setCreateBy(user);
		masterInfo.setCreateDate(new Date());
		masterInfo.setUpdateBy(user);
		masterInfo.setUpdateDate(masterInfo.getCreateDate());

		orderMasterInfoDao.insert(masterInfo);
		return masterInfo;
	}

}