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
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefundGoods;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;

import java.util.List;

/**
 * 子订单DAO接口
 * @author a
 * @version 2017-12-26
 */
@MyBatisDao
public interface OrderInfoDao extends CrudDao<OrderInfo> {
    //app根据订单id获取订单
    OrderInfo appGet(OrderInfo info);

    List<BasicOrganization> findOrganizationList(BasicOrganization organization);

    OrderInfo formData(OrderInfo info);
    //获取订单详情的服务信息
    List<OrderGoods> getOrderGoodsList(OrderInfo info);

    List<OrderDispatch> getOrderDispatchList(OrderInfo info);
    //app改派根据订单id技师id获取改派表
    OrderDispatch appGetOrderDispatch(OrderInfo info);

    void cancelData(OrderInfo orderInfo);

    void saveTime(OrderInfo orderInfo);

    List<OrderGoods> getGoodsList(OrderGoods orderInfo);

    List<SerSkillSort> getSkillIdBySortId(SerSkillSort serchSkillSort);

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
    //app修改订单备注
    int appUpdateRemark(OrderInfo orderInfo);
    //app订单支付
    int appUpdatePay(OrderInfo orderInfo);
    //app获取订单列表
    List<OrderInfo> appFindList(OrderInfo orderInfo);
    //app获取图片
    List<String> appGetPics(String id);
    //app获取订单对应商品
    List<String> getGoods(OrderInfo orderInfo);
    //app根据商品id获取订单对应商品的对接code
    String getGoodsCode(String goodsId);

    int openUpdateOrder(OrderInfo orderInfo);

    //BasicOrganization getBasicOrganizationByOrgId(OrderInfo orderInfo);

    List<OrderDispatch> getOrderDispatchMsgTechList(OrderInfo orderMsg);

    OrderInfo getBySn(OrderInfo orderInfo);

    OrderInfo checkGasqSnOrderSn(OrderInfo checkInfo);

    int openUpdateOrderForBusiness(OrderInfo orderInfo);

    int openUpdateOrderForShop(OrderInfo orderInfo);

    int orderCancel(OrderInfo orderInfo);

    String getSortIdByOrderId(OrderInfo orderInfo);

    List<ServiceTechnicianWorkTime> listTechWorkByTechsTime(ServiceTechnicianWorkTime serchInfo);

    List<TechScheduleInfo> listTechScheduleByTechsTime(TechScheduleInfo serchInfo);

    List<OrderGoods> listNotRefundOrderGoodsByOrderId(OrderInfo info);

    List<OrderGoods> listRefundOrderGoodsByOrderIdGoods(OrderInfo info);
}