package com.gdut.entity;

import lombok.Data;

/**
 * @author liujunliang
 * @date 2026/1/26
 * Excel原始数据接收类
 */
@Data
public class ExcelRawData {
    private String col0;
    private String col1;
    private String col2;
    private String col3;
    private String col4;
    private String col5;
    private String col6;
    private String col7;
    private String col8;
    private String col9;
    private String col10;
    private String col11;
    private String col12;
    private String col13;
    private String col14;
    private String col15;
    private String col16;
    private String col17;
    private String col18;
    private String col19;
    private String col20;

    // 预留sheetNo字段（监听器手动赋值）
    private int sheetNo;
}
