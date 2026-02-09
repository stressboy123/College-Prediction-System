package com.gdut.entity;

import lombok.Getter;

/**
 * @author liujunliang
 * @date 2026/2/9
 * 问卷问题类型枚举
 */
@Getter
public enum QuestionTypeEnum {
    /**
     * 单选
     */
    SINGLE_CHOICE("single_choice", "单选"),
    /**
     * 多选
     */
    MULTIPLE_CHOICE("multiple_choice", "多选"),
    /**
     * 填空
     */
    FILL_BLANK("fill_blank", "填空");

    /**
     * 类型编码（存数据库）
     */
    private final String code;
    /**
     * 类型名称（前端展示）
     */
    private final String name;

    QuestionTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 根据编码获取枚举
    public static QuestionTypeEnum getByCode(String code) {
        for (QuestionTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
