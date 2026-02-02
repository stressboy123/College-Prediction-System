package com.gdut.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 志愿方案主表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_volunteer_plan")
public class TVolunteerPlan {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户详细信息表主键（关联t_user_info.id）
     */
    private Integer userInfoId;
    /**
     * 方案名称（如“冲稳保方案1”）
     */
    private String planName;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否默认方案（0=否，1=是）
     */
    private Boolean isDefault;
}
