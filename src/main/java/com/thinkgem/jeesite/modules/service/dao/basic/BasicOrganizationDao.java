/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.basic;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganizationEshop;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.Dict;

import java.util.List;

/**
 * 机构DAO接口
 * @author a
 * @version 2017-12-11
 */
@MyBatisDao
public interface BasicOrganizationDao extends CrudDao<BasicOrganization> {

    List<BasicOrganization> getByName(BasicOrganization organization);
    //add by wyr验证E店编码
    List<BasicOrganization> getByECode(BasicOrganization organization);

    int getStationList(String id);

    List<BasicOrganization> getOrganizationListByJointEshopCode(BasicOrganization organizationSerch);

    List<Dict> getPlatform();

    BasicGasqEshop getEShopByCode(BasicGasqEshop basicGasqEshop);

    int getOrgEShopByCode(BasicGasqEshop basicGasqEshop);

    void deleteEcode(BasicOrganization basicOrganization);

    void insetOrgEshop(BasicOrganizationEshop boe);

    List<BasicOrganizationEshop> findListByOrgId(BasicOrganization basicOrganization);

    void deleteEshop(BasicOrganization basicOrganization);

    void updEshopGoods(BasicOrganization basicOrganization);

    void updEshopGoodsYes(BasicOrganizationEshop boe);

    void deleteEshopGoodsByEshopCode(BasicOrganization basicOrganization);

    List<SerItemCommodityEshop> getJointGoodsCodes(BasicOrganization basicOrganization);
}