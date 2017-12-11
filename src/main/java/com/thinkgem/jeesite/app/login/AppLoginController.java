///**
// * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
// */
//package com.thinkgem.jeesite.app.login;
//
//import com.thinkgem.jeesite.common.result.FailResult;
//import com.thinkgem.jeesite.common.result.SuccResult;
//import com.thinkgem.jeesite.common.utils.StringUtils;
//import com.thinkgem.jeesite.common.web.BaseController;
//import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
//import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
//import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * app登录Controller
// *
// * @author ThinkGem
// * @version 2013-5-31
// */
//@Controller
//@Api(tags = "APP登录类", description = "APP登录相关接口")
//public class AppLoginController extends BaseController {
//    @Autowired
//    ServiceTechnicianInfoService serviceTechnicianInfoService;
//
//    @ResponseBody
//    @RequestMapping(value = "${appPath}/appLogin", method = RequestMethod.POST)
//    @ApiOperation(value = "登入系统", notes = "用户登录")
//    public Object appLogin(@RequestBody LoginUser user) {
//        AppServiceTechnicianInfo entity = null;
//        if (StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getPassword())){
//            entity = serviceTechnicianInfoService.appLogin(user);
//        }
//        if (entity == null) {
//            return new FailResult("登陆失败");
//        } else {
//            entity.setToken("token");
//            return new SuccResult(entity);
//        }
//    }
//
//}
