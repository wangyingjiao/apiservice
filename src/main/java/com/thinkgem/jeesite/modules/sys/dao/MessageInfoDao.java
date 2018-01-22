/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;

import java.util.List;

/**
 * 服务技师基础信息DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface MessageInfoDao extends CrudDao<MessageInfo> {
    //app 根据id查看消息
    MessageInfo get(MessageInfo messageInfo);
    //app 列表查看消息
    List<MessageInfo> findList(MessageInfo messageInfo);
    //增加
    int insert(MessageInfo messageInfo);

}