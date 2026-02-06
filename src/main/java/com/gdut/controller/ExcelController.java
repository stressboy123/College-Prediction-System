package com.gdut.controller;

import com.gdut.entity.Result;
import com.gdut.service.ExcelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/1/31
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {
    @Resource
    private ExcelService excelService;

    @PostMapping("/addCollege")
    public Result<String> importCollegeExcel() {
        return excelService.addCollege();
    }

    @PostMapping("/addMajor")
    public Result<String> importMajorExcel() {
        return excelService.addMajor();
    }

    @PostMapping("/addEnrollmentPlan")
    public Result<String> importEnrollmentPlanExcel() {
        return excelService.addEnrollmentPlan();
    }

    @PostMapping("/addAdmissionData")
    public Result<String> importAdmissionDataExcel() {
        return excelService.addAdmissionData();
    }

    @PostMapping("/addScoreRank")
    public Result<String> importScoreRankExcel() {
        return excelService.addScoreRank();
    }
}
