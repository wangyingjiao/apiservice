/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;

import java.util.List;

/**
 * 服务技师休假时间DAO接口
 * @author a
 * @version 2017-11-29
 */
@MyBatisDao
public interface ServiceTechnicianHolidayDao extends CrudDao<ServiceTechnicianHoliday> {

    int getOrderTechRelationHoliday(ServiceTechnicianHoliday info);

    List<ServiceTechnicianHoliday> getServiceTechnicianWorkTime(ServiceTechnicianHoliday serviceTechnicianHoliday);

    List<ServiceTechnicianHoliday> getHolidayHistory(ServiceTechnicianHoliday info);
    //app获取技师休假列表
    List<ServiceTechnicianHoliday> appFindPage(ServiceTechnicianHoliday serviceTechnicianHoliday);
    //审核app的休假
    int updateHoliday(ServiceTechnicianHoliday serviceTechnicianHoliday);
    //审核未通过的休假详情
    ServiceTechnicianHoliday getHolidayById(ServiceTechnicianHoliday serviceTechnicianHoliday);
}