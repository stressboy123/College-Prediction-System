package com.gdut.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.gdut.entity.BaseExcelEntity;
import com.gdut.entity.ExcelRawData;
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
     * @param headRowNum 表头行数
     * @param type Excel文件类型
     * @return 所有Sheet的原始映射数据
     */
    public static List<BaseExcelEntity> readAllSheets(String filePath, int headRowNum, ExcelTypeEnum type) {
        List<BaseExcelEntity> allSheetData = new ArrayList<>();

        try {
            File excelFile = new File(filePath);
            if (!excelFile.exists()) {
                throw new RuntimeException("Excel文件不存在：" + filePath);
            }

            // 获取所有Sheet信息
            ExcelReader excelReader = EasyExcel.read(excelFile)
                    .excelType(type)
                    .build();
            List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();

            // 逐个读取Sheet
            for (ReadSheet readSheet : readSheets) {
                MultiAliasExcelListener listener = new MultiAliasExcelListener(BaseExcelEntity.class);
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
     * 读取单个Sheet的数据
     * @param filePath Excel文件路径
     * @param sheetNo Sheet编号
     * @param headRowNum 表头行数
     * @param type Excel文件类型
     * @return 单个Sheet的映射数据
     */
    public static List<BaseExcelEntity> readSingleSheet(String filePath, int sheetNo, int headRowNum, ExcelTypeEnum type) {
        MultiAliasExcelListener listener = new MultiAliasExcelListener(BaseExcelEntity.class);
        EasyExcel.read(filePath)
                .headRowNumber(headRowNum)
                .excelType(type)
                .head(ExcelRawData.class)
                .registerReadListener(listener)
                .sheet(sheetNo)
                .doRead();
        return listener.getDataList();
    }
}
