package com.thinkgem.jeesite.modules.sys.service.impl;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.mapper.UserDao;
import com.thinkgem.jeesite.modules.sys.service.UserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}
