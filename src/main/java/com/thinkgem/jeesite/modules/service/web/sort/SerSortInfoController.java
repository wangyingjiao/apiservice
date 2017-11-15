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
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortInfoService;

/**
 * 服务分类Controller
 * @author a
 * @version 2017-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/service/sort/serSortInfo")
public class SerSortInfoController extends BaseController {

	@Autowired
	private SerSortInfoService serSortInfoService;
	
	@ModelAttribute
	public SerSortInfo get(@RequestParam(required=false) String id) {
		SerSortInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = serSortInfoService.get(id);
		}
		if (entity == null){
			entity = new SerSortInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:sort:serSortInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerSortInfo serSortInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerSortInfo> page = serSortInfoService.findPage(new Page<SerSortInfo>(request, response), serSortInfo); 
		model.addAttribute("page", page);
		return "modules/service/sort/serSortInfoList";
	}

	@RequiresPermissions("service:sort:serSortInfo:view")
	@RequestMapping(value = "form")
	public String form(SerSortInfo serSortInfo, Model model) {
		model.addAttribute("serSortInfo", serSortInfo);
		return "modules/service/sort/serSortInfoForm";
	}

	@RequiresPermissions("service:sort:serSortInfo:edit")
	@RequestMapping(value = "save")
	public String save(SerSortInfo serSortInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serSortInfo)){
			return form(serSortInfo, model);
		}
		serSortInfoService.save(serSortInfo);
		addMessage(redirectAttributes, "保存服务分类成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortInfo/?repage";
	}
	
	@RequiresPermissions("service:sort:serSortInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(SerSortInfo serSortInfo, RedirectAttributes redirectAttributes) {
		serSortInfoService.delete(serSortInfo);
		addMessage(redirectAttributes, "删除服务分类成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortInfo/?repage";
	}

}