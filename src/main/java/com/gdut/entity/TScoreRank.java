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
 * 各省历年一分一段表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_score_rank")
public class TScoreRank {
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
     * 分数
     */
    private Integer score;
    /**
     * 分数段人数
     */
    private Integer scoreSegmentCount;
    /**
     * 累计人数
     */
    private Integer cumulativeCount;
}
