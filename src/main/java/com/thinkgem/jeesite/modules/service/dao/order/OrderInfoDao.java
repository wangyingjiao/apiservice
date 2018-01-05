/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;

import java.util.List;

/**
 * 子订单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderInfoDao extends CrudDao<OrderInfo> {

    List<BasicOrganization> findOrganizationList(BasicOrganization organization);

    List<ServiceTechnicianWorkTime> findServiceTimeList(OrderInfo orderInfo);

    OrderInfo formData(OrderInfo info);

    List<OrderGoods> getOrderGoodsList(OrderInfo info);

    List<OrderDispatch> getOrderDispatchList(OrderInfo info);

    void cancelData(OrderInfo orderInfo);

    void saveTime(OrderInfo orderInfo);

    List<OrderGoods> getGoodsList(OrderInfo orderInfo);

    String getSkillIdBySortId(String sortId);

    List<OrderDispatch> getTechListBySkillId(OrderDispatch serchInfo);

    List<String> getTechByWorkTime(OrderDispatch serchInfo);

    List<String> getTechByHoliday(OrderDispatch serchInfo);

    List<OrderDispatch> getTechByOrder(OrderDispatch serchInfo);
}