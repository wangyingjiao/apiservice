package com.thinkgem.jeesite.modules.service.entity.skill;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.List;

/**
 * 技能 分类中间表 Entity
 * Created by Administrator on 2018/1/3.
 */
public class SerSkillSort extends DataEntity<SerSkillSort> {

    private static final long serialVersionUID = 1L;
    private String skillId; //技能id
    private String sortId;  //分类id
    private String orgId;

    public SerSkillSort() {
        super();
    }

    public SerSkillSort(String id){
        super(id);
    }
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getSkillId() {

        return skillId;
    }

    public String getSortId() {
        return sortId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}



