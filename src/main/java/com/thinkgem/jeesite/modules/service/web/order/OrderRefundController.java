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
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefund;
import com.thinkgem.jeesite.modules.service.service.order.OrderRefundService;

/**
 * 退款单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderRefund")
public class OrderRefundController extends BaseController {

	@Autowired
	private OrderRefundService orderRefundService;
	
	@ModelAttribute
	public OrderRefund get(@RequestParam(required=false) String id) {
		OrderRefund entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderRefundService.get(id);
		}
		if (entity == null){
			entity = new OrderRefund();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderRefund:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderRefund orderRefund, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderRefund> page = orderRefundService.findPage(new Page<OrderRefund>(request, response), orderRefund); 
		model.addAttribute("page", page);
		return "modules/service/order/orderRefundList";
	}

	@RequiresPermissions("service:order:orderRefund:view")
	@RequestMapping(value = "form")
	public String form(OrderRefund orderRefund, Model model) {
		model.addAttribute("orderRefund", orderRefund);
		return "modules/service/order/orderRefundForm";
	}

	@RequiresPermissions("service:order:orderRefund:edit")
	@RequestMapping(value = "save")
	public String save(OrderRefund orderRefund, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderRefund)){
			return form(orderRefund, model);
		}
		orderRefundService.save(orderRefund);
		addMessage(redirectAttributes, "保存退款单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderRefund/?repage";
	}
	
	@RequiresPermissions("service:order:orderRefund:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderRefund orderRefund, RedirectAttributes redirectAttributes) {
		orderRefundService.delete(orderRefund);
		addMessage(redirectAttributes, "删除退款单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderRefund/?repage";
	}

}