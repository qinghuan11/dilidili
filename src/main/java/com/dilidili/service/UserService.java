package com.dilidili.service;

import com.dilidili.dao.domain.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户对象
     * @throws IllegalArgumentException 如果用户名或邮箱已存在
     */
    void register(User user);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象，未找到返回 null
     */
    User findByUsername(String username);

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 清除用户缓存
     *
     * @param username 用户名
     */
    void evictUserCache(String username);
}