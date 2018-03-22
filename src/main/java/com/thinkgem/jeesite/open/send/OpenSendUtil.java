/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.send;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.dao.SysJointWaitDao;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import com.thinkgem.jeesite.modules.sys.utils.OpenWaitUtils;
import com.thinkgem.jeesite.open.entity.*;
import java.util.*;

/**
 * 调用国安社区接口
 * @author a
 * @version 2018-1-11
 */

public class OpenSendUtil {
	private static SysJointWaitDao sysJointWaitDao = SpringContextHolder.getBean(SysJointWaitDao.class);

	/**
	 *  国安社区开放接口 - 更新订单信息
	 *  更新服务时间、改派服务人员、服务人员端备注
	 * @param info
	 * @return
	 */
	public static void openSendSaveOrder(OrderInfo info) {
		String service_time = null;
		if(info.getServiceTime() != null){
			service_time = DateUtils.formatDateTime(info.getServiceTime());//上门服务时间；上门服务时间和服务人员备注必传其一
		}
		String order_remark = info.getOrderRemark();//服务人员备注 ；上门服务时间和服务人员备注必传其一
		List<String>  order_remark_pic = info.getOrderRemarkPics();//服务人员备注 ；上门服务时间和服务人员备注必传其一
		String order_sn = info.getOrderNumber();//订单编号
		List<OrderDispatch> techList = info.getTechList();//技师信息

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
		String url =  Global.getConfig("openSendPath_gasq_updateOrderInfo");

		SysJointWait waitInfo = new SysJointWait();
		waitInfo.setSendType("save_order");
		//waitInfo.setOrderNumber(order_sn);
		waitInfo.setUrl(url);
		waitInfo.setMany("yes");
		waitInfo.setNum(0);
		waitInfo.setRequestContent(json);
		//OpenWaitUtils.saveSendWait(waitInfo);
		waitInfo.preInsert();
		System.out.println("-- OpenSendUtil---openSendSaveOrder---" + waitInfo.toString());
		sysJointWaitDao.insert(waitInfo);
	}

    /***
     * 编辑机构删除关联E店,或对接平台为请选择
     * 机构E店表已经删除
     * 商品E店表已改
     *
     * 删除E店对接商品信息
     * 成功失败都不进行任何操作
     */
	public static void removeJointGoodsCodeByOrg(SerItemInfo serItemInfo){
		OpenSendRemoveJointGoodsRequest request = new OpenSendRemoveJointGoodsRequest();
		List<String> eshop_code = new ArrayList<>();
		HashMap<String,Object> product = new HashMap<>();

		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();//商品信息

		for(SerItemCommodity serItemCommodity :commoditys){
			List<SerItemCommodityEshop> commodityEshops = serItemCommodity.getCommodityEshops();

			HashMap<String,String> eidGid = new HashMap<>();
			for(SerItemCommodityEshop serItemCommodityEshop : commodityEshops){
				eidGid.put(serItemCommodityEshop.getEshopCode(),serItemCommodityEshop.getJointGoodsCode());

				if(!eshop_code.contains(serItemCommodityEshop.getEshopCode())){
					eshop_code.add(serItemCommodityEshop.getEshopCode());
				}
			}
			if(eidGid.size()>0) {
				product.put(serItemCommodity.getSelfCode(), eidGid);
			}
		}

		request.setProduct(product);
		request.setEshop_code(eshop_code);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_removeJointGoods_task"));
		String json = JsonMapper.toJsonString(request);
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		SysJointWait waitInfo = new SysJointWait();
		waitInfo.setSendType("org_del_goods");
		waitInfo.setUrl(url);
		waitInfo.setMany("no");
		waitInfo.setNum(0);
		waitInfo.setRequestContent(json);
		//OpenWaitUtils.saveSendWait(waitInfo);
		waitInfo.preInsert();
		System.out.println("-- OpenSendUtil---removeJointGoodsCodeByOrg---" + waitInfo.toString());
		sysJointWaitDao.insert(waitInfo);
	}

