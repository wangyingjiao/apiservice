/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTechRelation;

/**
 * 订单技师关联DAO接口
 * @author a
 * @version 2017-11-23
 */
@MyBatisDao
public interface OrderTechRelationDao extends CrudDao<OrderTechRelation> {
	
	public void updateStatus(OrderTechRelation orderTechRelation);
	
}