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
 * 院校表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_college")
public class TCollege {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 院校编码
     */
    private String collegeCode;
    /**
     * 院校名称
     */
    private String collegeName;
    /**
     * 主管部门
     */
    private String competentAuthority;
    /**
     * 所属省份主键（关联t_province.id）
     */
    private Integer provinceId;
    /**
     * 办学层次（如本科/专科/双一流）
     */
    private String schoolLevel;
    /**
     * 办学性质（如公办/民办/中外合作）
     */
    private String schoolNature;
    /**
     * 院校性质（如综合类/理工类/文史类）
     */
    private String collegeType;
    /**
     * 详细地址
     */
    private String detailedAddress;
    /**
     * 官方网址
     */
    private String officialWebsite;
    /**
     * 招生网址
     */
    private String enrollmentWebsite;
    /**
     * 官方电话
     */
    private String officialPhone;
    /**
     * 院校满意度（0-5分）
     */
    private String collegeSatisfaction;
    /**
     * 专业满意度（0-5分）
     */
    private String majorSatisfaction;
    /**
     * 专业推荐人数
     */
    private String majorRecommendCount;
    /**
     * 专业推荐指数（0-5分）
     */
    private String majorRecommendIndex;
}
