package com.thinkgem.jeesite.modules.sys.service.impl;

import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.mapper.LogDao;
import com.thinkgem.jeesite.modules.sys.service.LogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogDao, Log> implements LogService {

}
