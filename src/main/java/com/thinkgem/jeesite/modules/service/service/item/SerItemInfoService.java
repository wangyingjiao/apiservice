/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCityDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;

/**
 * 服务项目Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemInfoService extends CrudService<SerItemInfoDao, SerItemInfo> {
	@Autowired
	SerItemInfoDao serItemInfoDao;
	@Autowired
	SerItemCityDao serItemCityDao;
	@Autowired
	SerItemCommodityDao serItemCommodityDao;

	@Autowired
	SerItemCityService serItemCityService;
	@Autowired
	SerItemCommodityService serItemCommodityService;

	public SerItemInfo get(String id) {
		return super.get(id);
	}

	/**
	 * 保存
	 * @param serItemInfo
	 */
	@Transactional(readOnly = false)
	public void save(SerItemInfo serItemInfo) {
		if (StringUtils.isNotBlank(serItemInfo.getId())) {
			//更新时，删除定向城市
			serItemCityDao.delSerItemCityByItem(serItemInfo);
			//删除商品信息
			serItemCommodityDao.delSerItemCommodity(serItemInfo);
		}

		List<SerItemCity> citys = serItemInfo.getCitys();
		if(citys==null || 0==citys.size()){
			if(StringUtils.isNotBlank(serItemInfo.getSortId())){
				//若选择了分类，则展示当前分类指定的城市列表
				//获取分类下所有定向城市
				citys = serItemCityDao.getSortCitys(serItemInfo);
			}else{
				//若未选择分类，则展示当前机构的所有城市列表
				//获取机构下所有定向城市
				citys = serItemCityDao.getOfficeCitys(serItemInfo);
			}
		}

		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();

		super.save(serItemInfo);
		//批量插入定向城市
		for(SerItemCity city:citys){
			city.setItemId(serItemInfo.getId());
			city.setItemName(serItemInfo.getName());
			serItemCityService.save(city);
		}
		//批量插入商品信息
		for(SerItemCommodity commodity : commoditys){
			commodity.setItemId(serItemInfo.getId());
			commodity.setItemName(serItemInfo.getName());
			serItemCommodityService.save(commodity);
		}
	}

	public List<SerItemInfo> findList(SerItemInfo serItemInfo) {
		return super.findList(serItemInfo);
	}
	
	public Page<SerItemInfo> findPage(Page<SerItemInfo> page, SerItemInfo serItemInfo) {
		return super.findPage(page, serItemInfo);
	}

	/**
	 * 根据ID获取服务项目
	 * @param id
	 * @return
	 */
	public SerItemInfo getData(String id) {
		return super.get(id);
	}

	
	@Transactional(readOnly = false)
	public void delete(SerItemInfo serItemInfo) {
		//删除定向城市
		serItemCityDao.delSerItemCityByItem(serItemInfo);

		//删除商品信息
		List<SerItemCommodity> commoditys = serItemInfoDao.getSerItemCommoditys(serItemInfo);
		for(SerItemCommodity commodity : commoditys){
			serItemCommodityService.delete(commodity);
		}
		super.delete(serItemInfo);
	}

	/**
	 * 检查服务项目名是否重复
	 * @param serItemInfo
	 * @return
	 */
	public int checkDataName(SerItemInfo serItemInfo) {
		return serItemInfoDao.checkDataName(serItemInfo);
	}

	@Transactional(readOnly = false)
    public void updateSerItemPicNum(SerItemInfo serItemInfo) {
		serItemInfo.preUpdate();
		serItemInfoDao.updateSerItemPicNum(serItemInfo);
    }

	public SerItemInfo getSerItemInfoPic(SerItemInfo serItemInfo) {
		return serItemInfoDao.getSerItemInfoPic(serItemInfo);
	}
}