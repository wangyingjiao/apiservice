/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.order;

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
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderCustomInfoService;

/**
 * 客户信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderCustomInfo")
public class OrderCustomInfoController extends BaseController {

	@Autowired
	private OrderCustomInfoService orderCustomInfoService;
	
	@ModelAttribute
	public OrderCustomInfo get(@RequestParam(required=false) String id) {
		OrderCustomInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderCustomInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderCustomInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderCustomInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderCustomInfo orderCustomInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderCustomInfo> page = orderCustomInfoService.findPage(new Page<OrderCustomInfo>(request, response), orderCustomInfo); 
		model.addAttribute("page", page);
		return "modules/service/order/orderCustomInfoList";
	}

	@RequiresPermissions("service:order:orderCustomInfo:view")
	@RequestMapping(value = "form")
	public String form(OrderCustomInfo orderCustomInfo, Model model) {
		model.addAttribute("orderCustomInfo", orderCustomInfo);
		return "modules/service/order/orderCustomInfoForm";
	}

	@RequiresPermissions("service:order:orderCustomInfo:edit")
	@RequestMapping(value = "save")
	public String save(OrderCustomInfo orderCustomInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderCustomInfo)){
			return form(orderCustomInfo, model);
		}
		orderCustomInfoService.save(orderCustomInfo);
		addMessage(redirectAttributes, "保存客户信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderCustomInfo/?repage";
	}
	
	@RequiresPermissions("service:order:orderCustomInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderCustomInfo orderCustomInfo, RedirectAttributes redirectAttributes) {
		orderCustomInfoService.delete(orderCustomInfo);
		addMessage(redirectAttributes, "删除客户信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderCustomInfo/?repage";
	}

}