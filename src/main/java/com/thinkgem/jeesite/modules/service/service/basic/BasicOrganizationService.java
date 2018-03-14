/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.basic;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganizationEshop;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.open.send.OpenSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicOrganizationDao;

/**
 * 机构Service
 * @author a
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class BasicOrganizationService extends CrudService<BasicOrganizationDao, BasicOrganization> {

	@Autowired
	BasicServiceCityService basicServiceCityService;
	@Autowired
	private BasicOrganizationDao basicOrganizationDao;

	public Page<BasicOrganization> findPage(Page<BasicOrganization> page, BasicOrganization office){
		//office.setPage(page);
        List<BasicOrganization> list = null;
        Page<BasicOrganization> pageResult = null;
		User user = UserUtils.getUser();
		office.getSqlMap().put("dsf", BaseService.dataOrganFilter(user, "a"));
		//page.setList(dao.findList(office));
        pageResult = super.findPage(page, office);
        list = pageResult.getList();
        if (list.size() != 0 && null != list) {//返回查询条件对应的服务分类的商品集合
            for (BasicOrganization basicOrganization : list) {
                List<BasicOrganizationEshop> findListByOrgId = basicOrganizationDao.findListByOrgId(basicOrganization);
                //basicOrganization.setBasicOrganizationEshops(findListByOrgId);
                if (findListByOrgId.size()>0) {
                    String names = "";
                    for (BasicOrganizationEshop boe : findListByOrgId) {
                        names += boe.getName() + ",";
                    }
                    names = names.substring(0, names.length() - 1);
                    basicOrganization.setEshopNames(names);
                }
            }
        }
		return page;
	}

	/**
	 * 验证名称是否重复
	 * @param organization
	 * @return
	 */
	public boolean getByName(BasicOrganization organization) {
		List<BasicOrganization> basicOrganization = dao.getByName(organization);
		return basicOrganization.size() != 0;
	}
	/**
	 * E店编码不能重复
	 * @param organization
	 * @return
	 */
	public boolean getByECode(BasicOrganization organization) {
		List<BasicOrganization> basicOrganization = dao.getByECode(organization);
		return basicOrganization.size() != 0;
	}

	@Transactional(readOnly = false)
	public void save(BasicOrganization basicOrganization) {
		if(StringUtils.isNotBlank(basicOrganization.getId())){
			//更新
			deleteCitysByOrgId(basicOrganization);
		}
        //修改时，如平台选为请选择时，执行删除
        if (basicOrganization.getIsNewRecord()) {
		    basicOrganization.preInsert();
            List<BasicOrganizationEshop> eList=basicOrganization.getBasicOrganizationEshops();
            for (BasicOrganizationEshop boe : eList){
               boe.preInsert();
               boe.setOrgId(basicOrganization.getId());
               boe.setDockType(basicOrganization.getDockType());
                basicOrganizationDao.insetOrgEshop(boe);
                //basicOrganizationDao.updEshopGoodsYes(boe);
            }
            dao.insert(basicOrganization);
        }else {
            if (basicOrganization.getDockType().equals("")) {
                basicOrganizationDao.deleteEcode(basicOrganization);
            }else {
                List<BasicOrganizationEshop> eshopList=basicOrganization.getBasicOrganizationEshops();
                for (BasicOrganizationEshop boe : eshopList){
                    if (boe.getId()==null){
                        boe.preInsert();
                        boe.setOrgId(basicOrganization.getId());
                        boe.setDockType(basicOrganization.getDockType());
                        basicOrganizationDao.insetOrgEshop(boe);
                        //basicOrganizationDao.updEshopGoodsYes(boe);
                    }
                }
            }
            basicOrganization.preUpdate();
            dao.update(basicOrganization);
        }

        //super.save(basicOrganization);

		List<String> cityCodes = basicOrganization.getCityCodes();
		if(null != cityCodes){
			for(String cityCode : cityCodes ){
                BasicServiceCity city = new BasicServiceCity();
				city.setOrgId(basicOrganization.getId());
				city.setCityCode(cityCode);
				basicServiceCityService.save(city);
			}
		}
	}

	private void deleteCitysByOrgId(BasicOrganization basicOrganization) {
		dao.deleteCitysByOrgId(basicOrganization);
	}

	public BasicOrganization get(String id) {
		return super.get(id);
	}
	
	public List<BasicOrganization> findList(BasicOrganization basicOrganization) {
		return super.findList(basicOrganization);
	}

	/**
	 * 机构详情
	 * @param basicOrganization
	 * @return
	 */
	public BasicOrganization formData(BasicOrganization basicOrganization) {
		if(null == basicOrganization){
			return null;
		}
		BasicOrganization basicOrganizationRe = get(basicOrganization.getId());
        List<String> cityCodes = new ArrayList<String>();//服务城市
		List<BasicServiceCity> cityCodeList = basicServiceCityService.getCityCodesByOrgId(basicOrganization.getId());
		if(null != cityCodeList){
            for(BasicServiceCity city : cityCodeList){
                cityCodes.add(city.getCityCode());
            }
        }
		basicOrganizationRe.setCityCodes(cityCodes);

		int stations = dao.getStationList(basicOrganization.getId());
		if(stations == 0){
			basicOrganizationRe.setHaveStation(0);
		}else{
			basicOrganizationRe.setHaveStation(1);
		}
		List<BasicOrganizationEshop> boeList = basicOrganizationDao.findListByOrgId(basicOrganizationRe);
		basicOrganizationRe.setBasicOrganizationEshops(boeList);

		return basicOrganizationRe;
	}

	/**
	 * 获取当前机构下所有城市
	 * @param orgId
	 * @return
	 */
    public List<BasicServiceCity> getOrgCityCodes(String orgId) {
		List<BasicServiceCity> cityCodeList = basicServiceCityService.getCityCodesByOrgId(orgId);
		return cityCodeList;
    }

	public List<BasicOrganization> findListAll(BasicOrganization basicOrganization) {
		basicOrganization.getSqlMap().put("dsf", BaseService.dataOrganFilter( UserUtils.getUser(), "a"));
		return dao.findList(basicOrganization);
	}

    public List<Dict> getPlatform() {
    	return basicOrganizationDao.getPlatform();
    }

	public BasicGasqEshop getEShopByCode(BasicGasqEshop basicGasqEshop) {
    	return basicOrganizationDao.getEShopByCode(basicGasqEshop);
	}

	public int getOrgEShopByCode(BasicGasqEshop basicGasqEshop) {
    	return basicOrganizationDao.getOrgEShopByCode(basicGasqEshop);
	}

    public void deleteEcode(BasicOrganization basicOrganization) {
        basicOrganizationDao.deleteEcode(basicOrganization);
    }

    @Transactional(readOnly = false)
	public void deleteEshop(BasicOrganization basicOrganization) {
    	basicOrganizationDao.deleteEshop(basicOrganization);
        //basicOrganizationDao.updEshopGoods(basicOrganization);
        basicOrganizationDao.deleteEshopGoodsByEshopCode(basicOrganization);
        String eshopCode = basicOrganization.getEshopCode();
        List<SerItemCommodityEshop> jointGoodsCodes = basicOrganizationDao.getJointGoodsCodes(basicOrganization);
        if (jointGoodsCodes.size()>0) {
            //OpenSendUtil.delGoodsByOrgEshop(eshopCode, jointGoodsCodes);
        }
	}
}