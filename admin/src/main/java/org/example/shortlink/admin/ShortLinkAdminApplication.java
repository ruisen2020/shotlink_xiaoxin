package org.example.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @MapperScan是Java中Spring框架的一个注解，用于自动扫描指定的包及其子包下的所有Mapper接口，并将其注册到MyBatis的全局Mapper接口中。
@MapperScan("org.example.shortlink.admin.dao.mapper")
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class, args);
    }
}