	/**
	 * 解除对接
	 * @param serItemInfo
	 */
	public static void removeJointGoodsCode(SerItemInfo serItemInfo){
		OpenSendRemoveJointGoodsRequest request = new OpenSendRemoveJointGoodsRequest();
		List<String> eshop_code = new ArrayList<>();
		HashMap<String,Object> product = new HashMap<>();

		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();//商品信息

		for(SerItemCommodity serItemCommodity :commoditys){
			List<SerItemCommodityEshop> commodityEshops = serItemCommodity.getCommodityEshops();

			HashMap<String,String> eidGid = new HashMap<>();
			for(SerItemCommodityEshop serItemCommodityEshop : commodityEshops){
				eidGid.put(serItemCommodityEshop.getEshopCode(),serItemCommodityEshop.getJointGoodsCode());

				if(!eshop_code.contains(serItemCommodityEshop.getEshopCode())){
					eshop_code.add(serItemCommodityEshop.getEshopCode());
				}
			}
			if(eidGid.size()>0) {
				product.put(serItemCommodity.getSelfCode(), eidGid);
			}
		}

		request.setProduct(product);
		request.setEshop_code(eshop_code);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_removeJointGoods_task"));
		String json = JsonMapper.toJsonString(request);
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		SysJointWait waitInfo = new SysJointWait();
		waitInfo.setSendType("del_goods");
		waitInfo.setUrl(url);
		waitInfo.setMany("yes");
		waitInfo.setNum(0);
		waitInfo.setRequestContent(json);
		//OpenWaitUtils.saveSendWait(waitInfo);
		waitInfo.preInsert();
		System.out.println("-- OpenSendUtil---removeJointGoodsCode---" + waitInfo.toString());
		sysJointWaitDao.insert(waitInfo);
	}

	/**
	 *  国安社区开放接口 - 服务项目  设置对接
	 * @param serItemList
	 * @return
	 */
	public static void insertJointGoodsCode(List<SerItemInfo> serItemList){
		OpenSendSaveItemRequest request = new OpenSendSaveItemRequest();
		List<String> eshop_code = new ArrayList<>();
		List<OpenSendSaveItemProduct> productList = new ArrayList<>();

		for(SerItemInfo serItemInfo : serItemList) {
			//-图片-----------------------------------------------------
			List<String> carousel = serItemInfo.getPictures();
			List<String> info = serItemInfo.getPictureDetails();
			HashMap<String, Object> pictureMap = new HashMap<String, Object>();
			pictureMap.put("banner_pic", carousel);
			pictureMap.put("describe_pic", info);

			//--项目信息----------------------------------------------------
			String itemName = serItemInfo.getName();

			//系统标签格式：系统标签1,系统标签2,系统标签3,
			List<String> sysTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getTags(), ArrayList.class);
			String tags_system = ",";
			if (sysTags != null) {
				for (String sysTag : sysTags) {
					tags_system = tags_system + sysTag + ",";
				}
			}
			// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
			List<String> cusTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getCusTags(), ArrayList.class);
			String tags_custom = "";
			if (sysTags != null) {
				for (String cusTag : cusTags) {
					tags_custom = tags_custom + cusTag + ",";
				}
			}

