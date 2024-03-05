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

    private final static String str = "create table t_group_%d\n" +
            "(\n" +
            "    id          bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    gid         varchar(32)  null comment '分组标识',\n" +
            "    name        varchar(64)  null comment '分组名称',\n" +
            "    username    varchar(256) null comment '创建分组用户名',\n" +
            "    sort_order  int          null comment '分组排序',\n" +
            "    create_time datetime     null comment '创建时间',\n" +
            "    update_time datetime     null comment '修改时间',\n" +
            "    del_flag    tinyint(1)   null comment '删除标识 0：未删除 1：已删除',\n" +
            "    constraint idx_unique_gid\n" +
            "        unique (gid)\n" +
            ")\n" +
            "    charset = utf8mb4;";

    @Test
    void test01() {

//        for(int i=0;i<16;i++)
//        {
//
//            System.out.printf(str.replace("%d\n",String.valueOf(i)));
//        }
//        String LOGIN_USER_KEY = "login:" + "xiaoxin128";
//        String token = "1f44d083-2bbb-4ced-a354-fb8ccf11b58d";
//        Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(LOGIN_USER_KEY, token);
//        System.out.println(userInfoJsonStr != null);
    }


    public static void main(String[] args) {
//        int a = 52;
//        while (a > 0) {
////            System.out.println(a % 26);
//            if (a % 26 == 0) {
//                a -= 26;
//                System.out.println("Z");
//            } else {
//                char c = (char) (a % 26 - 1 + 'A');
//                System.out.println(c);
//            }
//            a /= 26;
//        }
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        String[] str = new String[n];
//        for (int i = 0; i < n; i++) {
//            str[i] = sc.next();
//        }
//        HashMap<String, Integer> hashMap = new HashMap<>();
//        // hashMap 增强for循环
//        hashMap.forEach((k, v) -> {
//            System.out.println(k + " " + v);
//        });
//        Arrays.sort(str, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                int a = Integer.parseInt(o1 + o2);
//                int b = Integer.parseInt(o2 + o1);
//                return a - b;
//            }
//        });
//
//        System.out.println(Arrays.asList(str));
    }
}