/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderPayInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderPayInfoDao;

/**
 * 支付信息Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderPayInfoService extends CrudService<OrderPayInfoDao, OrderPayInfo> {

	public OrderPayInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderPayInfo> findList(OrderPayInfo orderPayInfo) {
		return super.findList(orderPayInfo);
	}
	
	public Page<OrderPayInfo> findPage(Page<OrderPayInfo> page, OrderPayInfo orderPayInfo) {
		User user = UserUtils.getUser();
		BasicOrganization org = user.getOrganization();
		if (null != org && org.getId().trim().equals("0")) {
			orderPayInfo.setOrderSource("gasq");//全平台：只展示订单来源为国安社区的订单列表
		}
		orderPayInfo.getSqlMap().put("dsf", dataStatioRoleFilter(user, "a"));
		return super.findPage(page, orderPayInfo);
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