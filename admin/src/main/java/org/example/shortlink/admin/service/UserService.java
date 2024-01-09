package org.example.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.shortlink.admin.dao.entity.UserDO;
import org.example.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
// IService是一个服务接口，它定义了服务的一些基本操作的抽象方法，具体的实现类需要根据接口的定义来实现这些方法。
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);
}
