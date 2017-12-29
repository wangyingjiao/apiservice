/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.order;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.order.OrderDispatch;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoods;
import com.thinkgem.jeesite.modules.service.entity.order.OrderGoodsTypeHouse;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianWorkTime;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.order.OrderInfo;
import com.thinkgem.jeesite.modules.service.dao.order.OrderInfoDao;

/**
 * 子订单Service
 * @author a
 * @version 2017-12-26
 */
@Service
@Transactional(readOnly = true)
public class OrderInfoService extends CrudService<OrderInfoDao, OrderInfo> {

	public OrderInfo get(String id) {
		return super.get(id);
	}
	
	public List<OrderInfo> findList(OrderInfo orderInfo) {
		return super.findList(orderInfo);
	}

	public OrderInfo formData(OrderInfo info) {
		OrderInfo orderInfo = dao.formData(info);
		OrderGoods goodsInfo = new OrderGoods();
		List<OrderGoods> goodsInfoList = dao.getOrderGoodsList(info);    //服务信息
		if(goodsInfoList != null && goodsInfoList.size() != 0){
			goodsInfo.setServiceTime(goodsInfoList.get(0).getServiceTime());
			goodsInfo.setItemId(goodsInfoList.get(0).getItemId());
			goodsInfo.setItemName(goodsInfoList.get(0).getItemName());
			goodsInfo.setGoods(goodsInfoList);
		}

		List<OrderDispatch> techList = dao.getOrderDispatchList(info); //技师List
		orderInfo.setGoodsInfo(goodsInfo);
		orderInfo.setTechList(techList);
		return orderInfo;
	}

	public Page<OrderInfo> findPage(Page<OrderInfo> page, OrderInfo orderInfo) {
		orderInfo.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
		Page<OrderInfo> pageResult = super.findPage(page, orderInfo);
		return pageResult;
	}
	
	@Transactional(readOnly = false)
	public void save(OrderInfo orderInfo) {
		if(StringUtils.isBlank(orderInfo.getId())){
			//新增订单

		}


		super.save(orderInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(OrderInfo orderInfo) {
		super.delete(orderInfo);
	}

	/**
	 * 请求订单列表时返回查询条件服务机构下拉列表
	 * @return
	 */
	public List<BasicOrganization> findOrganizationList() {
		BasicOrganization organization = new BasicOrganization();
		organization.getSqlMap().put("dsf", dataOrganFilter(UserUtils.getUser(), "a"));
		return dao.findOrganizationList(organization);
	}

	/**
	 * 查看订单时返回可选时间
	 * @param orderInfo
	 * @return
	 */
    public List<ServiceTechnicianWorkTime> findServiceTimeList(OrderInfo orderInfo) {
		return dao.findServiceTimeList(orderInfo);
    }

	/**
	 * 取消订单
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public void cancelData(OrderInfo orderInfo) {
		dao.cancelData(orderInfo);
	}

	/**
	 * 更换时间
	 * @param orderInfo
	 */
	@Transactional(readOnly = false)
	public void saveTime(OrderInfo orderInfo) {
		//更新服务时间
		dao.saveTime(orderInfo);

		// order_dispatch tech status ==> no

		// insert order_dispatch new tech
	}

	public List<OrderGoods> editGoodsInit(OrderInfo orderInfo) {
		List<OrderGoods>  list = new ArrayList<OrderGoods>();
		List<OrderGoods> orderGoodsList = dao.getOrderGoodsList(orderInfo);    //订单已选商品信息
		List<OrderGoods> goodsList = dao.getGoodsList(orderInfo);    //服务项目下所有商品信息

		OrderGoods houseInfo = new OrderGoods();

		//得到除了居室以外的商品及选择信息
		if(goodsList!=null && goodsList.size()!=0){
			List<OrderGoodsTypeHouse> houses = new ArrayList<OrderGoodsTypeHouse>();
			for (OrderGoods goods : goodsList){//循环所有商品
				if("house".equals(goods.getGoodsType())){//按居室的商品
					OrderGoodsTypeHouse house = new OrderGoodsTypeHouse();
					house.setId(goods.getGoodsId());
					house.setName(goods.getGoodsName());
					houses.add(house);
				}else{
					if(orderGoodsList!=null && orderGoodsList.size()!=0){
						for (OrderGoods orderGoods : orderGoodsList){//循环订单选择商品
							if(goods.getGoodsId().equals(orderGoods.getGoodsId())){//同一个商品
								goods.setGoodsNum(orderGoods.getGoodsNum());
								goods.setGoodsChecked(true);
							}
						}
					}
					list.add(goods);
				}
			}
			//得到居室商品及选择信息
			for (OrderGoods orderGoods : orderGoodsList){//循环订单选择商品
				if("house".equals(orderGoods.getGoodsType())){//按居室的商品
					houseInfo.setItemId(orderGoods.getItemId());
					houseInfo.setItemName(orderGoods.getItemName());
					houseInfo.setGoodsId(orderGoods.getGoodsId());
					houseInfo.setGoodsName(orderGoods.getGoodsName());
					houseInfo.setPayPrice(orderGoods.getPayPrice());
					houseInfo.setMinPurchase(orderGoods.getMinPurchase());
					houseInfo.setGoodsType(orderGoods.getGoodsType());
					houseInfo.setGoodsNum(orderGoods.getGoodsNum());
					houseInfo.setGoodsChecked(true);
					houseInfo.setHouses(houses);
					list.add(houseInfo);
				}
			}
		}

		return list;
	}
}