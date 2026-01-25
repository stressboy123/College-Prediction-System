package com.gdut.utils;

import com.gdut.entity.BaseExcelEntity;
import com.gdut.target.ExcelMultiProperty;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * @author liujunliang
 * @date 2026/1/25
 * Excel OCR数据校验工具类（适配多Sheet数据，规则可灵活调整）
 */
public class ExcelDataValidator {
    // 正则：正整数（院校代码、排位）
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^\\d+$");
    // 正则：非负数字（可含小数，最低分）
    private static final Pattern NON_NEGATIVE_NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    /**
     * 校验单条数据：必填字段非空+格式，可选字段有值才校验
     */
    public static <T extends BaseExcelEntity> void validate(T entity) {
        StringBuilder errorMsg = new StringBuilder();
        Class<?> clazz = entity.getClass();

        try {
            for (Field field : clazz.getDeclaredFields()) {
                ExcelMultiProperty annotation = field.getAnnotation(ExcelMultiProperty.class);
                if (annotation == null) continue;

                field.setAccessible(true);
                String fieldValue = (String) field.get(entity);
                String fieldName = field.getName();
                String fieldCnName = getFieldCnName(fieldName);

                // 1. 必填字段：非空+格式校验
                if (!annotation.optional()) {
                    if (isEmpty(fieldValue)) {
                        errorMsg.append(fieldCnName).append("为空（必填字段）；");
                    } else {
                        validateFieldFormat(fieldName, fieldValue, errorMsg);
                    }
                }
                // 2. 可选字段：有值才校验格式
                else {
                    if (!isEmpty(fieldValue)) {
                        validateFieldFormat(fieldName, fieldValue, errorMsg);
                    }
                }
            }

            // 设置校验结果
            if (errorMsg.length() > 0) {
                entity.setValid(false);
                entity.setErrorMsg(errorMsg.toString());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("校验数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按字段类型校验格式
     */
    private static void validateFieldFormat(String fieldName, String fieldValue, StringBuilder errorMsg) {
        fieldValue = fieldValue.trim();
        switch (fieldName) {
            case "collegeCode":
                // 兼容江苏"需人工补充"的标记
                if (!POSITIVE_INTEGER_PATTERN.matcher(fieldValue).matches() && !"需人工补充".equals(fieldValue)) {
                    errorMsg.append("院校代码格式错误：'").append(fieldValue).append("'（应为正整数）；");
                }
                break;
            case "lowestScore":
                if (!NON_NEGATIVE_NUMBER_PATTERN.matcher(fieldValue).matches()) {
                    errorMsg.append("投档最低分格式错误：'").append(fieldValue).append("'（应为数字，可含小数）；");
                }
                break;
            case "lowestRank":
                if (!POSITIVE_INTEGER_PATTERN.matcher(fieldValue).matches()) {
                    errorMsg.append("投档最低排位格式错误：'").append(fieldValue).append("'（应为正整数）；");
                }
                break;
            // 名称类字段仅校验非空，不校验格式
            case "collegeName":
            case "majorGroup":
            case "majorName":
                break;
        }
    }

    /**
     * 字段英文名→中文名称（错误提示用）
     */
    private static String getFieldCnName(String fieldName) {
        switch (fieldName) {
            case "collegeCode":
                return "院校代码";
            case "collegeName":
                return "院校名称";
            case "majorGroup":
                return "专业组";
            case "lowestScore":
                return "投档最低分";
            case "lowestRank":
                return "投档最低排位";
            case "majorName":
                return "专业名称";
            default:
                return fieldName;
        }
    }

    /**
     * 空值判断（含空字符串）
     */
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
