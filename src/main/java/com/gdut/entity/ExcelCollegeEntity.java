package com.gdut.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/29
 */
@Data
public class ExcelCollegeEntity {
    /**
     * 序号	学校名称	学校标识码	主管部门	所在地	办学层次	备注
     */
    @ExcelProperty(value = "序号")
    private String serialNumber;
    @ExcelProperty(value = "学校名称")
    private String collegeName;
    @ExcelProperty(value = "学校标识码")
    private String collegeCode;
    @ExcelProperty(value = "主管部门")
    private String collegeDept;
    @ExcelProperty(value = "所在地")
    private String collegeLocation;
    @ExcelProperty(value = "办学层次")
    private String collegeLevel;
    @ExcelProperty(value = "备注")
    private String collegeRemark;
}
