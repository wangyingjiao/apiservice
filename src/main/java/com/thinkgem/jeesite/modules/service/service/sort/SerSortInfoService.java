/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.office.OfficeSeviceAreaListDao;
import com.thinkgem.jeesite.modules.service.dao.sort.SerSortCityDao;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortCity;
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
    SerSortInfoDao serSortInfoDao;
    @Autowired
    SerSortCityDao serSortCityDao;
    @Autowired
    OfficeSeviceAreaListDao officeSeviceAreaListDao;
    @Autowired
    private SerSortCityService serSortCityService;

    public SerSortInfo get(String id) {
        return super.get(id);
    }

    /**
     * 保存
     *
     * @param serSortInfo
     */
    @Transactional(readOnly = false)
    public void save(SerSortInfo serSortInfo) {
        if (StringUtils.isNotBlank(serSortInfo.getId())) {
            //更新时，先删除以保存的定向城市
            serSortCityDao.delSerSortCityBySort(serSortInfo.getId());
        }
        List<SerSortCity> citys = serSortInfo.getCitys();
        if(0 == citys.size()){
            //获取机构下所有定向城市
            User user = UserUtils.getUser();
            if (null != user) {
                citys = serSortCityDao.getOfficeCitys(user.getOfficeId());
            }
        }
        super.save(serSortInfo);
        //批量插入定向城市
        for(SerSortCity city:citys){
            city.setSortId(serSortInfo.getId());
            serSortCityService.save(city);
        }
    }

    public List<SerSortInfo> findList(SerSortInfo serSortInfo) {
        return super.findList(serSortInfo);
    }

    public Page<SerSortInfo> findPage(Page<SerSortInfo> page, SerSortInfo serSortInfo) {
        return super.findPage(page, serSortInfo);
    }

    /**
     * 根据ID获取服务分类
     *
     * @param id
     * @return
     */
    public SerSortInfo getData(String id) {
        SerSortInfo sortInfo = super.get(id);
        //获取机构下所有定向城市
        User user = UserUtils.getUser();
        if (null != user) {
            List<OfficeSeviceAreaList> officeCitys = officeSeviceAreaListDao.getOfficeCitys(user.getOfficeId());
            sortInfo.setOfficeCitys(officeCitys);
        }
        //获取分类的定向城市
        List<SerSortCity> citys = serSortCityDao.getCitys(id);
        sortInfo.setCitys(citys);
        return sortInfo;
    }

    @Transactional(readOnly = false)
    public void delete(SerSortInfo serSortInfo) {
        super.delete(serSortInfo);
    }

    /**
     * 检查服务分类下是否有服务项目
     *
     * @param serSortInfo
     * @return
     */
    public int checkedSortItem(SerSortInfo serSortInfo) {
        User user = UserUtils.getUser();
        if (null != user) {
            serSortInfo.setOfficeId(user.getOfficeId());
        }
        // 此分类下是否有服务项目
        return serSortInfoDao.checkedSortItem(serSortInfo);
    }

    /**
     * 检查同一机构下服务分类名称是否相同
     *
     * @param serSortInfo
     * @return
     */
    public int checkDataName(SerSortInfo serSortInfo) {
        User user = UserUtils.getUser();
        if (null != user) {
            serSortInfo.setOfficeId(user.getOfficeId());
        }
        return serSortInfoDao.checkDataName(serSortInfo);
    }

    /**
     * 检查定向城市是否设置服务项目
     *
     * @param serSortInfo
     * @return
     */
    public int checkCityItem(SerSortInfo serSortInfo) {
        User user = UserUtils.getUser();
        if (null != user) {
            serSortInfo.setOfficeId(user.getOfficeId());
        }
        return serSortInfoDao.checkCityItem(serSortInfo);
    }
}