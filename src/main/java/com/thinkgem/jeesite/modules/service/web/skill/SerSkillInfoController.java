/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.skill;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillInfoService;

/**
 * 技能管理Controller
 * @author a
 * @version 2017-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/service/skill/serSkillInfo")
public class SerSkillInfoController extends BaseController {

	@Autowired
	private SerSkillInfoService serSkillInfoService;
	
	@ModelAttribute
	public SerSkillInfo get(@RequestParam(required=false) String id) {
		SerSkillInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serSkillInfoService.get(id);
		}
		if (entity == null){
			entity = new SerSkillInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:skill:serSkillInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerSkillInfo serSkillInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerSkillInfo> page = serSkillInfoService.findPage(new Page<SerSkillInfo>(request, response), serSkillInfo); 
		model.addAttribute("page", page);
		return "modules/service/skill/serSkillInfoList";
	}

	@RequiresPermissions("service:skill:serSkillInfo:view")
	@RequestMapping(value = "form")
	public String form(SerSkillInfo serSkillInfo, Model model) {
		model.addAttribute("serSkillInfo", serSkillInfo);
		return "modules/service/skill/serSkillInfoForm";
	}

	@RequiresPermissions("service:skill:serSkillInfo:edit")
	@RequestMapping(value = "save")
	public String save(SerSkillInfo serSkillInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serSkillInfo)){
			return form(serSkillInfo, model);
		}
		serSkillInfoService.save(serSkillInfo);
		addMessage(redirectAttributes, "保存技能管理成功");
		return "redirect:"+Global.getAdminPath()+"/service/skill/serSkillInfo/?repage";
	}
	
	@RequiresPermissions("service:skill:serSkillInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(SerSkillInfo serSkillInfo, RedirectAttributes redirectAttributes) {
		serSkillInfoService.delete(serSkillInfo);
		addMessage(redirectAttributes, "删除技能管理成功");
		return "redirect:"+Global.getAdminPath()+"/service/skill/serSkillInfo/?repage";
	}

}