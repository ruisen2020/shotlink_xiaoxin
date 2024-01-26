package org.example.shortlink.common.constant;

/**
 * 短链接后管 Redis 缓存常量类
 */
public class RedisCacheConstant {
    public static final Long LOGIN_USER_TTL = 30L;
    /**
     * 用户登录缓存
     */
    public static final String LOGIN_USER_KEY = "login:";
    /**
     * 用户注册分布式锁
     */

    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-register:";

    /**
     * 分组创建分布式锁
     */
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group-create:%s";
}
