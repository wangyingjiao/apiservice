/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerGasqSort;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.sys.entity.Dict;

import java.util.List;

/**
 * 分类DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerGasqSortDao extends CrudDao<SerGasqSort> {

}