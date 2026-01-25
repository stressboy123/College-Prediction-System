package com.gdut.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.gdut.entity.BaseExcelEntity;
import com.gdut.listener.MultiAliasExcelListener;

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
     * 读取Excel所有Sheet的数据
     * @param filePath Excel文件路径
     * @param clazz 实体类字节码
     * @return 所有Sheet的原始映射数据
     */
    public static <T extends BaseExcelEntity> List<T> readAllSheets(String filePath, Class<T> clazz) {
        List<T> allSheetData = new ArrayList<>();

        try {
            File excelFile = new File(filePath);
            if (!excelFile.exists()) {
                throw new RuntimeException("Excel文件不存在：" + filePath);
            }

            // 获取所有Sheet信息
            com.alibaba.excel.ExcelReader excelReader = EasyExcel.read(excelFile)
                    .excelType(ExcelTypeEnum.XLSX) // xls格式改为ExcelTypeEnum.XLS
                    .build();
            List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();

            // 逐个读取Sheet
            for (ReadSheet readSheet : readSheets) {
                MultiAliasExcelListener<T> listener = new MultiAliasExcelListener<>(clazz);
                EasyExcel.read(excelFile)
                        .headRowNumber(2)
                        .excelType(ExcelTypeEnum.XLSX)
                        .head(clazz)
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
     * 读取单个Sheet的数据
     */
    public static <T extends BaseExcelEntity> List<T> readSingleSheet(String filePath, Class<T> clazz, int sheetNo) {
        MultiAliasExcelListener<T> listener = new MultiAliasExcelListener<>(clazz);
        EasyExcel.read(filePath)
                .headRowNumber(2)
                .excelType(ExcelTypeEnum.XLSX)
                .head(clazz)
                .registerReadListener(listener)
                .sheet(sheetNo)
                .doRead();
        return listener.getDataList();
    }
}
