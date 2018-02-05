/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;

import java.util.List;

/**
 * 客户信息DAO接口
 * @author a
 * @version 2017-11-23
 */
@MyBatisDao
public interface OrderCustomInfoDao extends CrudDao<OrderCustomInfo> {

    List<BasicOrganization> findOrganizationList(BasicOrganization organization);

    OrderCustomInfo findCustomInfo(OrderCustomInfo orderCustomInfo);

    List<ServiceStation> getStationsByOrgId(String orgId);

    List<OrderCustomInfo> findCusList(OrderCustomInfo orderCustomInfo);
}