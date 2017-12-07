/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.tech;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.service.service.technician.ServiceTechnicianInfoService;
import com.thinkgem.jeesite.modules.sys.entity.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 通讯录Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP通讯录类", description = "APP通讯录相关接口")
public class AppTechController extends BaseController {
    @Autowired
    ServiceTechnicianInfoService serviceTechnicianInfoService;

    @ResponseBody
    @RequestMapping(value = "${appPath}/getTechList", method = RequestMethod.POST)
    @ApiOperation(value = "通讯录", notes = "通讯录")
    public Object getTechList(@RequestBody LoginUser user) {
        List<AppServiceTechnicianInfo> entity = null;
        if (StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getPassword())){
           // entity = serviceTechnicianInfoService.getTechList(user);
        }
        if (entity == null) {
            return new FailResult("失败");
        } else {
            return new SuccResult(entity);
        }
    }

}
