package com.gdut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    // 批量查询角色ID
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    // 新增用户-角色关联（注册时分配角色）
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
