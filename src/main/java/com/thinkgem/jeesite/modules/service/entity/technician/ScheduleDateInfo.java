package com.thinkgem.jeesite.modules.service.entity.technician;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 排期未来7天实体
 */
public class ScheduleDateInfo extends DataEntity<ScheduleDateInfo> {
    private static final long serialVersionUID = 1L;
    private String sevenDate;
    private String workBeginTime;     //工作时间
    private String workEndTime;
    private List<TechScheduleInfo> techScheduleInfos;   //排期集合

    public String getSevenDate() {
        return sevenDate;
    }

    public void setSevenDate(String sevenDate) {
        this.sevenDate = sevenDate;
    }

    public String getWorkBeginTime() {
        return workBeginTime;
    }

    public void setWorkBeginTime(String workBeginTime) {
        this.workBeginTime = workBeginTime;
    }

    public String getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }

    public List<TechScheduleInfo> getTechScheduleInfos() {
        return techScheduleInfos;
    }

    public void setTechScheduleInfos(List<TechScheduleInfo> techScheduleInfos) {
        this.techScheduleInfos = techScheduleInfos;
    }
}
