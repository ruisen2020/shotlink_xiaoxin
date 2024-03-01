package org.example.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.shortlink.admin.dao.entity.GroupDO;
import org.example.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.example.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.example.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param groupName 分组名称
     */
    void saveGroup(String groupName);

    /**
     * 新增短链接分组
     * @param username  用户名
     * @param groupName 分组名
     */
    void saveGroup(String username,String groupName);

    /**
     * 查询短链接分组集合
     * @return 短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 更新短链接分组
     * @param shortLinkGroupUpdateReqDTO 分组信息
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO);

    /**
     * 删除短链接分组
     * @param gid 分组ID标识
     */
    void deleteGroup(String gid);

    /**
     * 排序短链接分组
     * @param shortLinkGroupSortReqDTOS 排序信息
     */

    void sortGroup(List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTOS);
}
