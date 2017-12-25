/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicServiceCityDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.dao.sort.SerCityScopeDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.service.sort.SerCityScopeService;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
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
	SerItemCommodityDao serItemCommodityDao;
	@Autowired
	SerItemCommodityService serItemCommodityService;
	@Autowired
	SerCityScopeDao serCityScopeDao;
	@Autowired
	SerCityScopeService serCityScopeService;
	@Autowired
	BasicServiceCityDao basicServiceCityDao;

	public SerItemInfo get(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void saveSort(SerItemInfo serItemInfo) {
		super.save(serItemInfo);
	}
	/**
	 * 保存
	 * @param serItemInfo
	 */
	@Transactional(readOnly = false)
	public void save(SerItemInfo serItemInfo) {
		if (StringUtils.isNotBlank(serItemInfo.getId())) {
			//更新时，删除定向城市
			serCityScopeDao.delSerCityScopeByMaster(serItemInfo.getId());

			//删除商品信息
			serItemCommodityDao.delSerItemCommodity(serItemInfo);
		}
		List<String> cityCodes = serItemInfo.getCityCodes();
		if(cityCodes==null || 0==cityCodes.size()){
			serItemInfo.setAllCity("yes");
		}else{
			serItemInfo.setAllCity("no");
		}

		List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();

		super.save(serItemInfo);
		//批量插入定向城市
		if(cityCodes != null){
			for(String cityCode : cityCodes){
				SerCityScope serCityScope = new SerCityScope();
				serCityScope.setMasterId(serItemInfo.getId());
				serCityScope.setType("1");//0:服务分类 1:服务项目
				serCityScope.setCityCode(cityCode);//市_区号
				serCityScopeService.save(serCityScope);
			}
		}
		if(commoditys != null) {
			//批量插入商品信息
			for (SerItemCommodity commodity : commoditys) {
				commodity.setItemId(serItemInfo.getId());
				serItemCommodityService.save(commodity);
			}
		}
	}

	public List<SerItemInfo> findList(SerItemInfo serItemInfo) {
		return super.findList(serItemInfo);
	}
	
	public Page<SerItemInfo> findPage(Page<SerItemInfo> page, SerItemInfo serItemInfo) {
		serItemInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
		Page<SerItemInfo> pageResult = super.findPage(page, serItemInfo);
		List<SerItemInfo> list = pageResult.getList();
		if(null != list){
			for(SerItemInfo entity : list){
				String picture = entity.getPicture();
				if(null != picture){
					List<String> pictures = (List<String>) JsonMapper.fromJsonString(picture,ArrayList.class);
					entity.setPictures(pictures);
				}
			}
		}
		return pageResult;
	}

	/**
	 * 根据ID获取服务项目
	 * @param id
	 * @return
	 */
	public SerItemInfo getData(String id) {
		SerItemInfo serItemInfo = super.get(id);
		//获取定向城市
		List<SerCityScope> citys = serCityScopeDao.getSerCityScopeByMaster(id);
		List<String> cityCodes = null;
		if(null != citys){
			cityCodes = new ArrayList<String>();
			for(SerCityScope city : citys){
				cityCodes.add(city.getCityCode());
			}
		}
		serItemInfo.setCityCodes(cityCodes);

		List<SerItemCommodity> commoditys = serItemCommodityDao.findListByItemId(serItemInfo);
		serItemInfo.setCommoditys(commoditys);

		SerItemInfo serItemInfoForAllCity = new SerItemInfo();
		User user = UserUtils.getUser();
		serItemInfoForAllCity.setOrgId(user.getOrganization().getId());//机构ID
		serItemInfoForAllCity.setSortId(serItemInfo.getSortId());
		List<SerCityScope>  allCitys = getAllCityCodes(serItemInfoForAllCity);
		serItemInfo.setAllCitys(allCitys);
		return serItemInfo;
	}

	
	@Transactional(readOnly = false)
	public void delete(SerItemInfo serItemInfo) {
		//删除定向城市
		serCityScopeDao.delSerCityScopeByMaster(serItemInfo.getId());

		//删除商品信息
		List<SerItemCommodity> commoditys = dao.getSerItemCommoditys(serItemInfo);
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
		return dao.checkDataName(serItemInfo);
	}

	@Transactional(readOnly = false)
    public void updateSerItemPicNum(SerItemInfo serItemInfo) {
		serItemInfo.preUpdate();
		dao.updateSerItemPicNum(serItemInfo);
    }

	public SerItemInfo getSerItemInfoPic(SerItemInfo serItemInfo) {
		return dao.getSerItemInfoPic(serItemInfo);
	}

    public List<Dict> getSerSortInfoList(SerItemInfo serItemInfo) {
		return dao.getSerSortInfoList(serItemInfo);
    }

	public List<SerCityScope> getAllCityCodes(SerItemInfo serItemInfo) {
		List<SerCityScope> citys = new ArrayList<SerCityScope>();
		if(StringUtils.isNotBlank(serItemInfo.getSortId())){
			SerSortInfo serSortInfo = dao.getSerSortInfo(serItemInfo.getSortId());
			if(serSortInfo == null || "yes".equals(serSortInfo.getAllCity())){
				List<BasicServiceCity> list = basicServiceCityDao.getCityCodesByOrgId(serItemInfo.getOrgId());
				if(null != list){
					for(BasicServiceCity city : list){
						SerCityScope cityScope = new SerCityScope();
						cityScope.setCityCode(city.getCityCode());
						cityScope.setCityName(city.getCityName());
						citys.add(cityScope);
					}
				}
			}else{
				citys = serCityScopeDao.getSerCityScopeByMaster(serItemInfo.getSortId());
			}
		}else{
			List<BasicServiceCity> list = basicServiceCityDao.getCityCodesByOrgId(serItemInfo.getOrgId());
			if(null != list){
				for(BasicServiceCity city : list){
					SerCityScope cityScope = new SerCityScope();
					cityScope.setCityCode(city.getCityCode());
					cityScope.setCityName(city.getCityName());
					citys.add(cityScope);
				}
			}
		}
		return  citys;
	}

}