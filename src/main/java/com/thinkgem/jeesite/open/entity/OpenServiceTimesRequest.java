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
	private List<OpenServiceInfo> service_info;//下单服务项目信息
	private String platform;//对接平台代号   默认值 : gasq

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getEshop_code() {
		return eshop_code;
	}

	public void setEshop_code(String eshop_code) {
		this.eshop_code = eshop_code;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public List<OpenServiceInfo> getService_info() {
		return service_info;
	}

	public void setService_info(List<OpenServiceInfo> service_info) {
		this.service_info = service_info;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}