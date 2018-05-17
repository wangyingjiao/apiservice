/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;

import java.util.List;

/**
 * 组合订单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface CombinationOrderDao extends CrudDao<CombinationOrderInfo> {

    // //组合订单列表
    List<CombinationOrderInfo> listDataCombination(CombinationOrderInfo combinationOrderInfo);
    //根据masterId查询组合订单详情
    CombinationOrderInfo getCombinationById(CombinationOrderInfo combinationOrderInfo);

    OrderInfo getOrderRemark(CombinationOrderInfo combinationOrderInfo);
}