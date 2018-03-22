package com.thinkgem.jeesite.modules.service.service.log;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.Base64Encoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import com.thinkgem.jeesite.modules.sys.utils.OpenWaitUtils;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.send.HTTPClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.log.SysJointLoggerDao;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;

import java.util.*;


/**
* @ClassName: SysJointLogService 
* @Description: TODO
* @author WYR
* @date 2018年1月31日 下午3:19:52 
*  
*/
@Service
@Transactional(readOnly = true)
public class SysJointLoggerService extends CrudService<SysJointLoggerDao, SysJointLogger> {
    private static final String SEND_TYPE_ORG_DEL_GOODS = "org_del_goods";
    private static final String SEND_TYPE_DEL_GOODS = "del_goods";
    private static final String SEND_TYPE_SAVE_GOODS = "save_goods";
    private static final String SEND_TYPE_SAVE_ORDER = "save_order";
    private static final String SOURCE_OWN = "own";
    private static final String MANY_YES = "yes";
    private static final int MANY_MAX_NUM = 5;

    @Autowired
    private SysJointLoggerDao sysJointLoggerDao;

    public Page<SysJointLogger> findPage(Page<SysJointLogger> page, SysJointLogger sysJointLogger) {
        //serviceLog.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
        Page<SysJointLogger> sysJointLoggerPage =  super.findPage(page, sysJointLogger);
        List<SysJointLogger> list = sysJointLoggerPage.getList();
        for(SysJointLogger info : list){
            if("own".equals(info.getSource()) && "no".equals(info.getIsSuccess())){
                info.setSendFlag("yes");
            }else{
                info.setSendFlag("no");
            }
        }
        return sysJointLoggerPage;
    }


    @Transactional(readOnly = false)
    public void doOpenSend(SysJointLogger sysJointLogger) {
        SysJointLogger info = sysJointLoggerDao.get(sysJointLogger);

        if(!SOURCE_OWN.equals(info.getSource()) || !"no".equals(info.getIsSuccess())){
            throw  new ServiceException("当前状态不可对接");
        }

        boolean flag = false;
        if (SEND_TYPE_ORG_DEL_GOODS.equals(info.getSendType())) {// 机构删除E店关联商品
            flag = doJointWaitOrgDelGoods(info);
        } else if (SEND_TYPE_DEL_GOODS.equals(info.getSendType())) {// 删除商品
            flag = doJointWaitDelGoods(info);
        } else if (SEND_TYPE_SAVE_GOODS.equals(info.getSendType())) {// 保存商品
            flag = doJointWaitSaveGoods(info);
        } else if (SEND_TYPE_SAVE_ORDER.equals(info.getSendType())) {// 更新订单信息
            flag = doJointWaitSaveOrder(info);
        } else {// 对接类型 未知
            throw  new ServiceException("当前状态不可对接");
        }
        if(flag){
            throw  new ServiceException("对接失败");
        }

        return;
    }


    /**
     * 更新订单信息
     * @param info
     */
    private boolean doJointWaitSaveOrder(SysJointLogger info) {
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
                //更新对接日志表
                SysJointLog log = new SysJointLog();
                log.setId(info.getId());
                log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                log.setResponseContent(JsonMapper.toJsonString(sendResponse));
                OpenLogUtils.updateSendLog(log);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 保存商品
     * @param info
     */
    private boolean doJointWaitSaveGoods(SysJointLogger info) {
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
                //更新对接日志表
                SysJointLog log = new SysJointLog();
                log.setId(info.getId());
                log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                log.setResponseContent(JsonMapper.toJsonString(sendResponse));
                OpenLogUtils.updateSendLog(log);

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

                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 删除商品
     * @param info
     */
    private boolean doJointWaitDelGoods(SysJointLogger info) {
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
                //更新对接日志表
                SysJointLog log = new SysJointLog();
                log.setId(info.getId());
                log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                log.setResponseContent(JsonMapper.toJsonString(sendResponse));
                OpenLogUtils.updateSendLog(log);

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

                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 机构删除E店关联商品
     * @param info
     */
    private boolean doJointWaitOrgDelGoods(SysJointLogger info) {
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
            if(response != null && response.getCode() == 0) {//执行成功
                //更新对接日志表
                SysJointLog log = new SysJointLog();
                log.setId(info.getId());
                log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                log.setResponseContent(JsonMapper.toJsonString(response));
                OpenLogUtils.updateSendLog(log);

                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
