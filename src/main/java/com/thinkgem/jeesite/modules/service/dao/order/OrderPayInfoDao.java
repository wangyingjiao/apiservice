/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderPayInfo;

/**
 * 支付信息DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderPayInfoDao extends CrudDao<OrderPayInfo> {

    //app根据masterId获取支付信息
    OrderPayInfo getByMasterId(String masterId);
    //根据支付表（普通订单）关联机构服务站
    OrderPayInfo getPayByOrderId(OrderPayInfo payInfo);
    //根据支付表（组合订单）关联组合订单主表机构服务站
    OrderPayInfo getPayInfoByComId(OrderPayInfo payInfo);

    OrderPayInfo getPayInfoByOrderId(OrderInfo info);

    void updateByOrderId(OrderPayInfo payInfo);
}