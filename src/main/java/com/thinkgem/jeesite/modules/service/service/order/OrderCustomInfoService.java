/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.List;

import com.thinkgem.jeesite.common.utils.MapUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
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

//		String station = "";
//		station = getStationId(orderCustomInfo.getAddrLatitude(),orderCustomInfo.getAddrLongitude());
		orderCustomInfo.setStationId(UserUtils.getUser().getStation().getId());//服务站ID

		super.save(orderCustomInfo);
	}

	private String getStationId(String addrLatitude, String addrLongitude) {

		MapUtils.GetDistance(29.490295,106.486654,29.490295,101.486654);
		return null;
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
}