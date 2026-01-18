package com.gdut.entity;

import lombok.Getter;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Getter
public enum ResultCode {
    // 成功状态
    SUCCESS(200, "操作成功"),
    // 参数错误（4xx）
    PARAM_ERROR(400, "参数错误"),
    PARAM_MISS(400, "缺少必填参数"),
    // 认证错误（4xx）
    USER_NOT_FOUND(401, "用户不存在"),
    INVALID_CREDENTIALS(401, "用户名或密码错误"),
    TOKEN_EXPIRED(401, "登录凭证已过期，请重新登录"),
    // 用户管理错误（4xx）
    USER_DISABLED(401, "账户已被禁用"),
    USERNAME_EXISTS(409, "用户名已存在"),
    USER_REGISTER_FAILED(422, "用户注册失败"),
    // 权限错误（4xx）
    NO_AUTH(401, "未认证，请登录"),
    NO_LOGIN(401, "请先登录"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    ROLE_NOT_FOUND(404, "角色不存在"),
    // 业务错误（1xxx，自定义业务场景）
    REPEAT_SUBMIT(1001, "重复提交，请勿频繁操作"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    // 系统错误（5xx）
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),
    MQ_ERROR(501, "消息投递失败"),
    DB_ERROR(502, "数据库操作失败"),
    // 密码相关
    WEAK_PASSWORD(400, "密码强度不够"),
    INVALID_OLD_PASSWORD(400, "原密码错误"),
    PASSWORD_MISMATCH(400, "两次输入的密码不一致"),
    // 数据库相关
    DUPLICATE_ENTRY(409, "数据重复"),
    DATA_INTEGRITY_VIOLATION(400, "数据完整性错误");

    private final int code;       // 状态码（遵循HTTP状态码规范，业务码自定义）
    private final String msg;     // 提示消息

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
