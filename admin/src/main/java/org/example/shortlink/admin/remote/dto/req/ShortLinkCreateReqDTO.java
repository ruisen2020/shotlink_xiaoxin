package org.example.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkCreateReqDTO {

    /**
     * 域名
     */
    private String domain;


    /**
     * 原始链接
     */
    private String originUrl;


    /**
     * 分组标识
     */
    private String gid;


    /**
     * 创建类型 0：控制台 1：接口
     */
    private int createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    // JsonFormat:用于指定JSON序列化和反序列化时日期格式的格式化规则。
    // 其中，pattern属性指定了日期格式的模式
    // timezone属性指定了时区。
    // 在序列化和反序列化日期对象时，会根据指定的格式化规则进行转换。
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 描述
     */
    private String describe;

}
