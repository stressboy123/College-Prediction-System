package com.gdut.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liujunliang
 * @date 2026/2/13
 */
@Data
public class EnrollmentGroupVO {
    // 聚合主键字段（和录取数据一致）
    private Integer provinceId;
    private Integer year;
    private String subjectType;
    private String batch;
    private String collegeCode;
    private String majorGroupCode;
    // 聚合后字段
    private Integer totalPlanCount; // 组内计划数总和（核心）
    private String subjectRequirement; // 选科要求（组内统一）
    private Integer tuitionFeeMin; // 组内最低学费（核心，用于筛选）
    private Integer tuitionFeeMax; // 组内最高学费（核心，用于展示）
    private BigDecimal tuitionFeeAvg; // 组内平均学费（可选）
    private Integer schoolSystem; // 学制（组内主流值）
}
