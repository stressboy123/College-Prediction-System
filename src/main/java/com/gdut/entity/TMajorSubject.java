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
 * 各省专业选科要求表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_major_subject")
public class TMajorSubject {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 所属省份（关联t_province.id）
     */
    private Integer provinceId;
    /**
     * 院校名称
     */
    private String collegeName;
    /**
     * 院校编码
     */
    private String collegeCode;
    /**
     * 办学层次
     */
    private String schoolLevel;
    /**
     * 专业（类）名称
     */
    private String majorName;
    /**
     * 专业（类）代码
     */
    private String majorCode;
    /**
     * 首选科目要求
     */
    private String firstSubject;
    /**
     * 再选科目要求
     */
    private String secondSubject;
    /**
     * 类中所含专业及代码（用逗号分隔）
     */
    private String includedMajorsCodes;
    /**
     * 招考方向
     */
    private String enrollmentDirection;
}
