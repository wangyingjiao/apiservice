package com.thinkgem.jeesite.common.redis;

import com.thinkgem.jeesite.common.config.Global;

public class JedisConstant {
    // 存储文件位置
    public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");
    // APP最新版本信息
    public static final String CACHE_NEWEST_VERSION = "newestVersion";
    // redis登陆密码
    public static final String REDIS_PASSWORD = "redisPwd";


}
