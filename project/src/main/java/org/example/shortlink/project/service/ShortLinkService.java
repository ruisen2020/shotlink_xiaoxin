package org.example.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.shortlink.project.dao.entity.ShortLinkDO;
import org.example.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.project.dto.resp.ShortLinkCreateRespDTO;


/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {


    ShortLinkCreateRespDTO createShotLink(ShortLinkCreateReqDTO shortLinkSaveReqDTO);
}
