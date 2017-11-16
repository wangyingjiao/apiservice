/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.web.sort;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.service.sort.SerSortInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @RequestMapping(value = "saveData", method = {RequestMethod.POST})
    //@RequiresPermissions("service:station:serSortInfo:edit")
    @ApiOperation("保存服务分类")
    public Result saveData(@RequestBody SerSortInfo serSortInfo) {
        if (!beanValidator(serSortInfo)) {
            return new FailResult("保存服务分类" + serSortInfo.getName() + "失败");
        }
        serSortInfoService.save(serSortInfo);
        return new SuccResult("保存服务分类" + serSortInfo.getName() + "成功");
    }

    @ResponseBody
    @RequestMapping(value = "listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取服务分类列表")
    public Result listData(@RequestBody SerSortInfo serSortInfo, HttpServletRequest request, HttpServletResponse response) {
        Page<SerSortInfo> stationPage = new Page<>(request, response);
        Page<SerSortInfo> page = serSortInfoService.findPage(stationPage, serSortInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    @RequestMapping(value = "getData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("根据ID查找服务分类")
    public Result getData(@RequestBody SerSortInfo serSortInfo) {
        SerSortInfo entity = null;
        if (StringUtils.isNotBlank(serSortInfo.getId())) {
            entity = serSortInfoService.getData(serSortInfo.getId());
        }
        if (entity == null) {
            return new FailResult("未找到此id：" + serSortInfo.getId() + "对应的服务分类。");
        } else {
            return new SuccResult(entity);
        }
    }

    @ResponseBody
    //@RequiresPermissions("service:station:serSortInfo:edit")
    @RequestMapping(value = "deleteSortInfo")
    @ApiOperation("删除服务分类")
    public Result deleteSortInfo(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkedSortItem(serSortInfo)) {
            return new FailResult("分类" + serSortInfo.getName() + "下有服务项目，则不可删除");
        }
        serSortInfoService.delete(serSortInfo);
        return new SuccResult("删除服务分类成功");
    }

    @ResponseBody
    @RequestMapping(value = "checkDataName", method = {RequestMethod.POST})
    @ApiOperation("验证服务分类名称是否重复")
    public Result checkDataName(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkDataName(serSortInfo)) {
            return new FailResult("当前机构已经包含服务分类名称" + serSortInfo.getName() + "");
        }
        return new SuccResult("服务分类名称" + serSortInfo.getName() + "可用");
    }

    @ResponseBody
    @RequestMapping(value = "checkCityItem", method = {RequestMethod.POST})
    @ApiOperation("验证定向城市是否设置服务项目")
    public Result checkCityItem(@RequestBody SerSortInfo serSortInfo) {
        if (0 != serSortInfoService.checkCityItem(serSortInfo)) {
            return new FailResult("当前机构已经包含服务分类名称" + serSortInfo.getName() + "");
        }
        return new SuccResult("服务分类名称" + serSortInfo.getName() + "可用");
    }


    /*
	@RequiresPermissions("service:sort:serSortInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(SerSortInfo serSortInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SerSortInfo> page = serSortInfoService.findPage(new Page<SerSortInfo>(request, response), serSortInfo);
		model.addAttribute("page", page);
		return "modules/service/sort/serSortInfoList";
	}
*/

/*	@RequiresPermissions("service:sort:serSortInfo:view")
	@RequestMapping(value = "form")
	public String form(SerSortInfo serSortInfo, Model model) {
		model.addAttribute("serSortInfo", serSortInfo);
		return "modules/service/sort/serSortInfoForm";
	}*/

/*	@RequiresPermissions("service:sort:serSortInfo:edit")
	@RequestMapping(value = "save")
	public String save(SerSortInfo serSortInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, serSortInfo)){
			return form(serSortInfo, model);
		}
		serSortInfoService.save(serSortInfo);
		addMessage(redirectAttributes, "保存服务分类成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortInfo/?repage";
	}*/
	
/*	@RequiresPermissions("service:sort:serSortInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(SerSortInfo serSortInfo, RedirectAttributes redirectAttributes) {
		serSortInfoService.delete(serSortInfo);
		addMessage(redirectAttributes, "删除服务分类成功");
		return "redirect:"+Global.getAdminPath()+"/service/sort/serSortInfo/?repage";
	}*/

}