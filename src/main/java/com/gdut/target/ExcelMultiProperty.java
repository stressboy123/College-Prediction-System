package com.gdut.target;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liujunliang
 * @date 2026/1/25
 * 自定义Excel多别名注解：适配不同省份的字段名差异
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMultiProperty {
    // 字段的所有可能列名
    String[] value();
    // 是否为可选字段（true=无值不校验，false=必填）
    boolean optional() default false;
}
