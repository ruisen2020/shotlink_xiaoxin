package org.example.shortlink.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.shortlink.admin.common.biz.user.UserContext;
import org.example.shortlink.admin.common.convention.exception.ClientException;
import org.example.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.example.shortlink.admin.dao.entity.UserDO;
import org.example.shortlink.admin.dao.mapper.UserMapper;
import org.example.shortlink.admin.dto.req.UserLoginReqDTO;
import org.example.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.example.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.example.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.example.shortlink.admin.dto.resp.UserRespDTO;
import org.example.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.example.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.example.shortlink.admin.common.constant.RedisCacheConstant.LOGIN_USER_KEY;
import static org.example.shortlink.admin.common.enums.UserErrorCodeEnum.*;

/**
 * 用户接口实现层
 */
// @Service注解是Spring框架的一个注解，用于标注一个类为服务类，表明该类是一个服务提供者，可以被Spring容器管理，从而可以在应用程序中进行依赖注入。
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    // 布隆过滤器，用于在用户注册时，判断用户是否存在
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        // LambdaQueryWrapper 是一个通用查询封装工具类，可以用于构建各种数据库查询条件。它提供了 lambda 表达式的方式，使得构建查询条件可以更加简洁和易读。通过使用 LambdaQueryWrapper，可以方便地对查询条件进行组合和过滤。
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null)
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
//        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
//        UserDO userDO = baseMapper.selectOne(queryWrapper);
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO userRegisterReqDTO) {
        System.out.println(userRegisterReqDTO);
        if (hasUsername(userRegisterReqDTO.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
//            throw  new RuntimeException();
        }
        // 使用分布式锁来防止恶意发起大量相同用户名注册，给数据库造成巨大压力
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + userRegisterReqDTO.getUsername());
        try {
            // 只尝试一次获取锁
            if (lock.tryLock()) {
                int insert = baseMapper.insert(BeanUtil.toBean(userRegisterReqDTO, UserDO.class));
                if (insert < 1) {
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
//                    throw  new RuntimeException();
                }
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
            } else {
                throw new ClientException(USER_NAME_EXIST);
//                throw  new RuntimeException();
            }
        } finally {
            lock.unlock();
        }


    }

    @Override
    public void update(UserUpdateReqDTO userUpdateReqDTO) {
        // 验证当前用户名是否为登录用户
        if (!Objects.equals(UserContext.getUsername(), userUpdateReqDTO.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_UPDATE_ERROR);
        }
        // 可以将用户id传入进来，或者根据token信息判断
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, userUpdateReqDTO.getUsername());
        int update = baseMapper.update(BeanUtil.toBean(userUpdateReqDTO, UserDO.class), updateWrapper);
        if (update < 1) {
            throw new ClientException(UserErrorCodeEnum.USER_UPDATE_ERROR);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDO::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(USER_LOGIN_FAIL);
        }
        String key = LOGIN_USER_KEY + userDO.getUsername();

        // 如果之前已经登录过了并且token没有失效，那么在登陆的时候删除原来的token，在创建一个新的token。
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY);
        String token = null;
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException(USER_LOGIN_FAIL));
            stringRedisTemplate.delete(key);
        }

        token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(key, token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        String key = LOGIN_USER_KEY + username;
        return stringRedisTemplate.opsForHash().get(key, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        String key = LOGIN_USER_KEY + username;
        if (!checkLogin(username, token)) {
            throw new ClientException(USER_NOT_LOGIN);
        }
        stringRedisTemplate.delete(key);
    }


}
