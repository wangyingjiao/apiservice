/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDropdownInfo;

import java.util.List;

/**
 * 订单服务关联DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderGoodsDao extends CrudDao<OrderGoods> {

    SerItemCommodity findItemGoodsByGoodId(String cate_goods_id);

    void deleteGoodsByOrderId(OrderGoods serchOrderGoods);

    List<OrderDropdownInfo> findItemList(OrderInfo info);

    void updateRefundNumByOrderIdItemId(OrderGoods orderGoods);
}