package com.thinkgem.jeesite.modules.service.entity.technician;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 服务技师排期表
 */
public class TechScheduleInfo  extends DataEntity<TechScheduleInfo> {


    private static final long serialVersionUID = 1L;
    private String techId;		// 技师ID
    private List<String> techIdList;
    private Date scheduleDate;		// 日期
    private String weekNum;      //周几
    private int scheduleWeek;		// 日期（周一，周二。。。1,2,3,4,5,6,7）
    private Date startTime;		// 起始时段
    private Date endTime;		// 结束时段
    private String typeId;		// 休假ID或订单ID
    private String type;        //holiday：休假  order：订单
    private String remark;      //备注信息
    private String reviewStatus;      //app新增休假时使用 参数传递使用

    private String scheduleDateStr;  //日期展示用
    private String startTimeStr;    //起始时间展示用
    private String endTimeStr;      //结束时间展示用

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
    }

    public String getScheduleDateStr() {
        return scheduleDateStr;
    }

    public void setScheduleDateStr(String scheduleDateStr) {
        this.scheduleDateStr = scheduleDateStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

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

    public int getScheduleWeek() {
        return scheduleWeek;
    }

    public void setScheduleWeek(int scheduleWeek) {
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

    public List<String> getTechIdList() {
        return techIdList;
    }

    public void setTechIdList(List<String> techIdList) {
        this.techIdList = techIdList;
    }
}
