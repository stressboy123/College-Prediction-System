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
 * 省份基础信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_province")
public class TProvince {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 省份编码（如行政区划代码）
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 是否参与新高考（0=否，1=是）
     */
    private Boolean isNewGaokao;
}
