package com.gdut.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Mapper
public interface SysUserRoleMapper {
    String getRolesByUserId(Long userId);
}
