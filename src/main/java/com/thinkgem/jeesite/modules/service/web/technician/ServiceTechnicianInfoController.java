/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.technician;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.BeanUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.service.service.basic.BasicOrganizationService;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务技师基础信息Controller
 *
 * @author a
 * @version 2017-11-16
 */
@Controller
@Api(tags = "技师管理",description = "技师管理相关接口")
@RequestMapping(value = "${adminPath}/service/technician/serviceTechnicianInfo")
public class ServiceTechnicianInfoController extends BaseController {

    @Autowired
    private ServiceTechnicianInfoService serviceTechnicianInfoService;
    @Autowired
    private BasicOrganizationService basicOrganizationService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ServiceStationService serviceStationService;

    @ModelAttribute
    public ServiceTechnicianInfo get(@RequestParam(required = false) String id) {
        ServiceTechnicianInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = serviceTechnicianInfoService.get(id);
        }
        if (entity == null) {
            entity = new ServiceTechnicianInfo();
        }
        return entity;
    }


    //获取用户所在服务站的工作时间
    @ResponseBody
    @ApiOperation(notes = "返回用户服务站的工作时间", value = "获取用户服务站的工作时间")
    @RequestMapping(value = {"getDate"}, method = RequestMethod.POST)
    public Result getDate(){
        String orgId = UserUtils.getUser().getOrganization().getId();
        BasicOrganization basicOrganization = basicOrganizationService.get(orgId);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        //开始时间
        Date workStartTime = basicOrganization.getWorkStartTime();
        String start = sdf.format(workStartTime);
        
        //结束时间
        Date workEndTime = basicOrganization.getWorkEndTime();
        String end = sdf.format(workEndTime);
        
        Map<String,String> map=new HashMap<String,String>();
        map.put("start", start);
        map.put("end", end);
        
        //add by wyr 前台新加返回的两字段并处理时间
        Calendar cal = Calendar.getInstance();  
        cal.setTime(workStartTime);
        cal.add(Calendar.MINUTE, -30);
        String startNew = sdf.format(cal.getTime());
        cal.setTime(workEndTime);
        cal.add(Calendar.MINUTE, 30);
        String endNew = sdf.format(cal.getTime());
        
        
        map.put("startNew", startNew);
        map.put("endNew", endNew);
        return new SuccResult(map);
    }

    @ResponseBody
    @ApiOperation("获取技师列表")
    @RequiresPermissions("techni_view")
    @RequestMapping(value = "listData", method = {RequestMethod.POST})
    public Result listData(@RequestBody(required = false) ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response) {
        if(null == serviceTechnicianInfo){
            serviceTechnicianInfo = new ServiceTechnicianInfo();
        }
        Page<ServiceTechnicianInfo> page = serviceTechnicianInfoService.findPage(new Page<ServiceTechnicianInfo>(request, response), serviceTechnicianInfo);

        User user = UserUtils.getUser();
        String orgId = user.getOrganization().getId();//机构ID
        List<BasicServiceStation> stations = serviceTechnicianInfoService.getStationsByOrgId(orgId);
        List<SerSkillInfo> skillInfos = serviceTechnicianInfoService.getSkillInfosByOrgId(orgId);

        HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
        objectObjectHashMap.put("page",page);
        objectObjectHashMap.put("stations",stations);
        objectObjectHashMap.put("skillInfos",skillInfos);
        return new SuccResult(objectObjectHashMap);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
    @RequestMapping(value = "formData", method = {RequestMethod.POST})
    @ApiOperation("根据ID查找技师")
    public Result formData(@RequestBody ServiceTechnicianInfo serviceTechnicianInfo) {
        ServiceTechnicianInfo entity = null;
        if (StringUtils.isNotBlank(serviceTechnicianInfo.getId())) {
            entity = serviceTechnicianInfoService.formData(serviceTechnicianInfo);
        }

        if (entity == null) {
            return new FailResult("未找到此id对应的服务分类。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    @RequiresPermissions("techni_delete")
    @ApiOperation("删除技师")
    @RequestMapping(value = "deleteData", method = RequestMethod.POST)
    public Result deleteData(@RequestBody ServiceTechnicianInfo serviceTechnicianInfo) {
        //服务人员还有未完成的订单，则不可删除
        if(serviceTechnicianInfoService.getOrderTechRelation(serviceTechnicianInfo) > 0){
            return new FailResult("服务人员有未完成订单,不可删除.");
        }
        //删除技师
        serviceTechnicianInfoService.delete(serviceTechnicianInfo);

        return new SuccResult("删除技师信息成功");
    }


    /**
     * 新增保存
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("新增保存")
    @RequiresPermissions("techni_insert")
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    public Result saveData(@RequestBody ServiceTechnicianInfo info) {
        List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }
        if (StringUtils.isEmpty(info.getOrgId())) {
            User user = UserUtils.getUser();
            info.setOrgId(user.getOrganization().getId());
        }
        //ServiceTechnicianInfo techInfo = serviceTechnicianInfoService.findTech(info);
        List<ServiceTechnicianInfo> techInfoList=serviceTechnicianInfoService.findTechList(info);
        
        if (null == techInfoList||techInfoList.size()==0) {
            String phoneSub = info.getPhone().substring(7,11);
            String appPwd = systemService.entryptPassword(phoneSub);//APP端登录密码默认为手机号的后4位
            info.setAppLoginPassword(appPwd);
            serviceTechnicianInfoService.save(info);

            return new SuccResult("保存成功");
        } else {
            return new FailResult("技师手机号不能重复！");
        }
    }

    /**
     * 更新保存
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("更新保存个人资料")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "upData", method = RequestMethod.POST)
    public Result upData(@RequestBody ServiceTechnicianInfo info) {
        /*List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }*/
    	//add by WYR 校验手机号重复
        /*if (StringUtils.isEmpty(info.getOrgId())) {
            User user = UserUtils.getUser();
            info.setOrgId(user.getOrganization().getId());
        }*/
        int i= serviceTechnicianInfoService.checkPhone(info);
        if (0!=i) {
            return new FailResult("技师手机号不能重复！");
        }
        serviceTechnicianInfoService.saveInfo(info);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    @ApiOperation("更新保存服务信息")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "upDataService", method = RequestMethod.POST)
    public Result upDataService(@RequestBody ServiceTechnicianInfo info) {
        /*List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }*/
        if("leave".equals(info.getJobStatus())){//离职员工判断是否有订单
            //服务人员还有未完成的订单，则不可离职
            if(serviceTechnicianInfoService.getOrderTechRelation(info) > 0){
                return new FailResult("服务人员有未完成订单,不可离职.");
            }
        }
        serviceTechnicianInfoService.saveService(info);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    @ApiOperation("更新保存补充资料")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "upDataPlus", method = RequestMethod.POST)
    public Result upDataPlus(@RequestBody ServiceTechnicianInfo info) {
        /*List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }*/
        serviceTechnicianInfoService.savePlus(info);
        return new SuccResult("保存成功");
    }

    @ResponseBody
    @ApiOperation("更新保存其它信息")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "upDataOther", method = RequestMethod.POST)
    public Result upDataOther(@RequestBody ServiceTechnicianInfo info) {
        /*List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }*/
        serviceTechnicianInfoService.saveOther(info);
        return new SuccResult("保存成功");
    }

    /**
     * App保存
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("APP保存")
    @RequiresPermissions("techni_app")
    @RequestMapping(value = "saveAppPassWordData", method = RequestMethod.POST)
    public Result saveAppPassWordData(@RequestBody ServiceTechnicianInfo info) {
        if(StringUtils.isBlank(info.getAppLoginPassword())){
            return new FailResult("保存失败");
        }
        String appPwd = systemService.entryptPassword(info.getAppLoginPassword());//APP端登录密码加密
        info.setAppLoginPassword(appPwd);
        serviceTechnicianInfoService.savePass(info);

        return new SuccResult("保存成功");
    }

    /**
     * 保存家庭成员
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("保存家庭成员")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "saveFamilyMembers", method = RequestMethod.POST)
    public Result saveFamilyMembers(@RequestBody ServiceTechnicianInfo info) {
        serviceTechnicianInfoService.saveFamilyMembers(info);
        List<ServiceTechnicianFamilyMembers> list = serviceTechnicianInfoService.findFamilyMembersListByTechId(info);
        return new SuccResult(list);
    }


    @ResponseBody
    @ApiOperation("删除家庭成员")
    @RequiresPermissions("techni_update")
    @RequestMapping(value = "deleteFamilyMembers", method = RequestMethod.POST)
    public Result deleteFamilyMembers(@RequestBody ServiceTechnicianFamilyMembers member) {
        //删除家庭成员
        serviceTechnicianInfoService.deleteFamilyMember(member);
        return new SuccResult("删除家庭成员成功");
    }


    @ResponseBody
    //@RequiresPermissions("skill_view")
    @RequestMapping(value = "scheduleList", method = { RequestMethod.POST, RequestMethod.GET })
    @ApiOperation("获取技师排期列表")
    public Result scheduleList(@RequestBody(required = false) ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response) {
        if (serviceTechnicianInfo == null) {
            serviceTechnicianInfo = new ServiceTechnicianInfo();
        }
        if (StringUtils.isEmpty(serviceTechnicianInfo.getStationId())){
            User user = UserUtils.getUser();
            if (user.getType().equals("station")){
                BasicOrganization organization = user.getOrganization();
                BasicServiceStation station = user.getStation();
                serviceTechnicianInfo.setOrgId(organization.getId());
                serviceTechnicianInfo.setStationId(station.getId());
            }
        }
        Page<ServiceTechnicianInfo> serSkillPage = new Page<>(request, response);
        Page<ServiceTechnicianInfo> page = serviceTechnicianInfoService.scheduleList(serSkillPage, serviceTechnicianInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "orgList", method = {RequestMethod.POST, RequestMethod.GET})
    //@RequiresPermissions("user")
    @ApiOperation("根据登录用户机构ID查询服务站和技能下拉")
    public Result orgList(HttpServletRequest request, HttpServletResponse response) {
        BasicOrganization organization = UserUtils.getUser().getOrganization();
        List<BasicOrganization> list0 = new ArrayList<>();
        List<BasicServiceStation> list = new ArrayList<>();
        List<SerSkillInfo> list1 = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<String,Object>();
        if (StringUtils.isNotBlank(organization.getId())) {
            BasicServiceStation serviceStation = new BasicServiceStation();
            serviceStation.setOrgId(organization.getId());
            list = serviceStationService.findList(serviceStation);
            ServiceTechnicianInfo serviceTechnicianInfo = new ServiceTechnicianInfo();
            serviceTechnicianInfo.setOrgId(organization.getId());
            list1 = serviceTechnicianInfoService.findSkil(serviceTechnicianInfo);
            list0.add(organization);
            map.put("organizations",list0);
            map.put("stations",list);
            map.put("skils",list1);
        }
        return new SuccResult(map);
    }

    @ResponseBody
    @RequestMapping(value = "listByOrgId", method = {RequestMethod.POST, RequestMethod.GET})
    //@RequiresPermissions("user")
    @ApiOperation("根据机构ID查询服务站和技能下拉")
    public Result listByOrgId(@RequestBody(required = false) ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response) {
        List<BasicServiceStation> list = new ArrayList<>();
        List<SerSkillInfo> list1 = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<String,Object>();
        if (StringUtils.isNotBlank(serviceTechnicianInfo.getOrgId())) {
            BasicServiceStation serviceStation = new BasicServiceStation();
            serviceStation.setOrgId(serviceTechnicianInfo.getOrgId());
            list = serviceStationService.findList(serviceStation);
            list1 = serviceTechnicianInfoService.findSkil(serviceTechnicianInfo);
            map.put("stations",list);
            map.put("skils",list1);
        }
        return new SuccResult(map);
    }
}