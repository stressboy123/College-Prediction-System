package com.gdut.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.gdut.entity.BaseExcelEntity;
import com.gdut.entity.ExcelRawData;
import com.gdut.target.ExcelMultiProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liujunliang
 * @date 2026/1/25
 * 实现多列名->统一实体字段的映射
 */
public class MultiAliasExcelListener extends AnalysisEventListener<ExcelRawData> {
    private final List<BaseExcelEntity> dataList = new ArrayList<>();
    private Map<String, Integer> headNameIndexMap; // 列名 → 列索引
    private Map<Field, List<String>> fieldAliasMap; // 实体字段 → 注解别名列表

    /**
     * 构造器：初始化时解析所有字段的注解别名
     * @param clazz 实体类字节码
     */
    public MultiAliasExcelListener(Class<BaseExcelEntity> clazz) {
        this.fieldAliasMap = parseFieldAlias(clazz);
    }

    /**
     * 遍历表头，存储「列名-列索引」映射
     * @param headMap 行数据
     * @param context 原始行数据
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        headNameIndexMap = new HashMap<>();
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            String headName = cleanString(entry.getValue().getStringValue());
            headNameIndexMap.put(headName, entry.getKey());
        }
    }

    /**
     * 行数据解析方法
     * @param entity 行数据初始转化实体（默认按索引映射字段）
     * @param context 原始行数据
     */
    @Override
    public void invoke(ExcelRawData entity, AnalysisContext context) {
        // 1. 过滤无用行（在整张表格当中，如果当前行B列、C列的单元格为空，就不属于表格数据，适配绝大部分表格异常数据）
        if (isColumnEmpty(entity)) {
            return;
        }

        // 2. 初始化默认值
        BaseExcelEntity targetEntity = new BaseExcelEntity();
        targetEntity.setSheetNo(context.readSheetHolder().getSheetNo()); // 赋值sheetNo
        targetEntity.setValid(true);
        targetEntity.setErrorMsg("");

        // 3. 遍历所有字段，按注解别名匹配赋值（核心逻辑）
        for (Map.Entry<Field, List<String>> entry : fieldAliasMap.entrySet()) {
            Field field = entry.getKey();
            List<String> aliases = entry.getValue(); // 当前字段的所有注解别名
            String cellValue = null;

            // 遍历别名，匹配到任意一个列名就取对应值
            for (String alias : aliases) {
                Integer colIndex = headNameIndexMap.get(alias);
                if (colIndex != null) {
                    cellValue = getRawDataValueByColIndex(entity, colIndex);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        break;
                    }
                }
            }

            // 4. 反射赋值到实体字段（有值才赋，未匹配则保持null）
            if (cellValue != null && !cellValue.isEmpty()) {
                try {
                    field.setAccessible(true);
                    field.set(targetEntity, cellValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("字段赋值失败：" + field.getName() + "，值：" + cellValue, e);
                }
            }
        }

        // 5. 将赋值后的实体加入列表
        dataList.add(targetEntity);
    }

    /**
     * 根据列索引从ExcelRawData获取对应colX的值（如索引0→col0，索引1→col1）
     * @param entity 行数据初始转化实体（默认按索引映射字段）
     * @param colIndex 列索引
     * @return 列值
     */
    private String getRawDataValueByColIndex(ExcelRawData entity, Integer colIndex) {
        try {
            Field field = ExcelRawData.class.getDeclaredField("col" + colIndex);
            field.setAccessible(true);
            Object value = field.get(entity);
            return value == null ? null : value.toString().trim();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 列索引超出定义（如col14不存在），返回null
            return null;
        }
    }

    /**
     * 判断当前行是否是列名或者数据
     * @param entity 行数据初始转化实体（默认按索引映射字段）
     * @return 是否是异常数据
     */
    private boolean isColumnEmpty(ExcelRawData entity) {
        return entity.getCol1() == null && entity.getCol2() == null;
    }

    /**
     * 字符串纯净处理（去空格/换行）
     * @param str 原始字符串
     * @return 处理后的字符串
     */
    private String cleanString(String str) {
        return str == null ? null : str.trim().replaceAll("\\s+", "");
    }

    /**
     * 解析@ExcelMultiProperty注解，构建「字段-别名列表」映射
     * @param clazz 注解类字节码
     * @return 字段-别名列表映射
     */
    private Map<Field, List<String>> parseFieldAlias(Class<BaseExcelEntity> clazz) {
        Map<Field, List<String>> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelMultiProperty annotation = field.getAnnotation(ExcelMultiProperty.class);
            if (annotation != null) {
                List<String> cleanAliases = new ArrayList<>();
                for (String alias : annotation.value()) {
                    cleanAliases.add(cleanString(alias));
                }
                map.put(field, cleanAliases);
            }
        }
        return map;
    }

    /**
     * 空实现：读取完成后无需处理
     * @param context 原始行数据
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    /**
     * 获取读取到的所有数据
     * @return 最终数据
     */
    public List<BaseExcelEntity> getDataList() {
        return dataList;
    }
}
