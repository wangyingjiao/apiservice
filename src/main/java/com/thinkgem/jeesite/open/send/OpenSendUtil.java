/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.send;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.Base64Encoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.open.entity.*;
import com.thinkgem.jeesite.open.service.OpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 调用国安社区接口
 * @author a
 * @version 2018-1-11
 */

public class OpenSendUtil {

	/**
	 *  国安社区开放接口 - 服务项目保存
	 * @param info
	 * @return
	 */
	public OpenSendSaveItemResponse openSendSaveItem(OpenSendSaveItemRequest info) {
		OpenSendSaveItemRequest info1 = new OpenSendSaveItemRequest();
		info1.setType("service");//类型：商品 product / 服务 service
		info1.setIs_combo("no");//单一商品 no / 组合商品 yes
		info1.setTags_system("灯具,厨卫五金,开关/插座,");//系统标签格式：系统标签1,系统标签2,系统标签3,
		info1.setTags_custom("保洁,灯具,大型灯");// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
		info1.setName("灯具保洁（大型灯）");// 商品名称格式：项目名称（商品名）
		info1.setPrice("100");// 商品价格
		info1.setUnit("个");// 商品单位格式：次/个/间
		info1.setStock_quantity(999999);// 999999 每日库存不限，写默认值
		info1.setMin_number(1);// 最小购买数量，起购数量
		info1.setMax_number(999999);// 最大购买数量，默认999999
		List<String> list = new ArrayList<>();
		list.add("openservice/2017/12/25/微信图片_20171113093258.jpg");
		info1.setBanner_pic(JsonMapper.toJsonString(list));// 最少一张，最多四张 格式：["openservice/2017/12/25/微信图片_20171113093258.jpg","openservice/2017/12/25/头像.jpg"]
		info1.setDescribe_pic(null);// 可以为空；格式：["openservice/2017/12/25/微信图片_20171113093258.jpg","openservice/2017/12/25/头像.jpg"]
		info1.setJoint_code("zyfw");// 对接CODE；自定义编码
		info1.setEshop_code("");// ESHOP_CODE

		String json = JsonMapper.toJsonString(info);
		String encode = Base64Encoder.encode(json);
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		return null;

	}

	/**
	 *  国安社区开放接口 - 更新订单信息
	 *  更新服务时间、改派服务人员、服务人员端备注
	 * @param info
	 * @return
	 */
	public OpenSendSaveOrderResponse openSendSaveOrder(OpenSendSaveOrderRequest info) {
		List<OpenSendSaveOrderServiceTechInfo> techList = new ArrayList<>();
		OpenSendSaveOrderServiceTechInfo techInfo1 = new OpenSendSaveOrderServiceTechInfo();
		techInfo1.setTech_name("李阿姨");
		techInfo1.setTech_phone("13500010002");
		techList.add(techInfo1);

		OpenSendSaveOrderServiceTechInfo techInfo2 = new OpenSendSaveOrderServiceTechInfo();
		techInfo2.setTech_name("张阿姨");
		techInfo2.setTech_phone("13500030004");
		techList.add(techInfo2);

		OpenSendSaveOrderRequest info1 = new OpenSendSaveOrderRequest();
		info1.setOrder_id("1");// 订单ID
		info1.setOrder_remark("服务人员备注");//服务人员备注
		info1.setService_tech_info(techList);// 技师信息

		OpenSendSaveOrderRequest info2 = new OpenSendSaveOrderRequest();
		info2.setOrder_id("1");// 订单ID
		info2.setService_time("2018-01-17 08:00:00");// 上门服务时间
		info2.setService_tech_info(techList);// 技师信息


		String json = JsonMapper.toJsonString(info1);
		String encode = Base64Encoder.encode(json);
		String md5Content = MD5Util.getStringMD5(encode+ Global.getConfig("openEncryptPassword_gasq"));

		return null;
	}



}