package org.example.shortlink.admin.test;


import org.example.shortlink.admin.ShortLinkAdminApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest(classes = {ShortLinkAdminApplication.class})
public class Test01 {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static String str = "create table t_user_%d\n" +
            "(\n" +
            "    id            bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    username      varchar(256) null comment '用户名',\n" +
            "    password      varchar(512) null comment '密码',\n" +
            "    real_name     varchar(256) null comment '真实姓名',\n" +
            "    phone         varchar(128) null comment '手机号',\n" +
            "    mail          varchar(512) null comment '邮箱',\n" +
            "    deletion_time bigint       null comment '注销时间戳',\n" +
            "    create_time   datetime     null comment '创建时间',\n" +
            "    update_time   datetime     null comment '修改时间',\n" +
            "    del_flag      tinyint(1)   null comment '删除标识 0：未删除 1：已删除',\n" +
            "    constraint idx_unique_username\n" +
            "        unique (username)\n" +
            ")\n" +
            "    charset = utf8mb4;";

    @Test
    void test01() {
        String LOGIN_USER_KEY = "login:" + "xiaoxin128";
        String token = "1f44d083-2bbb-4ced-a354-fb8ccf11b58d";
        Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(LOGIN_USER_KEY, token);
        System.out.println(userInfoJsonStr != null);
    }

    public static void main(String[] args) {

        for (int i = 0; i < 16; i++) {
            System.out.printf(str, i);
        }
    }
}