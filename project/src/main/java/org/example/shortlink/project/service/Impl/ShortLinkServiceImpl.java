package org.example.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shortlink.common.convention.exception.ClientException;
import org.example.shortlink.common.convention.exception.ServiceException;
import org.example.shortlink.project.dao.entity.ShortLinkDO;
import org.example.shortlink.project.dao.mapper.ShortLinkMapper;
import org.example.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.example.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.example.shortlink.project.service.ShortLinkService;
import org.example.shortlink.project.util.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.example.shortlink.common.constant.RedisCacheConstant.LOCK_GID_UPDATE_KEY;
import static org.example.shortlink.common.enums.ShortLinkErrorCodeEnum.*;


/**
 * 短链接接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {


    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public ShortLinkCreateRespDTO createShotLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        // 将长链接转为跳转连接
        String shortUri = generateSuffix(shortLinkCreateReqDTO);
        String fullShortUrl = StrBuilder.create(shortLinkCreateReqDTO.getDomain())
                .append("/")
                .append(shortUri)
                .toString();

        ShortLinkDO shortLinkDO = BeanUtil.copyProperties(shortLinkCreateReqDTO, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortUri);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setEnableStatus(0);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException duplicateKeyException) {
            // TODO 这里需要在做一个校验，因为出现了布隆过滤器和数据库数据不一致的情况
            // 我们得到的shortUri在布隆过滤器中是已经可用的了，但当去插入数据时
            // 数据库却已经存在了相同的shortUri，这说明缓存和数据库数据不一致了，所以需要做一些措施
            // 为什么会不一致呢？
            // 存在一种情况：加入两个线程同时创建，它俩生成了相同的shortUri，但布隆过滤器中没有该shortUri
            // 所以它俩都会将数据插入到数据库中，其中必然有一个插入成功，有一个插入失败
            // 而且还会有两种情况：
            // 1.创建成功的那个线程已经把数据加入到缓存中
            // 2.创建成功的那个线程还没有把数据加入到缓存中
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO != null) {
                log.warn("短链接：{} 重复入库", fullShortUrl);
                throw new ServiceException(SHORTLINK_SAVE_ERROR);
            }

        }
        // 将生成的短链接加入到布隆过滤器中
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        ShortLinkCreateRespDTO shortLinkCreateRespDTO = ShortLinkCreateRespDTO.builder()
                .gid(shortLinkCreateReqDTO.getGid())
                .originUrl(shortLinkCreateReqDTO.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .build();
        return shortLinkCreateRespDTO;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getGid, shortLinkPageReqDTO.getGid());
        IPage<ShortLinkDO> result = baseMapper.selectPage(shortLinkPageReqDTO, queryWrapper);
        IPage<ShortLinkPageRespDTO> convert = result.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
        return convert;
    }

    @Override
    public Long countShortLinkByGid(String gid) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {

        String originGid = shortLinkUpdateReqDTO.getOriginGid();
        String gid = shortLinkUpdateReqDTO.getGid();

        // 查询该链接的原数据
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, originGid)
                .eq(ShortLinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

        if (shortLinkDO == null) {
            log.warn("更新的短链接在表中不存在，gid: {}, shortUrl: {}", originGid, shortLinkUpdateReqDTO.getFullShortUrl());
            throw new ClientException(SHORTLINK_NULL);
        }

        // 设置更新时间为null，这样MyBatis-Plus会自动帮我们填充
        shortLinkDO.setUpdateTime(null);
        // 设置原始连接
        shortLinkDO.setOriginUrl(shortLinkUpdateReqDTO.getOriginUrl());
        // 设置有效时间
        shortLinkDO.setValidDateType(shortLinkUpdateReqDTO.getValidDateType());
        shortLinkDO.setValidDate(shortLinkUpdateReqDTO.getValidDate());
        // 设置描述信息
        shortLinkDO.setDescribe(shortLinkUpdateReqDTO.getDescribe());

        // 如果修改分组，因为分表的原因，就需要将原数据在原表中删除，添加到新的表中
        LambdaUpdateWrapper<ShortLinkDO> eq = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, originGid)
                .eq(ShortLinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        if (StringUtils.equals(shortLinkDO.getGid(), gid)) {
            // 如果不修改分组，直接更新数据
            shortLinkDO.setGid(shortLinkUpdateReqDTO.getOriginGid());
            baseMapper.update(shortLinkDO, eq);
        } else {
            // 使用分布式锁
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, shortLinkUpdateReqDTO.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            if (!rLock.tryLock()) {
                throw new ServiceException("短链接正在被访问，请稍后再试...");
            }
            try {
                // 如果修改分组，因为分表的原因，就需要将原数据在原表中删除，添加到新的表中
                baseMapper.delete(eq);
                shortLinkDO.setGid(shortLinkUpdateReqDTO.getGid());
                // 将id设置为null，这样数据库就会自己填充id
                shortLinkDO.setId(null);
                baseMapper.insert(shortLinkDO);
            } catch (Exception e) {
                log.error("修改分组失败，原分组：{}，新分组：{}", originGid, gid);
                throw new ServiceException(SHORTLINK_UPDATE_ERROR);
            }
        }




        return null;
    }


    private String generateSuffix(ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        String shortUri = "";
        String originUrl = shortLinkCreateReqDTO.getOriginUrl();
        String fullShortUrl = "";
        // 记录生成shortUri次数,超过一定次数就抛出异常，作为兜底，防止循环时间过长
        int customGenerateCount = 0;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException(SHORTLINK_CREATE_ERROR);
            }
            // 因为短链接生成规则为hash(originUrl)，所以需要将originUrl进行加盐，防止再次hash冲突
            originUrl += UUID.randomUUID().toString();
            shortUri = HashUtil.hashToBase62(originUrl);
            fullShortUrl = shortLinkCreateReqDTO.getDomain() + "/" + shortUri;
            // 防止短链接创建过程中大量访问数据库给数据库造成压力，从而使用布隆过滤器作为缓存用来判断短链接是否存在
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                // 这里先不能加入到布隆过滤器中，因为还没有真正地加入到数据库中，所以要等到真正加入到数据库中在加入到布隆过滤器中
//                shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
                break;
            }
            customGenerateCount++;
        }
        return shortUri;

    }
}

