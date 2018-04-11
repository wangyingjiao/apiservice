package com.thinkgem.jeesite.modules.service.service.log;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.dao.SysJointWaitDao;
import com.thinkgem.jeesite.modules.sys.entity.SysJointWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
* @ClassName: SysJointLogService 
* @Description: TODO
* @author WYR
* @date 2018年1月31日 下午3:19:52 
*  
*/
@Service
@Transactional(readOnly = true)
public class SysJointWaitService extends CrudService<SysJointWaitDao, SysJointWait> {
    @Autowired
    private SysJointWaitDao sysJointWaitDao;

    public Page<SysJointWait> findPage(Page<SysJointWait> page, SysJointWait sysJointWait) {
        Page<SysJointWait> sysJointWaitPage =  super.findPage(page, sysJointWait);
        return sysJointWaitPage;
    }
    
}
