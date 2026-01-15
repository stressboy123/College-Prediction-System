package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.SysUser;
import com.gdut.mapper.SysUserMapper;
import com.gdut.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public void saveUserWithRole(SysUser user, long l) {
        //保存用户（默认分配USER角色）
    }
}
