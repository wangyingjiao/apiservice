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
import com.thinkgem.jeesite.modules.service.entity.order.OrderMasterInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderMasterInfoService;

/**
 * 订单主表Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderMasterInfo")
public class OrderMasterInfoController extends BaseController {

	@Autowired
	private OrderMasterInfoService orderMasterInfoService;
	
	@ModelAttribute
	public OrderMasterInfo get(@RequestParam(required=false) String id) {
		OrderMasterInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderMasterInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderMasterInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderMasterInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderMasterInfo orderMasterInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderMasterInfo> page = orderMasterInfoService.findPage(new Page<OrderMasterInfo>(request, response), orderMasterInfo); 
		model.addAttribute("page", page);
		return "modules/service/order/orderMasterInfoList";
	}

	@RequiresPermissions("service:order:orderMasterInfo:view")
	@RequestMapping(value = "form")
	public String form(OrderMasterInfo orderMasterInfo, Model model) {
		model.addAttribute("orderMasterInfo", orderMasterInfo);
		return "modules/service/order/orderMasterInfoForm";
	}

	@RequiresPermissions("service:order:orderMasterInfo:edit")
	@RequestMapping(value = "save")
	public String save(OrderMasterInfo orderMasterInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderMasterInfo)){
			return form(orderMasterInfo, model);
		}
		orderMasterInfoService.save(orderMasterInfo);
		addMessage(redirectAttributes, "保存订单主表成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderMasterInfo/?repage";
	}
	
	@RequiresPermissions("service:order:orderMasterInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderMasterInfo orderMasterInfo, RedirectAttributes redirectAttributes) {
		orderMasterInfoService.delete(orderMasterInfo);
		addMessage(redirectAttributes, "删除订单主表成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderMasterInfo/?repage";
	}

}