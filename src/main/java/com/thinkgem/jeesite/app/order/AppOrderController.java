/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.app.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
@Api(tags = "APP订单类", description = "APP订单相关接口")
public class AppOrderController extends BaseController {

	@ResponseBody
    @RequestMapping(value = "${appPath}/getOrder", method = RequestMethod.POST)
    @ApiOperation(value = "订单", notes = "订单")
    public Object getOrder(@RequestParam(required=false) String type) {
    	if (type == null || type.equals(""))
    		type = "1";
    	String result = "";
    	switch (type) {
		case "1":
			result = "{" + 
	        		"  \"code\": 1," + 
	        		"  \"data\": {" + 
	        		"    \"id\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"    \"orderNumber\": \"201711160932322121212\"," + 
	        		"    \"customId\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"    \"orderTime\": \"2017-11-29 11:25:47\"," + 
	        		"    \"serTime\": \"2017-11-28 10:41:52\"," + 
	        		"    \"totalAccount\": \"120.00\"," + 
	        		"    \"orderStatus\": \"已上门\"," + 
	        		"    \"orderSource\": \"中信国安社区\"," + 
	        		"    \"shopName\": \"呼家楼门店\"," + 
	        		"    \"shopPhone\": \"13888888888\"," + 
	        		"    \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"    \"officeId\": \"cce1ffa65994451abdb00fe56b338e4d\"," + 
	        		"    \"officeName\": \"国安社区\"," + 
	        		"    \"stationId\": \"1\"," + 
	        		"    \"stationName\": \"AAA\"," + 
	        		"    \"customInfo\": {" + 
	        		"      \"id\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"      \"customName\": \"张三三\"," + 
	        		"      \"customPhone\": \"13200909090\"," + 
	        		"      \"customAddr\": \"北京市北京市朝阳区常营中路保利嘉园3号院1号楼1004\"," + 
	        		"      \"customRemarks\": \"把门后面也收拾一下，哈哈哈哈，呵呵呵。电脑付二哥，人热敢惹哦该\"," + 
	        		"      \"customImg\": \"src/url/custom\"" + 
	        		"    }," + 
	        		"    \"payInfo\": {" + 
	        		"      \"id\": \"560067be489f40088a0e618ccf057f20\"," + 
	        		"      \"orderId\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"      \"payMode\": \"银行卡\"," + 
	        		"      \"payTime\": \"2017-11-27 16:45:57\"," + 
	        		"      \"payAccount\": \"12.00\"," + 
	        		"      \"payStatus\": \"已支付\"" + 
	        		"    }," + 
	        		"    \"orderTechs\": [" + 
	        		"      {" + 
	        		"        \"id\": \"4350d02f3f754e23b57e2a3f0c0722a8\"," + 
	        		"        \"techId\": \"0010e39a70694f4794666b9162053596\"," + 
	        		"        \"techName\": \"张三\"," + 
	        		"        \"techSex\": \"男\"," + 
	        		"        \"techPhone\": \"18701016638\"," + 
	        		"        \"imgUrl\": \"src/url/tech1\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是张三的备注，可能有很多信息\"" + 
	        		"      }," + 
	        		"      {" + 
	        		"        \"id\": \"077bc2cfc9a24ca5bf3f06f83477515c\"," + 
	        		"        \"techId\": \"134178fd5ce64bff86ee19b41e57549f\"," + 
	        		"        \"techName\": \"李四\"," + 
	        		"        \"techSex\": \"女\"," + 
	        		"        \"techPhone\": \"13900000000\"," + 
	        		"        \"imgUrl\": \"src/url/tech2\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是李四的备注，可能有很多信息\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"orderItems\": [" + 
	        		"      {" + 
	        		"        \"id\": \"47fb10e84b9646b0a7b080a4785050ac\"," + 
	        		"        \"sortId\": \"00ea9c6db7f242c49eb40b43b38ad7b7\"," + 
	        		"        \"sortName\": \"FWFL\"," + 
	        		"        \"itemId\": \"220ae4ce19a14c2589e47f4aa0ccc5aa\"," + 
	        		"        \"itemName\": \"保洁家修\"," + 
	        		"        \"commodityId\": \"4ade90a58ed147ee8497a9989636aec5\"," + 
	        		"        \"commodityName\": \"日常保洁\"," + 
	        		"        \"meterage\": \"按居室\"," + 
	        		"        \"price\": 19," + 
	        		"        \"orderNum\": 1," + 
	        		"        \"itemImg\": \"src/url/item\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"serviceInfo\": {" + 
	        		"      \"id\": \"fadbba9d79a42fbae72211d03326f136\"," + 
	        		"      \"serviceName\": \"王大市\"," + 
	        		"      \"servicePhone\": \"13888888888\"," + 
	        		"      \"serviceRemarks\": \"哈哈哈，这是一个国安侠的备注，可能有很多信息\"," + 
	        		"      \"serviceImg\": \"src/url/service\"" + 
	        		"    }," + 
	        		"    \"shopInfo\": {" + 
	        		"      \"id\": \"326f131d2fbaed79a476fadbba912203\"," + 
	        		"      \"shopName\": \"呼家楼门店\"," + 
	        		"      \"shopPhone\": \"13888888888\"," + 
	        		"      \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"      \"shopRemarks\": \"哈哈哈，这是一个门店的备注，可能有很多信息\"," + 
	        		"      \"shopImg\": \"src/url/shop\"" + 
	        		"    }" + 
	        		"  }" + 
	        		"}";
			break;
		case "2":
			result = "{" + 
	        		"  \"code\": 1," + 
	        		"  \"data\": {" + 
	        		"    \"id\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"    \"orderNumber\": \"201711160932322121212\"," + 
	        		"    \"customId\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"    \"orderTime\": \"2017-11-29 11:25:47\"," + 
	        		"    \"serTime\": \"2017-11-28 10:41:52\"," + 
	        		"    \"totalAccount\": \"120.00\"," + 
	        		"    \"orderStatus\": \"已上门\"," + 
	        		"    \"orderSource\": \"中信国安社区\"," + 
	        		"    \"shopName\": \"呼家楼门店\"," + 
	        		"    \"shopPhone\": \"13888888888\"," + 
	        		"    \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"    \"officeId\": \"cce1ffa65994451abdb00fe56b338e4d\"," + 
	        		"    \"officeName\": \"国安社区\"," + 
	        		"    \"stationId\": \"1\"," + 
	        		"    \"stationName\": \"AAA\"," + 
	        		"    \"customInfo\": {" + 
	        		"      \"id\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"      \"customName\": \"张三三\"," + 
	        		"      \"customPhone\": \"13200909090\"," + 
	        		"      \"customAddr\": \"北京市北京市朝阳区常营中路保利嘉园3号院1号楼1004\"," + 
	        		"      \"customRemarks\": \"把门后面也收拾一下，哈哈哈哈，呵呵呵。电脑付二哥，人热敢惹哦该\"," + 
	        		"      \"customImg\": \"src/url/custom\"" + 
	        		"    }," + 
	        		"    \"payInfo\": {" + 
	        		"      \"id\": \"560067be489f40088a0e618ccf057f20\"," + 
	        		"      \"orderId\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"      \"payMode\": \"银行卡\"," + 
	        		"      \"payTime\": \"2017-11-27 16:45:57\"," + 
	        		"      \"payAccount\": \"12.00\"," + 
	        		"      \"payStatus\": \"已支付\"" + 
	        		"    }," + 
	        		"    \"orderTechs\": [" + 
	        		"      {" + 
	        		"        \"id\": \"4350d02f3f754e23b57e2a3f0c0722a8\"," + 
	        		"        \"techId\": \"0010e39a70694f4794666b9162053596\"," + 
	        		"        \"techName\": \"张三\"," + 
	        		"        \"techSex\": \"男\"," + 
	        		"        \"techPhone\": \"18701016638\"," + 
	        		"        \"imgUrl\": \"src/url/tech1\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是张三的备注，可能有很多信息\"" + 
	        		"      }," + 
	        		"      {" + 
	        		"        \"id\": \"077bc2cfc9a24ca5bf3f06f83477515c\"," + 
	        		"        \"techId\": \"134178fd5ce64bff86ee19b41e57549f\"," + 
	        		"        \"techName\": \"李四\"," + 
	        		"        \"techSex\": \"女\"," + 
	        		"        \"techPhone\": \"13900000000\"," + 
	        		"        \"imgUrl\": \"src/url/tech2\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是李四的备注，可能有很多信息\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"orderItems\": [" + 
	        		"      {" + 
	        		"        \"id\": \"47fb10e84b9646b0a7b080a4785050ac\"," + 
	        		"        \"sortId\": \"00ea9c6db7f242c49eb40b43b38ad7b7\"," + 
	        		"        \"sortName\": \"FWFL\"," + 
	        		"        \"itemId\": \"220ae4ce19a14c2589e47f4aa0ccc5aa\"," + 
	        		"        \"itemName\": \"平米保洁\"," + 
	        		"        \"commodityId\": \"4ade90a58ed147ee8497a9989636aec5\"," + 
	        		"        \"commodityName\": \"平米保洁\"," + 
	        		"        \"meterage\": \"按面积\"," + 
	        		"        \"price\": 19," + 
	        		"        \"orderNum\": 1," + 
	        		"        \"itemImg\": \"src/url/item\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"serviceInfo\": {" + 
	        		"      \"id\": \"fadbba9d79a42fbae72211d03326f136\"," + 
	        		"      \"serviceName\": \"王大市\"," + 
	        		"      \"servicePhone\": \"13888888888\"," + 
	        		"      \"serviceRemarks\": \"哈哈哈，这是一个国安侠的备注，可能有很多信息\"," + 
	        		"      \"serviceImg\": \"src/url/service\"" + 
	        		"    }," + 
	        		"    \"shopInfo\": {" + 
	        		"      \"id\": \"326f131d2fbaed79a476fadbba912203\"," + 
	        		"      \"shopName\": \"呼家楼门店\"," + 
	        		"      \"shopPhone\": \"13888888888\"," + 
	        		"      \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"      \"shopRemarks\": \"哈哈哈，这是一个门店的备注，可能有很多信息\"," + 
	        		"      \"shopImg\": \"src/url/shop\"" + 
	        		"    }" + 
	        		"  }" + 
	        		"}";
			break;
		case "3":
			result = "{" + 
	        		"  \"code\": 1," + 
	        		"  \"data\": {" + 
	        		"    \"id\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"    \"orderNumber\": \"201711160932322121212\"," + 
	        		"    \"customId\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"    \"orderTime\": \"2017-11-29 11:25:47\"," + 
	        		"    \"serTime\": \"2017-11-28 10:41:52\"," + 
	        		"    \"totalAccount\": \"120.00\"," + 
	        		"    \"orderStatus\": \"已上门\"," + 
	        		"    \"orderSource\": \"中信国安社区\"," + 
	        		"    \"shopName\": \"呼家楼门店\"," + 
	        		"    \"shopPhone\": \"13888888888\"," + 
	        		"    \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"    \"officeId\": \"cce1ffa65994451abdb00fe56b338e4d\"," + 
	        		"    \"officeName\": \"国安社区\"," + 
	        		"    \"stationId\": \"1\"," + 
	        		"    \"stationName\": \"AAA\"," + 
	        		"    \"customInfo\": {" + 
	        		"      \"id\": \"1d03326f179a42fbae36fadbba9d7221\"," + 
	        		"      \"customName\": \"张三三\"," + 
	        		"      \"customPhone\": \"13200909090\"," + 
	        		"      \"customAddr\": \"北京市北京市朝阳区常营中路保利嘉园3号院1号楼1004\"," + 
	        		"      \"customRemarks\": \"把门后面也收拾一下，哈哈哈哈，呵呵呵。电脑付二哥，人热敢惹哦该\"," + 
	        		"      \"customImg\": \"src/url/custom\"" + 
	        		"    }," + 
	        		"    \"payInfo\": {" + 
	        		"      \"id\": \"560067be489f40088a0e618ccf057f20\"," + 
	        		"      \"orderId\": \"1b46f1a7c8f448818934a3e279aa30cc\"," + 
	        		"      \"payMode\": \"银行卡\"," + 
	        		"      \"payTime\": \"2017-11-27 16:45:57\"," + 
	        		"      \"payAccount\": \"12.00\"," + 
	        		"      \"payStatus\": \"已支付\"" + 
	        		"    }," + 
	        		"    \"orderTechs\": [" + 
	        		"      {" + 
	        		"        \"id\": \"4350d02f3f754e23b57e2a3f0c0722a8\"," + 
	        		"        \"techId\": \"0010e39a70694f4794666b9162053596\"," + 
	        		"        \"techName\": \"张三\"," + 
	        		"        \"techSex\": \"男\"," + 
	        		"        \"techPhone\": \"18701016638\"," + 
	        		"        \"imgUrl\": \"src/url/tech1\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是张三的备注，可能有很多信息\"" + 
	        		"      }," + 
	        		"      {" + 
	        		"        \"id\": \"077bc2cfc9a24ca5bf3f06f83477515c\"," + 
	        		"        \"techId\": \"134178fd5ce64bff86ee19b41e57549f\"," + 
	        		"        \"techName\": \"李四\"," + 
	        		"        \"techSex\": \"女\"," + 
	        		"        \"techPhone\": \"13900000000\"," + 
	        		"        \"imgUrl\": \"src/url/tech2\"," + 
	        		"        \"techRemark\": \"哈哈哈，这是李四的备注，可能有很多信息\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"orderItems\": [" + 
	        		"      {" + 
	        		"        \"id\": \"47fb10e84b9646b0a7b080a4785050ac\"," + 
	        		"        \"sortId\": \"00ea9c6db7f242c49eb40b43b38ad7b7\"," + 
	        		"        \"sortName\": \"FWFL\"," + 
	        		"        \"itemId\": \"220ae4ce19a14c2589e47f4aa0ccc5aa\"," + 
	        		"        \"itemName\": \"灯具清洁\"," + 
	        		"        \"commodityId\": \"4ade90a58ed147ee8497a9989636aec5\"," + 
	        		"        \"commodityName\": \"大型灯\"," + 
	        		"        \"meterage\": \"按数量\"," + 
	        		"        \"price\": 19," + 
	        		"        \"orderNum\": 1," + 
	        		"        \"itemImg\": \"src/url/item\"" + 
	        		"      }" + 
	        		"    ]," + 
	        		"    \"serviceInfo\": {" + 
	        		"      \"id\": \"fadbba9d79a42fbae72211d03326f136\"," + 
	        		"      \"serviceName\": \"王大市\"," + 
	        		"      \"servicePhone\": \"13888888888\"," + 
	        		"      \"serviceRemarks\": \"哈哈哈，这是一个国安侠的备注，可能有很多信息\"," + 
	        		"      \"serviceImg\": \"src/url/service\"" + 
	        		"    }," + 
	        		"    \"shopInfo\": {" + 
	        		"      \"id\": \"326f131d2fbaed79a476fadbba912203\"," + 
	        		"      \"shopName\": \"呼家楼门店\"," + 
	        		"      \"shopPhone\": \"13888888888\"," + 
	        		"      \"shopAddr\": \"北京市北京市朝阳区关东街11号呼家楼门店\"," + 
	        		"      \"shopRemarks\": \"哈哈哈，这是一个门店的备注，可能有很多信息\"," + 
	        		"      \"shopImg\": \"src/url/shop\"" + 
	        		"    }" + 
	        		"  }" + 
	        		"}";
			break;

		default:
			break;
		}
        return result;
    }

