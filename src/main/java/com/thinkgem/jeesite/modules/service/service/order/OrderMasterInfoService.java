/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderMasterInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderMasterInfoDao;

/**
 * 订单主表Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderMasterInfoService extends CrudService<OrderMasterInfoDao, OrderMasterInfo> {

	public OrderMasterInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderMasterInfo> findList(OrderMasterInfo orderMasterInfo) {
		return super.findList(orderMasterInfo);
	}
	
	public Page<OrderMasterInfo> findPage(Page<OrderMasterInfo> page, OrderMasterInfo orderMasterInfo) {
		return super.findPage(page, orderMasterInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderMasterInfo orderMasterInfo) {
		super.save(orderMasterInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderMasterInfo orderMasterInfo) {
		super.delete(orderMasterInfo);
	}
	
}