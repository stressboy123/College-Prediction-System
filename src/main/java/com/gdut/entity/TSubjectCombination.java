package com.gdut.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liujunliang
 * @date 2026/1/30
 * 选科组合表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_subject_combination")
public class TSubjectCombination {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 选科编码
     */
    private String subjectCode;
    /**
     * 选科名称（如“物理+化学+生物”）
     */
    private String subjectName;
    /**
     * 首选科目（物理/历史）
     */
    private String firstSubject;
    /**
     * 再选科目（多个用逗号分隔）
     */
    private String secondSubject;
}
