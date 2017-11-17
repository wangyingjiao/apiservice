/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.sort;

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
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortCity;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortCityService;

/**
 * 服务分类对应的定向城市Controller
 * @author a
 * @version 2017-11-15
 */
//@Controller
//@RequestMapping(value = "${adminPath}/service/sort/serSortCity")
public class SerSortCityController extends BaseController {

	@Autowired
	private SerSortCityService serSortCityService;
	
	@ModelAttribute
	public SerSortCity get(@RequestParam(required=false) String id) {
		SerSortCity entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serSortCityService.get(id);
		}
		if (entity == null){
			entity = new SerSortCity();
		}
		return entity;
	}
	
	@RequiresPermissions("service:sort:serSortCity:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerSortCity serSortCity, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerSortCity> page = serSortCityService.findPage(new Page<SerSortCity>(request, response), serSortCity); 
		model.addAttribute("page", page);
		return "modules/service/sort/serSortCityList";
	}

	@RequiresPermissions("service:sort:serSortCity:view")
	@RequestMapping(value = "form")
	public String form(SerSortCity serSortCity, Model model) {
		model.addAttribute("serSortCity", serSortCity);
		return "modules/service/sort/serSortCityForm";
	}

	@RequiresPermissions("service:sort:serSortCity:edit")
	@RequestMapping(value = "save")
	public String save(SerSortCity serSortCity, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serSortCity)){
			return form(serSortCity, model);
		}
		serSortCityService.save(serSortCity);
		addMessage(redirectAttributes, "保存服务分类对应的定向城市成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortCity/?repage";
	}
	
	@RequiresPermissions("service:sort:serSortCity:edit")
	@RequestMapping(value = "delete")
	public String delete(SerSortCity serSortCity, RedirectAttributes redirectAttributes) {
		serSortCityService.delete(serSortCity);
		addMessage(redirectAttributes, "删除服务分类对应的定向城市成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortCity/?repage";
	}

}