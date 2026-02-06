package com.gdut.service;

import com.gdut.entity.Result;

/**
 * @author liujunliang
 * @date 2026/1/31
 */
public interface ExcelService {
    Result<String> addCollege();

    Result<String> addMajor();

    Result<String> addEnrollmentPlan();

    Result<String> addAdmissionData();

    Result<String> addScoreRank();
}
