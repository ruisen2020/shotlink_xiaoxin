package org.example.shortlink.admin.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.shortlink.admin.common.convention.exception.ClientException;
import org.example.shortlink.admin.common.enums.UserErrorCodeEnum;
import org.example.shortlink.admin.dao.entity.UserDO;
import org.example.shortlink.admin.dao.mapper.UserMapper;
import org.example.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.example.shortlink.admin.dto.resp.UserRespDTO;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.example.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.example.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;

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
}
