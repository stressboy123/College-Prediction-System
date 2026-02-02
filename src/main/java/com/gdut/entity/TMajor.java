package com.gdut.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 专业信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_major")
public class TMajor {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 专业门类（如工学/理学/文学）
     */
    private String majorCategory;
    /**
     * 专业类（如计算机类/电子信息类）
     */
    private String majorType;
    /**
     * 专业名称
     */
    private String majorName;
    /**
     * 专业代码
     */
    private String majorCode;
    /**
     * 修业年限
     */
    private String educationLength;
    /**
     * 授予学位
     */
    private String degreeAwarded;
    /**
     * 平均薪酬（元/月）
     */
    private String averageSalary;
    /**
     * 考研方向
     */
    private String postgraduateDirection;
    /**
     * 简介
     */
    private String majorIntro;
    /**
     * 综合满意度（0-5分）
     */
    private BigDecimal comprehensiveSatisfaction;
    /**
     * 办学条件（0-5分）
     */
    private BigDecimal schoolCondition;
    /**
     * 教学质量（0-5分）
     */
    private BigDecimal teachingQuality;
    /**
     * 就业情况（0-5分）
     */
    private BigDecimal employmentSituation;
    /**
     * 近三年就业率
     */
    private String threeYearEmploymentRate;
}
