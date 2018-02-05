/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;

import java.util.List;

/**
 * 消息基础信息DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface MessageInfoDao extends CrudDao<MessageInfo> {
    //app 根据id查看消息
    MessageInfo get(MessageInfo messageInfo);
    //app 列表查看消息
    List<MessageInfo> findList(MessageInfo messageInfo);
    //app未读消息数量
    int getCount(MessageInfo messageInfo);
    //增加
    int insert(MessageInfo messageInfo);
    //编辑
    int updateMessage(MessageInfo messageInfo);

    void updPushTime(MessageInfo messageInfo);

    List<MessageInfo> findFailPage(MessageInfo messageInfo);

    OrderInfo getOrderById(String id);

    MessageInfo getMessageById(MessageInfo messageInfo);
}