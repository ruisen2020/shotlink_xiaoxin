package org.example.shortlink.admin.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.shortlink.common.biz.user.UserContext;
import org.example.shortlink.common.biz.user.UserInfoDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static org.example.shortlink.common.constant.RedisCacheConstant.LOGIN_USER_KEY;
import static org.example.shortlink.common.constant.RedisCacheConstant.LOGIN_USER_TTL;


@RequiredArgsConstructor
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token) || StrUtil.isBlank(username)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String key = LOGIN_USER_KEY + username;
        Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(key, token);
        // 3.判断用户是否存在
        if (userInfoJsonStr == null) {
            return true;
        }
        // 5.将查询到的hash数据转为userInfoDTO
        UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
        // 6.存在，保存用户信息到 ThreadLocal
        UserContext.setUser(userInfoDTO);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserContext.removeUser();
    }
}
	