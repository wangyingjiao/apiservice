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
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderInfoService;

/**
 * 订单信息Controller
 * @author a
 * @version 2017-11-23
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderInfo")
public class OrderInfoController extends BaseController {

	@Autowired
	private OrderInfoService orderInfoService;
	
	@ModelAttribute
	public OrderInfo get(@RequestParam(required=false) String id) {
		OrderInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderInfo orderInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderInfo> page = orderInfoService.findPage(new Page<OrderInfo>(request, response), orderInfo); 
		model.addAttribute("page", page);
		return "modules/service/order/orderInfoList";
	}

	@RequiresPermissions("service:order:orderInfo:view")
	@RequestMapping(value = "form")
	public String form(OrderInfo orderInfo, Model model) {
		model.addAttribute("orderInfo", orderInfo);
		return "modules/service/order/orderInfoForm";
	}

	@RequiresPermissions("service:order:orderInfo:edit")
	@RequestMapping(value = "save")
	public String save(OrderInfo orderInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderInfo)){
			return form(orderInfo, model);
		}
		orderInfoService.save(orderInfo);
		addMessage(redirectAttributes, "保存订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderInfo/?repage";
	}
	
	@RequiresPermissions("service:order:orderInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderInfo orderInfo, RedirectAttributes redirectAttributes) {
		orderInfoService.delete(orderInfo);
		addMessage(redirectAttributes, "删除订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderInfo/?repage";
	}

}