/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 派单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderDispatchDao extends CrudDao<OrderDispatch> {

    List<OrderDispatch> formData(OrderDispatch info);

	List<OrderInfo> findOrderList(OrderInfo orderDispatch);

	Long findOrderCount(OrderInfo info);
	//app根据用户id和订单id查询派单表
	OrderDispatch getByOrderTechId(OrderDispatch info);

	//app根据订单id查询派单表集合
	List<OrderDispatch> getByOrderId(OrderInfo info);
	//根据订单id关联技师表 获取派单包含技师手机（发消息）
	List<OrderDispatch> getDisByOrderId(OrderInfo info);
}