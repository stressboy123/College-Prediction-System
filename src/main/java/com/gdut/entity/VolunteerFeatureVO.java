package com.gdut.entity;

import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/2/14
 * 志愿预测特征VO：线性回归+逻辑回归的统一输入
 */
@Data
public class VolunteerFeatureVO {
    // 一、用户特征（1维：和考生相关，固定值，所有院校专业组共享）
    private Integer userProvinceRank; // 考生全省排名（连续型）
    private Double subjectMatchRate; // 选科匹配度（0-1，离散型）
    // 二、院校专业组特征（核心，2维：每个院校专业组唯一，随候选集变化）
    private Double avgAdmissionRank; // 近3年录取最低排名平均值（连续型）
    private Double rankFluctuation; // 近3年录取排名波动值（标准差，连续型）
    private Double planChangeRate; // 2025相对2023的招生计划变化率（连续型）
    private Double schoolLevelScore; // 院校层次量化分（离散型）
    private Double majorHotScore; // 专业热度量化分（0-1，离散型，基于问卷和专业库）
    // 三、环境特征（1维：考生省份/年份相关，固定值，所有院校专业组共享）
    private Double provinceCandidateChangeRate; // 考生省份近3年高考人数变化率（连续型）
    private Double batchPlanTotalChangeRate; // 考生批次近3年招生计划总数变化率（连续型）
}
