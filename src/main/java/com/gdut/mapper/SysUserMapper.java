package com.gdut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 根据用户名查询用户（登录/注册校验）
    SysUser selectByUsername(@Param("username") String username);
}
