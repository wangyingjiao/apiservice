/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.basic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganizationEshop;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.entity.OpenSendSaveItemResponse;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import com.thoughtworks.xstream.mapper.Mapper.Null;

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

import java.text.SimpleDateFormat;
import java.util.HashMap;
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
	@RequiresPermissions("office_view")
	@RequestMapping(value = "listData", method = {RequestMethod.POST})
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


	/**
	 * 服务机构的下拉列表（搜索栏下拉列表）
	 * @param basicOrganization
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listDataAll", method = {RequestMethod.POST})
	public Result listDataAll(@RequestBody BasicOrganization basicOrganization, HttpServletRequest request, HttpServletResponse response) {
		if(basicOrganization == null){
			basicOrganization = new BasicOrganization();
		}
		List<BasicOrganization> page = basicOrganizationService.findListAll(basicOrganization);
        HashMap<Object,Object> map = new HashMap<>();
        map.put("list",page);
		return new SuccResult(map);
	}

    @ResponseBody
    //@RequiresPermissions("office_insert")
    @RequestMapping(value = "getEShopByCode", method = RequestMethod.POST)
    @ApiOperation(value = "E店编码验重")
    public Result getEShopByCode(@RequestBody BasicGasqEshop basicGasqEshop) {
	    if (basicGasqEshop == null && basicGasqEshop.getCode().equals("")){
	        return new FailResult("请输入正确E店编码");
        }else {
            BasicGasqEshop bge = basicOrganizationService.getEShopByCode(basicGasqEshop);
            if (bge == null){
                return new FailResult("请输入正确E店编码");
            }else {
                int count = basicOrganizationService.getOrgEShopByCode(basicGasqEshop);
                if (count > 0){
                    return new FailResult("该E店已对接其他机构，请重新选择");
                }
                return new SuccResult(bge);
            }
        }
    }


	@ResponseBody
	@RequiresPermissions("office_insert")
	@RequestMapping(value = "saveData", method = RequestMethod.POST)
	@ApiOperation(value = "更新机构保存")
	public Result saveData(@RequestBody BasicOrganization basicOrganization) {
		List<String> errors = errors(basicOrganization);
		if (errors.size() > 0) {
			return new FailResult(errors);
		}

		//检查重名
		if (basicOrganizationService.getByName(basicOrganization)) {
			return new FailResult("机构名称不能重复");
		}
		//E店编码不能重复
		/*if (org.apache.commons.lang3.StringUtils.isNotEmpty(basicOrganization.getJointEshopCode())) {
			if (basicOrganizationService.getByECode(basicOrganization)) {
				return new FailResult("E店编码不能重复");
			}
		}

		try {
			// 验证服务机构E店Code是否有效
			if(StringUtils.isNotEmpty(basicOrganization.getJointEshopCode())) {
				OpenSendSaveItemResponse sendResponse = OpenSendUtil.openSendCheckEshopCode(basicOrganization.getJointEshopCode());
				if (sendResponse == null) {
					return new FailResult("对接失败-返回值为空");
				} else if (sendResponse.getCode() != 0) {
					return new FailResult("E店编码验证失败");
				}
			}
		}catch (Exception e){
			return new FailResult("对接失败-系统异常");
		}*/
		if (!basicOrganization.getDockType().equals("")) {
			if (basicOrganization.getBasicOrganizationEshops().size() == 0) {
				return new FailResult("请选择E店");
			}
		}
		basicOrganizationService.save(basicOrganization);
		return new SuccResult<String>("保存成功");
	}

	@ResponseBody
	//@RequiresPermissions("office_update")
	@RequestMapping(value = "deleteEshop", method = RequestMethod.POST)
	@ApiOperation(value = "删除E店")
	public Result deleteEshop(@RequestBody BasicOrganization basicOrganization) {
		if (basicOrganization!=null&&!basicOrganization.getEshopCode().equals("")){
			basicOrganizationService.deleteEshop(basicOrganization);
			int i = basicOrganizationService.getOrgEShopList(basicOrganization);
			if (i==0){
				basicOrganizationService.updDockType(basicOrganization);
			}
		}
		return new SuccResult("删除成功");
	}

	@ResponseBody
	@RequiresPermissions("office_update")
	@RequestMapping(value = "upData", method = RequestMethod.POST)
	@ApiOperation(value = "更新机构保存")
	public Result upData(@RequestBody BasicOrganization basicOrganization) {
		List<String> errors = errors(basicOrganization);
		if (errors.size() > 0) {
			return new FailResult(errors);
		}

		//检查重名
		if (basicOrganizationService.getByName(basicOrganization)) {
			return new FailResult("机构名称不能重复");
		}
		/*//E店编码不能重复
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(basicOrganization.getJointEshopCode())) {
			if (basicOrganizationService.getByECode(basicOrganization)) {
				return new FailResult("E店编码不能重复");
			}
		}

		try {
			// 验证服务机构E店Code是否有效
			if(StringUtils.isNotEmpty(basicOrganization.getJointEshopCode())) {
				OpenSendSaveItemResponse sendResponse = OpenSendUtil.openSendCheckEshopCode(basicOrganization.getJointEshopCode());
				if (sendResponse == null) {
					return new FailResult("对接失败-返回值为空");
				} else if (sendResponse.getCode() != 0) {
					return new FailResult("E店编码验证失败");
				}
			}
		}catch (Exception e){
			return new FailResult("对接失败-系统异常");
		}*/

		basicOrganizationService.save(basicOrganization);
		return new SuccResult<String>("保存成功");
	}

	@ResponseBody
	@RequestMapping(value = "formData", method = {RequestMethod.POST})
	@ApiOperation(value = "机构详情")
	public Result formData(@RequestBody BasicOrganization basicOrganization) {
		BasicOrganization basicOrganizationRs = basicOrganizationService.formData(basicOrganization);
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		if (null!=basicOrganizationRs.getWorkStartTime()) {
			String WorkStartTimeNew = format.format(basicOrganizationRs.getWorkStartTime());
			basicOrganizationRs.setWorkStartTimeNew(WorkStartTimeNew);
		}
		if(null!=basicOrganizationRs.getWorkEndTime()){
			String WorkEndTimeNew = format.format(basicOrganizationRs.getWorkEndTime());
			basicOrganizationRs.setWorkEndTimeNew(WorkEndTimeNew);
		}
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
	@RequestMapping(value = "getPlatform", method = {RequestMethod.GET})
	@ApiOperation("获取所有平台下拉")
	public Result getPlatform(HttpServletRequest request, HttpServletResponse response) {
		//User user = UserUtils.getUser();
		//String orgId = user.getOrganization().getId();//机构ID
		List<Dict> platformDict = basicOrganizationService.getPlatform();
		return new SuccResult(platformDict);
	}

}