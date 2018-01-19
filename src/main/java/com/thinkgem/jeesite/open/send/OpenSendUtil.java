/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.send;

import com.alibaba.fastjson.JSON;
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
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.OpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 调用国安社区接口
 * @author a
 * @version 2018-1-11
 */

public class OpenSendUtil {

	/**
	 *  国安社区开放接口 - 服务项目保存
	 * @param serItemInfo
	 * @param sendType 0:新增 1：编辑
	 * @return
	 */
	public OpenSendSaveItemResponse openSendSaveItem(SerItemInfo serItemInfo, String sendType) {
		if (serItemInfo == null) {
			OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("服务项目为空");
			return responseRe;
		}

		//-图片-----------------------------------------------------
		List<String> carousel = serItemInfo.getPictures();
		List<String> info = serItemInfo.getPictureDetails();

		//--项目信息----------------------------------------------------
		String itemName = serItemInfo.getName();
		String eshop_id = serItemInfo.getEshopCode();// ESHOP_CODE
		String tags_system = serItemInfo.getTags();//系统标签格式：系统标签1,系统标签2,系统标签3,
		String tags_custom = serItemInfo.getCusTags();// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
		String content_shelf = "yes".equals(serItemInfo.getSale()) ? "on" : "off";//上架 下架 on off
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
				String self_code = commodity.getId();   //自营平台商品code  ID
				String min_number = String.valueOf(commodity.getMinPurchase());// 最小购买数量，起购数量

				OpenSendSaveItemProduct itemProduct = new OpenSendSaveItemProduct();
				itemProduct.setEshop_id(eshop_id);// ESHOP_CODE
				itemProduct.setContent_name(content_name);// 商品名称格式：项目名称（商品名）
				itemProduct.setTags_system(tags_system);//系统标签格式：系统标签1,系统标签2,系统标签3,
				itemProduct.setTags_custom(tags_custom);// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
				itemProduct.setContent_price(content_price);// 商品价格
				itemProduct.setContent_unit(content_unit);// 商品单位格式：次/个/间
				itemProduct.setSelf_code(self_code); //自营平台商品code  ID
				itemProduct.setMin_number(min_number);// 最小购买数量，起购数量
				itemProduct.setContent_shelf(content_shelf);//上架 下架 on off
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
			responseRe.setMessage("未找到商品信息");
			return responseRe;
		}

		//--SEND------------------------------------------------------------------
		OpenSendSaveItemRequest request = new OpenSendSaveItemRequest();
		request.setCarousel(carousel);
		request.setInfo(info);
		request.setProduct(productList);

		String json = JsonMapper.toJsonString(request);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		if("0".equals(sendType)){

		}else if("1".equals(sendType)){

		}else {
			OpenSendSaveItemResponse responseRe = new OpenSendSaveItemResponse();
			responseRe.setCode(1);
			responseRe.setMessage("传入参数有误");
			return responseRe;
		}
		return null;

	}

	/**
	 *  国安社区开放接口 - 更新订单信息
	 *  更新服务时间、改派服务人员、服务人员端备注
	 * @param info
	 * @return
	 */
	public OpenSendSaveOrderResponse openSendSaveOrder(OrderInfo info) {
		if (info == null) {
			OpenSendSaveOrderResponse responseRe = new OpenSendSaveOrderResponse();
			responseRe.setCode(1);
			responseRe.setMessage("上门服务时间和服务人员备注必传其一");
			return responseRe;
		}
		String service_time = DateUtils.formatDateTime(info.getServiceTime());//上门服务时间；上门服务时间和服务人员备注必传其一
		String order_remark = info.getOrderRemark();//服务人员备注 ；上门服务时间和服务人员备注必传其一
		if(StringUtils.isBlank(service_time) && StringUtils.isBlank(order_remark)){
			OpenSendSaveOrderResponse responseRe = new OpenSendSaveOrderResponse();
			responseRe.setCode(1);
			responseRe.setMessage("上门服务时间和服务人员备注必传其一");
			return responseRe;
		}
		String order_id = info.getId();//订单ID
		List<OrderDispatch> techList = info.getTechList();//技师信息
		List<OpenSendSaveOrderServiceTechInfo> techListSend = new ArrayList<>();
		if(techList != null){
			for(OrderDispatch dispatch : techList){
				OpenSendSaveOrderServiceTechInfo techInfo = new OpenSendSaveOrderServiceTechInfo();
				techInfo.setTech_name(dispatch.getTechName());
				techInfo.setTech_phone(dispatch.getTechPhone());
				techListSend.add(techInfo);
			}
		}else{
			OpenSendSaveOrderResponse responseRe = new OpenSendSaveOrderResponse();
			responseRe.setCode(1);
			responseRe.setMessage("未找到技师信息");
			return responseRe;
		}

		OpenSendSaveOrderRequest send = new OpenSendSaveOrderRequest();
		send.setOrder_id(order_id);// 订单ID
		send.setService_time(service_time);//上门服务时间
		send.setOrder_remark(order_remark);//服务人员备注
		send.setService_tech_info(techListSend);// 技师信息

		String json = JsonMapper.toJsonString(send);
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));




		return null;
	}



}