/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.item;

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
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.service.item.SerItemInfoService;

/**
 * 服务项目Controller
 * @author a
 * @version 2017-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/service/item/serItemInfo")
public class SerItemInfoController extends BaseController {

	@Autowired
	private SerItemInfoService serItemInfoService;
	
	@ModelAttribute
	public SerItemInfo get(@RequestParam(required=false) String id) {
		SerItemInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serItemInfoService.get(id);
		}
		if (entity == null){
			entity = new SerItemInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:item:serItemInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerItemInfo serItemInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerItemInfo> page = serItemInfoService.findPage(new Page<SerItemInfo>(request, response), serItemInfo); 
		model.addAttribute("page", page);
		return "modules/service/item/serItemInfoList";
	}

	@RequiresPermissions("service:item:serItemInfo:view")
	@RequestMapping(value = "form")
	public String form(SerItemInfo serItemInfo, Model model) {
		model.addAttribute("serItemInfo", serItemInfo);
		return "modules/service/item/serItemInfoForm";
	}

	@RequiresPermissions("service:item:serItemInfo:edit")
	@RequestMapping(value = "save")
	public String save(SerItemInfo serItemInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serItemInfo)){
			return form(serItemInfo, model);
		}
		serItemInfoService.save(serItemInfo);
		addMessage(redirectAttributes, "保存服务项目成功");
		return "redirect:"+Global.getAdminPath()+"/service/item/serItemInfo/?repage";
	}
	
	@RequiresPermissions("service:item:serItemInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(SerItemInfo serItemInfo, RedirectAttributes redirectAttributes) {
		serItemInfoService.delete(serItemInfo);
		addMessage(redirectAttributes, "删除服务项目成功");
		return "redirect:"+Global.getAdminPath()+"/service/item/serItemInfo/?repage";
	}

}