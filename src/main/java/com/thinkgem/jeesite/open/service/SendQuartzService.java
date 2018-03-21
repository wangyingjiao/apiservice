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

	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	SysJointWaitDao sysJointWaitDao;

	@Transactional(readOnly = false)
	public void doJointWait() {
		List<SysJointWait> list = sysJointWaitDao.findJointWaitList();
		if(list==null || list.size()==0){
			System.out.println("无待执行对接数据");
		}else{
			for(SysJointWait info : list){
				if(StringUtils.isBlank(info.getRequestContent())){
					//保存对接日志表
					SysJointLog log = new SysJointLog();
					log.setUrl(info.getUrl());
					log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
					log.setSendType(info.getSendType());
					log.setSource("own");
					log.setRequestContent(info.getRequestContent());
					OpenLogUtils.saveSendLog(log);

					//删除待执行表
					OpenWaitUtils.delSendWait(info);
					continue;
				}

				if("org_del_goods".equals(info.getSendType())){
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
						log.setSendType("org_del_goods");
						log.setSource("own");
						log.setRequestContent(json);
						OpenLogUtils.saveSendLog(log);

						//删除待执行表
						OpenWaitUtils.delSendWait(info);
					}catch (Exception e){
						e.printStackTrace();
					}

				}else if("del_goods".equals(info.getSendType())){
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
							log.setSendType("del_goods");
							log.setSource("own");
							log.setRequestContent(json);
							OpenLogUtils.saveSendLog(log);

							//删除待执行表
							OpenWaitUtils.delSendWait(info);

							//商品E店关联表不可用
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
											goodsEshopList.add(goodsEshop);
										}
									}
								}
							}
							OpenWaitUtils.updateGoodsEshopEnabledStatus(goodsEshopList);
						}else{
							if("yes".equals(info.getMany()) && info.getNum()<5){//多次请求，且5次之内
								//次数加1
								OpenWaitUtils.updateNumSendWait(info);
							}else{//执行失败
								//保存对接日志表
								SysJointLog log = new SysJointLog();
								log.setUrl(url);
								log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
								if(sendResponse != null) {
									log.setResponseContent(JsonMapper.toJsonString(sendResponse));
								}
								log.setSendType("del_goods");
								log.setSource("own");
								log.setRequestContent(json);
								OpenLogUtils.saveSendLog(log);

								//删除待执行表
								OpenWaitUtils.delSendWait(info);
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
												goodsEshop.setJointStatus("remove_fail");
												goodsEshopList.add(goodsEshop);
											}
										}
									}
								}
								OpenWaitUtils.updateGoodsEshopJointStatus(goodsEshopList);
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}else if("save_goods".equals(info.getSendType())){
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
							log.setSendType("save_goods");
							log.setSource("own");
							log.setRequestContent(json);
							OpenLogUtils.saveSendLog(log);

							//删除待执行表
							OpenWaitUtils.delSendWait(info);

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
											goodsEshopList.add(goodsEshop);
										}
									}
								}
							}

							OpenWaitUtils.updateGoodsEshopJointStatusAndCode(goodsEshopList);
						}else{
							if("yes".equals(info.getMany()) && info.getNum()<5){//多次请求，且5次之内
								//次数加1
								OpenWaitUtils.updateNumSendWait(info);
							}else{//执行失败
								//保存对接日志表
								SysJointLog log = new SysJointLog();
								log.setUrl(url);
								log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
								if(sendResponse != null) {
									log.setResponseContent(JsonMapper.toJsonString(sendResponse));
								}
								log.setSendType("save_goods");
								log.setSource("own");
								log.setRequestContent(json);
								OpenLogUtils.saveSendLog(log);

								//删除待执行表
								OpenWaitUtils.delSendWait(info);
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
												goodsEshopList.add(goodsEshop);
											}
										}
									}
								}
								OpenWaitUtils.updateGoodsEshopJointStatus(goodsEshopList);
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}else if("save_order".equals(info.getSendType())){
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
							log.setSendType("save_order");
							log.setSource("own");
							log.setRequestContent(json);
							OpenLogUtils.saveSendLog(log);

							//删除待执行表
							OpenWaitUtils.delSendWait(info);
						}else{
							if("yes".equals(info.getMany()) && info.getNum()<5){//多次请求，且5次之内
								//次数加1
								OpenWaitUtils.updateNumSendWait(info);
							}else{//执行失败
								//保存对接日志表
								SysJointLog log = new SysJointLog();
								log.setUrl(url);
								log.setIsSuccess(SysJointLog.IS_SUCCESS_NO);
								if(sendResponse != null) {
									log.setResponseContent(JsonMapper.toJsonString(sendResponse));
								}
								log.setSendType("save_order");
								log.setSource("own");
								log.setRequestContent(json);
								OpenLogUtils.saveSendLog(log);

								//删除待执行表
								OpenWaitUtils.delSendWait(info);
							}
						}

					}catch (Exception e){
						e.printStackTrace();
					}
				}else{

					//删除待执行表
					OpenWaitUtils.delSendWait(info);
				}
			}
		}

		return;
	}
}