package com.gdut.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/30
 */
@Data
public class ExcelEnrollmentPlanEntity {
    /**
     * 年份	生源地	批次	批次备注	科类	院校代码	院校名称	专业组代码	专业代码	专业名称	专业备注	其他要求	选科要求	计划人数	学制	学费
     */
    @ExcelProperty(value = "年份")
    private String year;
    @ExcelProperty(value = "生源地")
    private String origin;
    @ExcelProperty(value = "批次")
    private String batch;
    @ExcelProperty(value = "批次备注")
    private String batchRemark;
    @ExcelProperty(value = "科类")
    private String collegeClass;
    @ExcelProperty(value = "院校代码")
    private String collegeCode;
    @ExcelProperty(value = "院校名称")
    private String collegeName;
    @ExcelProperty(value = "专业组代码")
    private String majorGroupCode;
    @ExcelProperty(value = "专业代码")
    private String majorCode;
    @ExcelProperty(value = "专业名称")
    private String majorName;
    @ExcelProperty(value = "专业备注")
    private String majorRemark;
    @ExcelProperty(value = "其他要求")
    private String otherRequirements;
    @ExcelProperty(value = "选科要求")
    private String subjectRequirements;
    @ExcelProperty(value = "计划人数")
    private String planNumber;
    @ExcelProperty(value = "学制")
    private String studySystem;
    @ExcelProperty(value = "学费")
    private String tuition;
}
