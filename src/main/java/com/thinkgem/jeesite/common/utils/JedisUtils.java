/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thinkgem.jeesite.common.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * cluster Cache 工具类
 *
 * @author ThinkGem
 * @version 2014-6-29
 */
public class JedisUtils {

    private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static JedisCluster cluster = SpringContextHolder.getBean(JedisCluster.class);

    public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        String value = null;

        try {

            if (cluster.exists(key)) {
                value = cluster.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Object getObject(String key) {
        Object value = null;

        try {

            if (cluster.exists(getBytesKey(key))) {
                value = toObject(cluster.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObject {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String set(String key, String value, int cacheSeconds) {
        String result = null;

        try {

            result = cluster.set(key, value);
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result = null;

        try {

            result = cluster.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObject {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<String> getList(String key) {
        List<String> value = null;

        try {

            if (cluster.exists(key)) {
                value = cluster.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getList {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        List<Object> value = null;

        try {

            if (cluster.exists(getBytesKey(key))) {
                List<String> list = cluster.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (String bs : list) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectList {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;

        try {

            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.rpush(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setList {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;

        try {

            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            List<Object> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (String[]) list.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectList {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long listAdd(String key, String... value) {
        long result = 0;

        try {

            result = cluster.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listAdd {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long listObjectAdd(String key, Object... value) {
        long result = 0;

        try {

            List<Object> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (String[]) list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listObjectAdd {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<String> getSet(String key) {
        Set<String> value = null;

        try {

            if (cluster.exists(key)) {
                value = cluster.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getSet {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<Object> getObjectSet(String key) {
        Set<Object> value = null;

        try {

            if (cluster.exists(getBytesKey(key))) {
                value = Sets.newHashSet();
                Set<String> set = cluster.smembers(getBytesKey(key));
                for (String bs : set) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectSet {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;

        try {

            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.sadd(key, (String[]) value.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSet {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;

        try {

            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            Set<String> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = cluster.sadd(getBytesKey(key), (String[]) set.toArray());
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectSet {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long setSetAdd(String key, String... value) {
        long result = 0;

        try {

            result = cluster.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetAdd {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long setSetObjectAdd(String key, Object... value) {
        long result = 0;

        try {

            Set<String> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = cluster.rpush(getBytesKey(key), (String[]) set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetObjectAdd {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, String> getMap(String key) {
        Map<String, String> value = null;

        try {

            if (cluster.exists(key)) {
                value = cluster.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getMap {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;

        try {

            if (cluster.exists(getBytesKey(key))) {
                value = Maps.newHashMap();
                Map<String, String> map = cluster.hgetAll(getBytesKey(key));
                for (Map.Entry<String, String> e : map.entrySet()) {
                    value.put(e.getKey(), toObject(e.getValue()));
                }
                logger.debug("getObjectMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectMap {} = {}", key, value, e);
        }
        return value;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;

        try {

            if (cluster.exists(key)) {
                cluster.del(key);
            }
            result = cluster.hmset(key, value);
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setMap {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;

        try {

            if (cluster.exists(getBytesKey(key))) {
                cluster.del(key);
            }
            Map<String, String> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = cluster.hmset(getBytesKey(key), (Map<String, String>) map);
            if (cacheSeconds != 0) {
                cluster.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectMap {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String mapPut(String key, Map<String, String> value) {
        String result = null;

        try {

            result = cluster.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapPut {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;

        try {

            Map<String, String> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = cluster.hmset(getBytesKey(key), (Map<String, String>) map);
            logger.debug("mapObjectPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapObjectPut {} = {}", key, value, e);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long mapRemove(String key, String mapKey) {
        long result = 0;

        try {

            result = cluster.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapRemove {}  {}", key, mapKey, e);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long mapObjectRemove(String key, String mapKey) {
        long result = 0;

        try {

            result = cluster.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean mapExists(String key, String mapKey) {
        boolean result = false;

        try {

            result = cluster.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapExists {}  {}", key, mapKey, e);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;

        try {

            result = cluster.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectExists {}  {}", key, mapKey, e);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public static long del(String key) {
        long result = 0;

        try {

            if (cluster.exists(key)) {
                result = cluster.del(key);
                logger.debug("del {}", key);
            } else {
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public static long delObject(String key) {
        long result = 0;
        try {

            if (cluster.exists(getBytesKey(key))) {
                result = cluster.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("delObject {}", key, e);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean exists(String key) {
        boolean result = false;

        try {

            result = cluster.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.warn("exists {}", key, e);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean existsObject(String key) {
        boolean result = false;

        try {

            result = cluster.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        }
        return result;
    }

    /**
     * 获取资源
     * @return
     * @throws clusterException
     */
//	public static cluster getResource() throws clusterException {
//		cluster cluster = null;
//		try {
//			cluster = clusterPool.;
////			logger.debug("getResource.", cluster);
//		} catch (clusterException e) {
//			logger.warn("getResource.", e);
//			returnBrokenResource(cluster);
//			throw e;
//		}
//		return cluster;
//	}

    /**
     * 归还资源
     * @param cluster
     * @param isBroken
     */
//	public static void returnBrokenResource(cluster cluster) {
//		if (cluster != null) {
//			clusterPool.returnBrokenResource(cluster);
//		}
//	}

    /**
     * 释放资源
     * @param cluster
     * @param isBroken
     */
//	public static void returnResource(cluster cluster) {
//		if (cluster != null) {
//			clusterPool.returnResource(cluster);
//		}
//	}

    /**
     * 获取byte[]类型Key
     *
     * @param key
     * @return
     */
    public static String getBytesKey(Object object) {
        if (object instanceof String) {
            return (String) object;
        } else {
            byte[] bytes = ObjectUtils.serialize(object);
            BASE64Encoder encoder = new BASE64Encoder();
            String encode = encoder.encode(bytes);
            return encode;
        }
    }

    /**
     * 获取byte[]类型Key
     *
     * @param key
     * @return
     */
    public static Object getObjectKey(String key) {
        try {
            return key;
        } catch (UnsupportedOperationException uoe) {
            try {
                return JedisUtils.toObject(key);
            } catch (UnsupportedOperationException uoe2) {
                uoe2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Object转换byte[]类型
     *
     * @param key
     * @return
     */
    public static String toBytes(Object object) {
        byte[] bytes = ObjectUtils.serialize(object);
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(bytes);
        return encode;
    }

    /**
     * byte[]型转换Object
     *
     * @param key
     * @return
     */
    public static Object toObject(String string) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = new byte[0];
        try {
            bytes = decoder.decodeBuffer(string);
            return ObjectUtils.unserialize(bytes);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static JedisCluster getResource() {
        return cluster;
    }

    public static void returnResource(JedisCluster jedis) {
    }
}
