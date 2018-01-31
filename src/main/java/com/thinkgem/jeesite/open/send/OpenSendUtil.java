/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.send;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.*;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.Base64Encoder;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.OpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 调用国安社区接口
 * @author a
 * @version 2018-1-11
 */

public class OpenSendUtil {

	/**
	 *  国安社区开放接口 - 服务机构E店Code是否有效
	 * @param eshopCode E店编码
	 * @return
	 */
	public static OpenSendSaveItemResponse openSendCheckEshopCode(String eshopCode) {
		if (StringUtils.isEmpty(eshopCode)) {
			OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接验证信息失败-E店编码为空");
			return responseRe;
		}

		//--SEND------------------------------------------------------------------
		OpenSendSaveItemRequest request = new OpenSendSaveItemRequest();
		request.setEshop_code(eshopCode);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_eshopCode_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_eshopCode_task"));

		String json = JsonMapper.toJsonString(request);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			OpenSendSaveItemResponse response = JSON.parseObject(postClientResponse, OpenSendSaveItemResponse.class);


			SysJointLog log = new SysJointLog();
			log.setUrl(url);
			if(response != null) {
				log.setIsSuccess(0 == response.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
				log.setResponseContent(JsonMapper.toJsonString(response));
			}else{
				log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
			}
			log.setRequestContent(json);
			OpenLogUtils.saveSendLog(log);


			return response;
		}catch (Exception e){
			e.printStackTrace();
		}
		OpenSendSaveItemResponse failRe = new OpenSendSaveItemResponse();
		failRe.setMessage("对接验证信息失败-系统异常");
		failRe.setCode(1);

	/*	SysJointLog log = new SysJointLog();
		log.setUrl(url);
		if(failRe != null) {
			log.setIsSuccess(0 == failRe.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
			log.setResponseContent(JsonMapper.toJsonString(failRe));
		}else{
			log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
		}
		log.setRequestContent(json);
		OpenLogUtils.saveSendLog(log);*/

		return failRe;
	}

