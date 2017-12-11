package com.thinkgem.jeesite.modules.customer.service.impl;

import com.thinkgem.jeesite.modules.customer.entity.CustomerInfo;
import com.thinkgem.jeesite.modules.customer.mapper.CustomerInfoDao;
import com.thinkgem.jeesite.modules.customer.service.CustomerInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户信息 服务实现类
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@Service
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoDao, CustomerInfo> implements CustomerInfoService {

}
