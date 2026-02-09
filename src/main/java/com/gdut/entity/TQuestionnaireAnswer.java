package com.gdut.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 用户问卷作答表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_questionnaire_answer")
public class TQuestionnaireAnswer {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户ID（关联sys_user.id）
     */
    private Long userId;
    /**
     * 问卷调查问题库主键
     */
    private Integer questionnaireId;
    /**
     * 答案内容
     */
    private String answerContent;
}
