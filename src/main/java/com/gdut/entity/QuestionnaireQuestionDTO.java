package com.gdut.entity;

import lombok.Data;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@Data
public class QuestionnaireQuestionDTO {
    /**
     * 问题ID
     */
    private Integer id;
    /**
     * 问题标题
     */
    private String questionTitle;
    /**
     * 问题类型（single_choice/multiple_choice/fill_blank）
     */
    private String questionType;
    /**
     * 问题类型名称（单选/多选/填空）
     */
    private String questionTypeName;
    /**
     * 选项列表（单选/多选时返回，填空为null）
     */
    private List<String> options;
    /**
     * 排序
     */
    private Integer sort;
}
