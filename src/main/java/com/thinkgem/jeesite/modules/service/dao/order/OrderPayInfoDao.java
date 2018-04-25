/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
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

    OrderPayInfo getPayInfoByOrderId(OrderInfo info);

    void updateByOrderId(OrderPayInfo payInfo);
}