package org.example.shortlink.common.enums;


import org.example.shortlink.common.convention.errorcode.IErrorCode;

/**
 * 短链接分组错误码
 */
public enum GroupErrorCodeEnum implements IErrorCode {

    Group_SAVE_ERROR("A000201", "短链接分组新增失败"),

    Group_UPDATE_ERROR("A000202", "短链接分组修改失败"),

    Group_DELETE_ERROR("A000203", "短链接分组删除失败"),

    Group_NOT_EXIST("A000204", "短链接分组不存在"),

    Group_LIST_ERROR("A000205", "短链接分组列表获取失败"),

    Group_NULL("A000206", "短链接分组记录为空"),

    Group_SORT_ERROR("A000207", "短链接分组排序失败"),
    Group_COUNT_ERROR("A000208", "至少应保留一个分组");


    private final String code;

    private final String message;

    GroupErrorCodeEnum(String code, String message) {
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
