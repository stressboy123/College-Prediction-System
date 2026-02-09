package com.gdut.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@Data
public class QuestionnaireUpdateDTO {
    /**
     * 问题ID（必填，唯一标识）
     */
    @NotNull(message = "问题ID不能为空")
    @Positive(message = "问题ID必须为正整数")
    private Integer id;

    /**
     * 问题标题（选填，修改时传则更新，不传则保留原内容）
     */
    @Length(max = 100, message = "问题标题长度不能超过100个字符")
    private String questionTitle;

    /**
     * 问题类型（选填，single_choice/multiple_choice/fill_blank）
     */
    private String questionType;

    /**
     * 选项（选填，单选/多选时需传，填空不传）
     */
    private String options;

    /**
     * 排序（选填，数字越小越靠前）
     */
    @PositiveOrZero(message = "排序值必须为非负数")
    private Integer sort;
}
