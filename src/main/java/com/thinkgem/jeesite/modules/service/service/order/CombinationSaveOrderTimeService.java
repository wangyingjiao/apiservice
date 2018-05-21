/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.order.CombinationOrderInfo;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 组合订单设置子订单时间Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationSaveOrderTimeService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
    OrderCombinationGasqDao orderCombinationGasqDao;
	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
	FrequencyDao frequencyDao;
	@Autowired
	OrderDispatchDao orderDispatchDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	TechScheduleDao techScheduleDao;
	@Autowired
	private OrderToolsService orderToolsService;

	@Autowired
	ServiceTechnicianInfoDao serviceTechnicianInfoDao;

}