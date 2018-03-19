/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;

/**
 * 服务项目商品信息Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemCommodityService extends CrudService<SerItemCommodityDao, SerItemCommodity> {
	@Autowired
	SerItemCommodityDao serItemCommodityDao;

	public SerItemCommodity get(String id) {
		return super.get(id);
	}
	
	public List<SerItemCommodity> findList(SerItemCommodity serItemCommodity) {
		return super.findList(serItemCommodity);
	}
	
	public Page<SerItemCommodity> findPage(Page<SerItemCommodity> page, SerItemCommodity serItemCommodity) {
		return super.findPage(page, serItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void save(SerItemCommodity serItemCommodity) {
		super.save(serItemCommodity);
	}
	
	@Transactional(readOnly = false)
	public void delete(SerItemCommodity serItemCommodity) {
		super.delete(serItemCommodity);
	}

	public Page<SerItemCommodityEshop> findCommodityPage(Page<SerItemCommodityEshop> serItemCommodityEshopPage, SerItemCommodityEshop serItemCommodityEshop) {
		serItemCommodityEshop.setPage(serItemCommodityEshopPage);
		List<SerItemCommodityEshop> list = serItemCommodityDao.findCommodityList(serItemCommodityEshop);
		for (SerItemCommodityEshop sic : list){
		    sic.setNewName(sic.getItemName()+"("+sic.getGoodsName()+")");
		    sic.setUnivalence(sic.getPrice()+"元/"+sic.getUnit());
        }
		serItemCommodityEshopPage.setList(list);
		return serItemCommodityEshopPage;
	}

	public String getEshop(SerItemCommodityEshop serItemCommodityEshop) {
		int i = serItemCommodityDao.getEshop(serItemCommodityEshop);
		if (i>0){
			return "yes";
		}else {
			return "no";
		}
	}

	public int getGoodsEshop(SerItemCommodity serItemCommodity) {
		return serItemCommodityDao.getGoodsEshop(serItemCommodity);
	}

	public void insertGoodsEshop(SerItemCommodityEshop sice) {
		serItemCommodityDao.insertGoodsEshop(sice);
	}
}