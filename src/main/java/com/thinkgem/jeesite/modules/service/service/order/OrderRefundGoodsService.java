/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefundGoods;
import com.thinkgem.jeesite.modules.service.dao.order.OrderRefundGoodsDao;

/**
 * 退货单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderRefundGoodsService extends CrudService<OrderRefundGoodsDao, OrderRefundGoods> {

	public OrderRefundGoods get(String id) {
		return super.get(id);
	}
	
	public List<OrderRefundGoods> findList(OrderRefundGoods orderRefundGoods) {
		return super.findList(orderRefundGoods);
	}
	
	public Page<OrderRefundGoods> findPage(Page<OrderRefundGoods> page, OrderRefundGoods orderRefundGoods) {
		return super.findPage(page, orderRefundGoods);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderRefundGoods orderRefundGoods) {
		super.save(orderRefundGoods);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderRefundGoods orderRefundGoods) {
		super.delete(orderRefundGoods);
	}
	
}