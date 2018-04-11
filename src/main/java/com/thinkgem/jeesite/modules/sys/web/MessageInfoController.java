package com.thinkgem.jeesite.modules.sys.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.service.MessageInfoService;
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
 * 消息推送Controller
 * @author a
 * @version 2018-02-01
 */

@Controller
@RequestMapping(value = "${adminPath}/sys/pushMessage")
public class MessageInfoController extends BaseController {

    @Autowired
    private MessageInfoService messageInfoService;

    @ResponseBody
    //@RequiresPermissions("log_view")
    @RequestMapping(value = "/listFailData", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("获取发送消息失败列表")
    public Result listFailData(@RequestBody(required = false) MessageInfo messageInfo, HttpServletRequest request, HttpServletResponse response) {
        if(messageInfo == null){
            messageInfo = new MessageInfo();
        }
        Page<MessageInfo> serSortInfoPage = new Page<>(request, response);
        Page<MessageInfo> page = messageInfoService.findFailPage(serSortInfoPage, messageInfo);
        return new SuccResult(page);
    }

    @ResponseBody
    //@RequiresPermissions("log_view")
    @RequestMapping(value = "/pushFailMessage", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation("发送失败消息")
    public Result pushFailMessage(@RequestBody MessageInfo messageInfo, HttpServletRequest request, HttpServletResponse response){
        if(messageInfo == null){
            messageInfo = new MessageInfo();
        }
        int i=messageInfoService.pushFailMessage(messageInfo);
        if (i==1) {
            return new SuccResult("发送成功");
        }else {
            return new FailResult("发送失败");
        }
    }
}
