/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.station;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStoreDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
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

	//服务站机构联动（下拉列表） 参数 orgId
	@Override
	public List<BasicServiceStation> findList(BasicServiceStation serviceStation) {
		User user = UserUtils.getUser();
		List<BasicServiceStation> listByOrgId =new ArrayList<BasicServiceStation>();
		List<BasicServiceStation> listByOrgId2 =new ArrayList<BasicServiceStation>();
		//如果不是station用户 根据orgId展示所有服务站信息
		if (!"station".equals(user.getType())) {
			if ((!"sys".equals(serviceStation.getOrgId())) && (!"0".equals(serviceStation.getOrgId()))) {
				listByOrgId2 = basicServiceStationDao.findListByOrgId(serviceStation);
				BasicServiceStation station = new BasicServiceStation();
				station.setName("本机构");
                station.setId("0");
				listByOrgId.add(station);
				for (int i = 0; i < listByOrgId2.size(); i++) {
					listByOrgId.add(listByOrgId2.get(i));//开始复制一个list的内容到另外一个list
				}
			}else {
                listByOrgId = basicServiceStationDao.findListByOrgId(serviceStation);
            }
		}else {
			//如果是服务站用户 只展示用户的服务站信息
			BasicServiceStation station = basicServiceStationDao.get(user.getStation().getId());
			listByOrgId.add(station);
		}
		return listByOrgId;

	}

	//员工新增 服务站下拉列表 与角色，机构联动  参数 orgId type
	public List<BasicServiceStation> listStationByOrgId(BasicServiceStation serviceStation) {
		List<BasicServiceStation> listByOrgId =new ArrayList<BasicServiceStation>();
		String type = serviceStation.getType();
		String orgId = serviceStation.getOrgId();
		BasicServiceStation station=new BasicServiceStation();
		User user = UserUtils.getUser();
		//如果选择了sys 角色  机构id 也为sys
		if ("sys".equals(type) && "sys".equals(orgId)){
			if ("sys".equals(user.getType())){
					station.setName("全系统");
					station.setId("sys");
					listByOrgId.add(station);
			}else {
				throw new ServiceException("权限不足");
			}
		}
		//如果选择了sys 角色  机构id 也为sys
		if ("platform".equals(type) && "0".equals(orgId)){
			if ("sys".equals(user.getType()) || "platform".equals(user.getType())) {
				station.setName("全平台");
				station.setId("0");
				listByOrgId.add(station);
			}else {
				throw new ServiceException("权限不足");
			}
		}
		//如果选择了org 角色  机构id为orgId
		if ("org".equals(type)){
			if (!"station".equals(user.getType())) {
				station.setName("本机构");
				station.setId("0");
				listByOrgId.add(station);
			}else {
				throw new ServiceException("权限不足");
			}
		}
		//如果选择了机构或者服务站角色  机构id
		if ("station".equals(type)) {
			serviceStation.getSqlMap().put("dsf", BaseService.dataStationFilterAddUser(serviceStation , "a"));
			listByOrgId = basicServiceStationDao.getServiceStationList(serviceStation);
		}
		return listByOrgId;

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

	// 查询服务站的员工数量
	public int getCount(BasicServiceStation station) {
		return basicServiceStationDao.getCount(station);
	}

	// 更新服务站信息
	@Transactional(readOnly = false)
	public void update(BasicServiceStation serviceStation) {
		basicServiceStationDao.update(serviceStation);
	}

	@Transactional(readOnly = false)
	public Result saveStore(BasicServiceStation station) {
		if (StringUtils.isBlank(station.getId())) {
			return new FailResult("id 不能为空");
		}
		/*if (!(station.getStoreList().size() > 0)) {
			return new FailResult("请选择门店");
		}*/
		serviceStoreDao.deletebyStation(station);
		if (station.getStoreList().size()>0) {//有被选中的门店时做再做添加操作
			serviceStoreDao.saveStationStore(station);
		}
		return new SuccResult("保存成功");
	}

	public List<User> getUserListByStationId(BasicServiceStation serviceStation) {
		return dao.getUserListByStationId(serviceStation);
	}

	// add by WYR同一机构下的服务站名称应不可重复
	public int checkRepeatName(String name, String orgId) {
		return basicServiceStationDao.checkRepeatName(name, orgId);
	}

	// add by WYR编辑时同一机构下的服务站名称应不可重复
	public int checkRepeatNameUpdate(String name, String orgId, String id) {
		return basicServiceStationDao.checkRepeatNameUpdate(name, orgId, id);
	}

	public int getCountTech(BasicServiceStation serviceStation) {
		return basicServiceStationDao.getCountTech(serviceStation);
	}
}