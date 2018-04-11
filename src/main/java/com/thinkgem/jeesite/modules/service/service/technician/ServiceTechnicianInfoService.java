/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
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
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.dao.DictDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private AreaDao areaDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private ServiceStationService serviceStationService;

    @Autowired
    private ServiceTechnicianInfoDao serviceTechnicianInfoDao;

    public ServiceTechnicianInfo get(String id) {
        return super.get(id);
    }
    //app 根据手机获取用户id和stationId
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
                    if (workTime.getStartTimeStr() !=null&&workTime.getStartTimeStr() !="") {
                        workTime.setStartTimeStr(workTime.getStartTimeStr().substring(0, 5));
                    }
                    if (workTime.getEndTimeStr() !=null&&workTime.getEndTimeStr() !="") {
                        workTime.setEndTimeStr(workTime.getEndTimeStr().substring(0, 5));
                    }
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
        //删除排期数据 按技师
        technicianInfoDao.deleteSchedule(serviceTechnicianInfo);

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
    public void savePass(ServiceTechnicianInfo serviceTechnicianInfo){
        serviceTechnicianInfo.preUpdate();
        technicianInfoDao.savePass(serviceTechnicianInfo);
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
                serSkillTechnician.setTechStationId(serviceTechnicianInfo.getStationId());
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
            List<SerSkillTechnician> skills = serSkillTechnicianDao.getSkillByTechId(serviceTechnicianInfo);
            //删除时同时删除关联的技能
            delSerSkillTechnicianByTechnician(serviceTechnicianInfo);

            for(SerSkillTechnician serSkillTechnician : skills){
                //更新技师数量
                serSkillTechnicianDao.updateTechNum(serSkillTechnician.getSkillId());
            }

            for (String skillId : skillIds) {
                SerSkillTechnician serSkillTechnician = new SerSkillTechnician();
                serSkillTechnician.setSkillId(skillId);
                serSkillTechnician.setTechId(serviceTechnicianInfo.getId());
                serSkillTechnician.setTechStationId(serviceTechnicianInfo.getStationId());
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
    //手机获取用户
    public ServiceTechnicianInfo findTech(ServiceTechnicianInfo info) {
        return technicianInfoDao.findTech(info);
    }
    //app id获取用户
    public ServiceTechnicianInfo appFindTech(ServiceTechnicianInfo info) {
        return technicianInfoDao.appFindTech(info);
    }
    public AppServiceTechnicianInfo getTechnicianById(ServiceTechnicianInfo info){
        AppServiceTechnicianInfo technicianById = technicianInfoDao.getTechnicianById(info);
        return technicianById;
    }
    //app
    public ServiceTechnicianInfo getById(ServiceTechnicianInfo info){
        ServiceTechnicianInfo byId = technicianInfoDao.getById(info);
        return byId;
    }
    //app 新增
    @Transactional(readOnly = false)
    public int saveApp(ServiceTechnicianInfo serviceTechnicianInfo) {
        int i = super.saveAPP(serviceTechnicianInfo);
        return i;
    }

    //app 编辑
    @Transactional(readOnly = false)
    public int appUpdate(AppServiceTechnicianInfo appTech) {
        //将apptech转成技师
        ServiceTechnicianInfo tech=new ServiceTechnicianInfo();
        if (null == tech){
            throw new ServiceException("没有该用户");
        }
        tech.setId(appTech.getId());
        tech.setHeadPic(appTech.getImgUrlHead());
        tech.setName(appTech.getTechName());
        tech.setPhone(appTech.getTechPhone());
        tech.setSex(appTech.getTechSex());
        tech.setBirthDate(appTech.getTechBirthDate());
        tech.setAddress(appTech.getAddrDetailInfo());
        tech.setIdCard(appTech.getTechIdCard());
        tech.setIdCardPicBefor(appTech.getImgUrlCard());
        tech.setEmail(appTech.getTechEmail());
        if (StringUtils.isNotBlank(appTech.getTechHeight())){
            tech.setHeight(Integer.valueOf(appTech.getTechHeight()));
        }
        if (StringUtils.isNotBlank(appTech.getTechWeight())){
            tech.setWeight(Integer.valueOf(appTech.getTechWeight()));
        }
        //籍贯code
        tech.setNativeProvinceCode(appTech.getTechNativePlace());
        //民族code
        tech.setNation(appTech.getTechNation());
        tech.setDescription(appTech.getExperDesc());
        tech.setLifePic(appTech.getImgUrlLife());
        tech.setIdCardPicBefor(appTech.getImgUrlCardBefor());
        tech.setIdCardPicAfter(appTech.getImgUrlCardAfter());
        //省市区code
        tech.setProvinceCode(appTech.getProvinceCode());
        tech.setCityCode(appTech.getCityCode());
        tech.setAreaCode(appTech.getAreaCode());
        //身份证正反面
        Map<String,String> map=new HashMap<String,String>();
        map.put("befor",appTech.getImgUrlCardBefor());
        map.put("after",appTech.getImgUrlCardAfter());
        String s = JsonMapper.toJsonString(map);
        if (StringUtils.isNotBlank(appTech.getImgUrlCardBefor()) || StringUtils.isNotBlank(appTech.getImgUrlCardAfter())){
            tech.setIdCardPic(s);
        }
        tech.appPreUpdate();
        int i =dao.appUpdate(tech);
        return i;
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

    //登陆
    public AppServiceTechnicianInfo appLogin(LoginUser user) {
        AppServiceTechnicianInfo technician = technicianInfoDao.getTechnicianByPhone(user);
        if (technician == null){
            throw new ServiceException("用户名输入错误，没有该用户");
        }
        if (!SystemService.validatePassword(user.getPassword(), technician.getPassword())) {
           throw new ServiceException("用户密码输入错误");
        }
        if(StringUtils.isNotBlank(technician.getJobStatus())) {
            if (technician.getJobStatus().equals("leave")) {
                throw new ServiceException("用户已离职不可登录");
            }
        }
        PropertiesLoader loader = new PropertiesLoader("oss.properties");
        String ossHost = loader.getProperty("OSS_THUMB_HOST");
        String imgUrlHead = technician.getImgUrlHead();
        technician.setImgUrlHead(ossHost + imgUrlHead);
        String imgUrlLife = technician.getImgUrlLife();
        String imgUrlCard = technician.getImgUrlCard();
        //身份证正反面
        if (StringUtils.isNotBlank(imgUrlCard)){
            Map<String, String> map = (Map<String, String>) JsonMapper.fromJsonString(imgUrlCard, Map.class);
            technician.setImgUrlCardAfter(ossHost + map.get("after"));
            technician.setImgUrlCardBefor(ossHost + map.get("befor"));
        }
        technician.setImgUrlCard(imgUrlCard);
        technician.setImgUrlLife(ossHost+imgUrlLife);
        //头像
        technician.setImgUrl(ossHost+technician.getImgUrlHead());
        //民族
        if (StringUtils.isNotBlank(technician.getTechNationValue())){
            Dict dict=new Dict();
            dict.setType("ethnic");
            dict.setValue(technician.getTechNationValue());
            Dict name = dictDao.findName(dict);
            technician.setTechNation(name.getLabel());
        }
        //籍贯
        if (StringUtils.isNotBlank(technician.getTechNativePlaceValue())){
            List<Area> nameByCode = areaDao.getNameByCode(technician.getTechNativePlaceValue());
            technician.setTechNativePlace(nameByCode.get(0).getName());
        }
        //省
        if (StringUtils.isNotBlank(technician.getProvinceCode())){
            List<Area> sheng = areaDao.getNameByCode(technician.getProvinceCode());
            technician.setProvinceCodeName(sheng.get(0).getName());
        }
        //市
        if (StringUtils.isNotBlank(technician.getCityCode())){
            List<Area> shi = areaDao.getNameByCode(technician.getCityCode());
            technician.setCityCodeName(shi.get(0).getName());
        }
        //区
        if (StringUtils.isNotBlank(technician.getAreaCode())){
            List<Area> qu = areaDao.getNameByCode(technician.getAreaCode());
            technician.setAreaCodeName(qu.get(0).getName());
        }
        //获取技师服务站名称
        if (StringUtils.isNotBlank(technician.getTechPhone())){
            ServiceTechnicianInfo serviceTechnicianInfo = technicianInfoDao.getByPhone(technician.getTechPhone());
            BasicServiceStation basicServiceStation = serviceStationService.get(serviceTechnicianInfo.getStationId());
            technician.setStationName(basicServiceStation.getName());
        }
        return technician;
    }

    //app服务信息 -- 获取技师技能工作时间
    public ServiceTechnicianInfo appFindSkillList(ServiceTechnicianInfo serviceTechnicianInfo){
        //工作时间
        List<ServiceTechnicianWorkTime> serviceTechnicianWorkTimes = technicianInfoDao.appGetWeekByTechId(serviceTechnicianInfo);
        if (serviceTechnicianWorkTimes !=null && serviceTechnicianWorkTimes.size() >0) {
            for (ServiceTechnicianWorkTime work : serviceTechnicianWorkTimes) {
                Date startTime = work.getStartTime();
                Date endTime = work.getEndTime();
                //date转String
                work.setStartTimes(DateUtils.formatDate(startTime, "HH:mm"));
                work.setEndTimes(DateUtils.formatDate(endTime, "HH:mm"));
            }
        }
        //技师技能
        List<SerSkillInfo> serSkillInfos = serSkillInfoDao.appGetSkillByTech(serviceTechnicianInfo);
        serviceTechnicianInfo.setSkills(serSkillInfos);
        serviceTechnicianInfo.setTimes(serviceTechnicianWorkTimes);

        return serviceTechnicianInfo;
    }

    //app通讯录
    public Page<AppServiceTechnicianInfo> appGetFriendByStationId(Page<AppServiceTechnicianInfo> page,ServiceTechnicianInfo serviceTechnicianInfo){
        Page<ServiceTechnicianInfo> page1 = new Page<>();
        page1.setCount(page.getCount());
        page1.setPageNo(page.getPageNo());
        page1.setPageSize(page.getPageSize());
        serviceTechnicianInfo.setPage(page1);
        if (StringUtils.isNotBlank(serviceTechnicianInfo.getStationId()) && StringUtils.isNotBlank(serviceTechnicianInfo.getId())){
            List<AppServiceTechnicianInfo> serviceTechnicianInfos = technicianInfoDao.appGetFriendByStationId(serviceTechnicianInfo);
            PropertiesLoader loader = new PropertiesLoader("oss.properties");
            String ossHost = loader.getProperty("OSS_THUMB_HOST");
            if (serviceTechnicianInfos!=null && serviceTechnicianInfos.size()>0) {
                for (AppServiceTechnicianInfo appTech : serviceTechnicianInfos) {
                    appTech.setImgUrlHead(ossHost + appTech.getImgUrlHead());
                    appTech.setImgUrl(appTech.getImgUrlHead());
                }
                page.setCount(page1.getCount());
                page.setPageNo(page1.getPageNo());
                page.setPageSize(page1.getPageSize());
                page.setList(serviceTechnicianInfos);
            }else {
                throw new ServiceException("暂无数据");
            }
        }
        return page;
    }

    //编辑技师
    @Transactional(readOnly = false)
    public int updateTech(ServiceTechnicianInfo serviceTechnicianInfo){
        serviceTechnicianInfo.appPreUpdate();
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

    public Page<ServiceTechnicianInfo> scheduleList(Page<ServiceTechnicianInfo> schedulePage, ServiceTechnicianInfo serviceTechnicianInfo) {
        serviceTechnicianInfo.setPage(schedulePage);
        List<ServiceTechnicianInfo> stiList = serviceTechnicianInfoDao.getTechList(serviceTechnicianInfo); //查出所有技师信息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (serviceTechnicianInfo.getBeginTime() == null){  //如果前台传入日期
            try {
                serviceTechnicianInfo.setBeginTime(sdf.parse(sdf.format(new Date()))); //前台没传时间，拿当前时间
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (ServiceTechnicianInfo sti : stiList){  //循环技师
            List<ScheduleDateInfo> sdiList = new ArrayList<ScheduleDateInfo>();
            Date date = serviceTechnicianInfo.getBeginTime();
            for (int i = 0;i < 7;i++){  //循环7天
                ScheduleDateInfo sdi = new ScheduleDateInfo();

                sdi.setSevenDate(sdf.format(date));  //给七天日期赋值
                int weekNum = DateUtils.getWeekNum(date);  //判断当前循环的日期是周几
                sti.setWeekNum(weekNum);
                ScheduleDateInfo sdi1 = serviceTechnicianInfoDao.getWorkTime(sti);  //根据技师ID和周几查询技师工作时间
                if (sdi1 != null){
                    sdi.setWorkBeginTime(sdi1.getWorkBeginTime());  //赋值工作开始时间
                    sdi.setWorkEndTime(sdi1.getWorkEndTime());    //赋值工作结束时间
                }
                Date dateFirstTime = DateUtils.getDateFirstTime(date);  //将日期转换成带00:00:00
                Date dateLastTime = DateUtils.getDateLastTime(date);   //将日期转换成带23:59:59
                sti.setSchedulebeginTime(dateFirstTime);
                sti.setScheduleEndTime(dateLastTime);
                List<TechScheduleInfo> tsiList = serviceTechnicianInfoDao.getScheduleList(sti);  //查询当前技师的排期信息
                if (tsiList.size() > 0){
                    sdi.setTechScheduleInfos(tsiList);
                }
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
                date = c.getTime();
                sdiList.add(sdi);
            }
            sti.setScheduleDateInfos(sdiList);
        }

        schedulePage.setList(stiList);
        return schedulePage;
    }

    public List<SerSkillInfo> findSkil(ServiceTechnicianInfo serviceTechnicianInfo) {
        return serviceTechnicianInfoDao.findSkil(serviceTechnicianInfo);
    }

}