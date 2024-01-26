package org.example.shortlink.common.enums;


import org.example.shortlink.common.convention.errorcode.IErrorCode;

/**
 * 短链接错误码
 */
public enum ShortLinkErrorCodeEnum implements IErrorCode {

    SHORTLINK_SAVE_ERROR("A000201", "短链接新增失败"),

    SHORTLINK_UPDATE_ERROR("A000202", "短链接修改失败"),

    SHORTLINK_DELETE_ERROR("A000203", "短链接删除失败"),

    SHORTLINK_LIST_ERROR("A000204", "短链接列表获取失败"),
    SHORTLINK_CREATE_ERROR("A000205", "短链接新增频繁,请稍后再试"),

    SHORTLINK_NULL("A000206", "短链接记录为空"),

    SHORTLINK_SORT_ERROR("A000207", "短链接排序失败");


    private final String code;

    private final String message;

    ShortLinkErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
