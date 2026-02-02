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
 * 各省招生计划表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_enrollment_plan")
public class TEnrollmentPlan {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 年份
     */
    private Integer year;
    /**
     * 所属省份（关联t_province.id）
     */
    private Integer provinceId;
    /**
     * 批次（如本科提前批/本科批/专科批）
     */
    private String batch;
    /**
     * 批次备注
     */
    private String batchRemark;
    /**
     * 科类（如物理类/历史类/文史类/理工类）
     */
    private String subjectType;
    /**
     * 院校代码
     */
    private String collegeCode;
    /**
     * 院校名称
     */
    private String collegeName;
    /**
     * 专业组代码
     */
    private String majorGroupCode;
    /**
     * 专业代码
     */
    private String majorCode;
    /**
     * 专业名称
     */
    private String majorName;
    /**
     * 专业备注
     */
    private String majorRemark;
    /**
     * 其他要求
     */
    private String otherRequirements;
    /**
     * 选科要求
     */
    private String subjectRequirement;
    /**
     * 计划人数
     */
    private Integer planCount;
    /**
     * 学制
     */
    private Integer schoolSystem;
    /**
     * 学费（元/年）
     */
    private Integer tuitionFee;
}
