package com.thinkgem.jeesite.modules.service.service.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.dao.log.SysJointLoggerDao;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;



/** 
* @ClassName: SysJointLogService 
* @Description: TODO
* @author WYR
* @date 2018年1月31日 下午3:19:52 
*  
*/
@Service
@Transactional(readOnly = true)
public class SysJointLoggerService extends CrudService<SysJointLoggerDao, SysJointLogger> {
    @Autowired
    private SysJointLoggerDao SysJointLoggerDao;

    public Page<SysJointLogger> findPage(Page<SysJointLogger> page, SysJointLogger sysJointLogger) {
        //serviceLog.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, sysJointLogger);
    }
}
