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
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    public Result listData(@RequestBody(required = false) ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response) {
        if(null == serviceTechnicianInfo){
            serviceTechnicianInfo = new ServiceTechnicianInfo();
        }
        Page<ServiceTechnicianInfo> page = serviceTechnicianInfoService.findPage(new Page<ServiceTechnicianInfo>(request, response), serviceTechnicianInfo);

        return new SuccResult(page);
    }


    //	@RequiresPermissions("service:technician:serviceTechnicianInfo:view")
    @ResponseBody
    @RequestMapping(value = "form", method = RequestMethod.GET)
    public Result form(ServiceTechnicianInfo serviceTechnicianInfo, Model model) {
        ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoService.get(serviceTechnicianInfo.getId());
        technicianInfo.setImages(serviceTechnicianInfoService.getImages(technicianInfo));
        technicianInfo.setServiceInfo(serviceTechnicianInfoService.getServiceInfo(technicianInfo));
        technicianInfo.setWorkTime(serviceTechnicianInfoService.findWorkTimeByTech(technicianInfo));

        return new SuccResult(serviceTechnicianInfo);
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
     * 保存个人资料
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("保存创建个人资料")
    @RequestMapping(value = "savePersonalData", method = RequestMethod.POST)
    public Result savePersonalData(@RequestBody ServiceTechnicianInfo info) {

        Set<ConstraintViolation<ServiceTechnicianInfo>> validate = validator.validate(info, SavePersonalGroup.class);
        if (validate != null && validate.size() > 0) {
            ArrayList<String> errs = new ArrayList<>();
            for (ConstraintViolation<ServiceTechnicianInfo> violation : validate) {
                errs.add(violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return new FailResult(errs);
        }

        ServiceTechnicianInfo techInfo = serviceTechnicianInfoService.findTech(info);
        if (null == techInfo) {
            User user = UserUtils.getUser();
            info.setSort("0");
            info.setTechOfficeId(user.getOffice().getId());
            info.setTechOfficeName(user.getOffice().getName());
            info.setTechStationId(user.getStation().getId());
            info.setTechStationName(user.getStation().getName());
            if(StringUtils.isBlank(info.getId())){
                info.setStatus("0");//新增技师，状态不可用0 服务信息补充完毕并保存成功后，该技师变为可用状态1
            }
            serviceTechnicianInfoService.save(info);
            serviceTechnicianInfoService.saveImages(info);
            return new SuccResult(info);
        } else {
            return new FailResult("技师名称重复");
        }
    }

    /**
     * 保存更多信息,保存手机号密码，保存其它信息
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("保存补充个人资料,保存APP密码,保存其它信息")
    @RequestMapping(value = "saveMoreData", method = RequestMethod.POST)
    public Result saveMoreData(@RequestBody ServiceTechnicianInfo info) {

        Set<ConstraintViolation<ServiceTechnicianInfo>> validate = validator.validate(info, SaveMoreGroup.class);
        if (validate != null && validate.size() > 0) {
            ArrayList<String> errs = new ArrayList<>();
            for (ConstraintViolation<ServiceTechnicianInfo> violation : validate) {
                errs.add(violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return new FailResult(errs);
        }
        ServiceTechnicianInfo technicianInfo = serviceTechnicianInfoService.get(info.getId());
        BeanUtils.cloneProperties(info, technicianInfo);
        technicianInfo.setImages(info.getImages());
        serviceTechnicianInfoService.saveMoreData(technicianInfo);
       // serviceTechnicianInfoService.saveImages(info);
       // return new SuccResult(info);
        return new SuccResult("保存成功");
    }

    /**
     * 保存服务信息
     *
     * @param info
     * @return
     */
    @ResponseBody
    @ApiOperation("保存服务信息")
    @RequestMapping(value = "saveServiceInfoData", method = RequestMethod.POST)
    public Result saveServiceInfoData(@RequestBody ServiceTechnicianInfo info) {
        Set<ConstraintViolation<ServiceTechnicianInfo>> validate = validator.validate(info, SaveServiceInfoGroup.class, SaveMoreGroup.class);
        if (validate != null && validate.size() > 0) {
            ArrayList<String> errs = new ArrayList<>();
            for (ConstraintViolation<ServiceTechnicianInfo> violation : validate) {
                errs.add(violation.getPropertyPath() + ":" + violation.getMessage());
            }
            return new FailResult(errs);
        }
        serviceTechnicianInfoService.saveServiceInfo(info);
        serviceTechnicianInfoService.saveWorkTimes(info);

        serviceTechnicianInfoService.updateStatus(info.getId());
        return new SuccResult("保存服务信息成功");
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

    //	@RequiresPermissions("service:technician:serviceTechnicianInfo:edit")
    @ResponseBody
    @ApiOperation("得到技师的所有家庭成员")
    @RequestMapping(value = "findFamilyMember", method = RequestMethod.POST)
    public Result findFamilyMember(@RequestBody ServiceTechnicianInfo info) {
        //得到技师的所有家庭成员
        List<ServiceTechnicianFamilyMembers> members = serviceTechnicianInfoService.findFamilyMember(info);
        return new SuccResult(members);
    }

    @ResponseBody
    @ApiOperation("选择城市")
    @RequestMapping(value = "findOfficeSeviceAreaList", method = RequestMethod.GET)
    public Result findOfficeSeviceAreaList() {
        ServiceTechnicianInfo info = new ServiceTechnicianInfo();
        //选择城市
        User user = UserUtils.getUser();
        info.setTechOfficeId(user.getOffice().getId());
        info.setTechOfficeName(user.getOffice().getName());
        List<ServiceTechnicianInfo> list = serviceTechnicianInfoService.findOfficeSeviceAreaList(info);
        return new SuccResult(list);
    }

    @ResponseBody
    @ApiOperation("选择城市所属服务站")
    @RequestMapping(value = "findServiceStationByArea", method = RequestMethod.POST)
    public Result findServiceStationByArea(@RequestBody ServiceTechnicianInfo info) {
        //addr_city_id
        //List<OfficeSeviceAreaList> list = serviceTechnicianInfoService.findOfficeSeviceAreaList(info);
        List<ServiceTechnicianInfo> list = new ArrayList<ServiceTechnicianInfo>();

        ServiceTechnicianInfo o1 = new ServiceTechnicianInfo();
        o1.setTechStationId("1");
        o1.setTechStationName("服务站1");
        ServiceTechnicianInfo o2 = new ServiceTechnicianInfo();
        o2.setTechStationId("2");
        o2.setTechStationName("服务站2");
        ServiceTechnicianInfo o3 = new ServiceTechnicianInfo();
        o3.setTechStationId("3");
        o3.setTechStationName("服务站3");
        Random random = new Random();

        int s = random.nextInt(3);
        switch(s){
            case 1:
                list.add(o1);
                break;
            case 2:
                list.add(o2);
                break;
            default:
                list.add(o3);
                break;
        }
        return new SuccResult(list);
    }
}