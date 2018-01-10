/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.station;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStoreDao;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务站Service
 *
 * @author x
 * @version 2017-11-06
 */
@Service
@Transactional(readOnly = true)
public class ServiceStationService extends CrudService<BasicServiceStationDao, BasicServiceStation> {

    @Autowired
    BasicServiceStationDao basicServiceStationDao;

    @Autowired
    BasicServiceStoreDao serviceStoreDao;


    @Override
    public BasicServiceStation get(String id) {
        return super.get(id);
    }

    @Override
    public List<BasicServiceStation> findList(BasicServiceStation serviceStation) {
        User user = UserUtils.getUser();
        List<BasicServiceStation> list = new ArrayList<>();
        //如果用户服务站id不是0  list添加用户的服务站id
        if (!user.getStation().getId().equals("0")) {
            list.add(super.get(user.getStation().getId()));
            return list;
        }
        if (!serviceStation.getOrgId().equals("0") && user.getStation().getId().equals("0")) {
            BasicServiceStation station = super.get("0");
            station.setName("本机构");
            list.add(station);
        }
        List<BasicServiceStation> serviceStations = super.findList(serviceStation);
        if (serviceStations.size() > 0) {
            list.addAll(serviceStations);
        }
        return list;

    }

    public List<BasicServiceStation> findListTech(BasicServiceStation serviceStation) {
        List<BasicServiceStation> serviceStations = super.findList(serviceStation);
        return serviceStations;
    }

    @Override
    public Page<BasicServiceStation> findPage(Page<BasicServiceStation> page, BasicServiceStation serviceStation) {
        serviceStation.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, serviceStation);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(BasicServiceStation serviceStation) {
        super.save(serviceStation);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(BasicServiceStation serviceStation) {
        super.delete(serviceStation);
    }

    //查询服务站的员工数量
    public int getCount(BasicServiceStation station){
        return basicServiceStationDao.getCount(station);
    }


    //更新服务站信息
    @Transactional(readOnly = false)
    public void update(BasicServiceStation serviceStation) {
        basicServiceStationDao.update(serviceStation);
    }

    @Transactional(readOnly = false)
    public Result saveStore(BasicServiceStation station) {
        if (StringUtils.isBlank(station.getId())) {
            return new FailResult("id 不能为空");
        }
        if (!(station.getStoreList().size() > 0)) {
            return new FailResult("门店id为空");
        }
        serviceStoreDao.deletebyStation(station);
        serviceStoreDao.saveStationStore(station);
        return new SuccResult("保存成功");
    }

    public List<User> getUserListByStationId(BasicServiceStation serviceStation) {
        return  dao.getUserListByStationId(serviceStation);
    }
}