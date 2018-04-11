/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.web;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.OpenFailResult;
import com.thinkgem.jeesite.common.result.OpenResult;
import com.thinkgem.jeesite.common.result.OpenSuccResult;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.open.entity.OpenEshopInfoRequest;
import com.thinkgem.jeesite.open.entity.OpenEshopInfoResponse;
import com.thinkgem.jeesite.open.service.OpenEshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对接Controller
 * @author a
 * @version 2018-1-11
 */
@Controller
@RequestMapping(value = "${openPath_gasq}/eshop")
public class OpenEshopController extends BaseController {

	@Autowired
	private OpenEshopService openEshopService;

	/**
	 * E店信息新增、修改
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "info", method = {RequestMethod.POST})
	public OpenResult info(OpenEshopInfoRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			openEshopService.save(info);
			OpenEshopInfoResponse responseRe = new OpenEshopInfoResponse();
			responseRe.setSuccess(true);
			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}

	/**
	 * E店删除
	 * @param info
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delete", method = {RequestMethod.POST})
	public OpenResult delete(OpenEshopInfoRequest info, HttpServletRequest request, HttpServletResponse response) {
		try {
			openEshopService.delete(info);
			OpenEshopInfoResponse responseRe = new OpenEshopInfoResponse();
			responseRe.setSuccess(true);
			OpenSuccResult result =  new OpenSuccResult(responseRe, "操作成功");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","1");
			return result;
		}catch (Exception e){
			OpenFailResult result =  new OpenFailResult("操作失败");
			String openResponseJson = JsonMapper.toJsonString(result);
			request.setAttribute("openResponseJson",openResponseJson);
			request.setAttribute("openResponseCode","0");
			return result;
		}
	}
}