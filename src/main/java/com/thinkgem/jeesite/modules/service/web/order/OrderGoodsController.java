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
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.service.order.OrderGoodsService;

/**
 * 订单服务关联Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderGoods")
public class OrderGoodsController extends BaseController {

	@Autowired
	private OrderGoodsService orderGoodsService;
	
	@ModelAttribute
	public OrderGoods get(@RequestParam(required=false) String id) {
		OrderGoods entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderGoodsService.get(id);
		}
		if (entity == null){
			entity = new OrderGoods();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderGoods:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderGoods orderGoods, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderGoods> page = orderGoodsService.findPage(new Page<OrderGoods>(request, response), orderGoods); 
		model.addAttribute("page", page);
		return "modules/service/order/orderGoodsList";
	}

	@RequiresPermissions("service:order:orderGoods:view")
	@RequestMapping(value = "form")
	public String form(OrderGoods orderGoods, Model model) {
		model.addAttribute("orderGoods", orderGoods);
		return "modules/service/order/orderGoodsForm";
	}

	@RequiresPermissions("service:order:orderGoods:edit")
	@RequestMapping(value = "save")
	public String save(OrderGoods orderGoods, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderGoods)){
			return form(orderGoods, model);
		}
		orderGoodsService.save(orderGoods);
		addMessage(redirectAttributes, "保存订单服务关联成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderGoods/?repage";
	}
	
	@RequiresPermissions("service:order:orderGoods:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderGoods orderGoods, RedirectAttributes redirectAttributes) {
		orderGoodsService.delete(orderGoods);
		addMessage(redirectAttributes, "删除订单服务关联成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderGoods/?repage";
	}

}