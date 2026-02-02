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
 * 问卷调查问题库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_questionnaire")
public class TQuestionnaire {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 问题标题
     */
    private String questionTitle;
    /**
     * 问题类型（如单选/多选/填空）
     */
    private String questionType;
    /**
     * 选项（单选/多选时存储，用逗号分隔）
     */
    private String options;
    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;
}
