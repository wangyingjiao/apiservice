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
import com.thinkgem.jeesite.modules.service.entity.order.OrderAddress;
import com.thinkgem.jeesite.modules.service.service.order.OrderAddressService;

/**
 * 订单地址Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderAddress")
public class OrderAddressController extends BaseController {

	@Autowired
	private OrderAddressService orderAddressService;
	
	@ModelAttribute
	public OrderAddress get(@RequestParam(required=false) String id) {
		OrderAddress entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderAddressService.get(id);
		}
		if (entity == null){
			entity = new OrderAddress();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderAddress:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderAddress orderAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderAddress> page = orderAddressService.findPage(new Page<OrderAddress>(request, response), orderAddress); 
		model.addAttribute("page", page);
		return "modules/service/order/orderAddressList";
	}

	@RequiresPermissions("service:order:orderAddress:view")
	@RequestMapping(value = "form")
	public String form(OrderAddress orderAddress, Model model) {
		model.addAttribute("orderAddress", orderAddress);
		return "modules/service/order/orderAddressForm";
	}

	@RequiresPermissions("service:order:orderAddress:edit")
	@RequestMapping(value = "save")
	public String save(OrderAddress orderAddress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderAddress)){
			return form(orderAddress, model);
		}
		orderAddressService.save(orderAddress);
		addMessage(redirectAttributes, "保存订单地址成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderAddress/?repage";
	}
	
	@RequiresPermissions("service:order:orderAddress:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderAddress orderAddress, RedirectAttributes redirectAttributes) {
		orderAddressService.delete(orderAddress);
		addMessage(redirectAttributes, "删除订单地址成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderAddress/?repage";
	}

}