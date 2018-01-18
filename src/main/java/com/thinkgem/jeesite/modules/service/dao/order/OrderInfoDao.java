/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;

import java.util.List;

/**
 * 子订单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderInfoDao extends CrudDao<OrderInfo> {
    //app
    OrderInfo appGet(OrderInfo info);

    List<BasicOrganization> findOrganizationList(BasicOrganization organization);

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

    List<ServiceTechnicianWorkTime> findTechWorkTimeList(OrderDispatch tech);

    List<ServiceTechnicianHoliday> findTechHolidayList(OrderDispatch serchTech);

    List<OrderDispatch> findTechOrderList(OrderDispatch serchTech);

    List<OrderDispatch> getTechListOrderByNum(OrderDispatch serchTechInfo);
    //app修改订单
    int appUpdate(OrderInfo orderInfo);
    //app获取订单列表
    List<OrderInfo> appFindList(OrderInfo orderInfo);
    //app获取图片
    String appGetPics(String id);

    int openUpdateOrder(OrderInfo orderInfo);
}