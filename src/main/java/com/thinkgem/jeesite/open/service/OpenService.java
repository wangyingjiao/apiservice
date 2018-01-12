/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.open.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OpenService extends CrudService<OrderInfoDao, OrderInfo> {

	@Autowired
	OrderMasterInfoDao orderMasterInfoDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderAddressDao orderAddressDao;
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


	/**
     * 对接接口 选择服务时间
	 * @param info
     * @return
     */
    public List<OpenServiceTimesResponse> openServiceTimes(OpenServiceTimesRequest info) {
		return null;
    }

	/**
	 * 对接接口 订单创建
	 * @param info
	 * @return
	 */
	public OpenCreateResponse openCreate(OpenCreateRequest info) {
		OpenCreateResponse response = new OpenCreateResponse();
		if(null == info){
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}
		String store_id = info.getStore_id();//门店ID
		String eshop_code = info.getEshop_code();//E店编码
		String platform = info.getPlatform();// 对接平台代号 默认值 gasq
		String phone = info.getPhone();//用户电话
		String remark = info.getRemark();//订单备注(用户备注)
		String gasq_order_id = info.getGasq_order_id();//国安社区订单ID
		String area_code = info.getArea_code();//区CODE
		String province_code = info.getProvince_code();//省CODE
		String city_code = info.getCity_code();//市CODE
		OpenServiceInfo serviceInfo = info.getServiceInfo();//购买商品ID及数量
		List<OpenServiceInfo> serviceInfos = info.getServiceInfos();
		String servie_time = info.getServie_time();//服务时间
		String address = info.getAddress();//服务地址：小区+详细地址
		String latitude = info.getLatitude();//服务地址：纬度
		String longitude = info.getLongitude();//服务地址：经度
		String sum_price = info.getSum_price();//订单总支付价格
		String order_type = info.getOrder_type();//订单类型：common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单



		// order_master_info  订单主表 ---------------------------------------------------------------------------
		OrderMasterInfo masterInfo = new OrderMasterInfo();
		masterInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		masterInfo.preInsert();
		orderMasterInfoDao.insert(masterInfo);

		// order_address  订单地址表---------------------------------------------------------------------------------
		//省名称
		List<Area>  provinceList = areaDao.getNameByCode(province_code);
		String provinceName = "";
		if(provinceList != null && provinceList.size() > 0){
			provinceName = provinceList.get(0).getName();
		}
		//市名称
		List<Area>  cityList = areaDao.getNameByCode(province_code);
		String cityName = "";
		if(cityList != null && cityList.size() > 0){
			cityName = cityList.get(0).getName();
		}
		//区名称
		List<Area>  areaList = areaDao.getNameByCode(province_code);
		String areaName = "";
		if(areaList != null && areaList.size() > 0){
			areaName = areaList.get(0).getName();
		}
		//--------------------------------------
		OrderAddress orderAddress = new OrderAddress();
		orderAddress.setName("");//姓名
		orderAddress.setPhone(phone);//手机号
		orderAddress.setZipcode("");//邮编
		orderAddress.setProvinceCode(province_code);//省_区号
		orderAddress.setCityCode(city_code);//市_区号
		orderAddress.setAreaCode(area_code);//区_区号
		orderAddress.setDetailAddress(address);//详细地址
		orderAddress.setAddress(provinceName + cityName + areaName + address);//收货人完整地址
		orderAddress.preInsert();
		orderAddressDao.insert(orderAddress);

		// order_info  子订单信息 -------------------------------------------------------------------------------
		ArrayList<OrderGoods> orderGoods = new ArrayList<>();//商品信息
		BigDecimal originPrice = new BigDecimal(0);//商品总价
		BigDecimal openPrice = new BigDecimal(0);//对接总价
		String sortItemNames = "";//服务分类+服务项目
		String goodsNames = "";//商品名称

		int techDispatchNum = 0;//派人数量
		double orderTotalTime = 0.0;//订单所需时间
		double serviceHour = 0.0;//建议服务时长（小时）

		if(null != serviceInfos && serviceInfos.size() > 0){
			OrderGoods goods = new OrderGoods();
			for(OpenServiceInfo openServiceInfo : serviceInfos){
				String cate_goods_id = openServiceInfo.getCate_goods_id();
				int buy_num = openServiceInfo.getBuy_num();
				String pay_price = openServiceInfo.getPay_price();

				SerItemCommodity commodity = orderGoodsDao.findItemGoodsByGoodId(cate_goods_id);
				goods = new OrderGoods();
				goods.setSortId(commodity.getSortId());//服务分类ID
				goods.setItemId(commodity.getItemId());//服务项目ID
				goods.setItemName(commodity.getItemName());//项目名称
				goods.setGoodsId(commodity.getId());//商品ID
				goods.setGoodsName(commodity.getName());//商品名称
				goods.setGoodsNum(buy_num);//订购商品数
				goods.setPayPrice(pay_price);//对接后单价
				goods.setOriginPrice(commodity.getPrice().toString());//原价
				goods.setMajorSort(commodity.getMajorSort());
				orderGoods.add(goods);

				originPrice = originPrice.add(commodity.getPrice());//商品总价
				openPrice = openPrice.add(new BigDecimal(pay_price));
				sortItemNames = commodity.getSortName() + commodity.getItemName();//下单服务内容(服务分类+服务项目+商品名称)',
				goodsNames = goodsNames + commodity.getName();//下单服务内容(服务分类+服务项目+商品名称)',

				int goodsNum = buy_num;		// 订购商品数
				Double convertHours = commodity.getConvertHours();		// 折算时长
				int startPerNum = commodity.getStartPerNum();   		//起步人数（第一个4小时时长派人数量）
				int cappinPerNum = commodity.getCappingPerNum();		//封项人数

				int techNum = 0;//当前商品派人数量
				int addTechNum=0;
				Double goodsTime = convertHours * goodsNum;//gon
				orderTotalTime = orderTotalTime + goodsTime;

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
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}

		//通过对接方E店CODE获取机构
		BasicOrganization organizationSerch = new BasicOrganization();
		organizationSerch.setJointEshopCode(eshop_code);
		List<BasicOrganization> organization = basicOrganizationDao.getOrganizationListByJointEshopCode(organizationSerch);
		String orgId = "";
		if(null != organization && organization.size() > 0){
			orgId = organization.get(0).getId();
		}else{
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}
		//通过门店ID获取服务站
		BasicServiceStation stationSerch = new BasicServiceStation();
		stationSerch.setStoreId(store_id);
		List<BasicServiceStation> stations = basicServiceStationDao.getStationListByStoreId(stationSerch);
		String stationId = "";
		if(null != stations && stations.size() > 0){
			stationId = stations.get(0).getId();
		}else{
			response = new OpenCreateResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}

		//--------------------------------------------------
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setMasterId(masterInfo.getId());//主订单ID
		orderInfo.setOrderType(order_type);//订单类型（common：普通订单  group_split_yes:组合并拆单  group_split_no:组合不拆单）
		orderInfo.setOrderNumber(DateUtils.getDateAndRandomTenNum("01")); // 订单编号
		orderInfo.setOrgId(orgId);  //所属服务机构ID
		orderInfo.setStationId(stationId);        //服务站id
		orderInfo.setMajorSort(orderGoods.get(0).getMajorSort());               //分类(all:全部 clean:保洁 repair:家修)
		orderInfo.setPayPrice(sum_price);            //实际付款价格
		orderInfo.setOriginPrice(originPrice.toString());              //总价（原价）
		orderInfo.setOrderAddressId(orderAddress.getId());  // 订单地址ID
		orderInfo.setLatitude(latitude);                //服务地址  纬度
		orderInfo.setLongitude(longitude);         //服务地址  经度
		orderInfo.setOrderTime(DateUtils.parseDate(DateUtils.getDateTime()));    //下单时间
		orderInfo.setServiceTime(DateUtils.parseDate(servie_time));     //上门时间（服务时间）
		Double serviceSecond = serviceHour * 3600;
		orderInfo.setFinishTime(DateUtils.addSeconds(DateUtils.parseDate(servie_time),serviceSecond.intValue()));               //实际完成时间（用来计算库存）',
		orderInfo.setSuggestFinishTime(DateUtils.addSeconds(DateUtils.parseDate(servie_time),serviceSecond.intValue()));              //建议完成时间',
		orderInfo.setServiceHour(serviceHour);                //建议服务时长（小时）',
		orderInfo.setServiceStatus("wait_service");   // 服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
		orderInfo.setOrderStatus("dispatched");   // 订单状态(waitdispatch:待派单dispatched:已派单cancel:已取消started:已上门finish:已完成success:已成功stop:已暂停)
		orderInfo.setOrderSource("gasq");  // 订单来源(own:本机构 gasq:国安社区)
		orderInfo.setPayStatus("waitpay");   //支付状态（waitpay:待支付  payed：已支付） 冗余字段
		orderInfo.setCustomerId(null);    // 客户ID
		orderInfo.setCustomerRemark(remark);   // 客户备注
		orderInfo.setCustomerRemarkPic(null);    //客户备注图片
		orderInfo.setOrderContent(sortItemNames + goodsNames);               //下单服务内容(服务分类+服务项目+商品名称)',
		orderInfo.preInsert();
		orderInfoDao.insert(orderInfo);


		// order_dispatch -派单表----------------------------------------------------------------------------------
	/*	for() {
			OrderDispatch orderDispatch = new OrderDispatch();
			orderDispatch.setOrderId(orderInfo.getId());//订单ID
			orderDispatch.setTechId();//技师ID
			orderDispatch.setStatus("yes");//状态(yes：可用 no：不可用)
			orderDispatch.preInsert();
			orderDispatchDao.insert(orderDispatch);
		}
*/
		// order_goods-订单服务关联--------------------------------------------------------------------
		if(null != orderGoods && orderGoods.size() > 0){
			for(OrderGoods goods : orderGoods){
				goods.setOrderId(orderInfo.getId());//订单ID
				goods.preInsert();
				orderGoodsDao.insert(goods);
			}
		}

		// order_pay_info  支付信息 ------------------------------------------------------------------------------
		OrderPayInfo payInfo = new OrderPayInfo();
		payInfo.setPayNumber(DateUtils.getDateAndRandomTenNum("02"));//支付编号
		payInfo.setMasterId(masterInfo.getId());//主订单ID',
		payInfo.setPayPlatform(null);//支付平台(cash:现金 wx_pub_qr:微信扫码 wx:微信 alipay_qr:支付宝扫码 alipay:支付宝 pos:银行卡 balance:余额)
		payInfo.setPayMethod(null);//支付方式(online:在线 offline:货到付款)',
		payInfo.setPayTime(null);//支付时间',
		payInfo.setPayAccount(openPrice.toString());//支付总额',
		payInfo.setPayStatus("waitpay");//支付状态(waitpay:待支付 payed:已支付)',
		payInfo.preInsert();
		orderPayInfoDao.insert(payInfo);



		//------------------------------------------------------------------------------------------------
		response = new OpenCreateResponse();
		response.setSuccess(true);// 状态：true 成功；false 失败
		response.setService_order_id(orderInfo.getId());// 自营服务订单ID
		return response;
	}

	/**
	 * 对接接口 订单状态更新
	 * @param info
	 * @return
	 */
	public OpenUpdateStautsResponse openUpdateStauts(OpenUpdateStautsRequest info) {
		OpenUpdateStautsResponse response = new OpenUpdateStautsResponse();
		if(null == info){
			response = new OpenUpdateStautsResponse();
			response.setSuccess(false);
			response.setService_order_id("");
			return response;
		}
		String platform = info.getPlatform();//对接平台代号
		String orderId = info.getService_order_id();// 自营服务订单ID
		String cancelReason = info.getComment();//取消原因
		String status = info.getStatus();//cancel 取消；finish 已签收；success 完成

		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setId(orderId);// 自营服务订单ID
		if("cancel".equals(status)){
			orderInfo.setServiceStatus("cancel");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("cancel");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
			orderInfo.setCancelReason(cancelReason);//取消原因
		}else if("finish".equals(status)){
			orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("finish");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
		}else if("success".equals(status)){
			orderInfo.setServiceStatus("finish");	//服务状态(wait_service:待服务 started:已上门, finish:已完成, cancel:已取消)
			orderInfo.setOrderStatus("success");//订单状态(waitdispatch:待派单;dispatched:已派单;cancel:已取消;started:已上门;finish:已完成;success:已成功;stop:已暂停)',
		}

		int num = orderInfoDao.openUpdateGuoanxia(orderInfo);
		if(num == 0){
			response = new OpenUpdateStautsResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id(orderId);// 自营服务订单ID
			return response;
		}else{
			response = new OpenUpdateStautsResponse();
			response.setSuccess(true);// 状态：true 成功；false 失败
			response.setService_order_id(orderId);// 自营服务订单ID
			return response;
		}
	}

	/**
	 * 对接接口 更新订单信息
	 * @param info
	 * @return
	 */
	public OpenUpdateInfoResponse openUpdateInfo(OpenUpdateInfoRequest info) {
		OpenUpdateInfoResponse response = new OpenUpdateInfoResponse();
		if(null == info){
			response = new OpenUpdateInfoResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id("");// 自营服务订单ID
			return response;
		}
		int num = 0;//更新件数
		OpenServiceInfo service_info = info.getService_info();//更新服务项目信息及数量
		if(null != service_info){
			//TODO 更新服务项目信息及数量

			num = num + 0;
		}

		OpenGuoanxiaInfo guoanxia_info = info.getGuoanxia_info();//国安侠信息
		if(null != guoanxia_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setBusinessName(guoanxia_info.getName());//业务人员姓名
			orderInfo.setBusinessPhone(guoanxia_info.getPhone());//业务人员电话
			orderInfo.setBusinessRemark(guoanxia_info.getRemark());//业务人员备注
			List<String> remark_pic = guoanxia_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setBusinessRemarkPic(remarkPic);// 业务人员备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		OpenCostomerInfo costomer_info = info.getCostomer_info();//用户信息
		if(null != costomer_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setCustomerRemark(costomer_info.getRemark());//用户备注
			List<String> remark_pic = costomer_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setCustomerRemarkPic(remarkPic);// 用户备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		OpenStoreInfo store_info = info.getStore_info();//门店信息
		if(null != store_info){
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setId(info.getService_order_id());// 自营服务订单ID
			orderInfo.setShopName(store_info.getName());//门店名
			orderInfo.setShopPhone(store_info.getTelephone());//门店电话
			orderInfo.setShopRemark(store_info.getRemark());//门店备注
			List<String> remark_pic = store_info.getRemark_pic();
			if(null != remark_pic){
				String remarkPic = JsonMapper.toJsonString(remark_pic);
				orderInfo.setShopRemarkPic(remarkPic);// 门店备注图片
			}
			num = num + orderInfoDao.openUpdateGuoanxia(orderInfo);
		}

		if(num == 0){//更新件数
			response = new OpenUpdateInfoResponse();
			response.setSuccess(false);// 状态：true 成功；false 失败
			response.setService_order_id(info.getService_order_id());// 自营服务订单ID
			return response;
		}else{
			response = new OpenUpdateInfoResponse();
			response.setSuccess(true);// 状态：true 成功；false 失败
			response.setService_order_id(info.getService_order_id());// 自营服务订单ID
			return response;
		}
	}
}