/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.skill;

import java.util.List;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillItemDao;
import com.thinkgem.jeesite.modules.service.dao.skill.SerSkillTechnicianDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
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
		List<SerSkillItem> serItems = serSkillInfo.getSerItems();
		List<SerSkillTechnician> technicians = serSkillInfo.getTechnicians();
		if(technicians != null){
			serSkillInfo.setTechnicianNum(technicians.size());
		}else{
			serSkillInfo.setTechnicianNum(0);
		}

		super.save(serSkillInfo);
		if(serItems != null) {
			//批量插入商品信息
			for (SerSkillItem item : serItems) {
				item.setSkillId(serSkillInfo.getId());
				item.setSkillName(serSkillInfo.getName());
				serSkillItemService.save(item);
			}
		}
		if(technicians != null) {
			//批量插入技师信息
			for (SerSkillTechnician technician : technicians) {
				technician.setSkillId(serSkillInfo.getId());
				technician.setSkillName(serSkillInfo.getName());
				technician.setDelFlag("0");
				serSkillTechnicianService.save(technician);
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
		return serSkillInfoDao.getSkillInfoById(id);
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

    public Page<SerSkillItem> findSerPage(Page<SerSkillItem> objectPage, SerSkillItem serInfo) {
		serInfo.setPage(objectPage);
		objectPage.setList(serSkillInfoDao.choiceSerlist(serInfo));
		return objectPage;
    }

	public Page<SerSkillTechnician> findTechnicianPage(Page<SerSkillTechnician> objectPage, SerSkillTechnician technicianInfo) {
		technicianInfo.setPage(objectPage);
		objectPage.setList(serSkillInfoDao.choiceTechnicianlist(technicianInfo));
		return objectPage;
	}

    public List<ServiceStation> getServiceStationList() {
		return serSkillInfoDao.getServiceStationList();
    }
}