			String content_img = "";
			if (carousel != null) {
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

			if (commoditys != null) {
				for (SerItemCommodity commodity : commoditys) {
					HashMap<String, String> eidGid = new HashMap<>();
					List<SerItemCommodityEshop> commodityEshops = commodity.getCommodityEshops();
					if (commodityEshops != null) {
						for (SerItemCommodityEshop serItemCommodityEshop : commodityEshops) {
							eidGid.put(serItemCommodityEshop.getEshopCode(), serItemCommodityEshop.getJointGoodsCode());

							if (!eshop_code.contains(serItemCommodityEshop.getEshopCode())) {
								eshop_code.add(serItemCommodityEshop.getEshopCode());
							}
						}
					}

					String content_name = new StringBuilder(itemName).append("(").append(commodity.getName()).append(")").toString();// 商品名称格式：项目名称（商品名）
					String content_price = commodity.getPrice().toString();// 商品价格
					String content_unit = commodity.getUnit();// 商品单位格式：次/个/间
					String self_code = commodity.getSelfCode();   //自营平台商品code  ID
					String min_number = String.valueOf(commodity.getMinPurchase());// 最小购买数量，起购数量
					//String joint_goods_code = commodity.getJointGoodsCode();

					OpenSendSaveItemProduct itemProduct = new OpenSendSaveItemProduct();
					//itemProduct.setId(joint_goods_code);
					itemProduct.setContent_name(content_name);// 商品名称格式：项目名称（商品名）
					itemProduct.setTags_system(tags_system);//系统标签格式：系统标签1,系统标签2,系统标签3,
					itemProduct.setTags_custom(tags_custom);// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
					itemProduct.setContent_price(content_price);// 商品价格
					itemProduct.setContent_unit(content_unit);// 商品单位格式：次/个/间
					itemProduct.setSelf_code(self_code); //自营平台商品code  ID
					itemProduct.setMin_number(min_number);// 最小购买数量，起购数量
					itemProduct.setContent_img(content_img);//图片 一张
					itemProduct.setWarn_number(warn_number);// 0
					itemProduct.setMax_number(max_number);// 最大购买数量，默认999999
					itemProduct.setIs_combo_split(is_combo_split);//no
					itemProduct.setContent_type(content_type);//service 类型：商品 product / 服务 service
					itemProduct.setIs_combo(is_combo);//no 单一商品 no / 组合商品 yes
					itemProduct.setContent_number(content_number);//999999
					itemProduct.setAttachments(pictureMap);
					itemProduct.setEshop_codes(eidGid);
					productList.add(itemProduct);
				}
			}
		}
		//--SEND------------------------------------------------------------------
		HashMap<String,Object> productMap = new HashMap<String,Object>();
		for (OpenSendSaveItemProduct product :productList){
			productMap.put(product.getSelf_code(),product);
		}
		request.setProduct(productMap);
		request.setEshop_code(eshop_code);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_insertJointGoods_task"));

