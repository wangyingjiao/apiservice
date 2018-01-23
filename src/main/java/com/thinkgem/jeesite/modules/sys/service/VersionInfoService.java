/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.dao.MessageInfoDao;
import com.thinkgem.jeesite.modules.sys.dao.VersionInfoDao;
import com.thinkgem.jeesite.modules.sys.entity.MessageInfo;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 服务技师基础信息Service
 *
 * @author a
 * @version 2017-11-16
 */
@Service
@Transactional(readOnly = true)
public class VersionInfoService extends CrudService<VersionInfoDao, VersionInfo> {

    @Autowired
    private VersionInfoDao versionInfoDao;



    //获取最新的版本
    public VersionInfo getByTime(VersionInfo versionInfo){
        //获取传过来的code
        String build = versionInfo.getBuild();
        VersionInfo info=new VersionInfo();
        VersionInfo versionInfo1 = versionInfoDao.getByTime(info);
        //比较build值
        int i = versionInfo1.getBuild().compareTo(build);
        //>1 前大 需要更新
        if (i>0){
            return versionInfo1;
        }else{
            //如果最新的版本是不强制更新的
            if (versionInfo1.getForcedUpdate().equals("no")){
                info.setForcedUpdate("yes");
                VersionInfo byTime = versionInfoDao.getByTime(info);
                int i1 = byTime.getBuild().compareTo(build);
                if (i1>0){
                    return byTime;
                }
            }
            return null;
        }

    }



}