package com.thinkgem.jeesite.modules.service.web.log;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.log.ServiceLog;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.service.service.log.ServiceLogService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志Controller
 * @author a
 * @version 2018-01-26
 */

@Controller
@RequestMapping(value = "${adminPath}/service/log/serviceLog")
public class ServiceLogController extends BaseController{

    @Autowired
    private ServiceLogService serviceLogService;

    @ResponseBody
    //@RequiresPermissions("log_view")
    @RequestMapping(value = "/listData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取日志列表")
    public Result listData(@RequestBody ServiceLog serviceLog, HttpServletRequest request, HttpServletResponse response) {
        if(serviceLog == null){
            serviceLog = new ServiceLog();
        }
        Page<ServiceLog> serSortInfoPage = new Page<>(request, response);
        Page<ServiceLog> page = serviceLogService.findPage(serSortInfoPage, serviceLog);
        return new SuccResult(page);
    }
}
