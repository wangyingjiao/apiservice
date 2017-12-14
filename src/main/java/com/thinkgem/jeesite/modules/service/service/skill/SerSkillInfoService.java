/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillInfoDao;

/**
 * 技能管理Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerSkillInfoService extends CrudService<SerSkillInfoDao, SerSkillInfo> {
	@Autowired
	SerSkillInfoDao serSkillInfoDao;
	@Autowired
	SerSkillItemDao serSkillItemDao;
	@Autowired
	SerSkillTechnicianDao serSkillTechnicianDao;

	@Autowired
	SerSkillItemService serSkillItemService;
	@Autowired
	SerSkillTechnicianService serSkillTechnicianService;

	public SerSkillInfo get(String id) {
		return super.get(id);
	}

	@Transactional(readOnly = false)
	public void save(SerSkillInfo serSkillInfo) {
		if (StringUtils.isNotBlank(serSkillInfo.getId())) {
			//删除商品信息
			serSkillItemDao.delSerSkillItemBySkill(serSkillInfo);
			//更新时，删除技师关系
			serSkillTechnicianDao.delSerSkillTechnicianBySkill(serSkillInfo);
		}
		List<SerItemInfo> serItems = serSkillInfo.getItems();
		List<SerSkillTechnician> technicians = serSkillInfo.getTechnicians();
		if(technicians != null){
			serSkillInfo.setTechNum(technicians.size());
		}else{
			serSkillInfo.setTechNum(0);
		}

		super.save(serSkillInfo);
		if(serItems != null) {
			//批量插入商品信息
			for (SerItemInfo item : serItems) {
				List<SerItemCommodity> commoditys = item.getCommoditys();
				if(null != commoditys){
					for(SerItemCommodity commodity : commoditys){
						SerSkillItem goods = new SerSkillItem();
						goods.setSkillId(serSkillInfo.getId());
						goods.setItemId(item.getId());
						goods.setGoodsId(commodity.getId());
						goods.preInsert();
						serSkillItemDao.insert(goods);
					}
				}
			}
		}
		if(technicians != null) {
			//批量插入技师信息
			for (SerSkillTechnician technician : technicians) {
				technician.setSkillId(serSkillInfo.getId());
				technician.preInsert();
				serSkillTechnicianDao.insert(technician);
			}
		}
	}

	public List<SerSkillInfo> findList(SerSkillInfo serSkillInfo) {
		return super.findList(serSkillInfo);
	}
	
	public Page<SerSkillInfo> findPage(Page<SerSkillInfo> page, SerSkillInfo serSkillInfo) {
		return super.findPage(page, serSkillInfo);
	}

	public SerSkillInfo getData(String id) {
		SerSkillInfo serSkillInfo = super.get(id);
		//商品信息
		List<SerSkillItem> serItems = serSkillItemDao.getSerSkillItemBySkill(serSkillInfo);
		List<SerSkillItem> serGoods = serSkillItemDao.getSerSkillGoodsBySkill(serSkillInfo);
		List<SerItemInfo> itemList = new ArrayList<SerItemInfo>();
		if(null != serItems){
			for(SerSkillItem item : serItems){
				SerItemInfo itemInfo = new SerItemInfo();
				itemInfo.setId(item.getItemId());
				itemInfo.setName(item.getItemName());
				List<SerItemCommodity> commodityList = new ArrayList<SerItemCommodity>();
				if(null != serGoods){
					for(SerSkillItem goods : serGoods){
						if(item.getItemId().equals(goods.getItemId())){
							SerItemCommodity commodityInfo = new SerItemCommodity();
							commodityInfo.setId(goods.getGoodsId());
							commodityInfo.setName(goods.getGoodsName());
							commodityList.add(commodityInfo);
						}
					}
				}
				itemInfo.setCommoditys(commodityList);
				itemList.add(itemInfo);
			}
		}
		serSkillInfo.setItems(itemList);

		//技师关系
		List<SerSkillTechnician> technicians = serSkillTechnicianDao.getSerSkillTechnicianBySkill(serSkillInfo);
		serSkillInfo.setTechnicians(technicians);

		return serSkillInfo;
	}

	@Transactional(readOnly = false)
	public void delete(SerSkillInfo serSkillInfo) {

		//删除商品信息
		List<SerSkillItem> serItems = serSkillItemDao.getSerItems(serSkillInfo);
		for(SerSkillItem serItem : serItems) {
			serSkillItemService.delete(serItem);
		}

		//更新时，删除技师关系
		serSkillTechnicianDao.delSerSkillTechnicianBySkill(serSkillInfo);

		super.delete(serSkillInfo);
	}


	/**
	 * 检查技能名是否重复
	 * @param serSkillInfo
	 * @return
	 */
	public int checkDataName(SerSkillInfo serSkillInfo) {
		return serSkillInfoDao.checkDataName(serSkillInfo);
	}

    public List<SerItemInfo> findSerPage(SerSkillInfo serInfo) {
		return serSkillInfoDao.choiceSerlist(serInfo);
    }

	public List<SerSkillTechnician>  findTechnicianPage(SerSkillInfo technicianInfo) {
		return serSkillInfoDao.choiceTechnicianlist(technicianInfo);
	}

    public List<BasicServiceStation> getServiceStationList() {
		return serSkillInfoDao.getServiceStationList();
    }
}