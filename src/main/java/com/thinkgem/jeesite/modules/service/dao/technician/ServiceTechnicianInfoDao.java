/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianFamilyMembers;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;

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

    ServiceTechnicianInfo formData(ServiceTechnicianInfo serviceTechnicianInfo);

    AppServiceTechnicianInfo getTechnicianByPhone(LoginUser user);

    List<BasicServiceStation> getStationsByOrgId(BasicServiceStation serviceStation);

    List<SerSkillInfo> getSkillInfosByOrgId(String orgId);

    void deleteFamilyMembers(ServiceTechnicianInfo serviceTechnicianInfo);

    List<String> getSkillIds(ServiceTechnicianInfo serviceTechnicianInfo);
    //app通讯录
    List<ServiceTechnicianInfo> appGetFriendByStationId(ServiceTechnicianInfo serviceTechnicianInfo);

    void updateInfo(ServiceTechnicianInfo info);

    void updatePlus(ServiceTechnicianInfo info);

    void updateOther(ServiceTechnicianInfo info);

	int checkPhone(ServiceTechnicianInfo info);

	List<ServiceTechnicianInfo> findTechList(ServiceTechnicianInfo info);
    //app获取技师工作时间
    List<ServiceTechnicianWorkTime> appGetWeekByTechId(ServiceTechnicianInfo info);
    //app修改密码
    int appUpdatePassword(ServiceTechnicianInfo info);

    List<ServiceTechnicianFamilyMembers> findFamilyMembersListByTechId(ServiceTechnicianInfo info);
}