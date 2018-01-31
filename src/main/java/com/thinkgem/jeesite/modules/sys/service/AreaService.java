/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.entity.AreaTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 区域Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends CrudService<AreaDao, Area> {
	@Autowired
	AreaDao areaDao;

/*
	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}



	@Override
	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}

	public List<Area> findchildArea(Area area) {
		return areaDao.findchildArea(area);
	}
	
	public List<Area> treeArea() {
		return areaDao.findAllList(new Area());
	}
	
	public List<Area> findListByIds(String[] ids) {
		return areaDao.findListByIds(ids);
	}
*/

	public List<Area> appFindAllList(Area info){
		return areaDao.appFindAllList(info);
	}
	//app登陆 修改用户使用
	public List<Area> getNameByCode(String province_code){
		return areaDao.getNameByCode(province_code);
	}

    public List<AreaTree> getAreaTree() {
		Area info = new Area();
		info.setLevel(1);
		List<Area> provinceCodeList = dao.getListByLevel(info);//省_区号
		info.setLevel(2);
		List<Area> cityCodeList = dao.getListByLevel(info);//市_区号
		info.setLevel(3);
		List<Area> areaCodeList = dao.getListByLevel(info);//区_区号

		List<AreaTree> provinceList = new ArrayList<AreaTree>();//省_区号
		if(null != provinceCodeList){
			for(Area province : provinceCodeList){
				AreaTree provinceInfo = new AreaTree();
				provinceInfo.setValue(province.getCode());
				provinceInfo.setLabel(province.getName());

				List<AreaTree> cityList = new ArrayList<AreaTree>();//市_区号
				if(null != cityCodeList){
					for(Area city : cityCodeList){
						if(province.getCode().equals(city.getParentCode())){
							AreaTree cityInfo = new AreaTree();
							cityInfo.setValue(city.getCode());
							cityInfo.setLabel(city.getName());

							List<AreaTree> areaList = new ArrayList<AreaTree>();//区_区号
							if(null != areaCodeList){
								for(Area area : areaCodeList){
									if(city.getCode().equals(area.getParentCode())) {
										AreaTree areaInfo = new AreaTree();
										areaInfo.setValue(area.getCode());
										areaInfo.setLabel(area.getName());
										areaList.add(areaInfo);
									}
								}
							}
							cityInfo.setChildren(areaList);
							cityList.add(cityInfo);
						}
					}
				}
				provinceInfo.setChildren(cityList);
				provinceList.add(provinceInfo);
			}
		}

		return provinceList;
    }
}
