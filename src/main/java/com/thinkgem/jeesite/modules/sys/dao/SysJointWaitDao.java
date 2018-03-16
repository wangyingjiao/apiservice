/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;

import java.util.List;

/**
 * 日志DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface SysJointWaitDao extends CrudDao<SysJointWait> {

    List<SysJointWait> findJointWaitList();

    void deleteGoodsEshop(SerItemCommodityEshop goodsEshop);

    void updateGoodsEshop(SerItemCommodityEshop goodsEshop);
}
