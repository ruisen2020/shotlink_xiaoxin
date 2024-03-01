package org.example.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shortlink.admin.dao.entity.GroupDO;
import org.example.shortlink.admin.dao.mapper.GroupMapper;
import org.example.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.example.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.example.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import org.example.shortlink.admin.remote.ShortLinkRemoteService;
import org.example.shortlink.admin.service.GroupService;
import org.example.shortlink.admin.toolkit.RandomGenerator;
import org.example.shortlink.common.biz.user.UserContext;
import org.example.shortlink.common.convention.exception.ClientException;
import org.example.shortlink.common.convention.result.Result;
import org.example.shortlink.common.enums.GroupErrorCodeEnum;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final RedissonClient redissonClient;

    @Override
    public void SaveGroup(String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (hasGid(gid));
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .username(UserContext.getUsername())
                .name(groupName)
                .sortOrder(0)
                .build();
        int insert = baseMapper.insert(groupDO);
        if (insert < 1) {
            throw new ClientException(GroupErrorCodeEnum.Group_SAVE_ERROR);
        }
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(queryWrapper);
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOS = BeanUtil.copyToList(groupDOS, ShortLinkGroupRespDTO.class);
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        // 第一种方式，分别通过gid去查询，需要发送多次请求
//        shortLinkGroupRespDTOS.forEach(shortLinkGroupRespDTO -> {
//            String gid = shortLinkGroupRespDTO.getGid();
//            // 根据分组id查询当前分组下有多少个短链接
//            Result<Long> longResult = shortLinkRemoteService.countShortLinkByGid(gid);
//            shortLinkGroupRespDTO.setShortLinkCount(Math.toIntExact(longResult.getData()));
//        });
        // 第二种方式，讲全部的gid集合传给远程服务，远程服务再根据gid集合去查询
        List<String> gids = shortLinkGroupRespDTOS.stream()
                .map(ShortLinkGroupRespDTO::getGid)
                .collect(Collectors.toList());
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService.listGroupShortLinkCount(gids);
        List<ShortLinkGroupCountQueryRespDTO> data = listResult.getData();
       // 将list集合转为map集合
        Map<String, Integer> collect = data.stream()
                .collect(Collectors.toMap(ShortLinkGroupCountQueryRespDTO::getGid, ShortLinkGroupCountQueryRespDTO::getShortLinkCount));
        for (ShortLinkGroupRespDTO shortLinkGroupRespDTO : shortLinkGroupRespDTOS) {
            String gid = shortLinkGroupRespDTO.getGid();
            Integer shortLinkCount = collect.get(gid);
            shortLinkGroupRespDTO.setShortLinkCount(shortLinkCount);
        }
        return shortLinkGroupRespDTOS;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, shortLinkGroupUpdateReqDTO.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(shortLinkGroupUpdateReqDTO.getName());
        int update = baseMapper.update(groupDO, updateWrapper);
        if (update < 1) {
            throw new ClientException(GroupErrorCodeEnum.Group_UPDATE_ERROR);
        }
    }

    @Override
    public synchronized void deleteGroup(String gid) {
        // TODO 这里先检查后修改，可能会出现并发安全问题
        // 可以使用乐观锁或者悲观锁
        // 这是一个好问题
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        Long l = baseMapper.selectCount(queryWrapper);
        if (l <= 1) {
            throw new ClientException(GroupErrorCodeEnum.Group_COUNT_ERROR);
        }
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        int update = baseMapper.update(groupDO, updateWrapper);
        if (update < 1) {
            throw new ClientException(GroupErrorCodeEnum.Group_DELETE_ERROR);
        }
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTOS) {
        shortLinkGroupSortReqDTOS.forEach(shortLinkGroupSortReqDTO -> {
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, shortLinkGroupSortReqDTO.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            GroupDO groupDO = new GroupDO();
            groupDO.setSortOrder(shortLinkGroupSortReqDTO.getSortOrder());
            int update = baseMapper.update(groupDO, updateWrapper);
            if (update < 1) {
                throw new ClientException(GroupErrorCodeEnum.Group_SORT_ERROR);
            }
        });
    }

    /**
     * 查询是否有该Gid
     */
    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid);
        GroupDO hasGroupFlag = baseMapper.selectOne(queryWrapper);
        return hasGroupFlag != null;
    }


}
