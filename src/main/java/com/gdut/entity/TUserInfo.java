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
 * 用户详细信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_info")
public class TUserInfo {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 系统用户表主键
     */
    private Long sysUserId;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 考生省份（关联t_province.id）
     */
    private Integer candidateProvinceId;
    /**
     * 考生年份
     */
    private Integer candidateYear;
    /**
     * 选科组合（关联t_subject_combination.id）
     */
    private Integer subjectCombinationId;
    /**
     * 高考总分
     */
    private Integer gaokaoTotalScore;
    /**
     * 全省排名
     */
    private Integer provinceRank;
}
