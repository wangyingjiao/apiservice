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
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.service.service.basic.BasicOrganizationService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
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

    //@RequiresPermissions("service:technician:serviceTechnicianInfo:view")
    @ResponseBody
    @ApiOperation("获取技师列表")
    @RequestMapping(value = "listData", method = {RequestMethod.POST})
    public Result listData(@RequestBody(required = false) ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response) {
        if(null == serviceTechnicianInfo){
            serviceTechnicianInfo = new ServiceTechnicianInfo();
        }
        Page<ServiceTechnicianInfo> page = serviceTechnicianInfoService.findPage(new Page<ServiceTechnicianInfo>(request, response), serviceTechnicianInfo);

        User user = UserUtils.getUser();
        String orgId = user.getOrganization().getId();//机构ID
        List<BasicServiceCity> cityCodes = basicOrganizationService.getOrgCityCodes(orgId);

        List<BasicServiceStation> stations = serviceTechnicianInfoService.getStationsByOrgId(orgId);
        List<SerSkillInfo> skillInfos = serviceTechnicianInfoService.getSkillInfosByOrgId(orgId);

        HashMap<Object, Object> objectObjectHashMap = new HashMap<Object, Object>();
        objectObjectHashMap.put("page",page);
        objectObjectHashMap.put("cityCodes",cityCodes);
        objectObjectHashMap.put("stations",stations);
        objectObjectHashMap.put("skillInfos",skillInfos);
        return new SuccResult(objectObjectHashMap);
    }

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

    //	@RequiresPermissions("service:technician:serviceTechnicianInfo:edit")
    @ResponseBody
    @ApiOperation("删除技师")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public Result delete(@RequestBody ServiceTechnicianInfo serviceTechnicianInfo) {
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
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    public Result saveData(@RequestBody ServiceTechnicianInfo info) {
        List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }

        ServiceTechnicianInfo techInfo = serviceTechnicianInfoService.findTech(info);
        if (null == techInfo) {
            User user = UserUtils.getUser();
            info.setOrgId(user.getOrganization().getId());
            String phoneSub = info.getPhone().substring(7,11);
            String appPwd = systemService.entryptPassword(phoneSub);//APP端登录密码默认为手机号的后4位
            info.setAppLoginPassword(appPwd);
            serviceTechnicianInfoService.save(info);

            return new SuccResult("保存成功");
        } else {
            return new FailResult("技师名称重复");
        }
    }

    /**
     * 更新保存
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("更新保存")
    @RequestMapping(value = "upData", method = RequestMethod.POST)
    public Result upData(@RequestBody ServiceTechnicianInfo info) {
        List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }
        serviceTechnicianInfoService.save(info);
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
    @RequestMapping(value = "saveAppPassWordData", method = RequestMethod.POST)
    public Result saveAppPassWordData(@RequestBody ServiceTechnicianInfo info) {
        List<String> errList = errors(info);
        if (errList != null && errList.size() > 0) {
            return new FailResult(errList);
        }
        if(StringUtils.isBlank(info.getAppLoginPassword())){
            return new FailResult("保存失败");
        }
        String appPwd = systemService.entryptPassword(info.getAppLoginPassword());//APP端登录密码默认为手机号的后4位
        info.setAppLoginPassword(appPwd);
        serviceTechnicianInfoService.saveApp(info);

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
    @RequestMapping(value = "saveFamilyMembers", method = RequestMethod.POST)
    public Result saveFamilyMembers(@RequestBody ServiceTechnicianInfo info) {
        Set<ConstraintViolation<ServiceTechnicianInfo>> validate = validator.validate(info, SaveServiceInfoGroup.class, SaveMoreGroup.class);
        if (validate != null && validate.size() > 0) {
            ArrayList<String> errs = new ArrayList<>();
            for (ConstraintViolation<ServiceTechnicianInfo> violation : validate) {
                errs.add(violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return new FailResult(errs);
        }

        serviceTechnicianInfoService.saveFamilyMembers(info);
        return new SuccResult("保存家庭成员成功");
    }


    //	@RequiresPermissions("service:technician:serviceTechnicianInfo:edit")
    @ResponseBody
    @ApiOperation("删除家庭成员")
    @RequestMapping(value = "deleteFamilyMembers", method = RequestMethod.POST)
    public Result deleteFamilyMembers(@RequestBody ServiceTechnicianFamilyMembers member) {
        //删除家庭成员
        serviceTechnicianInfoService.deleteFamilyMember(member);
        return new SuccResult("删除家庭成员成功");
    }

}