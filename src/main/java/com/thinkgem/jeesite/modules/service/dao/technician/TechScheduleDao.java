/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.technician;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.technician.TechScheduleInfo;

import java.util.List;

/**
 * 服务技师排期表DAO接口
 * @author a
 * @version 2018-3-27
 */
@MyBatisDao
public interface TechScheduleDao extends CrudDao<TechScheduleInfo> {

    //根据typeId 技师id查询排期表
    List<TechScheduleInfo> getOrderSchedule(TechScheduleInfo info);

    //根据技师id 开始时间 结束时间 获取排期表集合
    List<TechScheduleInfo> getScheduleByTechId(TechScheduleInfo info);
    //根据技师id 开始时间 结束时间(masterId 或者typeId) 获取排期表集合
    List<TechScheduleInfo> getScheduleByTechIdAndMasterId(TechScheduleInfo info);
    //删除排期表
    int deleteSchedule(TechScheduleInfo info);
    //增加排期表
    int insertSchedule(TechScheduleInfo info);
    // 根据 type, typeId 删除排期表
    void deleteScheduleByTypeId(TechScheduleInfo scheduleInfo);
    // 根据 type, typeId, techId 删除排期表
    void deleteScheduleByTypeIdTechId(TechScheduleInfo scheduleInfo);
    // 根据 type, typeId, techId 更新排期表
    void updateScheduleByTypeIdTechId(TechScheduleInfo techScheduleInfo);
    //app修改订单完成状态时完成时间提前需要修改排期表
    void updateScheduleByTypeIdTech(TechScheduleInfo techScheduleInfo);
}