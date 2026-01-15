package com.gdut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gdut.entity.SysUser;
import com.gdut.mapper.SysUserMapper;
import com.gdut.mapper.SysUserRoleMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    public CustomUserDetailsService(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户信息
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getStatus, 1)); // 仅查询正常状态用户
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 2. 查询用户关联的角色（如ROLE_USER、ROLE_ADMIN）
        String roles = sysUserRoleMapper.getRolesByUserId(sysUser.getId()); // 需自行实现该方法

        // 3. 封装为Security的UserDetails（密码已加密，此处直接传入）
        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(roles) // 角色转权限集合
        );
    }
}
