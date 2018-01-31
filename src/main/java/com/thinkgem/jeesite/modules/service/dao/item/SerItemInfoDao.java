/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.sys.entity.Dict;

import java.util.List;

/**
 * 服务项目DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerItemInfoDao extends CrudDao<SerItemInfo> {

    int checkDataName(SerItemInfo serItemInfo);

    List<SerItemCommodity> getSerItemCommoditys(SerItemInfo serItemInfo);

    void updateSerItemPicNum(SerItemInfo serItemInfo);

    SerItemInfo getSerItemInfoPic(SerItemInfo serItemInfo);

    List<Dict> getSerSortInfoList(SerItemInfo serItemInfo);

    SerSortInfo getSerSortInfo(String sortId);

    List<SerItemInfo> getByName(SerItemInfo serItemInfo);

    BasicOrganization getBasicOrganizationByOrgId(SerItemInfo serItemInfo);

    void updateJointStatus(SerItemInfo serItemInfo);

    SerItemInfo getItemInfoByCommodityId(SerItemCommodity serItemCommodity);
}