		String json = JsonMapper.toJsonString(request);
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		SysJointWait waitInfo = new SysJointWait();
		waitInfo.setSendType("save_goods");
		waitInfo.setUrl(url);
		waitInfo.setMany("yes");
		waitInfo.setNum(0);
		waitInfo.setRequestContent(json);
		//OpenWaitUtils.saveSendWait(waitInfo);
		waitInfo.preInsert();
		System.out.println("-- OpenSendUtil---insertJointGoodsCode---" + waitInfo.toString());
		sysJointWaitDao.insert(waitInfo);
	}

	/**
	 *  国安社区开放接口 - 服务项目 更新保存
	 * @param serItemInfo
	 * @return
	 */
	public static void updateJointGoodsCode(SerItemInfo serItemInfo){
		OpenSendSaveItemRequest request = new OpenSendSaveItemRequest();
		List<String> eshop_code = new ArrayList<>();

		//-图片-----------------------------------------------------
		List<String> carousel = serItemInfo.getPictures();
		List<String> info = serItemInfo.getPictureDetails();
		HashMap<String,Object> pictureMap = new HashMap<String,Object>();
		pictureMap.put("banner_pic",carousel);
		pictureMap.put("describe_pic",info);

		//--项目信息----------------------------------------------------
		String itemName = serItemInfo.getName();

		//系统标签格式：系统标签1,系统标签2,系统标签3,
		List<String> sysTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getTags(), ArrayList.class);
		String tags_system = ",";
		if(sysTags!=null){
			for(String sysTag : sysTags){
				tags_system = tags_system + sysTag + ",";
			}
		}
		// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
		List<String> cusTags = (List<String>) JsonMapper.fromJsonString(serItemInfo.getCusTags(), ArrayList.class);
		String tags_custom = "";
		if(sysTags!=null){
			for(String cusTag : cusTags){
				tags_custom = tags_custom + cusTag + ",";
			}
		}

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
				HashMap<String,String> eidGid = new HashMap<>();
				List<SerItemCommodityEshop> commodityEshops = commodity.getCommodityEshops();
				if(commodityEshops != null){
					for(SerItemCommodityEshop serItemCommodityEshop : commodityEshops){
						eidGid.put(serItemCommodityEshop.getEshopCode(),serItemCommodityEshop.getJointGoodsCode());

						if(!eshop_code.contains(serItemCommodityEshop.getEshopCode())){
							eshop_code.add(serItemCommodityEshop.getEshopCode());
						}
					}
				}

				String content_name = new StringBuilder(itemName).append("(").append(commodity.getName()).append(")").toString();// 商品名称格式：项目名称（商品名）
				String content_price = commodity.getPrice().toString();// 商品价格
				String content_unit = commodity.getUnit();// 商品单位格式：次/个/间
				String self_code = commodity.getSelfCode();   //自营平台商品code  ID
				String min_number = String.valueOf(commodity.getMinPurchase());// 最小购买数量，起购数量
				//String joint_goods_code = commodity.getJointGoodsCode();

				OpenSendSaveItemProduct itemProduct = new OpenSendSaveItemProduct();
				//itemProduct.setId(joint_goods_code);
				itemProduct.setContent_name(content_name);// 商品名称格式：项目名称（商品名）
				itemProduct.setTags_system(tags_system);//系统标签格式：系统标签1,系统标签2,系统标签3,
				itemProduct.setTags_custom(tags_custom);// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
				itemProduct.setContent_price(content_price);// 商品价格
				itemProduct.setContent_unit(content_unit);// 商品单位格式：次/个/间
				itemProduct.setSelf_code(self_code); //自营平台商品code  ID
				itemProduct.setMin_number(min_number);// 最小购买数量，起购数量
				itemProduct.setContent_img(content_img);//图片 一张
				itemProduct.setWarn_number(warn_number);// 0
				itemProduct.setMax_number(max_number);// 最大购买数量，默认999999
				itemProduct.setIs_combo_split(is_combo_split);//no
				itemProduct.setContent_type(content_type);//service 类型：商品 product / 服务 service
				itemProduct.setIs_combo(is_combo);//no 单一商品 no / 组合商品 yes
				itemProduct.setContent_number(content_number);//999999
				itemProduct.setAttachments(pictureMap);
				itemProduct.setEshop_codes(eidGid);
				productList.add(itemProduct);
			}
		}

		//--SEND------------------------------------------------------------------
		HashMap<String,Object> productMap = new HashMap<String,Object>();
		for (OpenSendSaveItemProduct product :productList){
			productMap.put(product.getSelf_code(),product);
		}
		request.setProduct(productMap);
		request.setEshop_code(eshop_code);

		request.setCom(Global.getConfig("openSendPath_gasq_phpGoods_com"));
		request.setClient(Global.getConfig("openSendPath_gasq_phpGoods_client"));
		request.setVer(Global.getConfig("openSendPath_gasq_phpGoods_ver"));
		request.setRequestTimestamp(new Date());
		request.setMethod(Global.getConfig("openSendPath_gasq_phpGoods_method"));
		request.setApp_com(Global.getConfig("openSendPath_gasq_php_goods_appCom"));
		request.setTask(Global.getConfig("openSendPath_gasq_php_insertJointGoods_task"));

		String json = JsonMapper.toJsonString(request);
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		SysJointWait waitInfo = new SysJointWait();
		waitInfo.setSendType("save_goods");
		waitInfo.setUrl(url);
		waitInfo.setMany("yes");
		waitInfo.setNum(0);
		waitInfo.setRequestContent(json);
		//OpenWaitUtils.saveSendWait(waitInfo);
		waitInfo.preInsert();
		System.out.println("-- OpenSendUtil---updateJointGoodsCode---" + waitInfo.toString());
		sysJointWaitDao.insert(waitInfo);
	}

}