package org.example.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * 短链接分组查询每个分组下的短链接数量返回实体
 */
@Data
public class ShortLinkGroupCountQueryRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;
}
