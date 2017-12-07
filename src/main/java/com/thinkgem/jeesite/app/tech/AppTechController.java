/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.tech;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.web.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 通讯录Controller
 *
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@Api(tags = "APP通讯录类", description = "APP通讯录相关接口")
public class AppTechController extends BaseController {

	@ResponseBody
    @RequestMapping(value = "${appPath}/getTechList", method = RequestMethod.POST)
    @ApiOperation(value = "通讯录", notes = "通讯录")
    public Object getTechList() {
    	return "{" + 
    			"  \"code\": 1," + 
    			"  \"data\": {" + 
    			"    \"orderTechs\": [" + 
    			"      {" + 
    			"        \"id\": \"4350d02f3f754e23b57e2a3f0c0722a8\"," + 
    			"        \"techName\": \"张三\"," + 
    			"        \"techPhone\": \"18701016638\"," + 
    			"        \"imgUrl\": \"src/url/tech1\"" + 
    			"      }," + 
    			"      {" + 
    			"        \"id\": \"134178fd5ce64bff86ee19b41e57549f\"," + 
    			"        \"techName\": \"李四\"," + 
    			"        \"techPhone\": \"13900000000\"," + 
    			"        \"imgUrl\": \"src/url/tech2\"" + 
    			"      }" + 
    			"    ]" + 
    			"  }" + 
    			"}";
    }

}
