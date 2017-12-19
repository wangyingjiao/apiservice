/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderCustomInfoDao;

/**
 * 客户信息Service
 * @author a
 * @version 2017-11-23
 */
@Service
@Transactional(readOnly = true)
public class OrderCustomInfoService extends CrudService<OrderCustomInfoDao, OrderCustomInfo> {

	public OrderCustomInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderCustomInfo> findList(OrderCustomInfo orderCustomInfo) {
		return super.findList(orderCustomInfo);
	}
	
	public Page<OrderCustomInfo> findPage(Page<OrderCustomInfo> page, OrderCustomInfo orderCustomInfo) {
		return super.findPage(page, orderCustomInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderCustomInfo orderCustomInfo) {
		super.save(orderCustomInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderCustomInfo orderCustomInfo) {
		super.delete(orderCustomInfo);
	}

    public List<BasicOrganization> findOrganizationList() {
		return dao.findOrganizationList();
    }
}