/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderServiceRelation;
import com.thinkgem.jeesite.modules.service.dao.order.OrderServiceRelationDao;

/**
 * 订单服务关联Service
 * @author a
 * @version 2017-11-23
 */
@Service
@Transactional(readOnly = true)
public class OrderServiceRelationService extends CrudService<OrderServiceRelationDao, OrderServiceRelation> {

	public OrderServiceRelation get(String id) {
		return super.get(id);
	}
	
	public List<OrderServiceRelation> findList(OrderServiceRelation orderServiceRelation) {
		return super.findList(orderServiceRelation);
	}
	
	public Page<OrderServiceRelation> findPage(Page<OrderServiceRelation> page, OrderServiceRelation orderServiceRelation) {
		return super.findPage(page, orderServiceRelation);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderServiceRelation orderServiceRelation) {
		super.save(orderServiceRelation);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderServiceRelation orderServiceRelation) {
		super.delete(orderServiceRelation);
	}
	
}