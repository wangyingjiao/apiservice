/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.technician.*;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillTechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private ServiceTechnicianInfoDao serviceTechnicianInfoDao;

    public ServiceTechnicianInfo get(String id) {
        return super.get(id);
    }

    public List<ServiceTechnicianInfo> findList(ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findList(serviceTechnicianInfo);
    }

    public Page<ServiceTechnicianInfo> findPage(Page<ServiceTechnicianInfo> page, ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findPage(page, serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void save(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.save(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void saveMoreData(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfo.preUpdate();
        serviceTechnicianInfoDao.saveMoreData(serviceTechnicianInfo);
        saveImages(serviceTechnicianInfo);
    }
    /**
     * 服务人员有未完成订单
     * @param serviceTechnicianInfo
     * @return
     */
    public int getOrderTechRelation(ServiceTechnicianInfo serviceTechnicianInfo) {
        return serviceTechnicianInfoDao.getOrderTechRelation(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void delete(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.delete(serviceTechnicianInfo);

        //删除服务信息 按技师
        deleteTechnicianService(serviceTechnicianInfo);
        //删除工作时间 按技师
        deleteTechnicianWorkTime(serviceTechnicianInfo);
        //删除休假时间 按技师
        deleteTechnicianHoliday(serviceTechnicianInfo);
        //删除头像 按技师
        deleteTechnicianImages(serviceTechnicianInfo);

        //删除家庭成员 按技师
        deleteFamilyMembers(serviceTechnicianInfo);
        //删除时同时删除关联的技能
        delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
    }

    @Transactional(readOnly = false)
    public void deleteTechnicianService(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfoDao.deleteTechnicianService(serviceTechnicianInfo);
    }
    @Transactional(readOnly = false)
    public void deleteTechnicianWorkTime(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfoDao.deleteTechnicianWorkTime(serviceTechnicianInfo);
    }
    @Transactional(readOnly = false)
    public void deleteTechnicianHoliday(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfoDao.deleteTechnicianHoliday(serviceTechnicianInfo);
    }
    @Transactional(readOnly = false)
    public void deleteTechnicianImages(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfoDao.deleteTechnicianImages(serviceTechnicianInfo);
    }
    @Transactional(readOnly = false)
    public void delSerSkillTechnicianByTechnician(ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfoDao.delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
    }


    //技师服务信息
    @Autowired
    private ServiceTechnicianServiceInfoDao serviceInfoDao;

    @Autowired
    private SerSkillTechnicianService serSkillTechnicianService;

    @Transactional(readOnly = false)
    public void saveServiceInfo(ServiceTechnicianInfo info) {
        ServiceTechnicianInfo technicianInfo = get(info.getId());
        ServiceTechnicianServiceInfo serviceInfo = info.getServiceInfo();
        if (StringUtils.isNotBlank(serviceInfo.getId())) {
            serviceInfo.preUpdate();
            serviceInfoDao.update(serviceInfo);
        }else {
            serviceInfo.preInsert();
            serviceInfo.setTechId(technicianInfo.getId());
            serviceInfo.setTechName(technicianInfo.getTechName());
            serviceInfo.setSort("0");
            serviceInfoDao.insert(serviceInfo);
        }

        List<SerSkillInfo> skills = serviceInfo.getSkills();
        for (SerSkillInfo skill : skills) {
            SerSkillTechnician sst = new SerSkillTechnician(serviceInfo,skill);
            serSkillTechnicianService.save(sst);
        }
        /*if (info.getImages().size() > 0){
            saveImages(info);
        }*/

    }

    @Transactional(readOnly = false)
    public void updateStatus(String id) {
        ServiceTechnicianInfo info1 = new ServiceTechnicianInfo();
        info1.preUpdate();
        info1.setId(id);
        info1.setStatus("1");//服务信息补充完毕并保存成功后，该技师变为可用状态1
        serviceTechnicianInfoDao.updateStatus(info1);
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
                member.setTechName(info.getTechName());
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
                member.setSort("0");
                familyMembersDao.insert(member);
            } else {
                member.preUpdate();
                familyMembersDao.update(member);
            }
        }

    }

    /**
     * 删除家庭成员 按技师
     * @param info
     */
    @Transactional(readOnly = false)
    public void deleteFamilyMembers(ServiceTechnicianInfo info) {
        //List<ServiceTechnicianFamilyMembers> members = info.getFamilyMembers();
        List<ServiceTechnicianFamilyMembers> members = familyMembersDao.findListByTech(info);
        for (ServiceTechnicianFamilyMembers member : members) {
            if (info.getId().equals(member.getTechId())) {
                deleteFamilyMember(member);
            }
        }
    }

    /**
     * 删除家庭成员
     * @param member
     */
    @Transactional(readOnly = false)
    public void deleteFamilyMember(ServiceTechnicianFamilyMembers member) {
        if (StringUtils.isNotBlank(member.getId())) {
            familyMembersDao.delete(member);
        }
    }

    /**
     * 得到技师的所有家庭成员
     * @param info
     * @return
     */
    public List<ServiceTechnicianFamilyMembers> findFamilyMember(ServiceTechnicianInfo info) {
        if (StringUtils.isNotBlank(info.getId())) {
            List<ServiceTechnicianFamilyMembers> members = familyMembersDao.findListByTech(info);
            return members;
        }
        return new ArrayList<>();
    }

    /**
     * ****
     * 技师工作时间操作
     */
    @Autowired
    private ServiceTechnicianWorkTimeDao workTimeDao;

    @Transactional(readOnly = false)
    public void saveWorkTimes(ServiceTechnicianInfo info) {
        deleteWorkTimeByTech(info);
        List<ServiceTechnicianWorkTime> times = info.getWorkTimes();
        for (ServiceTechnicianWorkTime time : times) {
            time.setTechId(info.getId());
            time.setTechName(info.getTechName());
            saveWorkTime(time);
        }
    }

    /**
     * 保存工作时间
     *
     * @param time
     */
    @Transactional(readOnly = false)
    public void saveWorkTime(ServiceTechnicianWorkTime time) {
        if (StringUtils.isNotBlank(time.getTechId())) {
            if (StringUtils.isNotBlank(time.getTechId())) {
                if (StringUtils.isBlank(time.getId())) {
                    time.setSort("0");
                    time.preInsert();
                    workTimeDao.insert(time);
                } else {
                    time.preUpdate();
                    workTimeDao.update(time);
                }
            }
        }
    }

    /**
     * 删除工作时间
     *
     * @param time
     */
    @Transactional(readOnly = false)
    public void deleteWorkTime(ServiceTechnicianWorkTime time) {
        if (StringUtils.isNotBlank(time.getId())) {
            workTimeDao.delete(time);
        }
    }

    /**
     * 删除技师的所有工作时间
     *
     * @param info
     */
    public void deleteWorkTimeByTech(ServiceTechnicianInfo info) {
        if (StringUtils.isNotBlank(info.getId())) {
            List<ServiceTechnicianWorkTime> times = findWorkTimeByTech(info);
            for (ServiceTechnicianWorkTime workTime : times) {
                deleteWorkTime(workTime);
            }
        }
    }

    /**
     * 获到技师的所有工作时间（只有一周的时间）
     *
     * @param tech
     * @return
     */
    public List<ServiceTechnicianWorkTime> findWorkTimeByTech(ServiceTechnicianInfo tech) {
        if (StringUtils.isNotBlank(tech.getId())) {
            ServiceTechnicianWorkTime time = new ServiceTechnicianWorkTime();
            time.setTechId(tech.getId());
            return workTimeDao.findListByTech(time);
        }
        return new ArrayList<>();
    }


    @Autowired
    private ServiceTechnicianImagesDao imagesDao;

    /**
     * 保存多张图片
     * 使用循环方式保存
     *
     * @param info
     */
    @Transactional(readOnly = false)
    public void saveImages(ServiceTechnicianInfo info) {
        List<ServiceTechnicianImages> images = info.getImages();
        if (null != images && images.size() > 0) {
            for (ServiceTechnicianImages image : images) {
                image.setTechId(info.getId());
                image.setTechName(info.getTechName());
                saveImage(image);
            }
        }
    }

    /**
     * 保存单张图片
     *
     * @param image
     */
    @Transactional(readOnly = false)
    public void saveImage(ServiceTechnicianImages image) {
        if (StringUtils.isNotBlank(image.getTechId())) {
            if (StringUtils.isBlank(image.getId())) {
                image.preInsert();
                image.setSort("0");
                imagesDao.insert(image);
            } else {
                image.preUpdate();
                imagesDao.update(image);
            }

        }
    }

    /**
     * 删除图片
     */
    public void deleteImage(ServiceTechnicianImages image) {
        if (StringUtils.isNotBlank(image.getId())) {
            imagesDao.delete(image);
        }
    }

    /**
     * 获取图片
     */
    public ServiceTechnicianImages getImage(ServiceTechnicianImages image) {
        ServiceTechnicianImages images = new ServiceTechnicianImages();
        if (StringUtils.isNotBlank(image.getId())) {
            images = imagesDao.get(image);
        }
        return images;
    }

    public ServiceTechnicianInfo findTech(ServiceTechnicianInfo info) {
       return technicianInfoDao.findTech(info);

    }

    public List<ServiceTechnicianImages> getImages(ServiceTechnicianInfo technicianInfo) {
        return imagesDao.findAllByTech(technicianInfo);
    }

    public ServiceTechnicianServiceInfo getServiceInfo(ServiceTechnicianInfo technicianInfo) {
        ServiceTechnicianServiceInfo serviceInfo = serviceInfoDao.findByTech(technicianInfo);
        serviceInfo.setSkills(serSkillTechnicianService.findByTech(technicianInfo));
        return serviceInfo;
    }

    public List<ServiceTechnicianInfo> findOfficeSeviceAreaList(ServiceTechnicianInfo info) {
        return serviceTechnicianInfoDao.findOfficeSeviceAreaList(info);
    }

    public ServiceTechnicianInfo getData(ServiceTechnicianInfo serviceTechnicianInfo) {
        return serviceTechnicianInfoDao.getData(serviceTechnicianInfo);
    }
}