	@ResponseBody
    @RequestMapping(value = "${appPath}/techList", method = RequestMethod.POST)
    @ApiOperation(value = "技师列表", notes = "订单")
    public Object techList() {
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

	@ResponseBody
    @RequestMapping(value = "${appPath}/getItemList", method = RequestMethod.POST)
    @ApiOperation(value = "商品列表", notes = "订单")
    public Object getItemList() {
    	return "{" + 
    			"  \"code\": 1," + 
    			"  \"data\": {" + 
    			"    \"orderItems\": [" + 
    			"      {" + 
    			"        \"id\": \"47fb10e84b9646b0a7b080a4785050ac\"," + 
    			"        \"sortId\": \"00ea9c6db7f242c49eb40b43b38ad7b7\"," + 
    			"        \"sortName\": \"保洁\"," + 
    			"        \"itemId\": \"220ae4ce19a14c2589e47f4aa0ccc5aa\"," + 
    			"        \"itemName\": \"保洁\"," + 
    			"        \"commodityId\": \"4ade90a58ed147ee8497a9989636aec5\"," + 
    			"        \"commodityName\": \"日常保洁\"," + 
    			"        \"meterage\": \"按居室\"," + 
    			"        \"price\": 19," + 
    			"        \"orderNum\": 1," + 
    			"        \"itemImg\": \"src/url/item1\"" + 
    			"      }," + 
    			"      {" + 
    			"        \"id\": \"a4b946b0a7b6ac47fb7850508410e080\"," + 
    			"        \"sortId\": \"40f242c37e8ad7b7db4900ea9c6b43bb\"," + 
    			"        \"sortName\": \"家修\"," + 
    			"        \"itemId\": \"9a7f4aa0154c2589e4ae4cccc20aa2e1\"," + 
    			"        \"itemName\": \"家修\"," + 
    			"        \"commodityId\": \"7ee84974ade90a9ec5989636aa58ed14\"," + 
    			"        \"commodityName\": \"灯具清洁\"," + 
    			"        \"meterage\": \"按数量\"," + 
    			"        \"price\": 32," + 
    			"        \"orderNum\": 3," + 
    			"        \"itemImg\": \"src/url/item2\"" + 
    			"      }" + 
    			"    ]" + 
    			"  }" + 
    			"}";
    }


}
