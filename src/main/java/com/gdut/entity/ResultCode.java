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
    // 权限错误（4xx）
    NO_AUTH(401, "未授权，请登录"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    // 业务错误（1xxx，自定义业务场景）
    REPEAT_SUBMIT(1001, "重复提交，请勿频繁操作"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    // 系统错误（5xx）
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),
    MQ_ERROR(501, "消息投递失败"),
    DB_ERROR(502, "数据库操作失败");

    private final int code;       // 状态码（遵循HTTP状态码规范，业务码自定义）
    private final String msg;     // 提示消息

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
