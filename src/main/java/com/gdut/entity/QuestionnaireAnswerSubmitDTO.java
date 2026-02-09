package com.gdut.entity;

import lombok.Data;
import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@Data
public class QuestionnaireAnswerSubmitDTO {
    /**
     * 用户ID（关联sys_user.id）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 答案列表
     */
    @NotNull(message = "答案列表不能为空")
    private List<AnswerItemDTO> answerList;

    /**
     * 单题答案DTO
     */
    @Data
    public static class AnswerItemDTO {
        /**
         * 问题ID
         */
        @NotNull(message = "问题ID不能为空")
        private Integer questionnaireId;

        /**
         * 答案内容（多选用逗号分隔，如"选项1,选项2"；单选/填空直接存内容）
         */
        @NotNull(message = "答案内容不能为空")
        private String answerContent;
    }
}
