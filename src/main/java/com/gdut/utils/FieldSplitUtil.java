package com.gdut.utils;

import com.gdut.entity.ExcelAdmissionDataEntity;

/**
 * @author liujunliang
 * @date 2026/1/25
 * 专门处理湖北/江苏融合字段拆分，兼容常见格式
 */
public class FieldSplitUtil {

    /**
     * 对外暴露的拆分方法：手动传入省份，触发对应拆分逻辑
     * @param entity 待拆分的实体
     * @param province 省份名称（如"江苏"、"湖北"）
     */
    public static void splitFusedField(ExcelAdmissionDataEntity entity, String province) {
        if (entity == null || province == null) {
            return;
        }
        // 仅处理湖北/江苏，其他省份不拆分
        if ("湖北".equals(province)) {
            splitHubeiField(entity);
        } else if ("江苏".equals(province)) {
            splitJiangsuField(entity);
        }
        // 其他省份（如广东、广西）无需拆分，直接跳过
    }

    /**
     * 拆分湖北字段：从"院校专业组代号/名称"提取核心信息
     */
    private static void splitHubeiField(ExcelAdmissionDataEntity entity) {
        // 1. 拆分院校代码（从collegeCode："院校专业组代号"提取）
        String codeFused = entity.getCollegeCode();
        if (codeFused != null && codeFused.contains("-")) {
            String[] codeParts = codeFused.split("-");
            if (codeParts.length >= 2) {
                entity.setCollegeCode(codeParts[0].trim()); // 提取院校代码
                entity.setMajorGroup(codeParts[1].trim()); // 提取专业组
            } else {
                entity.setErrorMsg(entity.getErrorMsg() + "湖北代号拆分失败：格式无'-'分隔（" + codeFused + "）；");
                entity.setValid(false);
            }
        } else {
            entity.setErrorMsg(entity.getErrorMsg() + "湖北代号格式异常：" + codeFused + "；");
            entity.setValid(false);
        }

        // 2. 拆分院校名称（从collegeName："院校专业组名称"提取）
        String nameFused = entity.getCollegeName();
        if (nameFused != null) {
            // 按"-"或"（"分割，提取纯院校名称
            String[] nameParts = nameFused.split("[-（(]");
            if (nameParts.length > 0) {
                entity.setCollegeName(nameParts[0].trim());
            }
        } else {
            entity.setErrorMsg(entity.getErrorMsg() + "湖北院校名称为空；");
            entity.setValid(false);
        }
    }

    /**
     * 拆分江苏字段：从"院校、专业组（再选科目要求）"提取核心信息
     */
    private static void splitJiangsuField(ExcelAdmissionDataEntity entity) {
        // 融合字段已赋值到collegeName/majorGroup，取其中一个即可
        String fusedField = entity.getMajorGroup();
        if (fusedField == null || fusedField.isEmpty()) {
            entity.setErrorMsg(entity.getErrorMsg() + "江苏融合字段为空；");
            entity.setValid(false);
            return;
        }

        // 1. 拆分院校名称（按"、"分割）
        String[] nameParts = fusedField.split("、");
        if (nameParts.length < 2) {
            entity.setErrorMsg(entity.getErrorMsg() + "江苏格式无'、'分隔（" + fusedField + "）；");
            entity.setValid(false);
            return;
        }
        entity.setCollegeName(nameParts[0].trim()); // 覆盖为精准院校名称

        // 2. 拆分专业组（按"（"分割，提取数字/核心标识）
        String majorPart = nameParts[1].trim();
        String[] majorParts = majorPart.split("[（(]");
        String preciseMajorGroup = majorParts.length > 0 ? majorParts[0].trim() : majorPart;
        entity.setMajorGroup(preciseMajorGroup); // 覆盖为精准专业组

        // 3. 江苏无直接院校代码，标记需人工补充
        if (entity.getCollegeCode() == null || entity.getCollegeCode().isEmpty()) {
            entity.setCollegeCode("需人工补充");
        }
    }
}
