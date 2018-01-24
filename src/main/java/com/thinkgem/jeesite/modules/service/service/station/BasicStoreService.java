/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.station;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStoreDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicStoreDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicStore;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务站Service
 *
 * @author x
 * @version 2017-11-06
 */
@Service
@Transactional(readOnly = true)
public class BasicStoreService extends CrudService<BasicStoreDao, BasicStore> {

    @Autowired
    BasicStoreDao basicStoreDao;
    @Autowired
    BasicServiceStoreDao serviceStoreDao;
    @Autowired
    private AreaDao areaDao;


    @Override
    public BasicStore get(String id) {
        return super.get(id);
    }

    @Override
    public List<BasicStore> findList(BasicStore basicStore) {

        List<BasicStore> list = super.findList(basicStore);
        if (list.size() > 0) {
            List<BasicStore> treeStore = getTreeStore(list);
            return treeStore;
        }
        return list;
    }

    private List<BasicStore> getTreeStore(List<BasicStore> stores) {


        HashMap<String, List<BasicStore>> areaHashStore = new HashMap<>();
        List<Area> areas = areaDao.findAllList(new Area());

        //1.取得所有的按区分的组
        for (BasicStore store : stores) {
            String areaCode = store.getAreaCode();
            fixMap(areaHashStore, store, areaCode);
        }

        List<BasicStore> areaStore = getBasicStores(areaHashStore, areas);
        HashMap<String, List<BasicStore>> cityMapStore = new HashMap<>();

        for (BasicStore store : areaStore) {
            String cityCode = store.getCityCode();
            fixMap(cityMapStore, store, cityCode);
        }

        List<BasicStore> cityStore = getBasicStores(cityMapStore, areas);
        HashMap<String, List<BasicStore>> provMapStore = new HashMap<>();
        for (BasicStore store : cityStore) {
            String provinceCode = store.getProvinceCode();
            fixMap(provMapStore, store, provinceCode);
        }
        List<BasicStore> basicStores = getBasicStores(provMapStore, areas);
        return basicStores;

    }

    private void fixMap(HashMap<String, List<BasicStore>> provMapStore, BasicStore store, String code) {
        if (StringUtils.isBlank(code)) {
            return;
        }
        if (provMapStore.containsKey(code)) {
            provMapStore.get(code).add(store);
        } else {
            List<BasicStore> basicStores = new ArrayList<>();
            basicStores.add(store);
            provMapStore.put(code, basicStores);
        }
    }

    private List<BasicStore> getBasicStores(HashMap<String, List<BasicStore>> areaHashStore, List<Area> areaList) {
        List<BasicStore> areaStore = new ArrayList<>();
        Set<Map.Entry<String, List<BasicStore>>> entries = areaHashStore.entrySet();
        for (Map.Entry<String, List<BasicStore>> entry : entries) {
            String key = entry.getKey();
            Area a = new Area();
            for (Area area : areaList) {
                if (area.getCode().equals(key)) {
                    a = area;
                    break;
                }
            }
            if (StringUtils.isBlank(a.getCode())) {
                a.setCode("0");
            }
            BasicStore store = new BasicStore(a);
            BasicStore basicStore = entry.getValue().get(0);
            store.setProvinceCode(basicStore.getProvinceCode());
            store.setCityCode(basicStore.getCityCode());
            store.setAreaCode(basicStore.getAreaCode());
            store.setChildren(entry.getValue());

            areaStore.add(store);
        }
        return areaStore;
    }

    @Override
    public Page<BasicStore> findPage(Page<BasicStore> page, BasicStore basicStore) {
        basicStore.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, basicStore);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(BasicStore basicStore) {
        super.save(basicStore);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(BasicStore basicStore) {
        super.delete(basicStore);
    }

	public List<String> getInIds(String orgId) {
		List<String> orgIds = basicStoreDao.getInIds(orgId);
		return orgIds;
	}

	public List<BasicStore> findListNotIn(BasicStore basicStore) {

        List<BasicStore> list = basicStoreDao.findListNotIn(basicStore);
        if (list.size() > 0) {
            List<BasicStore> treeStore = getTreeStore(list);
            return treeStore;
        }
        return list;
    }

}