package com.thinkgem.jeesite.modules.service.service.log;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.log.ServiceLogDao;
import com.thinkgem.jeesite.modules.service.dao.technician.ServiceTechnicianHolidayDao;
import com.thinkgem.jeesite.modules.service.entity.log.ServiceLog;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianHoliday;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志Service
 * @author a
 * @version 2018-01-26
 */

@Service
@Transactional(readOnly = true)
public class ServiceLogService extends CrudService<ServiceLogDao, ServiceLog> {
    @Autowired
    private ServiceLogDao serviceLogDao;

    public Page<ServiceLog> findPage(Page<ServiceLog> page, ServiceLog serviceLog) {
        //serviceLog.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, serviceLog);
    }
}
