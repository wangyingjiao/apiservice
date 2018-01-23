/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;

import java.util.List;

/**
 * 服务技师基础信息DAO接口
 * @author a
 * @version 2017-11-16
 */
@MyBatisDao
public interface VersionInfoDao extends CrudDao<VersionInfo> {
    //app 根据最新时间 获取数据库最新版本
    VersionInfo getByTime(VersionInfo versionInfo);

}