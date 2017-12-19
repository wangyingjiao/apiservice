/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianFamilyMembersDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianWorkTimeDao;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianFamilyMembers;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务技师基础信息Service
 *
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class ServiceTechnicianInfoService extends CrudService<ServiceTechnicianInfoDao, ServiceTechnicianInfo> {


    //技师基础信息
    @Autowired
    private ServiceTechnicianInfoDao technicianInfoDao;
    //技师工作时间操作
    @Autowired
    private ServiceTechnicianWorkTimeDao workTimeDao;
    //技能
    @Autowired
    private SerSkillTechnicianDao serSkillTechnicianDao;

    public ServiceTechnicianInfo get(String id) {
        return super.get(id);
    }

    public List<ServiceTechnicianInfo> findList(ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findList(serviceTechnicianInfo);
    }

    public Page<ServiceTechnicianInfo> findPage(Page<ServiceTechnicianInfo> page, ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findPage(page, serviceTechnicianInfo);
    }

    public List<BasicServiceStation> getStationsByOrgId(String orgId) {
        return technicianInfoDao.getStationsByOrgId(orgId);
    }

    public List<SerSkillInfo> getSkillInfosByOrgId(String orgId) {
        return technicianInfoDao.getSkillInfosByOrgId(orgId);
    }

    public ServiceTechnicianInfo formData(ServiceTechnicianInfo serviceTechnicianInfo) {
        return technicianInfoDao.formData(serviceTechnicianInfo);
    }

    /**
     * 服务人员有未完成订单
     *
     * @param serviceTechnicianInfo
     * @return
     */
    public int getOrderTechRelation(ServiceTechnicianInfo serviceTechnicianInfo) {
        return technicianInfoDao.getOrderTechRelation(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void delete(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.delete(serviceTechnicianInfo);
        //删除工作时间 按技师
        deleteTechnicianWorkTime(serviceTechnicianInfo);
        //删除休假时间 按技师
        deleteTechnicianHoliday(serviceTechnicianInfo);
        //删除家庭成员 按技师
        deleteFamilyMembers(serviceTechnicianInfo);
        //删除时同时删除关联的技能
        delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void deleteTechnicianWorkTime(ServiceTechnicianInfo serviceTechnicianInfo) {
        technicianInfoDao.deleteTechnicianWorkTime(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void deleteTechnicianHoliday(ServiceTechnicianInfo serviceTechnicianInfo) {
        technicianInfoDao.deleteTechnicianHoliday(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void deleteFamilyMembers(ServiceTechnicianInfo serviceTechnicianInfo) {
        technicianInfoDao.deleteFamilyMembers(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void delSerSkillTechnicianByTechnician(ServiceTechnicianInfo serviceTechnicianInfo) {
        technicianInfoDao.delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void save(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.save(serviceTechnicianInfo);
        //删除工作时间 按技师
        deleteTechnicianWorkTime(serviceTechnicianInfo);
        //删除时同时删除关联的技能
        delSerSkillTechnicianByTechnician(serviceTechnicianInfo);

        List<ServiceTechnicianWorkTime> times = serviceTechnicianInfo.getWorkTimes();
        if (null != times) {
            for (ServiceTechnicianWorkTime time : times) {
                time.setTechId(serviceTechnicianInfo.getId());
                time.preInsert();
                workTimeDao.insert(time);
            }
        }

        List<String> skillIds = serviceTechnicianInfo.getSkillIds();
        if (null != skillIds) {
            for (String skillId : skillIds) {
                SerSkillTechnician serSkillTechnician = new SerSkillTechnician();
                serSkillTechnician.setSkillId(skillId);
                serSkillTechnician.setTechId(serviceTechnicianInfo.getId());
                serSkillTechnician.preInsert();
                serSkillTechnicianDao.insert(serSkillTechnician);
            }
        }
    }

    public ServiceTechnicianInfo findTech(ServiceTechnicianInfo info) {
        return technicianInfoDao.findTech(info);
    }

    @Transactional(readOnly = false)
    public void saveApp(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.save(serviceTechnicianInfo);
    }

    //技师家庭成员
    @Autowired
    private ServiceTechnicianFamilyMembersDao familyMembersDao;

    /**
     * 保存家庭成员列表
     *
     * @param info
     */
    @Transactional(readOnly = false)
    public void saveFamilyMembers(ServiceTechnicianInfo info) {
        if (StringUtils.isNotBlank(info.getId())) {
            List<ServiceTechnicianFamilyMembers> members = info.getFamilyMembers();
            for (ServiceTechnicianFamilyMembers member : members) {
                member.setTechId(info.getId());
                saveFamilyMember(member);
            }
        }
    }

    /**
     * 保存家庭成员
     *
     * @param member
     */
    @Transactional(readOnly = false)
    public void saveFamilyMember(ServiceTechnicianFamilyMembers member) {
        if (StringUtils.isNotBlank(member.getTechId())) {
            if (StringUtils.isBlank(member.getId())) {
                member.preInsert();
                familyMembersDao.insert(member);
            } else {
                member.preUpdate();
                familyMembersDao.update(member);
            }
        }

    }

    /**
     * 删除家庭成员
     *
     * @param member
     */
    @Transactional(readOnly = false)
    public void deleteFamilyMember(ServiceTechnicianFamilyMembers member) {
        if (StringUtils.isNotBlank(member.getId())) {
            familyMembersDao.delete(member);
        }
    }


    public AppServiceTechnicianInfo appLogin(LoginUser user) {
        AppServiceTechnicianInfo technician = technicianInfoDao.getTechnicianByPhone(user);
        String s = SystemService.entryptPassword(user.getPassword());
        boolean b = SystemService.validatePassword(user.getPassword(), s);
        if (SystemService.validatePassword(user.getPassword(), technician.getPassword())) {
            return technician;
        }
        return null;
    }
}