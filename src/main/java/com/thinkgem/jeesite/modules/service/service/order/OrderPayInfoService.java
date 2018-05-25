/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	OrderPayInfoDao orderPayInfoDao;
	public OrderPayInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderPayInfo> findList(OrderPayInfo orderPayInfo) {
		return super.findList(orderPayInfo);
	}
	
	public Page<OrderPayInfo> findPage(Page<OrderPayInfo> page, OrderPayInfo orderPayInfo) {
		User user = UserUtils.getUser();
		orderPayInfo.getSqlMap().put("dsf", dataRoleFilterForOrder(user, "ord"));
		Page<OrderPayInfo> page1 = super.findPage(page, orderPayInfo);
		List<OrderPayInfo> list = page1.getList();
		if (list != null && list.size() > 0) {
			for (OrderPayInfo orderPayInfo1:list){
				if ("common".equals(orderPayInfo1.getOrderType())){
					OrderPayInfo payInfoByOrderId = orderPayInfoDao.getPayByOrderId(orderPayInfo1);
					orderPayInfo1.setOrderNumber(payInfoByOrderId.getOrderNumber());
					orderPayInfo1.setOrgName(payInfoByOrderId.getOrgName());
					orderPayInfo1.setStationName(payInfoByOrderId.getStationName());
				}else {
					OrderPayInfo payInfoByComId = orderPayInfoDao.getPayInfoByComId(orderPayInfo1);
					orderPayInfo1.setOrgName(payInfoByComId.getOrgName());
					orderPayInfo1.setStationName(payInfoByComId.getStationName());
				}
			}
		}
		return page1;
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