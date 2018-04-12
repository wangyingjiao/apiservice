package com.thinkgem.jeesite.modules.service.web.log;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;
import com.thinkgem.jeesite.modules.service.service.log.SysJointLoggerService;
import com.thinkgem.jeesite.modules.service.service.log.SysJointWaitService;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: SysJointLogController
 * @Description: TODO
 * @author WYR
 * @date 2018年1月31日 下午3:18:36
 * 
 */
@Controller
@RequestMapping(value = "${adminPath}/service/log/sysJointWait")
public class SysJointWaitController {
	@Autowired
	private SysJointWaitService sysJointWaitService;

	@ResponseBody
	@RequestMapping(value = "/listData", method = { RequestMethod.POST, RequestMethod.GET })
	public Result listData(@RequestBody(required = false) SysJointWait sysJointWait, HttpServletRequest request,
			HttpServletResponse response) {
		if (sysJointWait == null) {
			sysJointWait = new SysJointWait();
		}
		Page<SysJointWait> serSortInfoPage = new Page<>(request, response);
		Page<SysJointWait> page = sysJointWaitService.findPage(serSortInfoPage, sysJointWait);
		return new SuccResult(page);
	}

}
