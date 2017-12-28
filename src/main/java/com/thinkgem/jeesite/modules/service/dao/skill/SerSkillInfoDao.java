/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.dao.skill;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.service.entity.item.SerItemInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillInfo;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillItem;
import com.thinkgem.jeesite.modules.service.entity.skill.SerSkillTechnician;
import com.thinkgem.jeesite.modules.service.entity.station.BasicServiceStation;
import com.thinkgem.jeesite.modules.service.entity.station.ServiceStation;
import com.thinkgem.jeesite.modules.service.entity.technician.ServiceTechnicianInfo;

import java.util.List;

/**
 * 技能管理DAO接口
 * @author a
 * @version 2017-11-15
 */
@MyBatisDao
public interface SerSkillInfoDao extends CrudDao<SerSkillInfo> {

    int checkDataName(SerSkillInfo serSkillInfo);

    List<SerItemInfo> choiceSerlist(SerSkillInfo serSkillInfo);

    List<SerSkillTechnician> choiceTechnicianlist(SerSkillInfo serSkillInfo);

    List<BasicServiceStation> getServiceStationList(BasicServiceStation station);
}