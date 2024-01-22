package org.example.shortlink.admin.config;

import org.example.shortlink.admin.common.biz.user.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserTransmitFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new UserTransmitFilter(stringRedisTemplate));
        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("excludedUris", "/api/short-link/admin/v1/user/login");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
