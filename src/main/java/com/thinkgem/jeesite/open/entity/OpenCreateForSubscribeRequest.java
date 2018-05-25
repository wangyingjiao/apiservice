/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.open.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 订单预约RequestEntity
 * @author a
 * @version 2017-12-11
 */
public class OpenCreateForSubscribeRequest extends DataEntity<OpenCreateForSubscribeRequest> {

	private static final long serialVersionUID = 1L;

	private String platform;// 对接平台代号 默认值 gasq
	private List<String>  remark_pic;//订单备注(用户备注)
	private String remark;//订单备注(用户备注)
	private String service_time;//服务时间
	private List<String> gasq_order_sn;//国安社区订单SN
	private String group_id;//订单组ID

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public List<String> getRemark_pic() {
		return remark_pic;
	}

	public void setRemark_pic(List<String> remark_pic) {
		this.remark_pic = remark_pic;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getService_time() {
		return service_time;
	}

	public void setService_time(String service_time) {
		this.service_time = service_time;
	}

	public List<String> getGasq_order_sn() {
		return gasq_order_sn;
	}

	public void setGasq_order_sn(List<String> gasq_order_sn) {
		this.gasq_order_sn = gasq_order_sn;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}