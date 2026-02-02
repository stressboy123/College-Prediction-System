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
 * 各省历年投档录取数据表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_admission_data")
public class TAdmissionData {
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
     * 批次
     */
    private String batch;
    /**
     * 批次备注
     */
    private String batchRemark;
    /**
     * 科类
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
     * 投档最低分
     */
    private BigDecimal lowestAdmissionScore;
    /**
     * 投档最低排位
     */
    private Integer lowestAdmissionRank;
}
