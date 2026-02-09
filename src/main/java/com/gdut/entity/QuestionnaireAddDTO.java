package com.gdut.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@Data
public class QuestionnaireAddDTO {
    /**
     * 问题标题（必填，长度1-100）
     */
    @NotBlank(message = "问题标题不能为空")
    @Length(max = 100, message = "问题标题长度不能超过100个字符")
    private String questionTitle;

    /**
     * 问题类型（必填，single_choice/multiple_choice/fill_blank）
     */
    @NotBlank(message = "问题类型不能为空")
    private String questionType;

    /**
     * 选项（单选/多选时必填，填空可为空，逗号分隔）
     */
    private String options;

    /**
     * 排序（必填，数字越小越靠前）
     */
    @NotNull(message = "排序值不能为空")
    @PositiveOrZero(message = "排序值必须为非负数")
    private Integer sort;
}
