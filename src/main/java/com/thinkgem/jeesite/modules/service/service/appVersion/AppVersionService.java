package com.thinkgem.jeesite.modules.service.service.appVersion;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.modules.service.dao.appVersion.AppVersionDao;
import com.thinkgem.jeesite.modules.service.entity.appVersion.AppVersion;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * APP发版管理Service
 * @author a
 * @version 2018-01-29
 */
@Service
@Transactional(readOnly = true)
public class AppVersionService extends CrudService<AppVersionDao,AppVersion> {
    @Autowired
    private AppVersionDao appVersionDao;

    public static final String CACHE_NEWEST_VERSION = "newestVersion";

    public Page<AppVersion> findPage(Page<AppVersion> page, AppVersion appVersion) {
        //appVersion.getSqlMap().put("dsf", dataRoleFilter(UserUtils.getUser(), "a"));
        return super.findPage(page, appVersion);
    }

    @Transactional(readOnly = false)
    public void save(AppVersion appVersion) {
            appVersion.setUpdateBy(UserUtils.getUser());
            appVersion.setUpdateDate(new Date());
            super.save(appVersion);
        AppVersion newest = appVersionDao.getNewestVersion();
        CacheUtils.put(CACHE_NEWEST_VERSION,newest);
    }

    public AppVersion getData(String id) {
        AppVersion appVersion = super.get(id);
        return appVersion;
    }

    @Transactional(readOnly = false)
    public void delete(AppVersion appVersion) {
        super.delete(appVersion);
        AppVersion newest = appVersionDao.getNewestVersion();
        CacheUtils.put(CACHE_NEWEST_VERSION,newest);
    }

    public AppVersion getNewest() {
        AppVersion newest = appVersionDao.getNewestVersion();
        return newest;
    }
}
