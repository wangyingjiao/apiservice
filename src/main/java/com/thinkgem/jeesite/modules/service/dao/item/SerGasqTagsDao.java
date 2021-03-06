/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerGasqSort;
import com.thinkgem.jeesite.modules.service.entity.item.SerGasqTags;

/**
 * 分类标签DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerGasqTagsDao extends CrudDao<SerGasqTags> {

}