/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.service;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.dao.SysJointLogDao;
import com.thinkgem.jeesite.modules.sys.dao.SysJointWaitDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import com.thinkgem.jeesite.modules.sys.utils.OpenWaitUtils;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.send.HTTPClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 对接接口Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class SendQuartzService extends CrudService<OrderInfoDao, OrderInfo> {
	private static final String SEND_TYPE_ORG_DEL_GOODS = "org_del_goods";
	private static final String SEND_TYPE_DEL_GOODS = "del_goods";
	private static final String SEND_TYPE_SAVE_GOODS = "save_goods";
	private static final String SEND_TYPE_SAVE_ORDER = "save_order";
	private static final String SOURCE_OWN = "own";
	private static final String MANY_YES = "yes";
	private static final int MANY_MAX_NUM = 5;

	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	SysJointWaitDao sysJointWaitDao;
	@Autowired
    SysJointLogDao sysJointLogDao;

    /**
	 * 待执行对接数据
	 */
	@Transactional(readOnly = false)
	public void doJointWait() {
		List<SysJointWait> list = sysJointWaitDao.findJointWaitList();
		if(list==null || list.size()==0){
			System.out.println("无待执行对接数据");
		}else{
			for(SysJointWait info : list){
				System.out.println("-- SendQuartzService---doJointWait---" + info.toString());
				if(StringUtils.isBlank(info.getRequestContent())){// 请求参数为空
					this.doJointWaitNull(info);
					continue;
				}else {
					if (SEND_TYPE_ORG_DEL_GOODS.equals(info.getSendType())) {// 机构删除E店关联商品
						this.doJointWaitOrgDelGoods(info);
						continue;
					} else if (SEND_TYPE_DEL_GOODS.equals(info.getSendType())) {// 删除商品
						this.doJointWaitDelGoods(info);
						continue;
					} else if (SEND_TYPE_SAVE_GOODS.equals(info.getSendType())) {// 保存商品
						this.doJointWaitSaveGoods(info);
						continue;
					} else if (SEND_TYPE_SAVE_ORDER.equals(info.getSendType())) {// 更新订单信息
						this.doJointWaitSaveOrder(info);
						continue;
					} else {// 对接类型 未知
						this.doJointWaitNull(info);
						continue;
					}
				}
			}
		}

		return;
	}

	/**
	 * 请求参数为空 或者  对接类型未知
	 * 删除待执行表 并 保存对接日志表
	 * @param info
	 */
	private void doJointWaitNull(SysJointWait info) {
		//保存对接日志表
		SysJointLog log = new SysJointLog();
		log.setUrl(info.getUrl());
		log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
		log.setSendType(info.getSendType());
		log.setSource(SOURCE_OWN);
		log.setRequestContent(info.getRequestContent());
		System.out.println("-- SendQuartzService---doJointWaitNull---" + log.toString());
		//OpenLogUtils.saveSendLog(log);
        log.preInsert();
        sysJointLogDao.insert(log);

		//删除待执行表
		System.out.println("-- SendQuartzService---doJointWaitNull---" + info.toString());
		//OpenWaitUtils.delSendWait(info);
        sysJointWaitDao.delete(info);
	}

	/**
	 * 更新订单信息
	 * @param info
	 */
	private void doJointWaitSaveOrder(SysJointWait info) {
		String json = info.getRequestContent();
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
		String url =  Global.getConfig("openSendPath_gasq_updateOrderInfo");
		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);

			OpenSendSaveOrderResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendSaveOrderResponse.class);

			if(sendResponse != null && sendResponse.getCode() == 0){//执行成功
				//保存对接日志表
				SysJointLog log = new SysJointLog();
				log.setUrl(url);
				log.setIsSuccess(SysJointLog.IS_SUCCESS_YES );
				log.setResponseContent(JsonMapper.toJsonString(sendResponse));
				log.setSendType(SEND_TYPE_SAVE_ORDER);
				log.setSource(SOURCE_OWN);
				log.setRequestContent(json);
				System.out.println("-- SendQuartzService---doJointWaitSaveOrder---1---" + log.toString());
                //OpenLogUtils.saveSendLog(log);
                log.preInsert();
                sysJointLogDao.insert(log);

				//删除待执行表
                //OpenWaitUtils.delSendWait(info);
                sysJointWaitDao.delete(info);
			}else{
				if(MANY_YES.equals(info.getMany()) && (info.getNum() < MANY_MAX_NUM)){//多次请求，且5次之内
					//次数加1
                    //OpenWaitUtils.updateNumSendWait(info);
                    sysJointWaitDao.update(info);
				}else{//执行失败
					//保存对接日志表
					SysJointLog log = new SysJointLog();
					log.setUrl(url);
					log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
					if(sendResponse != null) {
						log.setResponseContent(JsonMapper.toJsonString(sendResponse));
					}
					log.setSendType(SEND_TYPE_SAVE_ORDER);
					log.setSource(SOURCE_OWN);
					log.setRequestContent(json);
					System.out.println("-- SendQuartzService---doJointWaitSaveOrder---2---" + log.toString());
                    //OpenLogUtils.saveSendLog(log);
                    log.preInsert();
                    sysJointLogDao.insert(log);

					//删除待执行表
                    //OpenWaitUtils.delSendWait(info);
                    sysJointWaitDao.delete(info);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 保存商品
	 * @param info
	 */
	private void doJointWaitSaveGoods(SysJointWait info) {
		String json = info.getRequestContent();
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			OpenSendSaveItemResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendSaveItemResponse.class);

			if(sendResponse != null && sendResponse.getCode() == 0){//执行成功
				//保存对接日志表
				SysJointLog log = new SysJointLog();
				log.setUrl(url);
				log.setIsSuccess(SysJointLog.IS_SUCCESS_YES );
				log.setResponseContent(JsonMapper.toJsonString(sendResponse));
				log.setSendType(SEND_TYPE_SAVE_GOODS);
				log.setSource(SOURCE_OWN);
				log.setRequestContent(json);
				System.out.println("-- SendQuartzService---doJointWaitSaveGoods---1---" + log.toString());
                //OpenLogUtils.saveSendLog(log);
                log.preInsert();
                sysJointLogDao.insert(log);

				//删除待执行表
                //OpenWaitUtils.delSendWait(info);
                sysJointWaitDao.delete(info);

				//商品E店关联表不可用
				List<SerItemCommodityEshop> goodsEshopList = new ArrayList<>();
				SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
				Map<String, Object> responseData = (Map<String, Object>)JsonMapper.fromJsonString(sendResponse.getData().toString(), Map.class);
				if(responseData != null){
					Iterator iter = responseData.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String jointGoodsCode = entry.getKey().toString();
						HashMap<String,String> eidGid = (HashMap<String,String>)entry.getValue();
						if(eidGid != null) {
							Iterator iter2 = eidGid.entrySet().iterator();
							while (iter2.hasNext()) {
								Map.Entry entry2 = (Map.Entry) iter2.next();
								String eshopCode = entry2.getKey().toString();
								String value = entry2.getValue().toString();
								String goodsId = value.substring(value.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);

								goodsEshop = new SerItemCommodityEshop();
								goodsEshop.setGoodsId(goodsId);
								goodsEshop.setEshopCode(eshopCode);
								goodsEshop.setJointGoodsCode(jointGoodsCode);
								goodsEshop.setJointStatus("butt_success");
								//goodsEshopList.add(goodsEshop);
                                sysJointWaitDao.updateGoodsEshopJointStatusAndCode(goodsEshop);
							}
						}
					}
				}
				//OpenWaitUtils.updateGoodsEshopJointStatusAndCode(goodsEshopList);
			}else{
				if(MANY_YES.equals(info.getMany()) && (info.getNum() < MANY_MAX_NUM)){//多次请求，且5次之内
					//次数加1
					//OpenWaitUtils.updateNumSendWait(info);
                    sysJointWaitDao.update(info);
				}else{//执行失败
					//保存对接日志表
					SysJointLog log = new SysJointLog();
					log.setUrl(url);
					log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
					if(sendResponse != null) {
						log.setResponseContent(JsonMapper.toJsonString(sendResponse));
					}
					log.setSendType(SEND_TYPE_SAVE_GOODS);
					log.setSource(SOURCE_OWN);
					log.setRequestContent(json);
					System.out.println("-- SendQuartzService---doJointWaitSaveGoods---2---" + log.toString());
                    //OpenLogUtils.saveSendLog(log);
                    log.preInsert();
                    sysJointLogDao.insert(log);

					//删除待执行表
                    //OpenWaitUtils.delSendWait(info);
                    sysJointWaitDao.delete(info);

					//更新商品E店关联表状态-对接失败
					List<SerItemCommodityEshop> goodsEshopList = new ArrayList<>();
					SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
					OpenSendSaveItemRequest request = (OpenSendSaveItemRequest) JsonMapper.fromJsonString(json, OpenSendSaveItemRequest.class);
					HashMap<String, Object> product = request.getProduct();
					if(product != null){
						Iterator iter = product.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String key = entry.getKey().toString();
							String goodsId = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
							OpenSendSaveItemProduct itemProduct = (OpenSendSaveItemProduct)entry.getValue();
							HashMap<String,String> eidGid = itemProduct.getEshop_codes();
							if(eidGid != null){
								Iterator iter2 = eidGid.entrySet().iterator();
								while (iter2.hasNext()) {
									Map.Entry entry2 = (Map.Entry) iter2.next();
									String eshopCode = entry2.getKey().toString();
									String jointGoodsCode = entry2.getValue().toString();
									goodsEshop = new SerItemCommodityEshop();
									goodsEshop.setGoodsId(goodsId);
									goodsEshop.setEshopCode(eshopCode);
									goodsEshop.setJointGoodsCode(jointGoodsCode);
									goodsEshop.setJointStatus("butt_fail");
									//goodsEshopList.add(goodsEshop);
                                    sysJointWaitDao.updateGoodsEshopJointStatus(goodsEshop);
								}
							}
						}
					}
					//OpenWaitUtils.updateGoodsEshopJointStatus(goodsEshopList);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 删除商品
	 * @param info
	 */
	private void doJointWaitDelGoods(SysJointWait info) {
		String json = info.getRequestContent();
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");
		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");
			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			OpenSendRemoveJointGoodsResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendRemoveJointGoodsResponse.class);

			if(sendResponse != null && sendResponse.getCode() == 0){//执行成功
				//保存对接日志表
				SysJointLog log = new SysJointLog();
				log.setUrl(url);
				log.setIsSuccess(SysJointLog.IS_SUCCESS_YES );
				log.setResponseContent(JsonMapper.toJsonString(sendResponse));
				log.setSendType(SEND_TYPE_DEL_GOODS);
				log.setSource(SOURCE_OWN);
				log.setRequestContent(json);
				System.out.println("-- SendQuartzService---doJointWaitDelGoods---1---" + log.toString());
                //OpenLogUtils.saveSendLog(log);
                log.preInsert();
                sysJointLogDao.insert(log);

				//删除待执行表
                //OpenWaitUtils.delSendWait(info);
                sysJointWaitDao.delete(info);

				/*//商品E店关联表不可用
				List<SerItemCommodityEshop> goodsEshopList = new ArrayList<>();
				SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
				OpenSendRemoveJointGoodsRequest request = (OpenSendRemoveJointGoodsRequest) JsonMapper.fromJsonString(json, OpenSendRemoveJointGoodsRequest.class);
				HashMap<String, Object> product = request.getProduct();
				if(product != null){
					Iterator iter = product.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = entry.getKey().toString();
						String goodsId = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
						HashMap<String,String> eidGid = (HashMap<String,String>)entry.getValue();
						if(eidGid != null){
							Iterator iter2 = eidGid.entrySet().iterator();
							while (iter2.hasNext()) {
								Map.Entry entry2 = (Map.Entry) iter2.next();
								String eshopCode = entry2.getKey().toString();
								String jointGoodsCode = entry2.getValue().toString();
								goodsEshop = new SerItemCommodityEshop();
								goodsEshop.setGoodsId(goodsId);
								goodsEshop.setEshopCode(eshopCode);
								goodsEshop.setJointGoodsCode(jointGoodsCode);
								goodsEshop.setEnabledStatus("no");
								//goodsEshopList.add(goodsEshop);
                                sysJointWaitDao.updateGoodsEshopEnabledStatus(goodsEshop);
							}
						}
					}
				}*/
				//OpenWaitUtils.updateGoodsEshopEnabledStatus(goodsEshopList);
			}else{
				if(MANY_YES.equals(info.getMany()) && (info.getNum() < MANY_MAX_NUM)){//多次请求，且5次之内
					//次数加1
                    //OpenWaitUtils.updateNumSendWait(info);
                    sysJointWaitDao.update(info);
				}else{//执行失败
					//保存对接日志表
					SysJointLog log = new SysJointLog();
					log.setUrl(url);
					log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
					if(sendResponse != null) {
						log.setResponseContent(JsonMapper.toJsonString(sendResponse));
					}
					log.setSendType(SEND_TYPE_DEL_GOODS);
					log.setSource(SOURCE_OWN);
					log.setRequestContent(json);
					System.out.println("-- SendQuartzService---doJointWaitDelGoods---2---" + log.toString());
                    //OpenLogUtils.saveSendLog(log);
                    log.preInsert();
                    sysJointLogDao.insert(log);

					//删除待执行表
                    //OpenWaitUtils.delSendWait(info);
                    sysJointWaitDao.delete(info);

					//更新商品E店关联表状态-解除失败
					List<SerItemCommodityEshop> goodsEshopList = new ArrayList<>();
					SerItemCommodityEshop goodsEshop = new SerItemCommodityEshop();
					OpenSendRemoveJointGoodsRequest request = (OpenSendRemoveJointGoodsRequest) JsonMapper.fromJsonString(json, OpenSendRemoveJointGoodsRequest.class);
					HashMap<String, Object> product = request.getProduct();
					if(product != null){
						Iterator iter = product.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String key = entry.getKey().toString();
							String goodsId = key.substring(key.indexOf(Global.getConfig("openSendPath_goods_split")) + 1);
							HashMap<String,String> eidGid = (HashMap<String,String>)entry.getValue();
							if(eidGid != null){
								Iterator iter2 = eidGid.entrySet().iterator();
								while (iter2.hasNext()) {
									Map.Entry entry2 = (Map.Entry) iter2.next();
									String eshopCode = entry2.getKey().toString();
									String jointGoodsCode = entry2.getValue().toString();
									goodsEshop = new SerItemCommodityEshop();
									goodsEshop.setGoodsId(goodsId);
									goodsEshop.setEshopCode(eshopCode);
									goodsEshop.setJointGoodsCode(jointGoodsCode);
									goodsEshop.setEnabledStatus("yes");
									goodsEshop.setJointStatus("remove_fail");
									//goodsEshopList.add(goodsEshop);
                                    sysJointWaitDao.updateGoodsEshopJointStatusAndCode(goodsEshop);
								}
							}
						}
					}
					//OpenWaitUtils.updateGoodsEshopJointStatus(goodsEshopList);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 机构删除E店关联商品
	 * @param info
	 */
	private void doJointWaitOrgDelGoods(SysJointWait info) {
		String json = info.getRequestContent();
		String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
		String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");
		try {
			Map<String, String> params = new HashMap<>();
			params.put("md5",md5Content);
			params.put("appid", "selfService");

			String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
			OpenSendRemoveJointGoodsResponse response = JSON.parseObject(postClientResponse, OpenSendRemoveJointGoodsResponse.class);
			// 成功失败都不进行任何操作
			//保存对接日志表
			SysJointLog log = new SysJointLog();
			log.setUrl(url);
			if(response != null) {
				log.setIsSuccess(0 == response.getCode() ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
				log.setResponseContent(JsonMapper.toJsonString(response));
			}else{
				log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
			}
			log.setSendType(SEND_TYPE_ORG_DEL_GOODS);
			log.setSource(SOURCE_OWN);
			log.setRequestContent(json);
			System.out.println("-- SendQuartzService---doJointWaitOrgDelGoods---" + log.toString());
            //OpenLogUtils.saveSendLog(log);
            log.preInsert();
            sysJointLogDao.insert(log);

			//删除待执行表
            //OpenWaitUtils.delSendWait(info);
            sysJointWaitDao.delete(info);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}