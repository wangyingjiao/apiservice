/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.sort;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.UnsupportedEncodingException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.util.List;

/**
 * 服务分类Controller
 *
 * @author a
 * @version 2017-11-15
 */
@Controller
@Api(tags = "服务分类", description = "服务分类相关接口")
@RequestMapping(value = "${adminPath}/service/sort/serSortInfo")
public class SerSortInfoController extends BaseController {

	@Autowired
	private SerSortInfoService serSortInfoService;


	@ModelAttribute
	public SerSortInfo get(@RequestParam(required = false) String id) {
		SerSortInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = serSortInfoService.get(id);
		}
		if (entity == null) {
			entity = new SerSortInfo();
		}
		return entity;
	}

	@ResponseBody
	@RequestMapping(value = "saveData", method = { RequestMethod.POST })
	@RequiresPermissions("class_insert")
	@ApiOperation(value = "新增保存服务分类")
	public Result saveData(@RequestBody SerSortInfo serSortInfo) {
		List<String> errList = errors(serSortInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		if (!StringUtils.isNotBlank(serSortInfo.getId())) {// 新增时验证重复
			if (0 != serSortInfoService.checkDataName(serSortInfo)) {
				// return new FailResult("当前机构已经包含服务分类名称" +
				// serSortInfo.getName() + "");
				return new FailResult("同一所属类型的服务分类名称不能重复");
			}
		}
		serSortInfoService.save(serSortInfo);
		return new SuccResult("保存服务分类" + serSortInfo.getName() + "成功");
	}

   


	@ResponseBody
	@RequestMapping(value = "upData", method = { RequestMethod.POST })
	@RequiresPermissions("class_update")
	@ApiOperation(value = "更新保存服务分类")
	public Result upData(@RequestBody SerSortInfo serSortInfo) {
		List<String> errList = errors(serSortInfo);
		if (errList != null && errList.size() > 0) {
			return new FailResult(errList);
		}
		// add by WYR校验重复
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(serSortInfo.getName())
				&& org.apache.commons.lang3.StringUtils.isNotEmpty(serSortInfo.getMajorSort())) {
			int i = serSortInfoService.checkRepeatByNameMajorSort(serSortInfo);
			if (0!=i) {
				return new FailResult("同一所属类型下的的分类名称不能重复");
			}
		}
		serSortInfoService.save(serSortInfo);
		return new SuccResult("更新保存服务分类" + serSortInfo.getName() + "成功");
	}

	@ResponseBody
	@RequestMapping(value = "/listData", method = { RequestMethod.POST, RequestMethod.GET })
	@RequiresPermissions("class_view")
	@ApiOperation("获取服务分类列表")
	public Result listData(@RequestBody(required = false) SerSortInfo serSortInfo, HttpServletRequest request,
			HttpServletResponse response) {
		if (serSortInfo == null) {
			serSortInfo = new SerSortInfo();
		}
		Page<SerSortInfo> serSortInfoPage = new Page<>(request, response);
		Page<SerSortInfo> page = serSortInfoService.findPage(serSortInfoPage, serSortInfo);
		return new SuccResult(page);
	}

	@ResponseBody
	@RequestMapping(value = "/listDataAll", method = { RequestMethod.POST, RequestMethod.GET })
	// @RequiresPermissions("class_view")
	@ApiOperation("获取服务分类列表")
	public Result listDataAll(@RequestBody(required = false) SerSortInfo serSortInfo, HttpServletRequest request,
			HttpServletResponse response) {
		if (serSortInfo == null) {
			serSortInfo = new SerSortInfo();
		}
		List<SerSortInfo> page = serSortInfoService.listDataAll(serSortInfo);
		return new SuccResult(page);
	}

    @ResponseBody
    @RequestMapping(value = "formData", method = {RequestMethod.POST})
    @ApiOperation("服务分类编辑")
    public Result formData(@RequestBody SerSortInfo serSortInfo) {
        SerSortInfo entity = null;
        if (StringUtils.isNotBlank(serSortInfo.getId())) {
            entity = serSortInfoService.formData(serSortInfo);
        }
        if (entity == null) {
            return new FailResult("未找到此id：" + serSortInfo.getId() + "对应的服务分类。");
        } else {
            return new SuccResult(entity);
        }
    }


	@ResponseBody
	@RequiresPermissions("class_delete")
	@RequestMapping(value = "deleteSortInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation("删除服务分类")
	public Result deleteSortInfo(@RequestBody SerSortInfo serSortInfo) {
		if (0 != serSortInfoService.checkedSortItem(serSortInfo)) {
			//return new FailResult("分类" + serSortInfo.getName() + "下有服务项目，不可删除");
			return new FailResult("无操作权限");
		}
		serSortInfoService.delete(serSortInfo);
		return new SuccResult("删除服务分类成功");
	}

}