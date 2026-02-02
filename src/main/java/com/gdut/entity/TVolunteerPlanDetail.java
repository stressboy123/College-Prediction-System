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
 * 志愿方案详情表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_volunteer_plan_detail")
public class TVolunteerPlanDetail {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 志愿方案主表主键（关联t_volunteer_plan.id）
     */
    private Integer volunteerPlanId;
    /**
     * 院校表主键（关联t_college.id）
     */
    private Integer collegeId;
    /**
     * 专业信息表主键（关联t_major.id）
     */
    private Integer majorId;
    /**
     * 推荐类型（如冲/稳/保）
     */
    private String recommendType;
    /**
     * 录取概率（%）
     */
    private BigDecimal admissionProbability;
    /**
     * 推荐理由
     */
    private String recommendReason;
}
