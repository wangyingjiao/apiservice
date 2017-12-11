package com.thinkgem.jeesite.modules.order.service.impl;

import com.thinkgem.jeesite.modules.order.entity.OrderGoods;
import com.thinkgem.jeesite.modules.order.mapper.OrderGoodsDao;
import com.thinkgem.jeesite.modules.order.service.OrderGoodsService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单服务关联表 服务实现类
 * </p>
 *
 * @author X
 * @since 2017-12-10
 */
@Service
public class OrderGoodsServiceImpl extends ServiceImpl<OrderGoodsDao, OrderGoods> implements OrderGoodsService {

}
