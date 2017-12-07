/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.OrderServiceRelation;

/**
 * 订单服务关联DAO接口
 * @author a
 * @version 2017-11-23
 */
@MyBatisDao
public interface OrderServiceRelationDao extends CrudDao<OrderServiceRelation> {
	
	public List<OrderItemCommodity> findListByOffice(OrderServiceRelation orderServiceRelation);
	
}