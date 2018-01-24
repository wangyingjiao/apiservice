/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.login;

import com.thinkgem.jeesite.app.interceptor.Token;
import com.thinkgem.jeesite.app.interceptor.TokenManager;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.AppFailResult;
import com.thinkgem.jeesite.common.result.AppSuccResult;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.web.Servlets;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.station.ServiceStationService;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
import com.thinkgem.jeesite.modules.sys.interceptor.SameUrlData;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.DictService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 登录Controller
 *
 * @author ThinkGem
 * @version 2013-5-3
 */
@Controller
public class AppLoginController extends BaseController {
    @Autowired
    ServiceTechnicianInfoService serviceTechnicianInfoService;
    @Autowired
    ServiceStationService serviceStationService;
    @Autowired
    private DictService dictService;
    @Autowired
    private AreaService areaService;


    @Autowired
    private TokenManager tokenManager;

    @ResponseBody
    @RequestMapping(value = "${appPath}/appLogin",  method = {RequestMethod.POST})
    public Object appLogin( LoginUser user, HttpServletRequest request, HttpServletResponse response) {
        AppServiceTechnicianInfo entity = null;
        try {
            if (StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getPassword())) {
                entity = serviceTechnicianInfoService.appLogin(user);
            }
            Token token = tokenManager.createToken(entity);
            entity.setToken(token.getToken());
            response.setHeader("token", token.getToken());
            return new AppSuccResult(0, entity, "登陆成功");
        }catch (ServiceException e){
            return new AppFailResult(-1,null,e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "${appPath}/appLoginOut",  method = {RequestMethod.POST})
    public Object appLoginOut() {
        Token token = (Token) Servlets.getRequest().getAttribute("token");
        tokenManager.clearToken(token);
        String phone = token.getPhone();
        Subject subject = UserUtils.getSubject();
        System.out.println(subject);
        UserUtils.getSubject().logout();
        return new AppSuccResult(0,null,"退出成功");
    }

}
