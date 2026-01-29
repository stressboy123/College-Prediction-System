package com.gdut.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/29
 */
@Data
public class ExcelCollegeDetailEntity {
    /**
     * 学校名称	教育行政主管部门	院校特性	所在地	详细地址	官方网址	招生网址	官方电话	院校满意度	专业满意度	专业推荐人数	专业推荐指数
     */
    @ExcelProperty(value = "学校名称")
    private String collegeName;
    @ExcelProperty(value = "教育行政主管部门")
    private String collegeDept;
    @ExcelProperty(value = "院校特性")
    private String collegeCharacter;
    @ExcelProperty(value = "所在地")
    private String collegeLocation;
    @ExcelProperty(value = "详细地址")
    private String collegeAddress;
    @ExcelProperty(value = "官方网址")
    private String collegeWebsite;
    @ExcelProperty(value = "招生网址")
    private String collegeEnrollWebsite;
    @ExcelProperty(value = "官方电话")
    private String collegePhone;
    @ExcelProperty(value = "院校满意度")
    private String collegeSatisfaction;
    @ExcelProperty(value = "专业满意度")
    private String collegeMajorSatisfaction;
    @ExcelProperty(value = "专业推荐人数")
    private String collegeMajorRecommendNum;
    @ExcelProperty(value = "专业推荐指数")
    private String collegeMajorRecommendIndex;
}
