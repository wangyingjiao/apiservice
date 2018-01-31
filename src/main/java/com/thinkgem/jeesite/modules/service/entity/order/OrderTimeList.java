/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 时间Entity
 * @author a
 * @version 2017-12-26
 */
public class OrderTimeList {

	private String value ;//id
	private String label;//年月日
	private List<OrderDispatch> serviceTime;//时间

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<OrderDispatch> getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(List<OrderDispatch> serviceTime) {
		this.serviceTime = serviceTime;
	}
}