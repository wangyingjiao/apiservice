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
import com.thinkgem.jeesite.modules.service.dao.order.OrderReturnDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderReturn;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 订单支付信息Service
 * @author a
 * @version 2017-12-6
 */
@Service
@Transactional(readOnly = true)
public class OrderReturnService extends CrudService<OrderReturnDao, OrderReturn> {
	
	@Autowired
	OrderReturnDao orderReturnDao;

	public OrderReturn get(String id) {
		return super.get(id);
	}
	
	public List<OrderReturn> findList(OrderReturn orderReturn) {
		return super.findList(orderReturn);
	}
	
	public Page<OrderReturn> findPage(Page<OrderReturn> page, OrderReturn orderReturn) {
		orderReturn.setPage(page);
		User user = UserUtils.getUser();
		if (user.isAdmin()){
			page.setList(orderReturnDao.findAllList(orderReturn));
		}else{
			orderReturn.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			page.setList(orderReturnDao.findList(orderReturn));
		}
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(OrderReturn orderReturn) {
		super.save(orderReturn);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderReturn orderReturn) {
		super.delete(orderReturn);
	}
	
}