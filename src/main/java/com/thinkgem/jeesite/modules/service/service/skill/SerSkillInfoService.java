/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillSortDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.dao.station.BasicServiceStationDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemCommodity;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillSort;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
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
	SerSkillSortDao serSkillSortDao;
	@Autowired
	SerSkillTechnicianDao serSkillTechnicianDao;

	@Autowired
	BasicServiceStationDao basicServiceStationDao;

//	@Autowired
//	SerSkillItemService serSkillItemService;
//	@Autowired
//	SerSkillTechnicianService serSkillTechnicianService;

	public SerSkillInfo get(String id) {
		return super.get(id);
	}


	public SerSkillInfo getByName(SerSkillInfo serSkillInfo){
		return serSkillInfoDao.getByName(serSkillInfo);
	}

	@Transactional(readOnly = false)
	public void save(SerSkillInfo serSkillInfo) {
		if (StringUtils.isNotBlank(serSkillInfo.getId())) {
			//删除技能分类
			serSkillSortDao.delSerSkillSortBySkill(serSkillInfo);
			//更新时，删除技师关系
			serSkillInfo.getSqlMap().put("ert", dataStationIdRoleFilter(UserUtils.getUser(), "a"));
			serSkillTechnicianDao.delSerSkillTechnicianBySkill(serSkillInfo);
		}
		super.save(serSkillInfo);
		//List<SerItemInfo> serItems = serSkillInfo.getItems();
		List<String> sortIds = serSkillInfo.getSortIds();
		List<SerSkillTechnician> technicians = serSkillInfo.getTechnicians();

		//批量插入类别技能
		for (String id:sortIds) {
			SerSkillSort sortInfo = new SerSkillSort();
			sortInfo.setSkillId(serSkillInfo.getId());
			sortInfo.setOrgId(serSkillInfo.getOrgId());
			sortInfo.setSortId(id);
			sortInfo.preInsert();
			serSkillSortDao.insert(sortInfo);
		}
		/*if(serItems != null) {
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
		}*/
		if(technicians != null) {
			//批量插入技师信息
			for (SerSkillTechnician technician : technicians) {
				technician.setSkillId(serSkillInfo.getId());
				technician.preInsert();
				serSkillTechnicianDao.insert(technician);
			}
		}
		//更新技师数量
		serSkillTechnicianDao.updateTechNum(serSkillInfo.getId());


	}

	public List<SerSkillInfo> findList(SerSkillInfo serSkillInfo) {
		return super.findList(serSkillInfo);
	}
	
	public Page<SerSkillInfo> findPage(Page<SerSkillInfo> page, SerSkillInfo serSkillInfo) {
		serSkillInfo.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
		return super.findPage(page, serSkillInfo);
	}

	public SerSkillInfo getData(String id) {
		SerSkillInfo serSkillInfo = super.get(id);
		//商品信息
	//	List<SerSkillItem> serItems = serSkillItemDao.getSerSkillItemBySkill(serSkillInfo);
	//	List<SerSkillItem> serGoods = serSkillItemDao.getSerSkillGoodsBySkill(serSkillInfo);
		//根据技能id获取技能分类的id集合
		SerSkillSort serSkillSort=new SerSkillSort();
		serSkillSort.setSkillId(id);
		List<SerSkillSort> list = serSkillSortDao.findList(serSkillSort);
		List<String> sortIds = new ArrayList<String>();
		for (SerSkillSort s:list){
			String sortId = s.getSortId();
			sortIds.add(sortId);
		}
		//	List<SerItemInfo> itemList = new ArrayList<SerItemInfo>();
		/*if(null != serItems){
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
		}*/
	//	serSkillInfo.setItems(itemList);
		serSkillInfo.setSortIds(sortIds);
		//技师关系
		serSkillInfo.getSqlMap().put("dsf", dataStationIdRoleFilter(UserUtils.getUser(), "a"));
		List<SerSkillTechnician> technicians = serSkillTechnicianDao.getSerSkillTechnicianBySkill(serSkillInfo);
		serSkillInfo.setTechnicians(technicians);

		return serSkillInfo;
	}

	@Transactional(readOnly = false)
	public void delete(SerSkillInfo serSkillInfo) {

		//删除技能分类
		serSkillSortDao.delSerSkillSortBySkill(serSkillInfo);
//		List<SerSkillItem> serItems = serSkillItemDao.getSerItems(serSkillInfo);
//		for(SerSkillItem serItem : serItems) {
//			serSkillItemService.delete(serItem);
//		}

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

	//根据权限查看服务列表
    public List<SerItemInfo> findSerPage(SerSkillInfo serInfo) {
		serInfo.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
		return serSkillInfoDao.choiceSerlist(serInfo);
    }
	//根据权限查看技师列表
	public List<SerSkillTechnician>  findTechnicianPage(SerSkillInfo technicianInfo) {
			technicianInfo.getSqlMap().put("dsf", dataStatioRoleFilter(UserUtils.getUser(), "a"));
		return serSkillInfoDao.choiceTechnicianlist(technicianInfo);
	}
	//根据权限获取对应的服务站
    public List<BasicServiceStation> getServiceStationList(BasicServiceStation station) {
		station.getSqlMap().put("dsf", dataStationFilter(UserUtils.getUser(), "a"));
		return basicServiceStationDao.getServiceStationList(station);
    }

	public int checkSkillSort(SerSkillInfo serSkillInfo) {
		return serSkillInfoDao.checkSkillSort(serSkillInfo);
	}
}