	/**
	 *  国安社区开放接口 - 服务项目保存
	 * @param serItemInfo
	 * @param eshopCode eshopCode
	 * @return
	 */
	public static OpenSendSaveItemResponse openSendSaveItem(SerItemInfo serItemInfo, String eshopCode) {
		if (serItemInfo == null) {
			OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接保存信息失败-服务项目为空");
			return responseRe;
		}

		//-图片-----------------------------------------------------
		List<String> carousel = serItemInfo.getPictures();
		List<String> info = serItemInfo.getPictureDetails();

		//--项目信息----------------------------------------------------
		String itemName = serItemInfo.getName();

		List<String> haveTags = new ArrayList<>();
		//系统标签格式：系统标签1,系统标签2,系统标签3,
		List<String> sysTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getTags(), ArrayList.class);
		String tags_system = ",";
		if(sysTags!=null){
			for(String sysTag : sysTags){
				if(haveTags.contains(sysTag)){
					OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
					responseRe.setCode(1);
					responseRe.setMessage("对接保存信息失败-标签重复");
					return responseRe;
				}else{
					tags_system = tags_system + sysTag + ",";
					haveTags.add(sysTag);
				}
			}
		}
		// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
		List<String> cusTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getCusTags(), ArrayList.class);
		String tags_custom = "";
		if(sysTags!=null){
			for(String cusTag : cusTags){
				if(haveTags.contains(cusTag)){
					OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
					responseRe.setCode(1);
					responseRe.setMessage("对接保存信息失败-标签重复");
					return responseRe;
				}else{
					tags_custom = tags_custom + cusTag + ",";
					haveTags.add(cusTag);
				}
			}
		}

		//String content_shelf = "yes".equals(serItemInfo.getSale()) ? "on" : "off";//上架 下架 on off
		String content_img = "";
		if(carousel != null){
			content_img = carousel.get(0);//图片 一张
		}

		String warn_number = "0";// 0
		String max_number = "999999";// 最大购买数量，默认999999
		String is_combo_split = "no";//no
		String content_type = "service";//service 类型：商品 product / 服务 service
		String is_combo = "no";//no 单一商品 no / 组合商品 yes
		String content_number = "999999";//999999

		//---商品List----------------------------------------------------------------------------
		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();//商品信息
		List<OpenSendSaveItemProduct> productList = new ArrayList<>();
		if(commoditys != null){
			for(SerItemCommodity commodity : commoditys){
				String content_name = new StringBuilder(itemName).append("(").append(commodity.getName()).append(")").toString();// 商品名称格式：项目名称（商品名）
				String content_price = commodity.getPrice().toString();// 商品价格
				String content_unit = commodity.getUnit();// 商品单位格式：次/个/间
				String self_code = commodity.getSelfCode();   //自营平台商品code  ID
				String min_number = String.valueOf(commodity.getMinPurchase());// 最小购买数量，起购数量
				String joint_goods_code = commodity.getJointGoodsCode();

				OpenSendSaveItemProduct itemProduct = new OpenSendSaveItemProduct();
				itemProduct.setId(joint_goods_code);
				itemProduct.setContent_name(content_name);// 商品名称格式：项目名称（商品名）
				itemProduct.setTags_system(tags_system);//系统标签格式：系统标签1,系统标签2,系统标签3,
				itemProduct.setTags_custom(tags_custom);// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
				itemProduct.setContent_price(content_price);// 商品价格
				itemProduct.setContent_unit(content_unit);// 商品单位格式：次/个/间
				itemProduct.setSelf_code(self_code); //自营平台商品code  ID
				itemProduct.setMin_number(min_number);// 最小购买数量，起购数量
				//itemProduct.setContent_shelf(content_shelf);//上架 下架 on off
				itemProduct.setContent_img(content_img);//图片 一张
				itemProduct.setWarn_number(warn_number);// 0
				itemProduct.setMax_number(max_number);// 最大购买数量，默认999999
				itemProduct.setIs_combo_split(is_combo_split);//no
				itemProduct.setContent_type(content_type);//service 类型：商品 product / 服务 service
				itemProduct.setIs_combo(is_combo);//no 单一商品 no / 组合商品 yes
				itemProduct.setContent_number(content_number);//999999
				productList.add(itemProduct);
			}
		}else{
			OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接保存信息失败-未找到商品信息");
			return responseRe;
		}

		//--SEND------------------------------------------------------------------
		OpenSendSaveItemRequest request = new OpenSendSaveItemRequest();
		request.setEshop_code(eshopCode);
		HashMap<String,Object> attachmentsMap = new HashMap<String,Object>();
		HashMap<String,Object> productMap = new HashMap<String,Object>();
		HashMap<String,Object> pictureMap = new HashMap<String,Object>();
		pictureMap.put("banner_pic",carousel);
		pictureMap.put("describe_pic",info);
		for (OpenSendSaveItemProduct product :productList){
			productMap.put(product.getSelf_code(),product);
			attachmentsMap.put(product.getSelf_code(),pictureMap);
		}
		request.setProduct(productMap);
		request.setAttachments(attachmentsMap);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_goodsSave_task"));

		String json = JsonMapper.toJsonString(request);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			OpenSendSaveItemResponse response = JSON.parseObject(postClientResponse, OpenSendSaveItemResponse.class);


			SysJointLog log = new SysJointLog();
			log.setUrl(url);
			if(response != null) {
				log.setIsSuccess(0 == response.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
				log.setResponseContent(JsonMapper.toJsonString(response));
			}else{
				log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
			}
			log.setRequestContent(json);
			OpenLogUtils.saveSendLog(log);


			return response;
		}catch (Exception e){
			e.printStackTrace();
		}
		OpenSendSaveItemResponse failRe = new OpenSendSaveItemResponse();
		failRe.setMessage("对接保存信息失败-系统异常");
		failRe.setCode(1);

		/*SysJointLog log = new SysJointLog();
		log.setUrl(url);
		if(failRe != null) {
			log.setIsSuccess(0 == failRe.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
			log.setResponseContent(JsonMapper.toJsonString(failRe));
		}else{
			log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
		}
		log.setRequestContent(json);
		OpenLogUtils.saveSendLog(log);*/

		return failRe;
	}

	/**
	 *  国安社区开放接口 - 删除服务项目保存
	 * @param serItemInfo
	 * @param eshopCode eshopCode
	 * @return
	 */
	public static OpenSendDeleteItemResponse openSendDeleteItem(SerItemInfo serItemInfo, String eshopCode) {
		if (serItemInfo == null) {
			OpenSendDeleteItemResponse responseRe = new OpenSendDeleteItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接删除失败-服务项目为空");
			return responseRe;
		}

		//---商品List----------------------------------------------------------------------------
		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();//商品信息
		List<String> productList = new ArrayList<>();
		if(commoditys != null){
			for(SerItemCommodity commodity : commoditys){
				String joint_goods_code = commodity.getJointGoodsCode();
				productList.add(joint_goods_code);
			}
		}else{
			OpenSendDeleteItemResponse responseRe = new OpenSendDeleteItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接删除失败-未找到商品信息");
			return responseRe;
		}

		//--SEND------------------------------------------------------------------
		OpenSendDeleteItemRequest request = new OpenSendDeleteItemRequest();
		request.setEshop_code(eshopCode);
		List<String> id_list = new ArrayList<>();
		List<String> master_id_list = new ArrayList<>();
		for (String product :productList){
			id_list.add(product);
			master_id_list.add(product);
		}
		request.setId(id_list);
		request.setMaster_id(master_id_list);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_goodsDel_task"));

		String json = JsonMapper.toJsonString(request);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			System.out.println(postClientResponse);
			OpenSendDeleteItemResponse response = JSON.parseObject(postClientResponse, OpenSendDeleteItemResponse.class);


			SysJointLog log = new SysJointLog();
			log.setUrl(url);
			if(response != null) {
				log.setIsSuccess(0 == response.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
				log.setResponseContent(JsonMapper.toJsonString(response));
			}else{
				log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
			}
			log.setRequestContent(json);
			OpenLogUtils.saveSendLog(log);


			return response;
		}catch (Exception e){
			e.printStackTrace();
		}
		OpenSendDeleteItemResponse failRe = new OpenSendDeleteItemResponse();
		failRe.setMessage("对接删除失败-系统异常");
		failRe.setCode(1);

		/*SysJointLog log = new SysJointLog();
		log.setUrl(url);
		if(failRe != null) {
			log.setIsSuccess(0 == failRe.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
			log.setResponseContent(JsonMapper.toJsonString(failRe));
		}else{
			log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
		}
		log.setRequestContent(json);
		OpenLogUtils.saveSendLog(log);*/

		return failRe;
	}

	/**
	 *  国安社区开放接口 - 更新订单信息
	 *  更新服务时间、改派服务人员、服务人员端备注
	 * @param info
	 * @return
	 */
	public static OpenSendSaveOrderResponse openSendSaveOrder(OrderInfo info) {
		if (info == null) {
			OpenSendSaveOrderResponse responseRe = new OpenSendSaveOrderResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接保存信息失败-上门服务时间,服务人员,备注必传其一");
			return responseRe;
		}
		String service_time = null;
		if(info.getServiceTime() != null){
			service_time = DateUtils.formatDateTime(info.getServiceTime());//上门服务时间；上门服务时间和服务人员备注必传其一
		}
		String order_remark = info.getOrderRemark();//服务人员备注 ；上门服务时间和服务人员备注必传其一
		List<String>  order_remark_pic = info.getOrderRemarkPics();//服务人员备注 ；上门服务时间和服务人员备注必传其一
		String order_sn = info.getOrderNumber();//订单编号
		List<OrderDispatch> techList = info.getTechList();//技师信息

		if(StringUtils.isBlank(service_time) && (StringUtils.isBlank(order_remark) && order_remark_pic==null) && techList==null){
			OpenSendSaveOrderResponse responseRe = new OpenSendSaveOrderResponse();
			responseRe.setCode(1);
			responseRe.setMessage("对接保存信息失败-上门服务时间,服务人员,备注必传其一");
			return responseRe;
		}
		List<OpenSendSaveOrderServiceTechInfo> techListSend = new ArrayList<>();
		if(techList != null){
			for(OrderDispatch dispatch : techList){
				OpenSendSaveOrderServiceTechInfo techInfo = new OpenSendSaveOrderServiceTechInfo();
				techInfo.setTech_name(dispatch.getTechName());
				techInfo.setTech_phone(dispatch.getTechPhone());
				techListSend.add(techInfo);
			}
		}

		OpenSendSaveOrderRequest send = new OpenSendSaveOrderRequest();
		send.setOrder_id(order_sn);// 订单编号
		send.setService_time(service_time);//上门服务时间
		send.setOrder_remark(order_remark);//服务人员备注
		send.setOrder_remark_pic(order_remark_pic);//服务人员备注
		send.setService_tech_info(techListSend);// 技师信息

		String json = JsonMapper.toJsonString(send);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		String url =  Global.getConfig("openSendPath_gasq_updateOrderInfo");
		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);

			OpenSendSaveOrderResponse response = JSON.parseObject(postClientResponse, OpenSendSaveOrderResponse.class);

			SysJointLog log = new SysJointLog();
			log.setUrl(url);
			if(response != null) {
				log.setIsSuccess(0 == response.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
				log.setResponseContent(JsonMapper.toJsonString(response));
			}else{
				log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
			}
			log.setRequestContent(json);
			OpenLogUtils.saveSendLog(log);


			return response;
		}catch (Exception e){
			e.printStackTrace();
		}
		OpenSendSaveOrderResponse failRe = new OpenSendSaveOrderResponse();
		failRe.setMessage("对接保存信息失败-系统异常");
		failRe.setCode(1);

		/*SysJointLog log = new SysJointLog();
		log.setUrl(url);
		if(failRe != null) {
			log.setIsSuccess(0 == failRe.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
			log.setResponseContent(JsonMapper.toJsonString(failRe));
		}else{
			log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
		}
		log.setRequestContent(json);
		OpenLogUtils.saveSendLog(log);*/

		return failRe;
	}

}