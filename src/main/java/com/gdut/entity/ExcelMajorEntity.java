package com.gdut.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/30
 */
@Data
public class ExcelMajorEntity {
    /**
     * 专业门类	专业类	专业名称	专业代码	修业年限	授予学位	平均薪酬	选科建议	考研方向	简介	综合满意度	办学条件	教学质量	就业情况	近三年就业率
     */
    @ExcelProperty(value = "专业门类")
    private String majorCategory;
    @ExcelProperty(value = "专业类")
    private String majorClass;
    @ExcelProperty(value = "专业名称")
    private String majorName;
    @ExcelProperty(value = "专业代码")
    private String majorCode;
    @ExcelProperty(value = "修业年限")
    private String majorYears;
    @ExcelProperty(value = "授予学位")
    private String majorDegree;
    @ExcelProperty(value = "平均薪酬")
    private String majorSalary;
    @ExcelProperty(value = "选科建议")
    private String majorSubject;
    @ExcelProperty(value = "考研方向")
    private String majorKaoYan;
    @ExcelProperty(value = "简介")
    private String majorIntro;
    @ExcelProperty(value = "综合满意度")
    private String majorSatisfaction;
    @ExcelProperty(value = "办学条件")
    private String majorCondition;
    @ExcelProperty(value = "教学质量")
    private String majorQuality;
    @ExcelProperty(value = "就业情况")
    private String majorEmployment;
    @ExcelProperty(value = "近三年就业率")
    private String majorEmploymentRate;
}
