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
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.service.order.OrderDispatchService;

/**
 * 派单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderDispatch")
public class OrderDispatchController extends BaseController {

	@Autowired
	private OrderDispatchService orderDispatchService;
	
	@ModelAttribute
	public OrderDispatch get(@RequestParam(required=false) String id) {
		OrderDispatch entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderDispatchService.get(id);
		}
		if (entity == null){
			entity = new OrderDispatch();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderDispatch:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderDispatch orderDispatch, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderDispatch> page = orderDispatchService.findPage(new Page<OrderDispatch>(request, response), orderDispatch); 
		model.addAttribute("page", page);
		return "modules/service/order/orderDispatchList";
	}

	@RequiresPermissions("service:order:orderDispatch:view")
	@RequestMapping(value = "form")
	public String form(OrderDispatch orderDispatch, Model model) {
		model.addAttribute("orderDispatch", orderDispatch);
		return "modules/service/order/orderDispatchForm";
	}

	@RequiresPermissions("service:order:orderDispatch:edit")
	@RequestMapping(value = "save")
	public String save(OrderDispatch orderDispatch, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderDispatch)){
			return form(orderDispatch, model);
		}
		orderDispatchService.save(orderDispatch);
		addMessage(redirectAttributes, "保存派单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderDispatch/?repage";
	}
	
	@RequiresPermissions("service:order:orderDispatch:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderDispatch orderDispatch, RedirectAttributes redirectAttributes) {
		orderDispatchService.delete(orderDispatch);
		addMessage(redirectAttributes, "删除派单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderDispatch/?repage";
	}

}