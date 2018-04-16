/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.math.BigDecimal;
import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefundGoods;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefund;
import com.thinkgem.jeesite.modules.service.dao.order.OrderRefundDao;

/**
 * 退款单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderRefundService extends CrudService<OrderRefundDao, OrderRefund> {

	public OrderRefund get(String id) {
		return super.get(id);
	}
	
	public List<OrderRefund> findList(OrderRefund orderRefund) {
		return super.findList(orderRefund);
	}
	
	public Page<OrderRefund> findPage(Page<OrderRefund> page, OrderRefund orderRefund) {
		User user = UserUtils.getUser();
		BasicOrganization org = user.getOrganization();
		if (null != org && org.getId().trim().equals("0")) {
			orderRefund.setOrderSource("gasq");//全平台：只展示订单来源为国安社区的订单列表
		}
		orderRefund.getSqlMap().put("dsf", dataStatioRoleFilter(user, "a"));
		return super.findPage(page, orderRefund);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderRefund orderRefund) {
		super.save(orderRefund);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderRefund orderRefund) {
		super.delete(orderRefund);
	}

	public OrderRefund formData(OrderRefund orderRefund) {
		OrderRefund info = dao.formData(orderRefund);
		if(info == null){
			return null;
		}
		List<OrderRefundGoods> refundGoodsList = dao.listRefundGoodsByRefundId(orderRefund);
		if(refundGoodsList!=null && refundGoodsList.size()>0) {
			for(OrderRefundGoods refundGoods : refundGoodsList){
				BigDecimal b1 = new BigDecimal(refundGoods.getPayPrice());
				BigDecimal b2 = new BigDecimal(refundGoods.getGoodsNum());
				refundGoods.setPayPriceSum(b1.multiply(b2).toString());
			}
			info.setRefundGoodsList(refundGoodsList);
		}
		return info;
	}
}