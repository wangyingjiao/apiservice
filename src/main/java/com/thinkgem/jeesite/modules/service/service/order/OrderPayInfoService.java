/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.order.OrderPayInfoDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderPayInfo;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 订单支付信息Service
 * @author a
 * @version 2017-12-6
 */
@Service
@Transactional(readOnly = true)
public class OrderPayInfoService extends CrudService<OrderPayInfoDao, OrderPayInfo> {
	
	@Autowired
	OrderPayInfoDao orderPayInfoDao;

	public OrderPayInfo get(String id) {
		return super.get(id);
	}
	
	public OrderPayInfo getByOrderId(String orderId) {
		return orderPayInfoDao.getByOrderId(orderId);
	}
	
	public List<OrderPayInfo> findList(OrderPayInfo orderPayInfo) {
		return super.findList(orderPayInfo);
	}
	
	public Page<OrderPayInfo> findPage(Page<OrderPayInfo> page, OrderPayInfo orderPayInfo) {
		orderPayInfo.setPage(page);
		User user = UserUtils.getUser();
		if (user.isAdmin()){
			page.setList(orderPayInfoDao.findAllList(orderPayInfo));
		}else{
			orderPayInfo.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			page.setList(orderPayInfoDao.findList(orderPayInfo));
		}
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(OrderPayInfo orderPayInfo) {
		super.save(orderPayInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderPayInfo orderPayInfo) {
		super.delete(orderPayInfo);
	}
	
}