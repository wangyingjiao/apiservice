/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;

/**
 * 派单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderDispatchDao extends CrudDao<OrderDispatch> {
	
}