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
 * 选择服务时间ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenCreateResponse extends DataEntity<OpenCreateResponse> {

	private static final long serialVersionUID = 1L;
	private boolean success;		//接单状态：true 已接单；false 拒单
	private String service_order_id;		//接单时：返回自营服务的订单ID；拒单时：返回空

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getService_order_id() {
		return service_order_id;
	}

	public void setService_order_id(String service_order_id) {
		this.service_order_id = service_order_id;
	}
}