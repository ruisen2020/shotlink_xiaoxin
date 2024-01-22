package org.example.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.shortlink.admin.common.database.BaseDO;

@Data // @Data是Lombok的一个注解，用于同时生成getter、setter、toString、equals和hashcode方法。可以简化Java类的代码编写，提高开发效率。
@TableName("t_user") // @TableName注解用于在Java中指定函数操作的目标表是哪个，方便在代码中进行数据库操作时指定表名。
public class UserDO extends BaseDO {


    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;


}
