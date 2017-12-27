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
import com.thinkgem.jeesite.modules.service.entity.order.OrderPayInfo;
import com.thinkgem.jeesite.modules.service.service.order.OrderPayInfoService;

/**
 * 支付信息Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderPayInfo")
public class OrderPayInfoController extends BaseController {

	@Autowired
	private OrderPayInfoService orderPayInfoService;
	
	@ModelAttribute
	public OrderPayInfo get(@RequestParam(required=false) String id) {
		OrderPayInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderPayInfoService.get(id);
		}
		if (entity == null){
			entity = new OrderPayInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderPayInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderPayInfo orderPayInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderPayInfo> page = orderPayInfoService.findPage(new Page<OrderPayInfo>(request, response), orderPayInfo); 
		model.addAttribute("page", page);
		return "modules/service/order/orderPayInfoList";
	}

	@RequiresPermissions("service:order:orderPayInfo:view")
	@RequestMapping(value = "form")
	public String form(OrderPayInfo orderPayInfo, Model model) {
		model.addAttribute("orderPayInfo", orderPayInfo);
		return "modules/service/order/orderPayInfoForm";
	}

	@RequiresPermissions("service:order:orderPayInfo:edit")
	@RequestMapping(value = "save")
	public String save(OrderPayInfo orderPayInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderPayInfo)){
			return form(orderPayInfo, model);
		}
		orderPayInfoService.save(orderPayInfo);
		addMessage(redirectAttributes, "保存支付信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderPayInfo/?repage";
	}
	
	@RequiresPermissions("service:order:orderPayInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderPayInfo orderPayInfo, RedirectAttributes redirectAttributes) {
		orderPayInfoService.delete(orderPayInfo);
		addMessage(redirectAttributes, "删除支付信息成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderPayInfo/?repage";
	}

}