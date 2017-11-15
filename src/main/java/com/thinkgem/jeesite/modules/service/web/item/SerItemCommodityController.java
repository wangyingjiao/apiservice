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
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.service.item.SerItemCommodityService;

/**
 * 服务项目商品信息Controller
 * @author a
 * @version 2017-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/service/item/serItemCommodity")
public class SerItemCommodityController extends BaseController {

	@Autowired
	private SerItemCommodityService serItemCommodityService;
	
	@ModelAttribute
	public SerItemCommodity get(@RequestParam(required=false) String id) {
		SerItemCommodity entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serItemCommodityService.get(id);
		}
		if (entity == null){
			entity = new SerItemCommodity();
		}
		return entity;
	}
	
	@RequiresPermissions("service:item:serItemCommodity:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerItemCommodity serItemCommodity, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerItemCommodity> page = serItemCommodityService.findPage(new Page<SerItemCommodity>(request, response), serItemCommodity); 
		model.addAttribute("page", page);
		return "modules/service/item/serItemCommodityList";
	}

	@RequiresPermissions("service:item:serItemCommodity:view")
	@RequestMapping(value = "form")
	public String form(SerItemCommodity serItemCommodity, Model model) {
		model.addAttribute("serItemCommodity", serItemCommodity);
		return "modules/service/item/serItemCommodityForm";
	}

	@RequiresPermissions("service:item:serItemCommodity:edit")
	@RequestMapping(value = "save")
	public String save(SerItemCommodity serItemCommodity, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serItemCommodity)){
			return form(serItemCommodity, model);
		}
		serItemCommodityService.save(serItemCommodity);
		addMessage(redirectAttributes, "保存服务项目商品信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/item/serItemCommodity/?repage";
	}
	
	@RequiresPermissions("service:item:serItemCommodity:edit")
	@RequestMapping(value = "delete")
	public String delete(SerItemCommodity serItemCommodity, RedirectAttributes redirectAttributes) {
		serItemCommodityService.delete(serItemCommodity);
		addMessage(redirectAttributes, "删除服务项目商品信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/item/serItemCommodity/?repage";
	}

}