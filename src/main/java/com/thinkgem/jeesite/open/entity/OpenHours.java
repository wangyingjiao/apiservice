/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 下单服务项目信息Entity
 * @author a
 * @version 2017-12-11
 */
public class OpenHours extends DataEntity<OpenHours> {
	private static final long serialVersionUID = 1L;

	private String disenable;//该时间点是否可选,enable:可选；disable:不可选
	private String hour;//格式：09:30

	public String getDisenable() {
		return disenable;
	}

	public void setDisenable(String disenable) {
		this.disenable = disenable;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}
}