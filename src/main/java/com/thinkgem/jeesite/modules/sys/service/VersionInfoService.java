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
        String build = versionInfo.getReceiveBuild();
        Integer integer = Integer.valueOf(build);
        VersionInfo info=new VersionInfo();
        VersionInfo versionInfo1 = versionInfoDao.getByTime(info);
        //比较build值 > 需要更新 <=不更新
        if (versionInfo1.getBuild()>integer){
            //如果最新的版本是不强制更新的
            if (versionInfo1.getForcedUpdate().equals("no")){
                info.setForcedUpdate("yes");
                //取数据库查询最新的一条强更的版本
                VersionInfo byTime = versionInfoDao.getByTime(info);
                Integer build1 = byTime.getBuild();
                //如果最新的一版是强更的 且比传来的版本高 把最新的版本传过去 变成强更（中间隔着一个强更的版本没更新 让他强制更新最新版本）
                if (byTime.getBuild()>integer){
                    versionInfo1.setForcedUpdate("yes");
                    return versionInfo1;
                }
            }
            return versionInfo1;
        }else{
            return null;
        }

    }

}