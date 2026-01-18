package com.gdut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    // 新增用户-角色关联（注册时分配角色）
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
