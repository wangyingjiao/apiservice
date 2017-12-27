/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.basic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.service.basic.BasicOrganizationService;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 机构Controller
 * @author a
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/organization")
public class BasicOrganizationController extends BaseController {

	@Autowired
	private BasicOrganizationService basicOrganizationService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	//@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "/listData", method = {RequestMethod.POST})
	@ApiOperation(value = "获得机构列表")
	public Result listData(@RequestBody BasicOrganization basicOrganization, HttpServletRequest request, HttpServletResponse response) {
		if(basicOrganization == null){
			basicOrganization = new BasicOrganization();
		}
		String size = request.getParameter("pageSize");
		System.out.println(size);
		Page<BasicOrganization> organizationPage = new Page<>(request, response, Integer.parseInt(size));
		Page<BasicOrganization> page = basicOrganizationService.findPage(organizationPage, basicOrganization);
		return new SuccResult(page);
	}

	@ResponseBody
	//@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "saveData", method = RequestMethod.POST)
	@ApiOperation(value = "新建，更新机构")
	public Result saveData(@RequestBody BasicOrganization basicOrganization) {
		List<String> errors = errors(basicOrganization);
		if (errors.size() > 0) {
			return new FailResult(errors);
		}

		//检查重名
		if (basicOrganizationService.getByName(basicOrganization.getName())) {
			return new FailResult("机构名称不能重复");
		}
		basicOrganizationService.save(basicOrganization);
		return new SuccResult<String>("保存成功");
	}

	@ResponseBody
	//@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation(value = "机构详情")
	public Result formData(@RequestBody BasicOrganization basicOrganization) {
		BasicOrganization basicOrganizationRs = basicOrganizationService.formData(basicOrganization);
		return new SuccResult<>(basicOrganizationRs);
	}

	@ModelAttribute("organization")
	public BasicOrganization get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return basicOrganizationService.get(id);
		} else {
			return new BasicOrganization();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getOrgCityCodes", method = {RequestMethod.GET})
	@ApiOperation("获取当前机构下所有城市")
	public Result getOrgCityCodes(HttpServletRequest request, HttpServletResponse response) {
		User user = UserUtils.getUser();
		String orgId = user.getOrganization().getId();//机构ID
		List<BasicServiceCity> cityCodes = basicOrganizationService.getOrgCityCodes(orgId);
		return new SuccResult(cityCodes);
	}

}