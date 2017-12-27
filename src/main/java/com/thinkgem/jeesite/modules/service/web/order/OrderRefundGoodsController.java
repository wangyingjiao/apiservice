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
import com.thinkgem.jeesite.modules.service.entity.order.OrderRefundGoods;
import com.thinkgem.jeesite.modules.service.service.order.OrderRefundGoodsService;

/**
 * 退货单Controller
 * @author a
 * @version 2017-12-26
 */
@Controller
@RequestMapping(value = "${adminPath}/service/order/orderRefundGoods")
public class OrderRefundGoodsController extends BaseController {

	@Autowired
	private OrderRefundGoodsService orderRefundGoodsService;
	
	@ModelAttribute
	public OrderRefundGoods get(@RequestParam(required=false) String id) {
		OrderRefundGoods entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = orderRefundGoodsService.get(id);
		}
		if (entity == null){
			entity = new OrderRefundGoods();
		}
		return entity;
	}
	
	@RequiresPermissions("service:order:orderRefundGoods:view")
	@RequestMapping(value = {"list", ""})
	public String list(OrderRefundGoods orderRefundGoods, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OrderRefundGoods> page = orderRefundGoodsService.findPage(new Page<OrderRefundGoods>(request, response), orderRefundGoods); 
		model.addAttribute("page", page);
		return "modules/service/order/orderRefundGoodsList";
	}

	@RequiresPermissions("service:order:orderRefundGoods:view")
	@RequestMapping(value = "form")
	public String form(OrderRefundGoods orderRefundGoods, Model model) {
		model.addAttribute("orderRefundGoods", orderRefundGoods);
		return "modules/service/order/orderRefundGoodsForm";
	}

	@RequiresPermissions("service:order:orderRefundGoods:edit")
	@RequestMapping(value = "save")
	public String save(OrderRefundGoods orderRefundGoods, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, orderRefundGoods)){
			return form(orderRefundGoods, model);
		}
		orderRefundGoodsService.save(orderRefundGoods);
		addMessage(redirectAttributes, "保存退货单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderRefundGoods/?repage";
	}
	
	@RequiresPermissions("service:order:orderRefundGoods:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderRefundGoods orderRefundGoods, RedirectAttributes redirectAttributes) {
		orderRefundGoodsService.delete(orderRefundGoods);
		addMessage(redirectAttributes, "删除退货单成功");
		return "redirect:"+Global.getAdminPath()+"/service/order/orderRefundGoods/?repage";
	}

}