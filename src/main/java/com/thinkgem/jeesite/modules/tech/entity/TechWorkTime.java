package com.thinkgem.jeesite.modules.tech.entity;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务技师工作时间
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("tech_work_time")
public class TechWorkTime extends BaseEntity<TechWorkTime> {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
	@TableField("tech_id")
	private String techId;
    /**
     * 工作日期（周一，周二。。。）
     */
	private Integer week;
    /**
     * 起始时段
     */
	@TableField("start_time")
	private Date startTime;
    /**
     * 结束时段
     */
	@TableField("end_time")
	private Date endTime;


	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "TechWorkTime{" +
			", techId=" + techId +
			", week=" + week +
			", startTime=" + startTime +
			", endTime=" + endTime +
			"}";
	}
}
