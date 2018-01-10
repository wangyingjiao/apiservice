/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.sort;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.basic.BasicServiceCityDao;
import com.thinkgem.jeesite.modules.service.dao.office.OfficeSeviceAreaListDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillSortDao;
import com.thinkgem.jeesite.modules.service.dao.sort.SerCityScopeDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicServiceCity;
import com.thinkgem.jeesite.modules.service.entity.office.OfficeSeviceAreaList;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
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
    SerSkillSortDao serSkillSortDao;

	
	
    public SerSortInfo get(String id) {
        return super.get(id);
    }
    /**
     * 校验重复
     * @param serSortInfo
     * @return
     */
	public int checkRepeatByNameMajorSort(SerSortInfo serSortInfo) {
        return dao.checkRepeatByNameMajorSort(serSortInfo);
    }
    /**
     * 检查服务分类名称是否相同
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
        super.save(serSortInfo);
    }

    public List<SerSortInfo> findList(SerSortInfo serSortInfo) {
        List<SerSortInfo> list = dao.findSortAllList(serSortInfo);

        List<String> sortIds = serSkillSortDao.findSortIdList(serSortInfo);

        List<SerSortInfo> listRE = new ArrayList<SerSortInfo>();

        for(SerSortInfo info :list){
            String id = info.getId();
            if(!sortIds.contains(id)){
                if (info.getMajorSort().equals("clean")){
                    info.setMajorSort("保洁");
                }
                if(info.getMajorSort().equals("repair")){
                    info.setMajorSort("家修");
                }
                info.setName(info.getName()+"("+info.getMajorSort()+")");
                listRE.add(info);
            }
        }

        return listRE;
    }

    public Page<SerSortInfo> findPage(Page<SerSortInfo> page, SerSortInfo serSortInfo) {
        return super.findPage(page, serSortInfo);
    }
    public List<SerSortInfo> listDataAll(SerSortInfo serSortInfo) {
        return dao.findList(serSortInfo);
    }

    /**
     * 根据ID获取服务分类
     *
     * @param serSortInfo
     * @return
     */
    public SerSortInfo formData(SerSortInfo serSortInfo) {
        SerSortInfo sortInfo = super.get(serSortInfo.getId());
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
        // 此分类下是否有服务项目
        return dao.checkedSortItem(serSortInfo);
    }
	

}