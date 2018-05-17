/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 组合订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class CombinationOrderService extends CrudService<CombinationOrderDao, CombinationOrderInfo> {

	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
    OrderCombinationGasqDao orderCombinationGasqDao;
	@Autowired
	CombinationOrderDao combinationOrderDao;
	@Autowired
	FrequencyDao frequencyDao;


	@Autowired
	private CombinationToolsService combinationToolsService;

	//组合订单列表
	public Page<CombinationOrderInfo> listDataCombination(Page<CombinationOrderInfo> page, CombinationOrderInfo combinationOrderInfo) {
		User user = UserUtils.getUser();
		combinationOrderInfo.getSqlMap().put("dsf", dataRoleFilterForOrder(user, "a"));
		combinationOrderInfo.setPage(page);
		List<CombinationOrderInfo> orderList = combinationOrderDao.listDataCombination(combinationOrderInfo);
		page.setList(orderList);
		return page;
	}

	//组合订单详情
	public CombinationOrderInfo getCombinationById(CombinationOrderInfo combinationOrderInfo) {
		CombinationOrderInfo combinationById = combinationOrderDao.getCombinationById(combinationOrderInfo);
		List<OrderCombinationFrequencyInfo> frequencyList = frequencyDao.getFrequencyList(combinationOrderInfo);
		combinationById.setFreList(frequencyList);
        List<OrderCombinationGasqInfo> listbyMasterId = orderCombinationGasqDao.getListbyMasterId(combinationOrderInfo);
        combinationById.setOrderCombinationGasqInfos(listbyMasterId);
        return combinationById;
	}










	/**
	 * 查看备注
	 * @param combinationOrderInfo
	 * @return
	 */
    public OrderInfo getOrderRemark(CombinationOrderInfo combinationOrderInfo) {
		OrderInfo orderInfo = combinationOrderDao.getOrderRemark(combinationOrderInfo);
		if(orderInfo == null){
			throw new ServiceException("未找到订单信息");
		}
		// 用户备注图片
		String customerRemarkPic = orderInfo.getCustomerRemarkPic();
		if(null != customerRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(customerRemarkPic,ArrayList.class);
			orderInfo.setCustomerRemarkPics(pictureDetails);
		}
		// 订单备注图片
		String orderRemarkPic = orderInfo.getOrderRemarkPic();
		if(null != orderRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(orderRemarkPic,ArrayList.class);
			orderInfo.setOrderRemarkPics(pictureDetails);
		}
		// 业务人员备注图片
		String businessRemarkPic = orderInfo.getBusinessRemarkPic();
		if(null != businessRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(businessRemarkPic,ArrayList.class);
			orderInfo.setBusinessRemarkPics(pictureDetails);
		}
		// 门店备注图片
		String shopRemarkPic = orderInfo.getShopRemarkPic();
		if(null != shopRemarkPic){
			List<String> pictureDetails = (List<String>) JsonMapper.fromJsonString(shopRemarkPic,ArrayList.class);
			orderInfo.setShopRemarkPics(pictureDetails);
		}

		return orderInfo;
    }

	public OrderInfo initCombinationOrderTech(CombinationOrderInfo combinationOrderInfo) {
		OrderInfo orderInfo = new OrderInfo();
		List<OrderDispatch> techList = combinationOrderDao.initCombinationOrderTech(combinationOrderInfo); //技师List
		if(techList == null || techList.size() == 0){
			throw new ServiceException("未找到技师信息");
		}
		orderInfo.setTechList(techList);

		return orderInfo;
	}

	public List<OrderDispatch> addCombinationOrderTech(CombinationOrderInfo combinationOrderInfo) {
		String serchTechName = combinationOrderInfo.getTechName();
		OrderInfo orderInfo = orderInfoDao.get(combinationOrderInfo.getOrderId());//当前订单
		orderInfo.setTechName(serchTechName);
		List<OrderDispatch> techList = combinationToolsService.listTechByGoodsAndTime(orderInfo);
		return techList;
	}

}