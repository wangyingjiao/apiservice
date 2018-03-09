package com.thinkgem.jeesite.modules.service.dao.appVersion;


import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.appVersion.AppVersion;

/**
 * APP发版管理DAO接口
 * @author a
 * @version 2018-01-29
 */
@MyBatisDao
public interface AppVersionDao extends CrudDao<AppVersion> {
    AppVersion getNewestVersion();
}
