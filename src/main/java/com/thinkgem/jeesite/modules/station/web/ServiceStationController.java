/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.station.web;

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
import com.thinkgem.jeesite.modules.station.entity.TServiceStation;
import com.thinkgem.jeesite.modules.station.service.TServiceStationService;

/**
 * 服务站Controller
 * @author x
 * @version 2017-10-27
 */
@Controller
@RequestMapping(value = "${adminPath}/station/ServiceStation")
public class ServiceStationController extends BaseController {

	@Autowired
	private TServiceStationService tServiceStationService;
	
	@ModelAttribute
	public TServiceStation get(@RequestParam(required=false) String id) {
		TServiceStation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tServiceStationService.get(id);
		}
		if (entity == null){
			entity = new TServiceStation();
		}
		return entity;
	}
	
	@RequiresPermissions("station:tServiceStation:view")
	@RequestMapping(value = {"list", ""})
	public String list(TServiceStation tServiceStation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TServiceStation> page = tServiceStationService.findPage(new Page<TServiceStation>(request, response), tServiceStation); 
		model.addAttribute("page", page);
		return "modules/station/tServiceStationList";
	}

	@RequiresPermissions("station:tServiceStation:view")
	@RequestMapping(value = "form")
	public String form(TServiceStation tServiceStation, Model model) {
		model.addAttribute("tServiceStation", tServiceStation);
		return "modules/station/tServiceStationForm";
	}

	@RequiresPermissions("station:tServiceStation:edit")
	@RequestMapping(value = "save")
	public String save(TServiceStation tServiceStation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tServiceStation)){
			return form(tServiceStation, model);
		}
		tServiceStationService.save(tServiceStation);
		addMessage(redirectAttributes, "保存服务站成功");
		return "redirect:"+Global.getAdminPath()+"/station/tServiceStation/?repage";
	}
	
	@RequiresPermissions("station:tServiceStation:edit")
	@RequestMapping(value = "delete")
	public String delete(TServiceStation tServiceStation, RedirectAttributes redirectAttributes) {
		tServiceStationService.delete(tServiceStation);
		addMessage(redirectAttributes, "删除服务站成功");
		return "redirect:"+Global.getAdminPath()+"/station/tServiceStation/?repage";
	}

}