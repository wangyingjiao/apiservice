/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.sys.dao.MessageInfoDao;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务技师基础信息Service
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

    //列表查看消息
    public Page<MessageInfo> findList(Page<MessageInfo> page,MessageInfo messageInfo){
        messageInfo.setPage(page);
        List<MessageInfo> list = messageInfoDao.findList(messageInfo);
        for (MessageInfo info:list){
            info.setOrderId(info.getTargetId());
            Date createTime = info.getPushTime();
            String s = DateUtils.formatDateTime(createTime);
            info.setCreateTime(s);
        }
        page.setList(list);
        return page;
    }
    //增加消息到数据库
    public int insert(MessageInfo messageInfo){
        int insert = messageInfoDao.insert(messageInfo);
        return insert;
    }

}