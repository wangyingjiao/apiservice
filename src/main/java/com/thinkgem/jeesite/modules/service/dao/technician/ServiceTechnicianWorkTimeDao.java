/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;

import java.util.List;

/**
 * 服务技师工作时间DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface ServiceTechnicianWorkTimeDao extends CrudDao<ServiceTechnicianWorkTime> {

    List<ServiceTechnicianWorkTime> findListByTech(ServiceTechnicianWorkTime time);
    //判断是否在工作时间
    int getTechWorkTime(ServiceTechnicianWorkTime time);
}