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

    private final static String str = "create table t_link_%d\n" +
            "(\n" +
            "    id              bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    domain          varchar(128)                   null comment '域名',\n" +
            "    short_uri       varchar(8) collate utf8mb3_bin null comment '短链接',\n" +
            "    full_short_url  varchar(128)                   null comment '完整短链接',\n" +
            "    origin_url      varchar(1024)                  null comment '原始链接',\n" +
            "    click_num       int default 0                  null comment '点击量',\n" +
            "    gid             varchar(32)                    null comment '分组标识',\n" +
            "    enable_status   tinyint(1)                     null comment '启用标识 0：未启用 1：已启用',\n" +
            "    created_type    tinyint(1)                     null comment '创建类型 0：控制台 1：接口',\n" +
            "    valid_date_type tinyint(1)                     null comment '有效期类型 0：永久有效 1：用户自定义',\n" +
            "    valid_date      datetime                       null comment '有效期',\n" +
            "    `describe`      varchar(1024)                  null comment '描述',\n" +
            "    create_time     datetime                       null comment '创建时间',\n" +
            "    update_time     datetime                       null comment '修改时间',\n" +
            "    del_flag        tinyint(1)                     null comment '删除标识 0：未删除 1：已删除',\n" +
            "    constraint idx_unique_full_short_uri\n" +
            "        unique (full_short_url)\n" +
            ")\n" +
            "    charset = utf8mb4;\n" +
            "\n";

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