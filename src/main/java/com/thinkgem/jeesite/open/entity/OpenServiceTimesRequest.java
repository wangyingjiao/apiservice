/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.RegexTool;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

/**
 * 选择服务时间RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenServiceTimesRequest extends DataEntity<OpenServiceTimesRequest> {

	private static final long serialVersionUID = 1L;

	private String store_id;//门店ID
	private String eshop_code;//E店编码
	private String latitude;//地址纬度
	private String longitude;//地址经度
	private OpenServiceInfo service_info;//下单服务项目信息
	private String platform;//对接平台代号   默认值 : gasq

}