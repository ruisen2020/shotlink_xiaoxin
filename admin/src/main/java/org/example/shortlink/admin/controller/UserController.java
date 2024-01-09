package org.example.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.example.shortlink.admin.common.convention.result.Result;
import org.example.shortlink.admin.common.convention.result.Results;
import org.example.shortlink.admin.dto.resp.UserActualRespDTO;
import org.example.shortlink.admin.dto.resp.UserRespDTO;
import org.example.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
// @RestController注解是Spring框架中的一个注解，用于将Java类标记为RESTful控制器。使用@RestController注解的类会自动映射RESTful请求，并将返回结果作为JSON格式返回
@RestController
// @RequiredArgsConstructor 使用构造器注入，该注解会为类生成一个带有一个参数的构造函数，并将传入的参数用于初始化类的属性。
@RequiredArgsConstructor
public class UserController {

    // 自动注入UserService对象， @Autowired 是根据类型自动注入对象，如果有多个实现类，则会报错

    // 使用@RequiredArgsConstructor注入的时候，一定要加final关键字
    private final UserService userService;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    // 使用@GetMapping注解表示对HTTP GET请求的处理。函数的路径为"/api/shortlink/v1/user/{username}"，即当访问该路径时，将会调用该函数。参数username为路径中的变量，可以被函数使用。
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/shortlink/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }
}
