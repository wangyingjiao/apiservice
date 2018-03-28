/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.pushMessage.PushMessageUtil;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.sys.dao.MessageInfoDao;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 消息基础信息Service
 *
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class MessageInfoService extends CrudService<MessageInfoDao, MessageInfo> {

    @Autowired
    private MessageInfoDao messageInfoDao;




    //根据id 查看消息
    public MessageInfo get(MessageInfo messageInfo){
        return messageInfoDao.get(messageInfo);

    }

    //app列表查看消息
    public Page<MessageInfo> findList(Page<MessageInfo> page,MessageInfo messageInfo){
        messageInfo.setPage(page);
        List<MessageInfo> list = messageInfoDao.findList(messageInfo);
        if (list !=null && list.size() > 0) {
            for (MessageInfo info : list) {
                info.setOrderId(info.getTargetId());
                Date createTime = info.getPushTime();
                String s = DateUtils.formatDateTime(createTime);
                info.setCreateTime(s);
            }
        }
        page.setList(list);
        return page;
    }

    //app未读消息数量
    public int getCount(MessageInfo messageInfo){
        int count = messageInfoDao.getCount(messageInfo);
        return count;
    }
    //增加消息到数据库
    @Transactional(readOnly = false)
    public int insertHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday, String orderType){
        MessageInfo messageInfo = new MessageInfo();
        if (orderType.equals("techHolidaySuccess")){
            messageInfo.setTitle("您的休假已通过审核");
            messageInfo.setMessage("请点击查看");
            messageInfo.setTargetType("holiday");
            return insertAndPushHoliday(serviceTechnicianHoliday,messageInfo);
        }
        if (orderType.equals("techHolidayFail")){
            messageInfo.setTitle("您的休假未通过审核");
            messageInfo.setMessage("请点击查看");
            messageInfo.setTargetType("holiday");
            return insertAndPushHoliday(serviceTechnicianHoliday,messageInfo);
        }
        return 0;
    }

    @Transactional(readOnly = false)
    public int insertAndPushHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday, MessageInfo messageInfo){

        messageInfo.setId(IdGen.uuid());
        messageInfo.setTechId(serviceTechnicianHoliday.getTechId());
        //查询数据库 技师手机号
        messageInfo.setReceivePhone(serviceTechnicianHoliday.getTechPhone());
        messageInfo.setCreateDate(new Date());
        User user = UserUtils.getUser();
        messageInfo.setCreateBy(user);
        messageInfo.setUpdateBy(user);
        messageInfo.setPushTime(new Date());
        messageInfo.setUpdateDate(new Date());
        messageInfo.setPushTime(new Date());
        messageInfoDao.insert(messageInfo);

        messageInfo.setDeviceIds("community_tech_"+messageInfo.getReceivePhone());
        // messageInfo.setExtParameters("{\"type\":\"order\",\"relate\":\""+orderInfo.getMajorSort()+"$."+orderInfo.getId()+"$."+messageInfo.getId()+"\"}");
        int flag = PushMessageUtil.pushMessage(messageInfo);
        if (flag==1){
            messageInfo.setPushTime(new Date());
            messageInfo.setIsSuccess("yes");
            messageInfoDao.updPushTime(messageInfo);
        }else {
            messageInfo.setIsSuccess("no");
            messageInfoDao.updPushTime(messageInfo);
        }
    return 1;
    }

    @Transactional(readOnly = false)
    public int insertAndPush(OrderInfo orderInfo, MessageInfo messageInfo){

        List<OrderDispatch> techList=orderInfo.getTechList();
        for (OrderDispatch odp:techList){
            messageInfo.setId(IdGen.uuid());
            messageInfo.setTechId(odp.getTechId());
            messageInfo.setReceivePhone(odp.getTechPhone());
            messageInfo.setTargetId(orderInfo.getId());
            messageInfo.setCreateDate(new Date());
            messageInfo.setCreateBy(orderInfo.getCreateBy());
            messageInfo.setUpdateBy(orderInfo.getCreateBy());
            messageInfo.setPushTime(new Date());
            messageInfo.setUpdateDate(new Date());
            messageInfo.setPushTime(new Date());
            messageInfoDao.insert(messageInfo);

            messageInfo.setDeviceIds("community_tech_"+messageInfo.getReceivePhone());
            messageInfo.setExtParameters("{\"type\":\"order\",\"relate\":\""+orderInfo.getMajorSort()+"$."+orderInfo.getId()+"$."+messageInfo.getId()+"\"}");
            int flag = PushMessageUtil.pushMessage(messageInfo);
            if (flag==1){
                messageInfo.setPushTime(new Date());
                messageInfo.setIsSuccess("yes");
                messageInfoDao.updPushTime(messageInfo);
            }else {
                messageInfo.setIsSuccess("no");
                messageInfoDao.updPushTime(messageInfo);
            }
        }
        return 1;
    }

    //增加消息到数据库
    @Transactional(readOnly = false)
    public int insert( OrderInfo orderInfo,String orderType){
        MessageInfo messageInfo = new MessageInfo();
        if (orderType != null && !orderType.equals("")) {
            if (orderType.equals("orderCreate")){
                messageInfo.setTitle("您有一个新的订单");
                messageInfo.setMessage("编号为"+orderInfo.getOrderNumber()+"，请点击查看");
                messageInfo.setTargetType("order");
                return insertAndPush(orderInfo,messageInfo);
            }
            if (orderType.equals("orderDispatch")){
                messageInfo.setTitle("订单已改派");
                messageInfo.setMessage("编号为"+orderInfo.getOrderNumber()+"的订单已改派给其他技师，请点击查看");
                messageInfo.setTargetType("order");
                return insertAndPush(orderInfo,messageInfo);
            }
            if (orderType.equals("orderCancel")){
                messageInfo.setTitle("订单已取消");
                messageInfo.setMessage("编号为"+orderInfo.getOrderNumber()+"的订单已取消，请点击查看");
                messageInfo.setTargetType("order");
                return insertAndPush(orderInfo,messageInfo);
            }
            if (orderType.equals("orderServiceTime")){
                messageInfo.setTitle("服务时间变更");
                messageInfo.setMessage("编号为"+orderInfo.getOrderNumber()+"的订单，服务时间更改为"+DateUtils.formatDate(orderInfo.getServiceTime(),"yyyy-MM-dd HH:mm:ss")+"，请点击查看");
                messageInfo.setTargetType("order");
                return insertAndPush(orderInfo,messageInfo);
            }
        }
        //int insert = messageInfoDao.insert(messageInfo);
        //int flag = PushMessageUtil.pushMessage(messageInfo);
        return 0;
    }
    //app编辑消息已读
    @Transactional(readOnly = false)
    public int updateMessage(MessageInfo messageInfo){
        int i =0;
        MessageInfo messageInfo1 = messageInfoDao.get(messageInfo);
        if (messageInfo1 != null) {
            if (messageInfo1.getIsRead().equals("no")) {
                messageInfo1.setIsRead("yes");
                messageInfo1.appPreUpdate();
                i = messageInfoDao.updateMessage(messageInfo1);
            } else {
                throw new ServiceException("已经读过该消息");
            }
        }else {
            throw new ServiceException("你没有该消息");
        }
        return i;
    }

    public Page<MessageInfo> findFailPage(Page<MessageInfo> page, MessageInfo entity) {
        entity.setPage(page);
        page.setList(messageInfoDao.findFailPage(entity));
        return page;
    }

    public OrderInfo getOrderById(String id) {
        return messageInfoDao.getOrderById(id);
    }

    @Transactional(readOnly = false)
    public int pushFailMessage( MessageInfo messageInfo) {
        MessageInfo mi=messageInfoDao.getMessageById(messageInfo);
        OrderInfo orderInfo=messageInfoDao.getOrderById(mi.getTargetId());
        mi.setDeviceIds("community_tech_"+mi.getReceivePhone());
        mi.setExtParameters("{\"type\":\""+mi.getTargetType()+"\",\"relate\":\""+orderInfo.getMajorSort()+"$."+orderInfo.getId()+"$."+mi.getId()+"\"}");
        int flag = PushMessageUtil.pushMessage(mi);
        if (flag==1){
            mi.setIsSuccess("yes");
            mi.preUpdate();
            mi.setPushTime(new Date());
            messageInfoDao.updPushTime(mi);
            return 1;
        }else {
            return 0;
        }
    }
}