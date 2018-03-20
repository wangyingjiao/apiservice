/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.item;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;

import java.util.List;

/**
 * 服务项目商品信息DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerItemCommodityDao extends CrudDao<SerItemCommodity> {

   // void delSerItemCommodityPersons(SerItemCommodity serItemCommodity);

    void delSerItemCommodity(SerItemInfo serItemInfo);

    List<SerItemCommodity> getSerItemCommodityByItem(SerItemInfo serItemInfo);

    List<SerItemCommodity> findListByItemId(SerItemInfo serItemInfo);

    void updateJointGoodsCode(SerItemCommodity goods);

    List<SerItemCommodityEshop> getEshopGoodsList(SerItemCommodity serItemCommodity);

    List<SerItemCommodityEshop> getEshopGoods(SerItemCommodity serItemCommodity);

    SerItemCommodity getGoods(SerItemCommodity serItemCommodity);
    //根据登录用户的机构id查询出对应E店名称
    List<BasicGasqEshop> getGoodsCode(String orgId);

    List<SerItemCommodityEshop> findCommodityList(SerItemCommodityEshop serItemCommodityEshop);

    int getEshop(SerItemCommodityEshop serItemCommodityEshop);

    //根据E店id 分类 商品名 对接code 查询出对应E店下的商品信息
    List<SerItemCommodityEshop> getGoodsList(SerItemCommodityEshop serItemCommodityEshop);

    //根据id 获取服务项目商品信息
    SerItemCommodity getSerItemCommodity(String id);
    //根据id查出对应的ser_item_info_goods_eshop
    SerItemCommodityEshop getGoodEshop(SerItemCommodityEshop serItemCommodity);

    //修改ser_item_info_goods_eshop表的状态 改为不可用
    int updateEshop(SerItemCommodityEshop serItemCommodity);

    int getGoodsEshop(SerItemCommodity serItemCommodity);

    void insertGoodsEshop(SerItemCommodityEshop sice);

    void updateGoodEshop(SerItemCommodity serItemCommodity);
}