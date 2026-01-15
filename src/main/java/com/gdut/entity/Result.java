package com.gdut.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private int code;
    private String msg;
    private T data;

    // 私有构造器：所有实例化通过静态方法完成
    private Result() {}

    // ==================== 成功场景：8个细分场景全覆盖 ====================
    /** 场景1：无数据+默认码+默认消息 */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(null);
        return result;
    }

    /** 场景2：带数据+默认码+默认消息 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    /** 场景3：无数据+默认码+自定义消息 */
    public static <T> Result<T> successWithCustomMsg(String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景4：带数据+默认码+自定义消息 */
    public static <T> Result<T> successWithCustomMsgAndData(String msg, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /** 场景5：无数据+自定义码+默认消息 */
    public static <T> Result<T> successWithCustomCode(int code) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(null);
        return result;
    }

    /** 场景6：带数据+自定义码+默认消息 */
    public static <T> Result<T> successWithCustomCodeAndData(int code, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    /** 场景7：无数据+自定义码+自定义消息 */
    public static <T> Result<T> successWithCustomCodeAndMsg(int code, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景8：带数据+自定义码+自定义消息 */
    public static <T> Result<T> successWithCustomCodeMsgAndData(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // ==================== 失败场景：9个细分场景全覆盖 ====================
    /** 场景1：基于ResultCode枚举+无数据 */
    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(null);
        return result;
    }

    /** 场景2：基于ResultCode枚举+自定义消息+无数据 */
    public static <T> Result<T> failWithCustomMsg(ResultCode resultCode, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(resultCode.getCode());
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景3：基于ResultCode枚举+自定义数据+默认消息 */
    public static <T> Result<T> failWithData(ResultCode resultCode, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(data);
        return result;
    }

    /** 场景4：基于ResultCode枚举+自定义消息+自定义数据 */
    public static <T> Result<T> failWithCustomMsgAndData(ResultCode resultCode, String msg, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(resultCode.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /** 场景5：自定义code+自定义msg+无数据 */
    public static <T> Result<T> failWithCustomCodeAndMsg(int code, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景6：自定义code+默认msg（匹配ResultCode，匹配不到用系统异常）+无数据 */
    public static <T> Result<T> failWithCustomCode(int code) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        // 匹配ResultCode的msg，匹配不到用系统异常msg
        String msg = getMsgByCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景7：自定义code+自定义msg+自定义数据 */
    public static <T> Result<T> failWithCustomCodeMsgAndData(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /** 场景8：仅自定义msg（默认code=500）+无数据 */
    public static <T> Result<T> failWithOnlyMsg(String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(ResultCode.SYSTEM_ERROR.getCode());
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /** 场景9：仅自定义msg（默认code=500）+自定义数据 */
    public static <T> Result<T> failWithOnlyMsgAndData(String msg, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(ResultCode.SYSTEM_ERROR.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 辅助方法：根据code匹配ResultCode的msg，匹配不到返回系统异常msg
    private static String getMsgByCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode.getMsg();
            }
        }
        return ResultCode.SYSTEM_ERROR.getMsg();
    }
}
