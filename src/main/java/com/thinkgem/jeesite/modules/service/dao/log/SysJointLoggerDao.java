package com.thinkgem.jeesite.modules.service.dao.log;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;

/**
 * 对接日志记录接口
 * @author a
 * @version 2018-01-26
 */
@MyBatisDao
public interface SysJointLoggerDao extends CrudDao<SysJointLogger> {


}
