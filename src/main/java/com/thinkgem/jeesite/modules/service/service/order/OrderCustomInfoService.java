/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import com.thinkgem.jeesite.modules.service.dao.order.OrderCustomAddressDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomAddress;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderCustomInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderCustomInfoDao;

/**
 * 客户信息Service
 * @author a
 * @version 2017-11-23
 */
@Service
@Transactional(readOnly = true)
public class OrderCustomInfoService extends CrudService<OrderCustomInfoDao, OrderCustomInfo> {
	@Autowired
	private OrderCustomInfoDao orderCustomInfoDao;
	@Autowired
	private OrderCustomAddressDao orderCustomAddressDao;

	public OrderCustomInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderCustomInfo> findList(OrderCustomInfo orderCustomInfo) {
		return super.findList(orderCustomInfo);
	}
	
	public Page<OrderCustomInfo> findPage(Page<OrderCustomInfo> page, OrderCustomInfo orderCustomInfo) {
		orderCustomInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
		return super.findPage(page, orderCustomInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(OrderCustomInfo orderCustomInfo) {
		super.save(orderCustomInfo);
	}

	@Transactional(readOnly = false)
	public void delete(OrderCustomInfo orderCustomInfo) {
		super.delete(orderCustomInfo);
	}

    public List<BasicOrganization> findOrganizationList() {
		BasicOrganization organization = new BasicOrganization();
		organization.getSqlMap().put("dsf", dataOrganFilter(UserUtils.getUser(), "a"));
		return dao.findOrganizationList(organization);
    }

    public OrderCustomInfo findCustomInfo(OrderCustomInfo orderCustomInfo) {
		return dao.findCustomInfo(orderCustomInfo);
    }

	public List<ServiceStation> getStationsByOrgId(String orgId) {
		return dao.getStationsByOrgId(orgId);
	}


	public List<OrderCustomInfo> findCusList(OrderCustomInfo orderCustomInfo) {
		return orderCustomInfoDao.findCusList(orderCustomInfo);
	}

	public OrderCustomInfo formData(OrderCustomInfo orderCustomInfo) {
		OrderCustomInfo info = super.get(orderCustomInfo.getId());
		return info;
	}

    public List<OrderCustomAddress> listCustomAddress(OrderCustomAddress customAddress) {
		List<OrderCustomAddress> list = orderCustomAddressDao.findList(customAddress);
		return list;
    }

	public void saveDataAddress(OrderCustomAddress customAddress) {
		List<OrderCustomAddress> list = orderCustomAddressDao.findList(customAddress);
		if(list!=null && list.size()>0){//默认将第一个添加的地址设为默认地址
			customAddress.setDefaultType("no");
		}else{
			customAddress.setDefaultType("yes");
		}
		customAddress.preInsert();
		orderCustomAddressDao.insert(customAddress);
	}

	public void upDataAddress(OrderCustomAddress customAddress) {
		customAddress.preUpdate();
		orderCustomAddressDao.update(customAddress);
	}

	public void deleteDataAddress(OrderCustomAddress customAddress) {
		orderCustomAddressDao.delete(customAddress);
	}

	public OrderCustomAddress formDataAddress(OrderCustomAddress customAddress) {
		return orderCustomAddressDao.get(customAddress);
	}

	public void setDefaultAddress(OrderCustomAddress customAddress) {
		customAddress.preUpdate();
		orderCustomAddressDao.updateDefaultNoByCustomer(customAddress);
		orderCustomAddressDao.updateDefaultYesById(customAddress);
	}
}