/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefund;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefundGoods;
import java.util.List;

import java.util.List;

/**
 * 退货单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderRefundGoodsDao extends CrudDao<OrderRefundGoods> {

    List<OrderRefundGoods> listRefundGoodsByOrderId(OrderInfo info);

    //根据refundId查询出所有的退货单表
    List<OrderRefundGoods> getRefundGoodsByRefundId(OrderRefund orderRefund);

    //根据orderId查询出所有的退货单表
    List<OrderRefundGoods> getRefundGoodsByOrderId(OrderInfo info);
}