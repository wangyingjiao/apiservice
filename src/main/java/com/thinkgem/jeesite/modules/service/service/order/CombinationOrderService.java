/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.service.dao.item.CombinationCommodityDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.dao.order.*;
import com.thinkgem.jeesite.modules.service.entity.item.CombinationCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.order.*;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    CombinationCommodityDao combinationCommodityDao;
	@Autowired
	FrequencyDao frequencyDao;
	@Autowired
	SerItemCommodityDao serItemCommodityDao;


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
        //计算预约次数 组合并拆单 没有order_combination_frequency order_combination_gasq
        if ("group_split_yes".equals(combinationById.getOrderType())) {
			List<OrderCombinationFrequencyInfo> frequencyList = frequencyDao.getFrequencyList(combinationOrderInfo);
			if (frequencyList != null && frequencyList.size() > 0) {
				for (OrderCombinationFrequencyInfo frequencyInfo : frequencyList) {
					frequencyInfo.setTimeArea(DateUtils.formatDate(frequencyInfo.getStartTime(),"HH:mm")+"-"+DateUtils.formatDate(frequencyInfo.getEndTime(),"HH:mm"));
				}
			}
			combinationById.setFreList(frequencyList);
			//根据masterId获取组合订单的订单集合
			List<OrderCombinationGasqInfo> listbyMasterId = orderCombinationGasqDao.getListbyMasterId(combinationOrderInfo);
			combinationById.setOrderCombinationGasqInfos(listbyMasterId);
            int bespeakNum = combinationById.getBespeakNum();
            int bespeakTotal = combinationById.getBespeakTotal();
            int surplusNum = 0;
            if (bespeakTotal > bespeakNum) {
                surplusNum = bespeakTotal - bespeakNum;
            }
            combinationById.setSurplusNum(surplusNum);
            BigDecimal serviceNum = new BigDecimal(combinationById.getServiceNum());
            BigDecimal multiply = serviceNum.multiply(new BigDecimal(combinationById.getServiceHour()));
            combinationById.setServiceAllHour(multiply.doubleValue());
        }else if ("group_split_no".equals(combinationById.getOrderType())){
			OrderInfo orderListbyMasterId = orderCombinationGasqDao.getOrderListbyMasterId(combinationOrderInfo);
			List<OrderInfo> orderInfos=new ArrayList<OrderInfo>();
			orderInfos.add(orderListbyMasterId);
			OrderCombinationGasqInfo gasqInfo=new OrderCombinationGasqInfo();
			gasqInfo.setOrderList(orderInfos);
			List<OrderCombinationGasqInfo> orderCombinationGasqInfos=new ArrayList<OrderCombinationGasqInfo>();
			orderCombinationGasqInfos.add(gasqInfo);
			combinationById.setOrderCombinationGasqInfos(orderCombinationGasqInfos);
		}
        //根据com表中goodId获取所有组合商品-子商品
		List<CombinationCommodity> listByGoodsId = combinationCommodityDao.findListByGoodsId(combinationById);
		List<CombinationCommodity> comGoods=new ArrayList<CombinationCommodity>();
        for (CombinationCommodity commodity:listByGoodsId){
			//根据单个子商品的id 获取名称集合
			CombinationCommodity comGood = combinationCommodityDao.getComGood(commodity);
			comGoods.add(comGood);
		}
		CombinationOrderInfo info=new CombinationOrderInfo();
		List<CombinationOrderInfo> list=new ArrayList<CombinationOrderInfo>();
		//name 数量 总计 商品集合
		info.setCombinationCommoditys(comGoods);
		info.setCombinationGoodsName(combinationById.getCombinationGoodsName());
		info.setCombinationGoodsNum(combinationById.getCombinationGoodsNum());
		//价格  单位
		SerItemCommodity goodsByGoodsId = serItemCommodityDao.getGoodsByGoodsId(combinationById);
		info.setUnit(goodsByGoodsId.getUnit());
		info.setPrice(goodsByGoodsId.getPrice());
		BigDecimal bigDecimal = new BigDecimal(combinationById.getCombinationGoodsNum());
		BigDecimal multiply = bigDecimal.multiply(goodsByGoodsId.getPrice());
		info.setSum(multiply);
		list.add(info);
		combinationById.setCombinationOrderInfos(list);
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
}