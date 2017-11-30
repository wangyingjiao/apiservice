/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderTechRelation;
import com.thinkgem.jeesite.modules.service.dao.order.OrderTechRelationDao;

/**
 * 订单商品关联Service
 * @author a
 * @version 2017-11-23
 */
@Service
@Transactional(readOnly = true)
public class OrderTechRelationService extends CrudService<OrderTechRelationDao, OrderTechRelation> {

	public OrderTechRelation get(String id) {
		return super.get(id);
	}
	
	public List<OrderTechRelation> findList(OrderTechRelation orderTechRelation) {
		return super.findList(orderTechRelation);
	}
	
	public Page<OrderTechRelation> findPage(Page<OrderTechRelation> page, OrderTechRelation orderTechRelation) {
		return super.findPage(page, orderTechRelation);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderTechRelation orderTechRelation) {
		super.save(orderTechRelation);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderTechRelation orderTechRelation) {
		super.delete(orderTechRelation);
	}
	
}