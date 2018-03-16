package com.thinkgem.jeesite.modules.service.service.log;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.Base64Encoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.modules.service.dao.order.OrderInfoDao;
import com.thinkgem.jeesite.modules.sys.dao.SysJointWaitDao;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import com.thinkgem.jeesite.modules.sys.utils.OpenWaitUtils;
import com.thinkgem.jeesite.open.entity.OpenSendDeleteItemRequest;
import com.thinkgem.jeesite.open.entity.OpenSendDeleteItemResponse;
import com.thinkgem.jeesite.open.entity.OpenSendSaveItemResponse;
import com.thinkgem.jeesite.open.entity.OpenSendSaveOrderResponse;
import com.thinkgem.jeesite.open.send.HTTPClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.log.SysJointLoggerDao;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @Autowired
    private SysJointLoggerDao sysJointLoggerDao;
    @Autowired
    SysJointWaitDao sysJointWaitDao;

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
        return page;
    }


    @Transactional(readOnly = false)
    public void doOpenSend(SysJointLogger sysJointLogger) {
        sysJointLogger = sysJointLoggerDao.get(sysJointLogger);

        if(!"own".equals(sysJointLogger.getSource()) || !"no".equals(sysJointLogger.getIsSuccess())){
            throw  new ServiceException("当前状态不可对接");
        }

        if("org_del_goods".equals(sysJointLogger.getSendType())){
            String json = sysJointLogger.getRequestContent();
            String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
            String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
            String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");
            try {
                Map<String, String> params = new HashMap<>();
                params.put("md5",md5Content);
                params.put("appid", "selfService");

                String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
                OpenSendDeleteItemResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendDeleteItemResponse.class);
                if(sendResponse != null && sendResponse.getCode() == 0) {//执行成功
                    SysJointLog log = new SysJointLog();
                    log.setId(sysJointLogger.getId());
                    log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                    log.setResponseContent(JsonMapper.toJsonString(sendResponse));

                    OpenLogUtils.updateSendLog(log);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if("del_goods".equals(sysJointLogger.getSendType())){
            String json = sysJointLogger.getRequestContent();
            String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
            String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
            String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");
            try {
                Map<String, String> params = new HashMap<>();
                params.put("md5",md5Content);
                params.put("appid", "selfService");
                String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
                OpenSendDeleteItemResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendDeleteItemResponse.class);

                OpenSendDeleteItemRequest sendDeleteItem = JSON.parseObject(json, OpenSendDeleteItemRequest.class);
                String eshopCode = sendDeleteItem.getEshop_code();
                List<String> jointGoodsCodes = sendDeleteItem.getId();

                if(sendResponse != null && sendResponse.getCode() == 0) {//执行成功
                    SysJointLog log = new SysJointLog();
                    log.setId(sysJointLogger.getId());
                    log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                    log.setResponseContent(JsonMapper.toJsonString(sendResponse));
                    OpenLogUtils.updateSendLog(log);

                    //删除商品E店关联表
                    OpenWaitUtils.delGoodsEshop(eshopCode,jointGoodsCodes);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if("save_goods".equals(sysJointLogger.getSendType())){
            String json = sysJointLogger.getRequestContent();
            String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
            String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

            String url =  Global.getConfig("openSendPath_gasq_insertItemInfo");

            try {
                Map<String, String> params = new HashMap<>();
                params.put("md5",md5Content);
                params.put("appid", "selfService");

                String postClientResponse = HTTPClientUtils.postClient(url,encode,params);
                OpenSendSaveItemResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendSaveItemResponse.class);

                if(sendResponse != null && sendResponse.getCode() == 0) {//执行成功
                    SysJointLog log = new SysJointLog();
                    log.setId(sysJointLogger.getId());
                    log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                    log.setResponseContent(JsonMapper.toJsonString(sendResponse));
                    OpenLogUtils.updateSendLog(log);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if("save_order".equals(sysJointLogger.getSendType())){
            String json = sysJointLogger.getRequestContent();
            String encode = Base64Encoder.encode(json).replace("\n", "").replace("\r", "");
            String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));
            String url =  Global.getConfig("openSendPath_gasq_updateOrderInfo");
            try {
                Map<String, String> params = new HashMap<>();
                params.put("md5",md5Content);
                params.put("appid", "selfService");

                String postClientResponse = HTTPClientUtils.postClient(url,encode,params);

                OpenSendSaveOrderResponse sendResponse = JSON.parseObject(postClientResponse, OpenSendSaveOrderResponse.class);

                if(sendResponse != null && sendResponse.getCode() == 0) {//执行成功
                    SysJointLog log = new SysJointLog();
                    log.setId(sysJointLogger.getId());
                    log.setIsSuccess(SysJointLog.IS_SUCCESS_YES);
                    log.setResponseContent(JsonMapper.toJsonString(sendResponse));

                    OpenLogUtils.updateSendLog(log);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return;
    }
}
