/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderAddress;
import com.thinkgem.jeesite.modules.service.dao.order.OrderAddressDao;

/**
 * 订单地址Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderAddressService extends CrudService<OrderAddressDao, OrderAddress> {

	public OrderAddress get(String id) {
		return super.get(id);
	}
	
	public List<OrderAddress> findList(OrderAddress orderAddress) {
		return super.findList(orderAddress);
	}
	
	public Page<OrderAddress> findPage(Page<OrderAddress> page, OrderAddress orderAddress) {
		return super.findPage(page, orderAddress);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderAddress orderAddress) {
		super.save(orderAddress);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderAddress orderAddress) {
		super.delete(orderAddress);
	}
	
}