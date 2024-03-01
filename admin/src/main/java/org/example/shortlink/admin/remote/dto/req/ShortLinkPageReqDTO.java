package org.example.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.example.shortlink.admin.dao.entity.ShortLinkDO;


@Data
public class ShortLinkPageReqDTO  extends Page<ShortLinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}
