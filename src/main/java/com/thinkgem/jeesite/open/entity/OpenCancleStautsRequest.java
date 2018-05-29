/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 订单取消RequestEntity
 * @author a
 * @version 2017-5-24
 */
public class OpenCancleStautsRequest extends DataEntity<OpenCancleStautsRequest> {

	private static final long serialVersionUID = 1L;

	private String platform;//对接平台代号  默认值: gasq
	private String group_id;//com joint_group_id
	private String gasq_order_sn;//国安社区订单SN gasq  sn

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getGasq_order_sn() {
		return gasq_order_sn;
	}

	public void setGasq_order_sn(String gasq_order_sn) {
		this.gasq_order_sn = gasq_order_sn;
	}
}