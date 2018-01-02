/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.sort;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;

import java.util.List;

/**
 * 服务分类DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerSortInfoDao extends CrudDao<SerSortInfo> {

    int checkDataName(SerSortInfo serSortInfo);

    int checkedSortItem(SerSortInfo serSortInfo);
}