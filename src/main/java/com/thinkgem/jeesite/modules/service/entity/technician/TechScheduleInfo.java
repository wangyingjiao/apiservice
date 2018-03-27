package com.thinkgem.jeesite.modules.service.entity.technician;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

public class TechScheduleInfo  extends DataEntity<TechScheduleInfo> {


    private static final long serialVersionUID = 1L;
    private String techId;		// 技师ID
    private Date scheduleDate;		// 日期
    private String scheduleWeek;		// 日期（周一，周二。。。1,2,3,4,5,6,7）
    private Date startTime;		// 起始时段
    private Date endTime;		// 结束时段
    private String typeId;		// 休假ID或订单ID
    private String type;        //holiday：休假  order：订单
    private String remark;      //备注信息

    public TechScheduleInfo() {
        super();
    }

    public TechScheduleInfo(String id) {
        super(id);
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleWeek() {
        return scheduleWeek;
    }

    public void setScheduleWeek(String scheduleWeek) {
        this.scheduleWeek = scheduleWeek;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
