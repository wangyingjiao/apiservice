/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefund;
import com.thinkgem.jeesite.modules.service.dao.order.OrderRefundDao;

/**
 * 退款单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderRefundService extends CrudService<OrderRefundDao, OrderRefund> {

	public OrderRefund get(String id) {
		return super.get(id);
	}
	
	public List<OrderRefund> findList(OrderRefund orderRefund) {
		return super.findList(orderRefund);
	}
	
	public Page<OrderRefund> findPage(Page<OrderRefund> page, OrderRefund orderRefund) {
		return super.findPage(page, orderRefund);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderRefund orderRefund) {
		super.save(orderRefund);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderRefund orderRefund) {
		super.delete(orderRefund);
	}
	
}