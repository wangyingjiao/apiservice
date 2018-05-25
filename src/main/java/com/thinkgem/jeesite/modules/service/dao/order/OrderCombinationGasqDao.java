/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCombinationGasqInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;

import java.util.List;

/**
 * 组合订单对接编号信息DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderCombinationGasqDao extends CrudDao<OrderCombinationGasqInfo> {
    //根据masterId获取组合并拆单订单的订单集合
    List<OrderCombinationGasqInfo> getListbyMasterId(CombinationOrderInfo combinationOrderInfo);
    //根据masterId获取组合不拆单订单的订单集合
    OrderInfo getOrderListbyMasterId(CombinationOrderInfo combinationOrderInfo);

    //根据groupId获取组合订单的订单集合
    List<OrderCombinationGasqInfo> getListByOrderGroupId(OrderCombinationGasqInfo orderCombinationGasqInfo);

    //根据orderNumber获取组合订单的orderGroupId
    OrderCombinationGasqInfo getListByOrderNumber(OrderInfo info);
    //根据orderSn masterId获取组合订单 一个
    OrderCombinationGasqInfo getGasqByOrderSn(CombinationOrderInfo info);

    List<OrderCombinationGasqInfo> listFreeGasqSn(String masterId);

    void updateOrderGroup(OrderCombinationGasqInfo combinationGasqInfo);

    List<OrderCombinationGasqInfo> listGroupOrderByOrderId(CombinationOrderInfo combinationOrderInfo);

    List<OrderCombinationGasqInfo> getListbyOrderId(OrderInfo orderInfo);

    void updateOrderGroupByMasterJointSn(OrderCombinationGasqInfo combinationGasqInfo);
}