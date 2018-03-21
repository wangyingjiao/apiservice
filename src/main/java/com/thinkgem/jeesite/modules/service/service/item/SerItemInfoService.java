/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicGasqEshop;
import com.thinkgem.jeesite.modules.service.entity.basic.BasicOrganization;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodityEshop;
import com.thinkgem.jeesite.modules.service.entity.sort.SerSortInfo;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

import com.thinkgem.jeesite.open.send.OpenSendUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;

/**
 * 服务项目Service
 * 
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerItemInfoService extends CrudService<SerItemInfoDao, SerItemInfo> {

	@Autowired
	SerItemInfoDao serItemInfoDao;

	@Autowired
	SerItemCommodityDao serItemCommodityDao;
	@Autowired
	SerItemCommodityService serItemCommodityService;

	public SerItemInfo get(String id) {
		return super.get(id);
	}

	/**
	 * 保存
	 * 
	 * @param serItemInfo
	 */
	@Transactional(readOnly = false)
	public List<SerItemInfo> getByName(SerItemInfo serItemInfo) {
		return serItemInfoDao.getByName(serItemInfo);
	}

	@Transactional(readOnly = false)
	public int saveItem(SerItemInfo serItemInfo) {
		try {
			List<String> pictures = serItemInfo.getPictures();
			if (null != pictures) {
                String picture = JsonMapper.toJsonString(pictures);
                serItemInfo.setPicture(picture);
            }
			// add by wyr 保存服务项目图文详情 saveData方法里的 pictureDetails属性
			List<String> pictureDetails = serItemInfo.getPictureDetails();
			if (null != pictureDetails) {
                String pictureDetail = JsonMapper.toJsonString(pictureDetails);
                serItemInfo.setPictureDetail(pictureDetail);
            }
			serItemInfo.preUpdate();

			// add by wyr编辑项目服务需要获取当前的机构id
			User user = UserUtils.getUser();
			serItemInfo.setOrgId(user.getOrganization().getId());

			List<SerItemCommodity> commoditys = serItemInfo.getCommoditys();
			/*
             * if (StringUtils.isNotBlank(serItemInfo.getId())) { //删除商品信息
             * //serItemCommodityDao.delSerItemCommodity(serItemInfo);
             *
             * //只删除前台删除的数据，其余商品新增或者编辑 List<SerItemCommodity> commodityListOld =
             * serItemCommodityDao.findListByItemId(serItemInfo);
             * if(commodityListOld != null){ commodityListOld.removeAll(commoditys);
             * if(commodityListOld.size() != 0){ for (SerItemCommodity commodityDel
             * : commodityListOld) { serItemCommodityDao.delete(commodityDel); } } }
             * }
             */

			List<SerItemCommodity> sendGoodsList = new ArrayList<>();

			super.save(serItemInfo);
			if (commoditys != null) {
                // 批量插入商品信息
                for (SerItemCommodity commodity : commoditys) {
					commodity.setItemId(serItemInfo.getId());
					commodity.setSortId(serItemInfo.getSortId());
					commodity.setMinPurchase(commodity.getMinPurchase() == 0 ? 1 : commodity.getMinPurchase());// DEFAULT
					commodity.setStartPerNum(commodity.getStartPerNum() == 0 ? 1 : commodity.getStartPerNum());// DEFAULT
					commodity.setCappingPerNum(commodity.getCappingPerNum() == 0 ? 30 : commodity.getCappingPerNum());// DEFAULT
					serItemCommodityService.save(commodity);

					if (commodity.getId() != null && !commodity.getId().equals("")) {

						List<SerItemCommodityEshop> siceList1 = serItemInfoDao.getEshopGoods(commodity);
						List<SerItemCommodityEshop> siceList2 = new ArrayList<SerItemCommodityEshop>();
						for (SerItemCommodityEshop sice : siceList1) {
							int count = serItemInfoDao.getEshop(sice);
							if (count > 0) {
								siceList2.add(sice);
							}
						}

						if (siceList2.size() > 0) {

							// 对接商品信息
							SerItemCommodity sendGoods = new SerItemCommodity();
							sendGoods.setName(commodity.getName());// 商品名称格式：项目名称（商品名）
							sendGoods.setPrice(commodity.getPrice());// 商品价格
							sendGoods.setUnit(commodity.getUnit());// 商品单位格式：次/个/间
							//sendGoods.setJointGoodsCode("");
                    /*if (StringUtils.isNotBlank(commodity.getId())) {
                        SerItemCommodity commodityForJoin = serItemCommodityService.get(commodity.getId());
                        if (commodityForJoin != null && StringUtils.isNotEmpty(commodityForJoin.getJointGoodsCode())) {
                            sendGoods.setJointGoodsCode(commodityForJoin.getJointGoodsCode());
                        }
                    }*/
							sendGoods.setSelfCode(
									serItemInfo.getSortId() + Global.getConfig("openSendPath_goods_split") + commodity.getId()); // 自营平台商品code
							// ID
							sendGoods.setMinPurchase(commodity.getMinPurchase());// 最小购买数量，起购数量
							sendGoods.setCommodityEshops(siceList2);
							sendGoodsList.add(sendGoods);

						}
					}
				}
            }

		serItemInfo = dao.get(serItemInfo);
		String pictureDetail = serItemInfo.getPictureDetail();
		if (null != pictureDetail) {
			List<String> pictureDetailsa = (List<String>) JsonMapper.fromJsonString(pictureDetail, ArrayList.class);
			serItemInfo.setPictureDetails(pictureDetailsa);
		}
		String picture = serItemInfo.getPicture();
		if (null != picture) {
			List<String> pictures1 = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
			serItemInfo.setPictures(pictures1);
		}

		// 对接项目信息
		SerItemInfo sendItem = new SerItemInfo();
		sendItem.setPictures(serItemInfo.getPictures());
		sendItem.setPictureDetails(serItemInfo.getPictureDetails());
		sendItem.setName(serItemInfo.getName());
		sendItem.setTags(serItemInfo.getTags());// 系统标签格式：系统标签1,系统标签2,系统标签3,
		sendItem.setCusTags(serItemInfo.getCusTags());// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
		// sendItem.setSale(serItemInfo.getSale());//上架 下架 on off
		sendItem.setCommoditys(sendGoodsList);
		OpenSendUtil.updateJointGoodsCode(sendItem);
		/*String jointEshopCode = "";
		BasicOrganization organization = dao.getBasicOrganizationByOrgId(serItemInfo);
		if (organization != null) {
			jointEshopCode = organization.getJointEshopCode();
		}*/

		/*HashMap<String, Object> map = new HashMap<>();
		map.put("info", sendItem);
		map.put("jointEshopCode", jointEshopCode);
		map.put("item", serItemInfo);
		return map;*/
		return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<SerItemInfo> findList(SerItemInfo serItemInfo) {
		return super.findList(serItemInfo);
	}

	public Page<SerItemInfo> findPage(Page<SerItemInfo> page, SerItemInfo serItemInfo) {
		//add by wyr优化服务项目查询条件=======================================start
		List<SerItemInfo> list = null;
		Page<SerItemInfo> pageResult = null;
		if (null != serItemInfo.getSortIdandGoodsId()) {// 对接编码查询时，根据商品id获取到对应的服务项目，再获取到本服务项目下的所有商品集合
			String goodsId = "";
			String ids = serItemInfo.getSortIdandGoodsId();
			if (ids.contains("_")) {
				goodsId = ids.substring(ids.indexOf("_") + 1);// 截取到商品id编号
				serItemInfo.setSortIdandGoodsId(goodsId);
			}
			serItemInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
			pageResult = super.findPage(page, serItemInfo);
			list = pageResult.getList();
			/*if (list.size() != 0 && null != list) {
				for (SerItemInfo itemInfo : list) {
					List<SerItemCommodity> findListByItemId = serItemCommodityDao.findListByItemId(itemInfo);
					itemInfo.setCommoditys(findListByItemId);
				}
			}*/
		} else {
			serItemInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
			pageResult = super.findPage(page, serItemInfo);
			list = pageResult.getList();
		}
		if (list.size() != 0 && null != list) {//返回查询条件对应的服务分类的商品集合
			for (SerItemInfo itemInfo : list) {
				List<SerItemCommodity> findListByItemId = serItemCommodityDao.findListByItemId(itemInfo);
				for (SerItemCommodity sic : findListByItemId){
					List<SerItemCommodityEshop> siceList = serItemCommodityDao.getEshopGoods(sic);
					if (siceList.size()>0){
					    sic.setJointEshopFlag("yes");
                    }else {
					    sic.setJointEshopFlag("no");
                    }
				}
				itemInfo.setCommoditys(findListByItemId);
			}
		}
		if (null != list) {
			for (SerItemInfo entity : list) {
				String picture = entity.getPicture();
				if (null != picture) {
					List<String> pictures = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
					entity.setPictures(pictures);
				}
				if (entity.getJointEshopCode() != null && !entity.getJointEshopCode().equals("")) {
					List<SerItemCommodity> scy = entity.getCommoditys();
					if (scy != null && scy.size() > 0) {
						for (SerItemCommodity com : scy) {
							if (com.getJointGoodsCode() == null) {
								entity.setFlag("no");
								break;
							}
						}
					}
					if (entity.getFlag().equals("yes")) {
						if (entity.getJointStatus().equals("no")) {
							entity.setFlag("no");
						}
					}
				}
			}

		}
		return pageResult;
		//优化服务项目查询条件=======================================end
		
		// add by wyr 对接编号(商品id编号)查询赋值
		/*
		 * if (null != serItemInfo.getSortIdandGoodsId()) { String goodsId = "";
		 * String ids = serItemInfo.getSortIdandGoodsId(); if
		 * (ids.contains("_")) { goodsId = ids.substring(ids.indexOf("_") +
		 * 1);// 截取到商品id编号 serItemInfo.setSortIdandGoodsId(goodsId); } }
		 * serItemInfo.getSqlMap().put("dsf",
		 * dataRoleFilter(UserUtils.getUser(), "a")); Page<SerItemInfo>
		 * pageResult = super.findPage(page, serItemInfo); List<SerItemInfo>
		 * list = pageResult.getList(); if (null != list) { for (SerItemInfo
		 * entity : list) { String picture = entity.getPicture(); if (null !=
		 * picture) { List<String> pictures = (List<String>)
		 * JsonMapper.fromJsonString(picture, ArrayList.class);
		 * entity.setPictures(pictures); } if (entity.getJointEshopCode() !=
		 * null && !entity.getJointEshopCode().equals("")) {
		 * List<SerItemCommodity> scy = entity.getCommoditys(); if (scy != null
		 * && scy.size() > 0) { for (SerItemCommodity com : scy) { if
		 * (com.getJointGoodsCode() == null) { entity.setFlag("no"); break; } }
		 * } if (entity.getFlag().equals("yes")) { if
		 * (entity.getJointStatus().equals("no")) { entity.setFlag("no"); } } }
		 * }
		 * 
		 * } return pageResult;
		 */
	}

	/**
	 * 根据ID获取服务项目
	 * 
	 * @param id
	 * @return
	 */
	public SerItemInfo getData(String id) {
		SerItemInfo serItemInfo = super.get(id);

		List<SerItemCommodity> commoditys = serItemCommodityDao.findListByItemId(serItemInfo);
		serItemInfo.setCommoditys(commoditys);

		return serItemInfo;
	}

	@Transactional(readOnly = false)
	public void delete(SerItemInfo serItemInfo) {
		// 删除商品信息
		List<SerItemCommodity> commoditys = dao.getSerItemCommoditys(serItemInfo);
		for (SerItemCommodity commodity : commoditys) {
			serItemCommodityService.delete(commodity);
		}
		super.delete(serItemInfo);
	}

	@Transactional(readOnly = false)
	public void deleteSerItemInfo(SerItemInfo serItemInfo) {
		super.delete(serItemInfo);
	}

	/**
	 * 检查服务项目名是否重复
	 * 
	 * @param serItemInfo
	 * @return
	 */
	public int checkDataName(SerItemInfo serItemInfo) {
		return dao.checkDataName(serItemInfo);
	}
/*
	@Transactional(readOnly = false)
	public HashMap<String, Object> updateSerItemPicNum(SerItemInfo serItemInfo) {
		List<String> pictureDetails = serItemInfo.getPictureDetails();
		if (null != pictureDetails) {
			String pictureDetail = JsonMapper.toJsonString(pictureDetails);
			serItemInfo.setPictureDetail(pictureDetail);
		}
		serItemInfo.preUpdate();
		dao.updateSerItemPicNum(serItemInfo);

		serItemInfo = dao.get(serItemInfo);
		// 对接商品信息
		String jointEshopCode = "";
		BasicOrganization organization = dao.getBasicOrganizationByOrgId(serItemInfo);
		if (organization != null) {
			jointEshopCode = organization.getJointEshopCode();
		}

		String pictureDetail = serItemInfo.getPictureDetail();
		if (null != pictureDetail) {
			List<String> pictureDetailsa = (List<String>) JsonMapper.fromJsonString(pictureDetail, ArrayList.class);
			serItemInfo.setPictureDetails(pictureDetailsa);
		}
		String picture = serItemInfo.getPicture();
		if (null != picture) {
			List<String> pictures = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
			serItemInfo.setPictures(pictures);
		}

		HashMap<String, Object> map = new HashMap<>();
		map.put("jointEshopCode", jointEshopCode);

		if (StringUtils.isNotEmpty(jointEshopCode)) {

			List<SerItemCommodity> commoditys = serItemCommodityDao.findListByItemId(serItemInfo);

			List<SerItemCommodity> sendGoodsList = new ArrayList<>();
			if (commoditys != null) {
				// 批量插入商品信息
				for (SerItemCommodity commodity : commoditys) {
					// 对接商品信息
					SerItemCommodity sendGoods = new SerItemCommodity();
					sendGoods.setName(commodity.getName());// 商品名称格式：项目名称（商品名）
					sendGoods.setPrice(commodity.getPrice());// 商品价格
					sendGoods.setUnit(commodity.getUnit());// 商品单位格式：次/个/间
					sendGoods.setJointGoodsCode("");
					if (StringUtils.isNotBlank(commodity.getId())) {
						SerItemCommodity commodityForJoin = serItemCommodityService.get(commodity.getId());
						if (commodityForJoin != null && StringUtils.isNotEmpty(commodityForJoin.getJointGoodsCode())) {
							sendGoods.setJointGoodsCode(commodityForJoin.getJointGoodsCode());
						}
					}
					sendGoods.setSelfCode(
							serItemInfo.getSortId() + Global.getConfig("openSendPath_goods_split") + commodity.getId()); // 自营平台商品code
																															// ID
					sendGoods.setMinPurchase(commodity.getMinPurchase());// 最小购买数量，起购数量
					sendGoodsList.add(sendGoods);
				}
			}

			// 对接项目信息
			SerItemInfo sendItem = new SerItemInfo();
			sendItem.setPictures(serItemInfo.getPictures());
			sendItem.setPictureDetails(serItemInfo.getPictureDetails());
			sendItem.setName(serItemInfo.getName());
			sendItem.setTags(serItemInfo.getTags());// 系统标签格式：系统标签1,系统标签2,系统标签3,
			sendItem.setCusTags(serItemInfo.getCusTags());// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
			sendItem.setSale(serItemInfo.getSale());// 上架 下架 on off
			sendItem.setCommoditys(sendGoodsList);

			map.put("info", sendItem);
			map.put("item", serItemInfo);
		}
		return map;
	}*/

	public SerItemInfo getSerItemInfoPic(SerItemInfo serItemInfo) {
		return dao.getSerItemInfoPic(serItemInfo);
	}

	public List<Dict> getSerSortInfoList(SerItemInfo serItemInfo) {
		return dao.getSerSortInfoList(serItemInfo);
	}

	@Transactional(readOnly = false)
	public void updateCommodityJointCode(SerItemCommodity goods) {
		serItemCommodityDao.updateJointGoodsCode(goods);
	}

	/*public HashMap<String, Object> sendItemData(SerItemInfo serItemInfo) {
		serItemInfo = dao.get(serItemInfo);
		// 对接商品信息
		String jointEshopCode = "";
		BasicOrganization organization = dao.getBasicOrganizationByOrgId(serItemInfo);
		if (organization != null) {
			jointEshopCode = organization.getJointEshopCode();
		}

		String pictureDetail = serItemInfo.getPictureDetail();
		if (null != pictureDetail) {
			List<String> pictureDetailsa = (List<String>) JsonMapper.fromJsonString(pictureDetail, ArrayList.class);
			serItemInfo.setPictureDetails(pictureDetailsa);
		}
		String picture = serItemInfo.getPicture();
		if (null != picture) {
			List<String> pictures = (List<String>) JsonMapper.fromJsonString(picture, ArrayList.class);
			serItemInfo.setPictures(pictures);
		}

		HashMap<String, Object> map = new HashMap<>();
		map.put("jointEshopCode", jointEshopCode);

		if (StringUtils.isNotEmpty(jointEshopCode)) {

			List<SerItemCommodity> commoditys = serItemCommodityDao.findListByItemId(serItemInfo);

			List<SerItemCommodity> sendGoodsList = new ArrayList<>();
			if (commoditys != null) {
				// 批量插入商品信息
				for (SerItemCommodity commodity : commoditys) {
					// 对接商品信息
					SerItemCommodity sendGoods = new SerItemCommodity();
					sendGoods.setName(commodity.getName());// 商品名称格式：项目名称（商品名）
					sendGoods.setPrice(commodity.getPrice());// 商品价格
					sendGoods.setUnit(commodity.getUnit());// 商品单位格式：次/个/间
					sendGoods.setJointGoodsCode("");
					if (StringUtils.isNotBlank(commodity.getId())) {
						SerItemCommodity commodityForJoin = serItemCommodityService.get(commodity.getId());
						if (commodityForJoin != null && StringUtils.isNotEmpty(commodityForJoin.getJointGoodsCode())) {
							sendGoods.setJointGoodsCode(commodityForJoin.getJointGoodsCode());
						}
					}
					sendGoods.setSelfCode(
							serItemInfo.getSortId() + Global.getConfig("openSendPath_goods_split") + commodity.getId()); // 自营平台商品code
																															// ID
					sendGoods.setMinPurchase(commodity.getMinPurchase());// 最小购买数量，起购数量
					sendGoodsList.add(sendGoods);
				}
			}

			// 对接项目信息
			SerItemInfo sendItem = new SerItemInfo();
			sendItem.setPictures(serItemInfo.getPictures());
			sendItem.setPictureDetails(serItemInfo.getPictureDetails());
			sendItem.setName(serItemInfo.getName());
			sendItem.setTags(serItemInfo.getTags());// 系统标签格式：系统标签1,系统标签2,系统标签3,
			sendItem.setCusTags(serItemInfo.getCusTags());// 自定义标签格式：自定义标签1,自定义标签2,自定义标签3
			sendItem.setSale(serItemInfo.getSale());// 上架 下架 on off
			sendItem.setCommoditys(sendGoodsList);

			map.put("info", sendItem);
			map.put("item", serItemInfo);
		}
		return map;
	}*/

	@Transactional(readOnly = false)
	public void updateJointStatus(SerItemInfo serItemInfo) {
		dao.updateJointStatus(serItemInfo);
	}

	/**
	 * 单个删除商品时，取得对接信息
	 * 
	 * @param serItemCommodity
	 * @return
	 */
	/*public HashMap<String, Object> getDeleteGoodsSendInfo(SerItemCommodity serItemCommodity) {
		SerItemInfo serItemInfo = dao.getItemInfoByCommodityId(serItemCommodity);
		// 对接商品信息
		String jointEshopCode = "";
		BasicOrganization organization = dao.getBasicOrganizationByOrgId(serItemInfo);
		if (organization != null) {
			jointEshopCode = organization.getJointEshopCode();
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("jointEshopCode", jointEshopCode);

		if (StringUtils.isNotEmpty(jointEshopCode)) {
			List<SerItemCommodity> sendGoodsList = new ArrayList<>();
			SerItemCommodity sendGoods = new SerItemCommodity();
			if (StringUtils.isNotBlank(serItemCommodity.getId())) {
				SerItemCommodity commodityForJoin = serItemCommodityService.get(serItemCommodity.getId());
				if (commodityForJoin != null && StringUtils.isNotEmpty(commodityForJoin.getJointGoodsCode())) {
					sendGoods.setJointGoodsCode(commodityForJoin.getJointGoodsCode());
					sendGoodsList.add(sendGoods);
				}
			}

			// 对接项目信息
			SerItemInfo sendItem = new SerItemInfo();
			sendItem.setCommoditys(sendGoodsList);

			map.put("info", sendItem);
		}
		return map;
	}*/

	@Transactional(readOnly = false)
	public void deleteGoodsInfo(SerItemCommodity serItemCommodity) {
		serItemCommodityDao.delete(serItemCommodity);
	}

	/*public HashMap<String, Object> getDeleteGoodsSendList(SerItemInfo serItemInfo) {
		serItemInfo = dao.get(serItemInfo);
		// 对接商品信息
		String jointEshopCode = "";
		BasicOrganization organization = dao.getBasicOrganizationByOrgId(serItemInfo);
		if (organization != null) {
			jointEshopCode = organization.getJointEshopCode();
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("jointEshopCode", jointEshopCode);

		if (StringUtils.isNotEmpty(jointEshopCode)) {
			List<SerItemCommodity> commoditys = serItemCommodityDao.findListByItemId(serItemInfo);
			List<SerItemCommodity> sendGoodsList = new ArrayList<>();
			List<SerItemCommodity> noSendGoodsList = new ArrayList<>();
			if (commoditys != null) {
				// 批量插入商品信息
				for (SerItemCommodity commodity : commoditys) {
					// 对接商品信息
					SerItemCommodity sendGoods = new SerItemCommodity();
					SerItemCommodity noSendGoods = new SerItemCommodity();
					if (StringUtils.isNotBlank(commodity.getId())) {
						SerItemCommodity commodityForJoin = serItemCommodityService.get(commodity.getId());
						if (commodityForJoin != null && StringUtils.isNotEmpty(commodityForJoin.getJointGoodsCode())) {
							sendGoods.setJointGoodsCode(commodityForJoin.getJointGoodsCode());
							sendGoods.setId(commodity.getId());
							sendGoods.setName(commodity.getName());
							sendGoodsList.add(sendGoods);
						} else {
							noSendGoods.setId(commodity.getId());
							noSendGoods.setName(commodity.getName());
							noSendGoodsList.add(noSendGoods);
						}
					}
				}
			}

			// 对接项目信息
			SerItemInfo sendItem = new SerItemInfo();
			sendItem.setCommoditys(sendGoodsList);

			map.put("info", sendItem);
			map.put("item", serItemInfo);
			map.put("noSendGoodsList", noSendGoodsList);
		}
		return map;
	}*/

	public List<SerItemCommodity> getListByInfoId(SerItemInfo info) {
		return serItemCommodityDao.getSerItemCommodityByItem(info);
	}

	public List<SerItemCommodityEshop> getEshopGoodsList(SerItemCommodity serItemCommodity) {
		return serItemCommodityDao.getEshopGoodsList(serItemCommodity);
	}

    public SerItemCommodity getEshopGoods(SerItemCommodity serItemCommodity) {
        SerItemCommodity sc = serItemCommodityDao.getGoods(serItemCommodity);
        sc.setJointCode(sc.getSortId()+Global.getConfig("openSendPath_goods_split")+sc.getId());

        List<SerItemCommodityEshop> siceList = serItemCommodityDao.getEshopGoods(serItemCommodity);
        sc.setCommodityEshops(siceList);

        return sc;
    }

	 //下拉列表 根据登录用户的机构id查询出对应E店名称
	public List<BasicGasqEshop> getGoodsCode(String orgId) {
		//根据机构id查询出 E店名称的集合
		List<BasicGasqEshop> goodsCode = serItemCommodityDao.getGoodsCode(orgId);
		return goodsCode;
	}

	//据id 获取服务项目商品信息
	@Transactional(readOnly = false)
	public SerItemInfo getGoodEshop(SerItemCommodity serItemCommodity) {
		//goodIds中存的是SerItemCommodityEshop的id
		List<String> goodIds = serItemCommodity.getGoodIds();
		List<String> list=new ArrayList<String>();
		SerItemInfo serItemInfo=new SerItemInfo();
		//获取eshopCode 存入eshop对象中再放入商品属性中
		String eshopCode = serItemCommodity.getEshopCode();
		List<SerItemCommodityEshop> commodityEshops =new ArrayList<SerItemCommodityEshop>();
		List<SerItemCommodity> commoditys=new ArrayList<SerItemCommodity>();
		//获取商品id的集合
		if (goodIds != null && goodIds.size()>0) {
			for (String s : goodIds) {
				SerItemCommodityEshop serItemCommodityEshop =new SerItemCommodityEshop();
				serItemCommodityEshop.setId(s);
				//去数据库中查询对应的SerItemCommodityEshop表
				SerItemCommodityEshop goodEshop = serItemCommodityDao.getGoodEshop(serItemCommodityEshop);
				//如果jointGoodsCode为空 将状态改为no
				if (goodEshop!=null) {
					if (StringUtils.isBlank(goodEshop.getJointGoodsCode())) {
						goodEshop.setEnabledStatus("no");
						serItemCommodityDao.updateEshop(goodEshop);
					} else {
						commodityEshops.add(goodEshop);
						//根据商品的id去获取商品信息  加入到服务项目中
						SerItemCommodity serItemCommodity1 = serItemCommodityDao.get(goodEshop.getGoodsId());
						serItemCommodity1.setSelfCode(serItemCommodity1.getSortId()+"_"+serItemCommodity1.getId());
						serItemCommodity1.setCommodityEshops(commodityEshops);
						commoditys.add(serItemCommodity1);
					}
				}
			}
			serItemInfo.setCommoditys(commoditys);
		}
		return serItemInfo;
	}


	//根据E店id 分类 商品名 对接code 查询出对应E店下的商品信息
	public Page<SerItemCommodityEshop> getGoodsList(Page<SerItemCommodityEshop> page,SerItemCommodityEshop serItemCommodityEshop) {
		//根据机构id查询出 E店名称的集合
		serItemCommodityEshop.setPage(page);
		//将对接编码通过_截取 赋值给商品id
		String selfCode = serItemCommodityEshop.getSelfCode();
		if (StringUtils.isNotBlank(selfCode)) {
			String[] split = selfCode.split("_");
			if (split.length>1) {
				String goodsId = split[1];
				serItemCommodityEshop.setGoodsId(goodsId);
			}
		}
		serItemCommodityEshop.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
		List<SerItemCommodityEshop> goodsCode = serItemCommodityDao.getGoodsList(serItemCommodityEshop);
		if (goodsCode !=null && goodsCode.size()>0) {
			for (SerItemCommodityEshop shop : goodsCode) {
				//根据商品id获取商品
				SerItemCommodity serItemCommodity = serItemCommodityDao.getSerItemCommodity(shop.getGoodsId());
				if (serItemCommodity == null) {
					throw  new ServiceException("错误，没有商品");
				}
                //拼接 商品名称
                shop.setNewName(shop.getItemName() + "(" + serItemCommodity.getName() + ")");
                //拼接 价格/单位
                shop.setUnivalence(serItemCommodity.getPrice() + "/" + serItemCommodity.getUnit());
                //拼接对接编码
                shop.setSelfCode(serItemCommodity.getSortId() + "_" + serItemCommodity.getId());
			}
		}
		page.setList(goodsCode);
		return page;
	}

}