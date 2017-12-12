/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicServiceCityDao;
import com.thinkgem.jeesite.modules.service.dao.office.OfficeSeviceAreaListDao;
import com.thinkgem.jeesite.modules.service.dao.sort.SerCityScopeDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.sort.SerCityScope;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortInfoDao;

/**
 * 服务分类Service
 *
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSortInfoService extends CrudService<SerSortInfoDao, SerSortInfo> {
    @Autowired
    SerCityScopeDao serCityScopeDao;
    @Autowired
    SerCityScopeService serCityScopeService;

    public SerSortInfo get(String id) {
        return super.get(id);
    }

    /**
     * 检查同一机构下服务分类名称是否相同
     *
     * @param serSortInfo
     * @return
     */
    public int checkDataName(SerSortInfo serSortInfo) {
        return dao.checkDataName(serSortInfo);
    }

    /**
     * 保存
     *
     * @param serSortInfo
     */

    @Transactional(readOnly = false)
    public void save(SerSortInfo serSortInfo) {
        if (StringUtils.isNotBlank(serSortInfo.getId())) {
            //更新时，删除定向城市
            serCityScopeDao.delSerCityScopeByMaster(serSortInfo.getId());
        }
        List<String> cityCodes = serSortInfo.getCityCodes();
        if(cityCodes==null || 0==cityCodes.size()){
            serSortInfo.setAllCity("yes");
        }else{
            serSortInfo.setAllCity("no");
        }
        super.save(serSortInfo);
        //批量插入定向城市
        if(cityCodes != null){
            for(String cityCode : cityCodes){
                SerCityScope serCityScope = new SerCityScope();
                serCityScope.setMasterId(serSortInfo.getId());
                serCityScope.setType("0");//0:服务分类 1:服务项目
                serCityScope.setCityCode(cityCode);//市_区号
                serCityScopeService.save(serCityScope);
            }
        }
    }


    public List<SerSortInfo> findList(SerSortInfo serSortInfo) {
        return super.findList(serSortInfo);
    }

    public Page<SerSortInfo> findPage(Page<SerSortInfo> page, SerSortInfo serSortInfo) {
        //serSortInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, serSortInfo);
    }

    /**
     * 根据ID获取服务分类
     *
     * @param serSortInfo
     * @return
     */
    public SerSortInfo getData(SerSortInfo serSortInfo) {
        SerSortInfo sortInfo = super.get(serSortInfo.getId());
        //获取分类的定向城市
        List<SerCityScope> citys = serCityScopeDao.getSerCityScopeByMaster(serSortInfo.getId());
        List<String> cityCodes = null;
        if(null != citys){
            cityCodes = new ArrayList<String>();
            for(SerCityScope city : citys){
                cityCodes.add(city.getCityCode());
            }
        }
        sortInfo.setCityCodes(cityCodes);
        return sortInfo;
    }

    @Transactional(readOnly = false)
    public void delete(SerSortInfo serSortInfo) {
        //删除定向城市
        serCityScopeDao.delSerCityScopeByMaster(serSortInfo.getId());
        super.delete(serSortInfo);
    }

    /**
     * 检查服务分类下是否有服务项目
     *
     * @param serSortInfo
     * @return
     */
    public int checkedSortItem(SerSortInfo serSortInfo) {
        // 此分类下是否有服务项目
        return dao.checkedSortItem(serSortInfo);
    }

    /**
     * 检查定向城市是否设置服务项目
     *
     * @param serSortInfo
     * @return
     */
    public int checkCityItem(SerSortInfo serSortInfo) {
        return dao.checkCityItem(serSortInfo);
    }

}