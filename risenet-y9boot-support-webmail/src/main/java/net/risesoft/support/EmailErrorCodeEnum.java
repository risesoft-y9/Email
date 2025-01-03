package net.risesoft.support;

import lombok.RequiredArgsConstructor;

import net.risesoft.exception.ErrorCode;

/**
 * 电子邮件错误代码枚举
 *
 * @author shidaobang
 * @date 2023/04/27
 */
@RequiredArgsConstructor
public enum EmailErrorCodeEnum implements ErrorCode {
    EMAIL_NOT_EXIST(0, "邮件不存在");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return 13;
    }

    @Override
    public int moduleCode() {
        return 0;
    }

    @Override
    public int moduleErrorCode() {
        return this.moduleErrorCode;
    }

    @Override
    public int getCode() {
        return ErrorCode.super.formatCode();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
