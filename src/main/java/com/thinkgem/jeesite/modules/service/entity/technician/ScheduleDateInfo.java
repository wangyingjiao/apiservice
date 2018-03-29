package com.thinkgem.jeesite.modules.service.entity.technician;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 排期未来7天实体
 */
public class ScheduleDateInfo extends DataEntity<ScheduleDateInfo> {
    private static final long serialVersionUID = 1L;
    private String workTime;     //工作时间
    private List<TechScheduleInfo> techScheduleInfos;   //排期集合

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public List<TechScheduleInfo> getTechScheduleInfos() {
        return techScheduleInfos;
    }

    public void setTechScheduleInfos(List<TechScheduleInfo> techScheduleInfos) {
        this.techScheduleInfos = techScheduleInfos;
    }
}
