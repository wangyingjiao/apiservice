package com.thinkgem.jeesite.modules.tech.entity;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.thinkgem.jeesite.modules.BaseEntity;

/**
 * <p>
 * 服务技师休假时间
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@TableName("tech_holiday")
public class TechHoliday extends BaseEntity<TechHoliday> {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
	@TableField("tech_id")
	private String techId;
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
    /**
     * 备注信息
     */
	private String remark;


	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "TechHoliday{" +
			", techId=" + techId +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", remark=" + remark +
			"}";
	}
}
