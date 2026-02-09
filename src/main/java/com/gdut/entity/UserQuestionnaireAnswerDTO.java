package com.gdut.entity;

import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@Data
public class UserQuestionnaireAnswerDTO {
    /**
     * 问题ID
     */
    private Integer questionnaireId;
    /**
     * 问题标题
     */
    private String questionTitle;
    /**
     * 问题类型
     */
    private String questionType;
    /**
     * 用户答案内容
     */
    private String answerContent;
}
