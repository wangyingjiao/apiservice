/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillInfoDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianFamilyMembersDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianWorkTimeDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private SerSkillInfoDao serSkillInfoDao;

    @Autowired
    private ServiceTechnicianInfoDao serviceTechnicianInfoDao;

    public ServiceTechnicianInfo get(String id) {
        return super.get(id);
    }
    public ServiceTechnicianInfo getByPhone(String phone){
        return technicianInfoDao.getByPhone(phone);
    }

    public List<ServiceTechnicianInfo> findList(ServiceTechnicianInfo serviceTechnicianInfo) {
        return super.findList(serviceTechnicianInfo);
    }

    public Page<ServiceTechnicianInfo> findPage(Page<ServiceTechnicianInfo> page, ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfo.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, serviceTechnicianInfo);
    }

    public List<BasicServiceStation> getStationsByOrgId(String orgId) {
        BasicServiceStation serviceStationSerch = new BasicServiceStation();
        serviceStationSerch.setOrgId(orgId);
        serviceStationSerch.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
        return technicianInfoDao.getStationsByOrgId(serviceStationSerch);
    }

    public List<SerSkillInfo> getSkillInfosByOrgId(String orgId) {
        return technicianInfoDao.getSkillInfosByOrgId(orgId);
    }

    public ServiceTechnicianInfo formData(ServiceTechnicianInfo serviceTechnicianInfo) {
        ServiceTechnicianInfo info = technicianInfoDao.formData(serviceTechnicianInfo);
        if(StringUtils.isNotEmpty(info.getIdCardPic())){
            Map map2 = (HashMap) JsonMapper.fromJsonString(info.getIdCardPic(), HashMap.class);
            if(null != map2.get("befor")) {
                info.setIdCardPicBefor(map2.get("befor").toString());
            }
            if(null != map2.get("after")) {
                info.setIdCardPicAfter(map2.get("after").toString());
            }
        }
        if(null != info){
            List<ServiceTechnicianWorkTime> workTimes = info.getWorkTimes();
            if(null != workTimes){
                for(ServiceTechnicianWorkTime workTime : workTimes){
                    String week = workTime.getWeek();
                    List<ServiceTechnicianWorkTimeWeek> weeks = new ArrayList<ServiceTechnicianWorkTimeWeek>();
                    if(StringUtils.isNotBlank(week)){
                        String str[] = week.split(",");
                        Arrays.sort(str);
                        List<String> weekList = Arrays.asList(str);
                        if(null != weekList && weekList.size()!=0){
                            for(String weekI : weekList){
                                if(StringUtils.isNotBlank(weekI)){
                                    ServiceTechnicianWorkTimeWeek serviceTechnicianWorkTimeWeek = new ServiceTechnicianWorkTimeWeek();
                                    serviceTechnicianWorkTimeWeek.setId(weekI);
                                    weeks.add(serviceTechnicianWorkTimeWeek);
                                }
                            }
                        }
                    }
                    workTime.setWeeks(weeks);
                }
            }
        }
        return info;
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

        List<String> skillIds = dao.getSkillIds(serviceTechnicianInfo);
        if (null != skillIds) {
            //删除时同时删除关联的技能
            delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
            for (String skillId : skillIds) {
                //更新技师数量
                serSkillTechnicianDao.updateTechNum(skillId);
            }
        }
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
        Map map = new HashMap();//身份证照片(正反)
        map.put("befor",serviceTechnicianInfo.getIdCardPicBefor());
        map.put("after",serviceTechnicianInfo.getIdCardPicAfter());
        String picture = JsonMapper.toJsonString(map);
        serviceTechnicianInfo.setIdCardPic(picture);//身份证照片(正反)

        super.save(serviceTechnicianInfo);

        List<ServiceTechnicianWorkTime> times = serviceTechnicianInfo.getWorkTimes();
        List<ServiceTechnicianWorkTime> timesList = new ArrayList<ServiceTechnicianWorkTime>();

        if (null != times) {
            //删除工作时间 按技师
            deleteTechnicianWorkTime(serviceTechnicianInfo);
            for (ServiceTechnicianWorkTime time : times) {
                List<ServiceTechnicianWorkTimeWeek> weeks = time.getWeeks();
                if(null != weeks){
                    for(ServiceTechnicianWorkTimeWeek week : weeks){
                        ServiceTechnicianWorkTime technicianWorkTime = new ServiceTechnicianWorkTime();
                        technicianWorkTime.setTechId(serviceTechnicianInfo.getId());
                        technicianWorkTime.setStartTime(time.getStartTime());
                        technicianWorkTime.setEndTime(time.getEndTime());
                        technicianWorkTime.setWeek(week.getId());
                        technicianWorkTime.preInsert();
                        timesList.add(technicianWorkTime);
                    }
                }
            }
        }

        if (null != timesList) {
            for (ServiceTechnicianWorkTime time : timesList) {
                workTimeDao.insert(time);
            }
        }

        List<String> skillIds = serviceTechnicianInfo.getSkillIds();
        if (null != skillIds) {
            //删除时同时删除关联的技能
            delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
            for (String skillId : skillIds) {
                SerSkillTechnician serSkillTechnician = new SerSkillTechnician();
                serSkillTechnician.setSkillId(skillId);
                serSkillTechnician.setTechId(serviceTechnicianInfo.getId());
                serSkillTechnician.preInsert();
                serSkillTechnicianDao.insert(serSkillTechnician);

                //更新技师数量
                serSkillTechnicianDao.updateTechNum(skillId);
            }
        }
    }

    @Transactional(readOnly = false)
    public void saveService(ServiceTechnicianInfo serviceTechnicianInfo) {
        super.save(serviceTechnicianInfo);

        List<ServiceTechnicianWorkTime> times = serviceTechnicianInfo.getWorkTimes();
        List<ServiceTechnicianWorkTime> timesList = new ArrayList<ServiceTechnicianWorkTime>();

        if (null != times) {
            //删除工作时间 按技师
            deleteTechnicianWorkTime(serviceTechnicianInfo);
            for (ServiceTechnicianWorkTime time : times) {
                List<ServiceTechnicianWorkTimeWeek> weeks = time.getWeeks();
                if(null != weeks){
                    for(ServiceTechnicianWorkTimeWeek week : weeks){
                        ServiceTechnicianWorkTime technicianWorkTime = new ServiceTechnicianWorkTime();
                        technicianWorkTime.setTechId(serviceTechnicianInfo.getId());
                        technicianWorkTime.setStartTime(time.getStartTime());
                        technicianWorkTime.setEndTime(time.getEndTime());
                        technicianWorkTime.setWeek(week.getId());
                        technicianWorkTime.preInsert();
                        timesList.add(technicianWorkTime);
                    }
                }
            }
        }

        if (null != timesList) {
            for (ServiceTechnicianWorkTime time : timesList) {
                workTimeDao.insert(time);
            }
        }

        List<String> skillIds = serviceTechnicianInfo.getSkillIds();
        if (null != skillIds) {
            //删除时同时删除关联的技能
            delSerSkillTechnicianByTechnician(serviceTechnicianInfo);
            for (String skillId : skillIds) {
                SerSkillTechnician serSkillTechnician = new SerSkillTechnician();
                serSkillTechnician.setSkillId(skillId);
                serSkillTechnician.setTechId(serviceTechnicianInfo.getId());
                serSkillTechnician.preInsert();
                serSkillTechnicianDao.insert(serSkillTechnician);

                //更新技师数量
                serSkillTechnicianDao.updateTechNum(skillId);
            }
        }
    }

    @Transactional(readOnly = false)
    public void saveInfo(ServiceTechnicianInfo info) {
        Map map = new HashMap();//身份证照片(正反)
        if(StringUtils.isNotEmpty(info.getIdCardPicBefor())){
            map.put("befor",info.getIdCardPicBefor());
        }
        if(StringUtils.isNotEmpty(info.getIdCardPicAfter())) {
            map.put("after", info.getIdCardPicAfter());
        }
        if(map.size() != 0) {
            String picture = JsonMapper.toJsonString(map);
            info.setIdCardPic(picture);//身份证照片(正反)
        }

        info.preUpdate();
        dao.updateInfo(info);
    }
    @Transactional(readOnly = false)
    public void savePlus(ServiceTechnicianInfo info) {
        info.preUpdate();
        dao.updatePlus(info);
    }
    @Transactional(readOnly = false)
    public void saveOther(ServiceTechnicianInfo info) {
        info.preUpdate();
        dao.updateOther(info);
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
        if (SystemService.validatePassword(user.getPassword(), technician.getPassword())) {
            return technician;
        }
        return null;
    }

    //app获取技师技能工作时间
    public ServiceTechnicianInfo appFindSkillList(ServiceTechnicianInfo serviceTechnicianInfo){
        //工作时间
        List<ServiceTechnicianWorkTime> serviceTechnicianWorkTimes = technicianInfoDao.appGetWeekByTechId(serviceTechnicianInfo);
        //技师技能
        List<SerSkillInfo> serSkillInfos = serSkillInfoDao.appGetSkillByTech(serviceTechnicianInfo);
        serviceTechnicianInfo.setSkills(serSkillInfos);
        serviceTechnicianInfo.setTimes(serviceTechnicianWorkTimes);

        return serviceTechnicianInfo;
    }

    //app通讯录
    public Page<AppTech> appGetFriendByStationId(Page<AppTech> page,ServiceTechnicianInfo serviceTechnicianInfo){

        List<ServiceTechnicianInfo> serviceTechnicianInfos = technicianInfoDao.appGetFriendByStationId(serviceTechnicianInfo);
        int totalPage=0;
        AppTech tech=new AppTech();
        List<AppTech> techs=new ArrayList<AppTech>();
        for (ServiceTechnicianInfo t:serviceTechnicianInfos){
            tech.setId(t.getId());
            tech.setTechName(t.getName());
            tech.setTechPhone(t.getPhone());
            tech.setImgUrl("https://openservice.guoanshequ.com/"+t.getHeadPic());
            techs.add(tech);
        }
        tech.setPage(page);
        Page<AppTech> pageList = page.setList(techs);
        return pageList;
    }

    //编辑技师
    @Transactional(readOnly = false)
    public int updateTech(ServiceTechnicianInfo serviceTechnicianInfo){
        return dao.appUpdatePassword(serviceTechnicianInfo);
    }
    //校验手机号重复
	public int checkPhone(ServiceTechnicianInfo info) {
		// TODO Auto-generated method stub
		return technicianInfoDao.checkPhone(info);
	}

	public List<ServiceTechnicianInfo> findTechList(ServiceTechnicianInfo info) {
		// TODO Auto-generated method stub
		return technicianInfoDao.findTechList(info);
	}

    public List<ServiceTechnicianFamilyMembers> findFamilyMembersListByTechId(ServiceTechnicianInfo info) {
        return dao.findFamilyMembersListByTechId(info);
    }
}