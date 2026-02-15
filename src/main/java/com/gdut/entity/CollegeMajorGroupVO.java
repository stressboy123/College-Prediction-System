package com.gdut.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liujunliang
 * @date 2026/2/13
 * 院校专业组基础信息：关联院校、招生计划、录取数据
 */
@Data
public class CollegeMajorGroupVO {
    // 院校信息
    private String collegeCode;
    private String collegeName;
    private Integer collegeProvinceId; // 院校所属省份ID
    private String schoolLevel; // 本科/专科
    private String schoolNature; // 公办/民办/中外合作
    private String collegeType; // 是否是双一流
    // 专业组信息
    private String majorGroupCode;
    private Integer provinceId; // 招生省份ID
    private Integer year;
    private String batch; //本科批/专科批
    private String subjectType; // 物理/历史
    private String subjectRequirement; // 选科要求
    private Integer totalPlanCount; // 招生计划专业组总人数（聚合当前专业组内所有专业信息）
    private Integer tuitionFeeMin; // 组内最低学费（元/年，用于筛选的核心）
    private Integer tuitionFeeMax; // 组内最高学费（元/年，用于前端展示）
    private BigDecimal tuitionFeeAvg; // 组内平均学费（元/年）
    // 录取信息
    private Integer lowestAdmissionScore;
    private Integer lowestAdmissionRank;
}
