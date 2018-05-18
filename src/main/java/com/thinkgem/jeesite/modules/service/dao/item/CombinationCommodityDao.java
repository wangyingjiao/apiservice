/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.CombinationCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;

import java.util.List;

/**
 * 组合商品-子商品DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface CombinationCommodityDao extends CrudDao<CombinationCommodity> {

    //根据com表中goodId获取组合商品-子商品集合
    List<CombinationCommodity> findListByGoodsId(CombinationOrderInfo info);
    //根据ser_item_combination_goods的id查询出子商品的服务名称商品名称
    CombinationCommodity getComGood(CombinationCommodity info);
}