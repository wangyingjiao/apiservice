/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;

/**
 * 派单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderDispatchService extends CrudService<OrderDispatchDao, OrderDispatch> {

	public OrderDispatch get(String id) {
		return super.get(id);
	}
	
	public List<OrderDispatch> findList(OrderDispatch orderDispatch) {
		return super.findList(orderDispatch);
	}
	
	public Page<OrderDispatch> findPage(Page<OrderDispatch> page, OrderDispatch orderDispatch) {
		return super.findPage(page, orderDispatch);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderDispatch orderDispatch) {
		super.save(orderDispatch);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderDispatch orderDispatch) {
		super.delete(orderDispatch);
	}
	
}