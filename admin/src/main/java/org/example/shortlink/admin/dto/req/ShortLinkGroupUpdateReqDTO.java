package org.example.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接修改分组实体类
 */
@Data
public class ShortLinkGroupUpdateReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
