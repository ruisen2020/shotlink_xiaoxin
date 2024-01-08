package org.example.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.shortlink.admin.dao.entity.UserDO;

/**
 * 用户持久层
 */
// BaseMapper是一个通用的数据库Mapper接口，它包含了大部分通用的数据库操作方法。继承它就可以少写很多方法。
public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户
     */
    UserDO selectByUsername(String username);
}
