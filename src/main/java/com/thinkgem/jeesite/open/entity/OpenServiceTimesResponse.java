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
public class OpenServiceTimesResponse extends DataEntity<OpenServiceTimesResponse> {

	private static final long serialVersionUID = 1L;
	private String format;		// 格式：2018-02-01
	private String dayOfWeek;		// 格式：周一
	private List<OpenHours> hours;  //该日服务时间点列表

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public List<OpenHours> getHours() {
		return hours;
	}

	public void setHours(List<OpenHours> hours) {
		this.hours = hours;
	}

}