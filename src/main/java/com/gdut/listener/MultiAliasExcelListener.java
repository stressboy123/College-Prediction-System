package com.gdut.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.gdut.entity.BaseExcelEntity;
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
public class MultiAliasExcelListener<T extends BaseExcelEntity> extends AnalysisEventListener<T> {
    private final List<T> dataList = new ArrayList<>();
    private final Class<T> clazz;
    private Map<String, Integer> headNameIndexMap; // 列名 → 列索引
    private Map<Field, List<String>> fieldAliasMap; // 实体字段 → 注解别名列表
    private int currentRowNum = 0; // 当前行号（过滤无用行）

    // 构造器：初始化时解析所有字段的注解别名
    public MultiAliasExcelListener(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldAliasMap = parseFieldAlias(clazz);
    }

    // 遍历表头，存储「列名-列索引」映射
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        headNameIndexMap = new HashMap<>();
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            String headName = cleanString(entry.getValue().getStringValue());
            headNameIndexMap.put(headName, entry.getKey());
        }
    }

    // 行数据解析方法
    @Override
    public void invoke(T entity, AnalysisContext context) {
        currentRowNum++;
        // 1. 过滤无用行（表头行、页码行、空行）
        String rowContent = getRowContent(context);
        if (currentRowNum < 1 || rowContent.endsWith("页")) {
            return;
        }

        // 2. 初始化：强制清空实体所有字段（避免EasyExcel默认填充）
        resetEntityFields(entity);

        // 3. 记录Sheet序号（供后续手动传省份拆分用）
        entity.setSheetNo(context.readSheetHolder().getSheetNo());

        // 4. 获取行数据
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();

        // 5. 遍历所有字段，按注解别名匹配赋值（核心逻辑）
        for (Map.Entry<Field, List<String>> entry : fieldAliasMap.entrySet()) {
            Field field = entry.getKey();
            List<String> aliases = entry.getValue(); // 当前字段的所有注解别名
            String cellValue = null;

            // 遍历别名，匹配到任意一个列名就取对应值
            for (String alias : aliases) {
                Integer colIndex = headNameIndexMap.get(alias);
                if (colIndex != null && cellMap.containsKey(colIndex)) {
                    Cell cell = cellMap.get(colIndex);
                    if (cell instanceof ReadCellData) {
                        ReadCellData<?> readCellData = (ReadCellData<?>) cell;
                        cellValue = getCellValueByType(readCellData);
                        break;
                    }
                }
            }

            // 4. 反射赋值到实体字段（有值才赋，未匹配则保持null）
            if (cellValue != null && !cellValue.isEmpty()) {
                try {
                    field.setAccessible(true);
                    field.set(entity, cellValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("字段赋值失败：" + field.getName() + "，值：" + cellValue, e);
                }
            }
        }

        // 5. 将赋值后的实体加入列表
        dataList.add(entity);
    }

    // 清空实体所有字段（覆盖EasyExcel默认填充）
    private void resetEntityFields(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                // 1. 处理基本数据类型：设置对应的默认值
                if (fieldType == int.class) {
                    field.set(entity, 0); // int默认值0
                } else if (fieldType == long.class) {
                    field.set(entity, 0L);
                } else if (fieldType == float.class) {
                    field.set(entity, 0.0f);
                } else if (fieldType == double.class) {
                    field.set(entity, 0.0d);
                } else if (fieldType == boolean.class) {
                    field.set(entity, false);
                } else if (fieldType == char.class) {
                    field.set(entity, '\u0000'); // char默认空字符
                } else if (fieldType == byte.class) {
                    field.set(entity, (byte) 0);
                } else if (fieldType == short.class) {
                    field.set(entity, (short) 0);
                } else {
                    // 2. 处理引用类型（String、包装类等）：设为null
                    field.set(entity, null);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("清空实体字段失败：" + field.getName(), e);
            }
        }
    }

    // 按单元格类型取值（NUMBER/STRING）
    private String getCellValueByType(ReadCellData<?> cellData) {
        if (cellData == null) {
            return null;
        }
        // 根据类型选择取值方法
        if (CellDataTypeEnum.NUMBER.equals(cellData.getType())) {
            // 数字类型：转字符串（避免科学计数法）
            return cellData.getNumberValue() != null ? cellData.getNumberValue().toString() : null;
        } else if (CellDataTypeEnum.STRING.equals(cellData.getType())) {
            // 字符串类型：直接取
            return cleanString(cellData.getStringValue());
        } else {
            // 其他类型（BOOLEAN/公式）：转字符串
            return cleanString(cellData.toString());
        }
    }

    // 字符串纯净处理（去空格/换行）
    private String cleanString(String str) {
        return str == null ? null : str.trim().replaceAll("\\s+", "");
    }

    // 获取当前行内容（过滤无用行）
    private String getRowContent(AnalysisContext context) {
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
        StringBuilder sb = new StringBuilder();
        for (Cell cell : cellMap.values()) {
            if (cell instanceof ReadCellData) {
                ReadCellData<?> cellData = (ReadCellData<?>) cell;
                sb.append(getCellValueByType(cellData)).append(" ");
            }
        }
        return cleanString(sb.toString());
    }

    // 解析@ExcelMultiProperty注解，构建「字段-别名列表」映射
    private Map<Field, List<String>> parseFieldAlias(Class<T> clazz) {
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

    // 空实现：读取完成后无需处理
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}

    // 获取读取到的所有数据
    public List<T> getDataList() {
        return dataList;
    }
}
