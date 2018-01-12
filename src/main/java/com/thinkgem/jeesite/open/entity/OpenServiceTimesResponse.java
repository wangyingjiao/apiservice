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
	private String format_date;		// 格式：2018-02-01
	private String weekday;		// 格式：周一
	private OpenHours hours;  //该日服务时间点列表

}