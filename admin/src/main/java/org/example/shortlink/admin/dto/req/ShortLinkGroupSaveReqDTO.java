package org.example.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组保存请求实体类
 */

@Data
public class ShortLinkGroupSaveReqDTO {

    /**
     * 分组名
     */
    private String name;
}
