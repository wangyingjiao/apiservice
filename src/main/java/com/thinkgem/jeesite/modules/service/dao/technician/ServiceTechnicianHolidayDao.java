/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
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

    int getHolidayHistory(ServiceTechnicianHoliday info);
}