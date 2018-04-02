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

    //根据typeId查询排期表
    TechScheduleInfo getSchedule(TechScheduleInfo info);
    //根据技师id 开始时间 结束时间 获取排期表集合
    List<TechScheduleInfo> getScheduleByTechId(TechScheduleInfo info);
    //删除排期表
    int deleteSchedule(TechScheduleInfo info);
    //增加排期表
    int insertSchedule(TechScheduleInfo info);
}