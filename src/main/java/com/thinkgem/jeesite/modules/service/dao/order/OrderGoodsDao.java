/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
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
    //获取补单商品表 根据订单id获取多个sortId位数小于3的商品
    List<SerItemInfo> listItemGoods(OrderInfo orderInfo);

    // 根据订单id查询所有的订单商品表中分类id小于3的商品结合
    List<OrderGoods> getbyOrderId(OrderInfo info);
    //根据id删除订单商品表  物理删除
    int deleteById(OrderGoods orderGoods);

}