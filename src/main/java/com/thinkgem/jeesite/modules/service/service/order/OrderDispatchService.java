/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.order.OrderDispatchDao;
import com.thinkgem.jeesite.modules.service.dao.order.OrderInfoDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

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
	
	public Page<OrderInfo> findPage(Page<OrderInfo> page, OrderInfo orderDispatch) {
		orderDispatch.getSqlMap().put("dsf", dataRoleFilterForOrder(UserUtils.getUser(), "a"));
		orderDispatch.setPage(page);
		page.setList(dao.findOrderList(orderDispatch));
		
		return page;
	}

    public List<OrderDispatch> formData(OrderDispatch info) {
		List<OrderDispatch> dispatchInfo = dao.formData(info);
		return dispatchInfo;
    }

	public Long findOrderCount(Page<OrderInfo> page, OrderInfo orderDispatch) {
		orderDispatch.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
		orderDispatch.setPage(page);
		//page.setList(dao.findOrderList(orderDispatch));
		Long count=dao.findOrderCount(orderDispatch);
		return count;
	}
}