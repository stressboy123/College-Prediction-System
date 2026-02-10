package com.gdut.entity;

import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/2/10
 */
@Data
public class UserInfoDTO {
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
     * 考生省份
     */
    private String candidateProvince;
    /**
     * 考生年份
     */
    private Integer candidateYear;
    /**
     * 首选科目
     */
    private String firstSubject;
    /**
     * 再选科目（多个用逗号分隔）
     */
    private String secondSubject;
    /**
     * 高考总分
     */
    private Integer gaokaoTotalScore;
    /**
     * 全省排名
     */
    private Integer provinceRank;
}
