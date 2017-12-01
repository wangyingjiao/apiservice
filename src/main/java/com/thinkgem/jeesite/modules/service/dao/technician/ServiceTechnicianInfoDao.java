/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;

import java.util.List;

/**
 * 服务技师基础信息DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface ServiceTechnicianInfoDao extends CrudDao<ServiceTechnicianInfo> {

    ServiceTechnicianInfo findTech(ServiceTechnicianInfo info);

    int getOrderTechRelation(ServiceTechnicianInfo serviceTechnicianInfo);

    void delSerSkillTechnicianByTechnician(ServiceTechnicianInfo serviceTechnicianInfo);

    void updateStatus(ServiceTechnicianInfo info1);

    void saveMoreData(ServiceTechnicianInfo serviceTechnicianInfo);

    void deleteTechnicianService(ServiceTechnicianInfo serviceTechnicianInfo);

    void deleteTechnicianWorkTime(ServiceTechnicianInfo serviceTechnicianInfo);

    void deleteTechnicianHoliday(ServiceTechnicianInfo serviceTechnicianInfo);

    void deleteTechnicianImages(ServiceTechnicianInfo serviceTechnicianInfo);

    List<ServiceTechnicianInfo> findOfficeSeviceAreaList(ServiceTechnicianInfo info);

    ServiceTechnicianInfo getData(ServiceTechnicianInfo serviceTechnicianInfo);
}