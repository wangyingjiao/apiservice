/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * E店信息RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenEshopInfoRequest extends DataEntity<OpenEshopInfoRequest> {

	private static final long serialVersionUID = 1L;

	private String operate;//create新增、update修改
	private List<OpenEshopInfo> eshopinfo;//E店信息对象
	private String code;//E店code

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public List<OpenEshopInfo> getEshopinfo() {
		return eshopinfo;
	}

	public void setEshopinfo(List<OpenEshopInfo> eshopinfo) {
		this.eshopinfo = eshopinfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}