package com.gdut.entity;

import com.gdut.target.ExcelMultiProperty;
import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/25
 * 通用基础实体类：适配 12 省差异化字段，仅保留核心字段
 */
@Data
public class BaseExcelEntity {
    /**
     * 广东省：院校代码	院校名称	专业组代码	投档最低分	投档最低排位
     * 广西壮族自治区：院校代码	院校名称	专业组	投档最低分
     * 贵州省：院校代码	院校名称	专业代码	专业名称	录取最低分	录取最低位次
     * 河北省：院校代号	院校名称	专业代号	专业名称	投档最低分
     * 黑龙江省：院校代号	院校名称	专业组代号	专业组名称	投档分数
     * 湖北省：院校专业组代号	院校专业组名称	投档线
     * 湖南省：院校代号	院校名称	专业组编号	专业组名称	投档线
     * 江苏省：院校代号	院校、专业组（再选科目要求）	投档最低分
     * 江西省：院校代码	院校名称	专业组代号	专业组名称	投档线	最低投档排名
     * 辽宁省：院校编号	院校名称	专业代号	专业名称	投档最低分
     * 内蒙古自治区：院校代号	院校名称	专业组	最低分
     * 重庆市：院校代号	院校名称	专业代号	专业名称	投档最低分
     */
    @ExcelMultiProperty({"院校代码", "院校代号", "院校编号", "院校专业组代号"})
    private String collegeCode;

    @ExcelMultiProperty({"院校名称", "院校专业组名称", "院校、专业组（再选科目要求）"})
    private String collegeName;

    @ExcelMultiProperty({"专业组代码", "专业组", "专业代码", "专业代号", "专业组代号", "专业组编号", "院校专业组名称", "院校、专业组（再选科目要求）"})
    private String majorGroup;

    @ExcelMultiProperty({"投档最低分", "录取最低分", "投档线", "最低分", "投档分数"})
    private String lowestScore;

    @ExcelMultiProperty(value = {"投档最低排位", "录取最低位次", "最低投档排名"}, optional = true)
    private String lowestRank;

    @ExcelMultiProperty(value = {"专业名称", "专业组名称"}, optional = true)
    private String majorName;

    private String originalCollegeMajorGroup;
    private int sheetNo;
    private boolean isValid = true;
    private String errorMsg = "";
}
