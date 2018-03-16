/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * E店信息ResponseEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenEshopInfoResponse extends DataEntity<OpenEshopInfoResponse> {

	private static final long serialVersionUID = 1L;
	private boolean success;		// 状态：true 成功；false 失败

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}