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
 * 更新订单信息ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenUpdateInfoResponse extends DataEntity<OpenUpdateInfoResponse> {

	private static final long serialVersionUID = 1L;
	private boolean success;		// 状态：true 成功；false 失败
	private String service_order_id;		// 自营服务订单ID

	public boolean getSuccess() {
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

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}