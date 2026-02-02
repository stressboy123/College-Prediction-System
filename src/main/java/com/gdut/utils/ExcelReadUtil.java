package com.gdut.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.gdut.entity.ExcelAdmissionDataEntity;
import com.gdut.entity.ExcelCollegeDetailEntity;
import com.gdut.entity.ExcelCollegeEntity;
import com.gdut.entity.ExcelEnrollmentPlanEntity;
import com.gdut.entity.ExcelMajorEntity;
import com.gdut.entity.ExcelRawData;
import com.gdut.listener.ExcelMultiAliasListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liujunliang
 * @date 2026/1/25
 * 通用读取工具类：读取数据+多别名映射
 */
public class ExcelReadUtil {

    /**
     * 读取 2025 全国普通高等学校名单
     *
     * @param excelFile excel文件
     * @param sheetNo  sheet编号
     * @param headRowNum 头行数
     * @param type       文件类型
     * @return 院校库基础数据
     */
    public static List<ExcelCollegeEntity> readForExcelCollege(File excelFile, int sheetNo, int headRowNum, ExcelTypeEnum type) {
        List<ExcelCollegeEntity> list = EasyExcel.read(excelFile)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelCollegeEntity.class)
                .sheet(sheetNo)
                .doReadSync();
        return list;
    }
    /**
     * 读取院校库
     *
     * @param excelFile excel文件
     * @param sheetNo  sheet编号
     * @param headRowNum 头行数
     * @param type       文件类型
     * @return 院校库详细数据
     */
    public static List<ExcelCollegeDetailEntity> readForExcelCollegeDetail(File excelFile, int sheetNo, int headRowNum, ExcelTypeEnum type) {
        List<ExcelCollegeDetailEntity> list = EasyExcel.read(excelFile)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelCollegeDetailEntity.class)
                .sheet(sheetNo)
                .doReadSync();
        return list;
    }

    /**
     * 读取专业库
     *
     * @param excelFile excel文件
     * @param sheetNo  sheet编号
     * @param headRowNum 头行数
     * @param type       文件类型
     * @return 专业库数据
     */
    public static List<ExcelMajorEntity> readForExcelMajor(File excelFile, int sheetNo, int headRowNum, ExcelTypeEnum type) {
        List<ExcelMajorEntity> list = EasyExcel.read(excelFile)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelMajorEntity.class)
                .sheet(sheetNo)
                .doReadSync();
        return list;
    }

    /**
     * 读取招生计划（多sheet）
     * @param excelFile excel文件
     * @param headRowNum 头行数
     * @param type 文件类型
     * @return 招生计划数据
     */
    public static List<ExcelEnrollmentPlanEntity> readForExcelEnrollmentPlan(File excelFile, int headRowNum, ExcelTypeEnum type) {
        List<ExcelEnrollmentPlanEntity> list = EasyExcel.read(excelFile)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelEnrollmentPlanEntity.class)
                .doReadAllSync();
        return list;
    }

    /**
     * 读取录取数据Excel所有Sheet的数据（多别名）
     * @param excelFile Excel文件
     * @param headRowNum 表头行数
     * @param type Excel文件类型
     * @return 所有Sheet的原始映射数据
     */
    public static List<ExcelAdmissionDataEntity> readForExcelAdmissionDataAllSheets(File excelFile, int headRowNum, ExcelTypeEnum type) {
        List<ExcelAdmissionDataEntity> allSheetData = new ArrayList<>();

        try {
            if (!excelFile.exists()) {
                throw new RuntimeException("Excel文件不存在：" + excelFile);
            }

            // 获取所有Sheet信息
            ExcelReader excelReader = EasyExcel.read(excelFile)
                    .excelType(type)
                    .build();
            List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();

            // 逐个读取Sheet
            for (ReadSheet readSheet : readSheets) {
                ExcelMultiAliasListener<ExcelAdmissionDataEntity> listener = new ExcelMultiAliasListener<>(ExcelAdmissionDataEntity.class);
                EasyExcel.read(excelFile)
                        .headRowNumber(headRowNum)
                        .excelType(type)
                        .head(ExcelRawData.class)
                        .registerReadListener(listener)
                        .sheet(readSheet.getSheetNo())
                        .doRead();
                allSheetData.addAll(listener.getDataList());
            }

            excelReader.close();
        } catch (Exception e) {
            throw new RuntimeException("读取Excel失败：" + e.getMessage(), e);
        }

        return allSheetData;
    }

    /**
     * 读取excel文件默认顺序数据
     * @param excelFile excel文件
     * @param headRowNum 头行数
     * @param type 文件类型
     * @return 原始读取数据
     */
    public static List<ExcelRawData> readForExcelAllSheetOrigin(File excelFile, int headRowNum, ExcelTypeEnum type) {
        List<ExcelRawData> list = EasyExcel.read(excelFile)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelRawData.class)
                .doReadAllSync();
        return list;
    }
}
