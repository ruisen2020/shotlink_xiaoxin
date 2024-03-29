package org.example.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.shortlink.project.dao.entity.ShortLinkDO;
import org.example.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.example.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;


/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {


    ShortLinkCreateRespDTO createShotLink(ShortLinkCreateReqDTO shortLinkSaveReqDTO);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkSaveReqDTO);

    Long countShortLinkByGid(String gid);

    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids);

    Void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO);
}
