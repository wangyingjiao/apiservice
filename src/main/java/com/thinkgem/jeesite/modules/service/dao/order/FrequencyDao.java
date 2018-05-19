/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCombinationFrequencyInfo;

import java.util.List;

/**
 * 组合订单-多次服务-服务时间DAO接口
 * @author a
 * @version 2018-5-17
 */
@MyBatisDao
public interface FrequencyDao extends CrudDao<OrderCombinationFrequencyInfo> {

    // 根据masterId获取组合订单的服务时间
    List<OrderCombinationFrequencyInfo> getFrequencyList(CombinationOrderInfo combinationOrderInfo);

    void deleteOldFrequencyByMasterId(String masterId);
}