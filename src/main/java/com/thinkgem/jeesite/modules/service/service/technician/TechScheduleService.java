/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.technician;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianInfoDao;
import com.thinkgem.jeesite.modules.service.dao.technician.TechScheduleDao;
import com.thinkgem.jeesite.modules.service.entity.technician.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务技师基础信息Service
 *
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class TechScheduleService extends CrudService<TechScheduleDao, TechScheduleInfo> {


    @Autowired
    private TechScheduleDao techScheduleDao;

    //根据技师id 开始时间 结束时间 获取排期表集合
    public List<TechScheduleInfo> getScheduleByTechId(TechScheduleInfo info){
        return techScheduleDao.getScheduleByTechId(info);
    }

}