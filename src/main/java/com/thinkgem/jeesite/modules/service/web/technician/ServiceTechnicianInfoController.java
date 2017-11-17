/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.technician;

import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianFamilyMembers;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 服务技师基础信息Controller
 *
 * @author a
 * @version 2017-11-16
 */
@Controller
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

//	@RequiresPermissions("service:technician:serviceTechnicianInfo:view")
//	@RequestMapping(value = {"list", ""})
//	public String list(ServiceTechnicianInfo serviceTechnicianInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
//		Page<ServiceTechnicianInfo> page = serviceTechnicianInfoService.findPage(new Page<ServiceTechnicianInfo>(request, response), serviceTechnicianInfo);
//		model.addAttribute("page", page);
//		return "modules/service/technician/serviceTechnicianInfoList";
//	}
//
//	@RequiresPermissions("service:technician:serviceTechnicianInfo:view")
//	@RequestMapping(value = "form")
//	public String form(ServiceTechnicianInfo serviceTechnicianInfo, Model model) {
//		model.addAttribute("serviceTechnicianInfo", serviceTechnicianInfo);
//		return "modules/service/technician/serviceTechnicianInfoForm";
//	}
//
//	@RequiresPermissions("service:technician:serviceTechnicianInfo:edit")
//	@RequestMapping(value = "save")
//	public String save(ServiceTechnicianInfo serviceTechnicianInfo, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, serviceTechnicianInfo)){
//			return form(serviceTechnicianInfo, model);
//		}
//		serviceTechnicianInfoService.save(serviceTechnicianInfo);
//		addMessage(redirectAttributes, "保存服务技师基础信息成功");
//		return "redirect:"+Global.getAdminPath()+"/service/technician/serviceTechnicianInfo/?repage";
//	}
//
//	@RequiresPermissions("service:technician:serviceTechnicianInfo:edit")
//	@RequestMapping(value = "delete")
//	public String delete(ServiceTechnicianInfo serviceTechnicianInfo, RedirectAttributes redirectAttributes) {
//		serviceTechnicianInfoService.delete(serviceTechnicianInfo);
//		addMessage(redirectAttributes, "删除服务技师基础信息成功");
//		return "redirect:"+Global.getAdminPath()+"/service/technician/serviceTechnicianInfo/?repage";
//	}

    /**
     * 1.保存服务技师个人资料，基本信息
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public Result saveData(@RequestBody ServiceTechnicianInfo info) {
        if (StringUtils.isBlank(info.getSort())) {
            info.setSort("0");
        }
        serviceTechnicianInfoService.save(info);  //更新或修改主信息


        return new SuccResult("保存：" + info.getTechName() + " 成功。");
    }

}