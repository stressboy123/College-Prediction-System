package com.gdut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    // 根据角色名称查询角色ID（注册时分配默认角色）
    Long selectIdByRoleName(@Param("roleName") String roleName);
}
